package Capstone.checkmate.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private boolean rememberMe; // 자동 로그인 여부
}
