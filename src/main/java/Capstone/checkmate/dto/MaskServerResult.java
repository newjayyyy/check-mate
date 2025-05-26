package Capstone.checkmate.dto;

import lombok.Data;

//모델 서버 응답 수신
@Data
public class MaskServerResult {
    private String label;
    private float confidence;
}