package Capstone.checkmate.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    /** 업로드: S3에 파일 올리고, 저장된 객체 키(objectKey)를 반환 */
    public String uploadFile(MultipartFile file) {
        // 1) 키 생성 (UUID_원본이름.ext)
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String objectKey = "images/" + UUID.randomUUID() + "_" + safeName;

        // 2) 메타데이터 설정
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(file.getContentType());
        meta.setContentLength(file.getSize());

        // 3) 업로드
        try (InputStream is = file.getInputStream()) {
            amazonS3.putObject(bucket, objectKey, is, meta);
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        return objectKey;
    }

    public URL generatePresignedUrl(String objectKey, long expirationMinutes) {
        Date expires = new Date(System.currentTimeMillis() +
                TimeUnit.MINUTES.toMillis(expirationMinutes));

        GeneratePresignedUrlRequest req =
                new GeneratePresignedUrlRequest(bucket, objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expires);

        return amazonS3.generatePresignedUrl(req);
    }

    /** 파일 삭제 */
    public void deleteFile(String objectKey) {
        amazonS3.deleteObject(bucket, objectKey);
    }

    /** 파일 존재 여부 확인 */
    public boolean checkExists(String objectKey) {
        return amazonS3.doesObjectExist(bucket, objectKey);
    }
}