package Capstone.checkmate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {
    private String username;
    private String password;
    private String name;
    private String email;
}
