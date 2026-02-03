package Capstone.checkmate.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {
    @Size(min = 3, message = "아이디는 최소 3자 이상이어야 합니다.")
    private String username;

    @Size(min = 3, message = "비밀번호는 최소 3자 이상이어야 합니다.")
    private String password;

    private String name;
    private String email;
}
