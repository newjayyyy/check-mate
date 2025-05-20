package Capstone.checkmate.controller;

import Capstone.checkmate.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    @PostMapping("/api/post")
    public ResponseEntity<?> postImage(@RequestParam("file") MultipartFile file) {
        String key = s3Service.uploadFile(file);
        System.out.println(key);
        URL url = s3Service.generatePresignedUrl(key, 5);
        return ResponseEntity.ok().body(url);
    }

}
