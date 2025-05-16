package Capstone.checkmate.dto;

import java.time.LocalDateTime;

public class ResultDto {

    private Long id;
    private String type;
    private String result;
    private String imagePath;
    private LocalDateTime createdAt;

    // ✅ 기본 생성자 
    public ResultDto() {}

    // 🔄 Getter/Setter (직접 추가했는지 확인)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 선택 사항: Entity → DTO 변환 메서드
    public static ResultDto fromEntity(Capstone.checkmate.Result result) {
        ResultDto dto = new ResultDto();
        dto.setId(result.getId());
        dto.setType(result.getType());
        dto.setResult(result.getResult());
        dto.setImagePath(result.getImagePath());
        dto.setCreatedAt(result.getCreatedAt());
        return dto;
    }
}
