package Capstone.checkmate.dto;
//모델 서버로 요청 전송

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaskUploadRequest {
    private Long memberId; //id
    private Long modelId; //model
    private List<MultipartFile> images; //image list
    
    public static MaskUploadRequest of(Long memberId, Long modelId, List<MultipartFile> images) {
        return MaskUploadRequest.builder()
                .memberId(memberId)
                .modelId(modelId)
                .images(images)
                .build();
    }
}
//@RequestPart사용->컨트롤러에서 @ModelAttribute로 받음