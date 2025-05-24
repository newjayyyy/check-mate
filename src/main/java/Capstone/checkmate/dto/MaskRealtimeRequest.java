package Capstone.checkmate.dto;
/*프론트에서 해당 dto를 통해 카메라 영상을 받아와->전송해야 합니다.
 * -> Base64 인코딩 기반으로 작성됨
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaskRealtimeRequest {
    private String imageBase64;
}
