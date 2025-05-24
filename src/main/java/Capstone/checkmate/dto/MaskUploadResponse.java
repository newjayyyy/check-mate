package Capstone.checkmate.dto;
//반환용

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaskUploadResponse {
    private long inspectionId;
    private int totalCount;        // 전체 이미지 수
    private int abnormalCount;     // 비정상 이미지 수
    private double abnormalRate;   // 비정상 비율
    private String message;        // 응답 메시지 (예: "업로드 완료")
    private List<MaskPredictionResult> results; //결과
}
