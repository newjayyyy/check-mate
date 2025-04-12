package Capstone.checkmate.service;

import Capstone.checkmate.domain.Member;
import Capstone.checkmate.dto.CreateUserRequest;
import Capstone.checkmate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public Long save(CreateUserRequest dto) {
        validateDuplicateMember(dto);
        return memberRepository.save(Member.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build()).getId();
    }

    private void validateDuplicateMember(CreateUserRequest dto) {
        if(memberRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 ID입니다.");
        }
        if(memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 Email입니다.");
        }
    }
}
