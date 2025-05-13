package Capstone.checkmate.controller;

import Capstone.checkmate.dto.CreateUserRequest;
import Capstone.checkmate.dto.LoginRequest;
import Capstone.checkmate.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid CreateUserRequest request) {
        memberService.save(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req, HttpServletRequest httpReq) {
        // AuthenticationException 은 GlobalExceptionHandler 이전에 Spring Security 에서 먼저 예외처리 되기에 try-catch 로 처리
        try {
            memberService.login(req, httpReq);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패 "+ e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpReq) {
        HttpSession session = httpReq.getSession(false);
        if(session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }
        return ResponseEntity.ok().body(authentication.getPrincipal());
    }
}
