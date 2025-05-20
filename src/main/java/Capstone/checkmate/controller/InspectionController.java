package Capstone.checkmate.controller;

import Capstone.checkmate.dto.InspectionResponse;
import Capstone.checkmate.dto.InspectionResultResponse;
import Capstone.checkmate.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    @PostMapping("/api/inspections")
    public ResponseEntity<InspectionResultResponse> createInspection(Authentication auth, @RequestParam String model, @RequestParam("files") List<MultipartFile> files) {
        String member = auth.getName();
        InspectionResultResponse results = inspectionService.createInspection(member, model, files);

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @PostMapping("/api/inspections/delete/{id}")
    public ResponseEntity<?> deleteInspection(@PathVariable Long id) {
        inspectionService.deleteInspection(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
