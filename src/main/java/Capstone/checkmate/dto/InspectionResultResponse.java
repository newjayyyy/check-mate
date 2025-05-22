package Capstone.checkmate.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InspectionResultResponse {
    private Long inspectionId;
    private double abnormalRate;
    private LocalDateTime uploadedAt;
    private List<InspectionResponse> results;
}
