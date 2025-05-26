package Capstone.checkmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;

//최종 결과
@Data
@AllArgsConstructor
@Builder
public class MaskPredictionResult implements PredictionResponse {
    private String fileName;     //이미지 이름
    private String result;       //"Yes", "No", "Improperly"
    private float confidence;    //
    private String imageUrl;
}