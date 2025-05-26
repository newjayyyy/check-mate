package Capstone.checkmate.dto;

import Capstone.checkmate.domain.InspectResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InspectionResponse implements PredictionResponse {
    private String fileName;
    private InspectResult inspectResult;
}
