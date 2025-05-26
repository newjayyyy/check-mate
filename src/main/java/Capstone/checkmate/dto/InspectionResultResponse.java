package Capstone.checkmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InspectionResultResponse {
    private Long inspectionId;
    private double abnormalRate;
    private LocalDateTime uploadedAt;
    private List<PredictionResponse> results;
}
