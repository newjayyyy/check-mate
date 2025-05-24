package Capstone.checkmate.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InspectionViewRequest {
    private LocalDate from;
    private LocalDate to;
}
