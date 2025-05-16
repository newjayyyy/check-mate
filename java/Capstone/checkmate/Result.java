package Capstone.checkmate;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import Capstone.checkmate.dto.ResultDto;

@Entity
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // 부품(part) or 마스크(mask)
    private String result; // 정상, 오염, 미착용 등
    private String imagePath; // 이미지 경로
    private LocalDateTime createdAt;

    // 생성자
    public Result() {}

    public static Result create(ResultDto dto) {
        Result result = new Result();
        result.type = dto.getType();
        result.result = dto.getResult();
        result.imagePath = dto.getImagePath();
        result.createdAt = dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now();
        return result;
    }

	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}
	// getter
	public String getType() { return type; }
	public String getResult() { return result; }
	public String getImagePath() { return imagePath; }
	public LocalDateTime getCreatedAt() { return createdAt; }

    
}

