package Capstone.checkmate.service;

import Capstone.checkmate.domain.*;
import Capstone.checkmate.dto.*;
import Capstone.checkmate.repository.InspectionImageRepository;
import Capstone.checkmate.repository.InspectionRepository;
import Capstone.checkmate.repository.MemberRepository;
import Capstone.checkmate.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InspectionService {

    private final RestTemplate restTemplate;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final ModelRepository modelRepository;
    private final InspectionRepository inspectionRepository;
    private final InspectionImageRepository inspectionImageRepository;

    @Value("${model.server.url}")
    private String modelServerUrl;

    private static final int MAX_UPLOAD_COUNT  = 30;
    private static final int MAX_TOTAL_IMAGE_COUNT = 200;

    /**
     * Inspection 수행 (mask, part 공용)
     */
    @Transactional
    public Object createInspection(String userName, String modelName, List<MultipartFile> files) {
        if (files.size() > MAX_UPLOAD_COUNT) {
            throw new IllegalArgumentException("최대 30개의 이미지만 업로드 가능합니다.");
        }

        // entity 조회
        Member member = memberRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        Model model = modelRepository.findByName(modelName)
                .orElseThrow(() -> new IllegalStateException("모델이 존재하지 않습니다."));

        // DB 저장 최대 이미지 수 조절
        handleStorageLimit(model, files.size());

        // S3 업로드 및 presigned URL 생성
        List<String> keys = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        List<String> originalFileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!isImage(file)) throw new IllegalArgumentException("이미지 파일만 허용됩니다.");
            String originalFilename = file.getOriginalFilename();
            String key = s3Service.uploadFile(file);

            originalFileNames.add(originalFilename);
            keys.add(key);
            urls.add(s3Service.generatePresignedUrl(key, 3)); // 3분 유효 Presigned URL
        }

        // 모델 서버 추론 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<String> urlStrings = urls.stream().map(URL::toString).toList();
        Map<String, Object> body = Map.of("image_urls", urlStrings);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<?> response;
        if(modelName.equalsIgnoreCase("mask")) {
            response = restTemplate.postForEntity(
                    modelServerUrl + "/predict/mask",
                    entity,
                    MaskServerResult[].class
            );
        } else {
            response = restTemplate.postForEntity(
                    modelServerUrl + "/predict/part",
                    entity,
                    PredictResponse[].class
            );
        }

        // InspectionImage 리스트 생성 (Inspection Entity 생성에 필요)
        List<InspectionImage> inspectionImages = new ArrayList<>();
        List<PredictionResponse> resultDTOs = new ArrayList<>();

        if (modelName.equalsIgnoreCase("mask")) {
            for (int i = 0; i < files.size(); i++) {
                MaskServerResult res = ((MaskServerResult[]) response.getBody())[i];
                InspectResult result = res.getLabel().equalsIgnoreCase("yes") ? InspectResult.NORMAL : InspectResult.ABNORMAL;

                // 엔티티 및 DTO 생성
                inspectionImages.add(InspectionImage.createInspectionImage(originalFileNames.get(i), keys.get(i), result));
                resultDTOs.add(MaskPredictionResult.builder()
                        .fileName(originalFileNames.get(i))
                        .result(res.getLabel())
                        .confidence(res.getConfidence())
                        .imageUrl(s3Service.generatePresignedUrl(keys.get(i), 10).toString())
                        .build());
            }

            // Inspection 생성 및 저장
            Inspection inspection = Inspection.createInspection(member, model, inspectionImages.toArray(new InspectionImage[0]));
            inspectionRepository.save(inspection); // cascade 옵션으로 image까지 자동 저장

            return MaskUploadResponse.builder()
                    .message("업로드 완료")
                    .inspectionId(inspection.getId())
                    .totalCount(inspection.getTotalCount())
                    .abnormalCount(inspection.getAbnormalCount())
                    .abnormalRate(inspection.getAbnormalRate())
                    .results(resultDTOs)
                    .build();
        } else {
            for (int i = 0; i < files.size(); i++) {
                PredictResponse r = ((PredictResponse[]) response.getBody())[i];
                InspectResult result = r.isAbnormal() ? InspectResult.ABNORMAL : InspectResult.NORMAL;

                // 엔티티 및 DTO 생성
                inspectionImages.add(InspectionImage.createInspectionImage(originalFileNames.get(i), keys.get(i), result));
                resultDTOs.add(new InspectionResponse(originalFileNames.get(i), result));
            }

            // Inspection 생성 및 저장
            Inspection inspection = Inspection.createInspection(member, model, inspectionImages.toArray(new InspectionImage[0]));
            inspectionRepository.save(inspection);

            return new InspectionResultResponse(
                    inspection.getId(),
                    inspection.getAbnormalRate(),
                    inspection.getUploadedAt(),
                    resultDTOs
            );
        }
    }

    /**
     * 실시간 이미지 추론 (마스크 탐지)
     */
    public MaskRealtimeResponse predictRealtime(MaskRealtimeRequest request) {
        String fastApiUrl = modelServerUrl + "/realtime";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MaskRealtimeRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<MaskRealtimeResponse> response = restTemplate.postForEntity(
                fastApiUrl,
                httpEntity,
                MaskRealtimeResponse.class
        );

        return response.getBody();
    }

    /**
     * 전체 Inspection 조회 (10분 유효)
     */
    public List<InspectionViewResponse> viewAllInspections(InspectionRequest request) {
        return inspectionRepository.findAll().stream()
                .filter(ins -> ins.getModel().getName().equals(request.getModelName()))
                .map(this::toViewDto)
                .toList();
    }

    /**
     * 날짜 기반 Inspection 조회 (10분 유효)
     */
    public List<InspectionViewResponse> viewInspectionsByDate(String modelName, LocalDateTime from, LocalDateTime to) {
        return inspectionRepository.findAllByUploadedAtBetween(from, to).stream()
                .filter(ins -> ins.getModel().getName().equals(modelName))
                .map(this::toViewDto)
                .toList();
    }


    /**
     * Inspection 삭제
     */
    @Transactional
    public void deleteInspection(Long inspectionId) {
        Inspection inspection = inspectionRepository.findById(inspectionId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 검사입니다."));

        List<InspectionImage> images = inspectionImageRepository.findAllByInspection(inspection);
        for (InspectionImage image : images) {
            s3Service.deleteFile(image.getImageUrl()); // bucket에 저장된 image 삭제
        }

        inspectionRepository.delete(inspection);
    }


    /**
     * 확장자 검사
     */
    public boolean isImage(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) return false;

        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".img");
    }

    /**
     * 조회 DTO 생성 메서드
     */
    private InspectionViewResponse toViewDto(Inspection inspection) {
        List<ImageResult> imageResults = new ArrayList<>();
        List<InspectionImage> images = inspectionImageRepository.findAllByInspection(inspection);

        for (InspectionImage image : images) {
            ImageResult imageResult = new ImageResult();

            URL url = s3Service.generatePresignedUrl(image.getImageUrl(), 10); // 10분간 유효한 이미지 조회 링크 생성

            imageResult.setFileName(image.getFileName());
            imageResult.setImageUrl(url);
            imageResult.setInspectResult(image.getResult());

            imageResults.add(imageResult);
        }

        InspectionViewResponse dto = new InspectionViewResponse();
        dto.setInspectId(inspection.getId());
        dto.setUploadedDate(inspection.getUploadedAt());
        dto.setImages(imageResults);
        return dto;
    }

    /**
     * DB 최대 이미지 수 제한 메서드
     */
    private void handleStorageLimit(Model model, int toUploadCount) {
        List<Inspection> inspections = new ArrayList<>(inspectionRepository.findAllByModel(model));
        inspections.sort(Comparator.comparing(Inspection::getUploadedAt));
        int currentCount = inspections.stream().mapToInt(Inspection::getTotalCount).sum();

        while (currentCount + toUploadCount > MAX_TOTAL_IMAGE_COUNT && !inspections.isEmpty()) {
            Inspection oldest = inspections.remove(0);
            currentCount -= oldest.getTotalCount();
            deleteInspection(oldest.getId());
        }
    }
}
