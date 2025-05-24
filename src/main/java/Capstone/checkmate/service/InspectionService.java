package Capstone.checkmate.service;

import Capstone.checkmate.domain.*;
import Capstone.checkmate.dto.*;
import Capstone.checkmate.repository.InspectionImageRepository;
import Capstone.checkmate.repository.InspectionRepository;
import Capstone.checkmate.repository.MemberRepository;
import Capstone.checkmate.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArraysOfChar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
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


    /**
     * Inspection 수행
     */
    @Transactional
    public InspectionResultResponse createInspection(String userName, String modelName, List<MultipartFile> files) {
        // entity 조회
        Member member = memberRepository.findByUsername(userName).orElseThrow(() -> new UsernameNotFoundException(userName));
        Model model = modelRepository.findByName(modelName).orElseThrow(() -> new IllegalStateException("Model does not exist"));

        // S3 업로드 및 presigned URL 생성
        List<String> keys = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        List<String> originalFileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!isImage(file)) throw new IllegalArgumentException("이미지 형식만 허용됩니다.");
            String originalFilename = file.getOriginalFilename();
            originalFileNames.add(originalFilename);

            String key = s3Service.uploadFile(file);
            keys.add(key);
            urls.add(s3Service.generatePresignedUrl(key, 3)); // 3분 유효 Presigned URL
        }

        // 모델 서버 추론 요청
        List<String> urlStrings = urls.stream().map(URL::toString).toList();
        Map<String, Object> body = Map.of("image_urls", urlStrings);

        ResponseEntity<PredictResponse[]> response = restTemplate.postForEntity(
                modelServerUrl + "/predict",
                new HttpEntity<>(body),
                PredictResponse[].class
        );

        PredictResponse[] results = Objects.requireNonNull(response.getBody());

        // InspectionImage 리스트 생성 (Inspection Entity 생성에 필요)
        List<InspectionImage> imageEntities = new ArrayList<>();
        List<InspectionResponse> imageResponses = new ArrayList<>();

        for (int i = 0; i < results.length; i++) {
            PredictResponse pr = results[i];
            InspectResult result = pr.isAbnormal() ? InspectResult.ABNORMAL : InspectResult.NORMAL;

            // DB 저장용 엔티티
            InspectionImage entity = InspectionImage.createInspectionImage(originalFileNames.get(i) ,keys.get(i), result);
            imageEntities.add(entity);

            // API 응답용 DTO
            InspectionResponse dto = new InspectionResponse();
            dto.setFileName(originalFileNames.get(i));
            dto.setInspectResult(result);
            imageResponses.add(dto);
        }

        // Inspection 생성 및 저장
        Inspection inspection = Inspection.createInspection(member, model, imageEntities.toArray(new InspectionImage[0]));
        inspectionRepository.save(inspection); // cascade 옵션으로 image까지 자동 저장

        // 결과 DTO 생성 및 return
        InspectionResultResponse responseDto = new InspectionResultResponse();
        responseDto.setInspectionId(inspection.getId());
        responseDto.setAbnormalRate(inspection.getAbnormalRate());
        responseDto.setUploadedAt(inspection.getUploadedAt());
        responseDto.setResults(imageResponses);

        return responseDto;
    }

    /**
     * 전체 Inspection 조회
     */
    public List<InspectionViewResponse> viewAllInspections(InspectionRequest request) {
        List<InspectionViewResponse> result = new ArrayList<>();
        List<Inspection> inspections = inspectionRepository.findAll(); // entity 조회

        String modelName = request.getModelName();
        modelRepository.findByName(modelName).orElseThrow(() -> new IllegalStateException("Model does not exist"));

        // dto 생성 및 추가
        for (Inspection inspection : inspections) {
            if(!inspection.getModel().getName().equals(modelName)) continue; // 원하는 모델 명과 다르면 continue로 넘기기

            InspectionViewResponse dto = new InspectionViewResponse();
            dto.setInspectId(inspection.getId());
            dto.setUploadedDate(inspection.getUploadedAt());

            List<InspectionImage> images = inspectionImageRepository.findAllByInspection(inspection);
            for (InspectionImage image : images) {
                ImageResult imageResult = new ImageResult();

                URL url = s3Service.generatePresignedUrl(image.getImageUrl(), 10); // 10분간 유효한 이미지 조회 링크 생성

                imageResult.setFileName(image.getFileName());
                imageResult.setImageUrl(url);
                imageResult.setInspectResult(image.getResult());

                dto.getImages().add(imageResult);
            }

            result.add(dto);
        }

        return result;
    }

    /**
     * 날짜 기반 Inspection 조회
     */
    public List<InspectionViewResponse> viewInspectionsByDate(String modelName, LocalDateTime from, LocalDateTime to) {
        List<InspectionViewResponse> result = new ArrayList<>();
        List<Inspection> inspections = inspectionRepository.findAllByUploadedAtBetween(from, to); // entity 조회

        modelRepository.findByName(modelName).orElseThrow(() -> new IllegalStateException("Model does not exist"));

        // dto 생성 및 추가
        for (Inspection inspection : inspections) {
            if(!inspection.getModel().getName().equals(modelName)) continue; // 원하는 모델 명과 다르면 continue로 넘기기

            InspectionViewResponse dto = new InspectionViewResponse();
            dto.setInspectId(inspection.getId());
            dto.setUploadedDate(inspection.getUploadedAt());

            List<InspectionImage> images = inspectionImageRepository.findAllByInspection(inspection);
            for (InspectionImage image : images) {
                ImageResult imageResult = new ImageResult();

                URL url = s3Service.generatePresignedUrl(image.getImageUrl(), 10); // 10분간 유효한 이미지 조회 링크 생성

                imageResult.setFileName(image.getFileName());
                imageResult.setImageUrl(url);
                imageResult.setInspectResult(image.getResult());

                dto.getImages().add(imageResult);
            }

            result.add(dto);
        }

        return result;
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
}
