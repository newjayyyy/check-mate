package Capstone.checkmate.dto;
//box 좌표 정보용
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaskBoundingBox {
    private int x1;
    private int y1;
    private int x2;
    private int y2;
}
