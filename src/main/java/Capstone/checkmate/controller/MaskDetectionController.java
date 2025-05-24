package Capstone.checkmate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import Capstone.checkmate.dto.MaskUploadRequest;
import Capstone.checkmate.dto.MaskUploadResponse;
import Capstone.checkmate.service.MaskDetectionService;

@RequiredArgsConstructor //
@RestController
@RequestMapping("/api/mask")
public class MaskDetectionController {

    private final MaskDetectionService maskDetectionService;

    @PostMapping("/{memberId}/{modelId}")
    public ResponseEntity<MaskUploadResponse> inspectImages(
        @PathVariable Long memberId,
        @PathVariable Long modelId,
        @RequestPart List<MultipartFile> images
    ) {
        MaskUploadRequest requestDto = MaskUploadRequest.of(memberId, modelId, images);
        MaskUploadResponse responseDto = maskDetectionService.inspect(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
