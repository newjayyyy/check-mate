package Capstone.checkmate.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class InspectionViewResponse {
    private Long inspectId;
    private LocalDateTime uploadedDate;
    private List<ImageResult> images = new ArrayList<>();
}
