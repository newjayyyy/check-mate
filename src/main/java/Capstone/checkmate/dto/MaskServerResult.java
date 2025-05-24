package Capstone.checkmate.dto;
//모델 서버 응답 수신
import lombok.Data;

@Data
public class MaskServerResult {
    private String label;
    private float confidence;
}
