package Capstone.checkmate.dto;

import Capstone.checkmate.domain.InspectResult;
import lombok.Data;

@Data
public class InspectionResponse {
    private String fileName;
    private InspectResult inspectResult;
}
