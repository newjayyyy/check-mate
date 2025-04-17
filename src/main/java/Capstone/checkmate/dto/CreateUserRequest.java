package Capstone.checkmate.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private String name;
    private String email;
}
