package Capstone.checkmate.service;

import Capstone.checkmate.domain.Member;
import Capstone.checkmate.dto.CreateUserRequest;
import Capstone.checkmate.dto.LoginRequest;
import Capstone.checkmate.exception.DuplicateMemberException;
import Capstone.checkmate.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final AuthenticationManager authManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public Long save(CreateUserRequest dto) {
        validateDuplicateMember(dto);
        Member member = Member.createMember(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), dto.getName(), dto.getEmail());
        return memberRepository.save(member).getId();
    }

    private void validateDuplicateMember(CreateUserRequest dto) {
        if(memberRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new DuplicateMemberException("이미 존재하는 ID입니다.");
        }
        if(memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateMemberException("이미 존재하는 Email입니다.");
        }
    }

    /**
     * 로그인
     */
    @Transactional
    public void login(LoginRequest req, HttpServletRequest httpReq) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        HttpSession session = httpReq.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }
}
