package Capstone.checkmate.controller;

import Capstone.checkmate.dto.InspectionResultResponse;
import Capstone.checkmate.dto.InspectionViewRequest;
import Capstone.checkmate.dto.InspectionViewResponse;
import Capstone.checkmate.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    // 검사 요청 및 이미지 업로드 API
    @PostMapping("/api/inspections")
    public ResponseEntity<InspectionResultResponse> createInspection(Authentication auth, @RequestParam String model, @RequestParam("files") List<MultipartFile> files) {
        String member = auth.getName();
        InspectionResultResponse results = inspectionService.createInspection(member, model, files);

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    // 전체 검사 조회 API
    @PostMapping("/api/viewAllInspections")
    public ResponseEntity<List<InspectionViewResponse>> viewAllInspections() {
        List<InspectionViewResponse> responses = inspectionService.viewAllInspections();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    // 날짜 기반 검사 조회 API
    @PostMapping("/api/viewInspectionsByDate")
    public ResponseEntity<List<InspectionViewResponse>> viewInspectionsByDate(@RequestBody InspectionViewRequest request) {
        LocalDateTime fromDate = request.getFrom().atStartOfDay();
        LocalDateTime toDate = request.getTo().atTime(LocalTime.MAX);

        List<InspectionViewResponse> responses = inspectionService.viewInspectionsByDate(fromDate, toDate);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }


    // 검사 삭제 API
    @PostMapping("/api/inspections/delete/{id}")
    public ResponseEntity<?> deleteInspection(@PathVariable Long id) {
        inspectionService.deleteInspection(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
