package Capstone.checkmate.service;

import Capstone.checkmate.dto.GetS3UrlDto;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    @Value("${app.s3.presign.expiration-minutes:60}")
    private long presignMinutes;

    /** 업로드용 PUT presigned URL */
    public URL generateUploadUrl(String prefix, String originalName, String contentType) {
        // 1) 키 생성
        String key = buildKey(prefix, originalName);

        // 2) 만료 시간 (현재 + presignMinutes 분)
        Date expiration = new Date(System.currentTimeMillis()
                + TimeUnit.MINUTES.toMillis(presignMinutes));

        // 3) 요청 빌드
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        // MIME 타입 메타데이터로 설정
        req.addRequestParameter(Headers.CONTENT_TYPE, contentType);
        // (ACL은 버킷 정책으로 제어)

        return amazonS3.generatePresignedUrl(req);
    }

    /** 다운로드용 GET presigned URL */
    public URL generateDownloadUrl(String key) {
        Date expiration = new Date(System.currentTimeMillis()
                + TimeUnit.MINUTES.toMillis(presignMinutes));

        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        return amazonS3.generatePresignedUrl(req);
    }

    /** 키 폴더/UUID_원본이름.ext */
    private String buildKey(String prefix, String originalName) {
        String safeName = originalName.trim().replaceAll("[^a-zA-Z0-9._-]", "_");
        String uuidName = UUID.randomUUID() + "_" + safeName;
        if (StringUtils.hasText(prefix)) {
            String p = prefix.replaceAll("/+$", "");
            return p + "/" + uuidName;
        }
        return uuidName;
    }
}