package Capstone.checkmate.dto;
//최종 결과

import Capstone.checkmate.domain.InspectResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;

@Data
@AllArgsConstructor
@Builder
public class MaskPredictionResult {
    private String fileName;     //이미지 이름
    private String result;       //"Yes", "No", "Improperly"
    private float confidence;    //
    private String imageUrl;
}
