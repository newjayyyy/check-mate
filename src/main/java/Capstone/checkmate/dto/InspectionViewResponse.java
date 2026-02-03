package Capstone.checkmate.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InspectionViewResponse {
    private Long inspectId;
    private String uploadedDate;
    private List<ImageResult> images = new ArrayList<>();
}
