package Capstone.checkmate.dto;
//서버의 영상 추론 결과 수신. 라벨(Yes만 수신하도록 함)
//box 그릴 거라면 이 항목의 box정보 가져다 쓰시면 됩니다

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaskRealtimeResponse {
    private String label;
    private float confidence;
    private MaskBoundingBox box; //box 좌표 정보, 영상에 박스를 그릴 거라면 필요 이외 용도x
}
