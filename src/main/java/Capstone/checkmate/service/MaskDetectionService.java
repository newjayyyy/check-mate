package Capstone.checkmate.service;

import Capstone.checkmate.domain.*;
import Capstone.checkmate.dto.*;
import Capstone.checkmate.repository.InspectionRepository;
import Capstone.checkmate.repository.MemberRepository;
import Capstone.checkmate.repository.ModelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MaskDetectionService {

    private final InspectionRepository inspectionRepository;
    private final ModelRepository modelRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final RestTemplate restTemplate;

    @Value("${model.server.url}")
    private String modelServerUrl;

    //요청
    public MaskUploadResponse inspect(MaskUploadRequest request) {
        Long modelId = request.getModelId();
        Long memberId = request.getMemberId();

        List<MultipartFile> images = request.getImages();

        final int MAX_IMAGE_COUNT = 30; //최대 이미지 개수 제한
        if (images.size() > MAX_IMAGE_COUNT) {
            throw new IllegalArgumentException("최대 업로드 가능 이미지 수는 " + MAX_IMAGE_COUNT + "개입니다.");
        } //최대 이미지 개수 체크

        //모델 및 사용자 정보 조회
        Model model = modelRepository.findById(modelId)
                .orElseThrow(() -> new IllegalArgumentException("모델을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Inspection> inspections = inspectionRepository.findAllByModel(model);
        int currentImageCount = inspections.stream()
                .mapToInt(Inspection::getTotalCount)
                .sum();

        final int MAX_TOTAL_IMAGE_COUNT = 100;
        if (currentImageCount >= MAX_TOTAL_IMAGE_COUNT && !inspections.isEmpty()) {
            inspectionRepository.delete(inspections.get(0));
        }
        List<String> uploadedKeys = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        
        for (MultipartFile file : images) {
            String fileName = file.getOriginalFilename();
            String key = s3Service.uploadFile(file);
            fileNames.add(fileName);
            uploadedKeys.add(key);
        }

        // Presigned URL 생성
        List<String> presignedUrls = new ArrayList<>();
        for (String key : uploadedKeys) {
            URL url = s3Service.generatePresignedUrl(key, 3);
            presignedUrls.add(url.toString());
        }

        // FastAPI 모델 서버로 요청 전송
        Map<String, Object> body = Map.of("image_urls", presignedUrls);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<MaskServerResult[]> response = restTemplate.postForEntity(
            modelServerUrl + "/predict",
            entity,
            MaskServerResult[].class
        );

        MaskServerResult[] resultDtos = response.getBody();
        
        List<InspectionImage> inspectionImages = new ArrayList<>(); //결과
        List<MaskPredictionResult> resultList = new ArrayList<>(); //엔티티

        //dto
        for (int i = 0; i < resultDtos.length; i++) {
            MaskServerResult result = resultDtos[i];
            String label = result.getLabel();
            float confidence = result.getConfidence();

            InspectResult resultEnum = label.equalsIgnoreCase("yes")
                ? InspectResult.ABNORMAL
                : InspectResult.NORMAL;

            // 엔티티 생성
            InspectionImage image = InspectionImage.createInspectionImage(
                fileNames.get(i), uploadedKeys.get(i), resultEnum);
            inspectionImages.add(image);

            // 응답용 DTO 생성
            resultList.add(MaskPredictionResult.builder()
                .fileName(fileNames.get(i))
                .result(label)
                .confidence(confidence)
                .imageUrl(s3Service.generatePresignedUrl(uploadedKeys.get(i), 10).toString())
                .build());
        }

        Inspection inspection = Inspection.createInspection(member, model,
                inspectionImages.toArray(new InspectionImage[0]));
        inspectionRepository.save(inspection);

        return MaskUploadResponse.builder()
                .totalCount(inspection.getTotalCount())
                .abnormalCount(inspection.getAbnormalCount())
                .abnormalRate(inspection.getAbnormalRate())
                .message("업로드 완료")
                .results(resultList)
                .build();
    }

    //검사(InspectionService참조)
    public boolean isImage(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) return false;

        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".img");
    }

    //실시간 이미지 추론
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
}
