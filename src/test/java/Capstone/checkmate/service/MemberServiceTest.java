package Capstone.checkmate.service;

import Capstone.checkmate.dto.CreateUserRequest;
import Capstone.checkmate.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;

    @Test
    public void 회원가입() throws Exception {
        //given
        CreateUserRequest member1 = CreateUserRequest.builder()
                .username("ID1")
                .password("pass1")
                .name("name1")
                .email("a@gmail.com")
                .build();

        //when
        Long saveId = memberService.save(member1);
        Long findId = memberRepository.findById(saveId).get().getId();

        //then
        Assertions.assertThat(saveId).isEqualTo(findId);
    }

    @Test
    public void 중복ID테스트() throws Exception {
        //given
        CreateUserRequest member1 = CreateUserRequest.builder()
                .username("ID1")
                .password("pass1")
                .name("name1")
                .email("a@gmail.com")
                .build();
        CreateUserRequest member2 = CreateUserRequest.builder()
                .username("ID1")
                .password("pass2")
                .name("name2")
                .email("b@gmail.com")
                .build();

        //when
        memberService.save(member1);

        //then
        assertThrows(IllegalStateException.class, () -> memberService.save(member2));
    }

    @Test
    public void 중복Email테스트() throws Exception {
        //given
        CreateUserRequest member1 = CreateUserRequest.builder()
                .username("ID1")
                .password("pass1")
                .name("name1")
                .email("a@gmail.com")
                .build();
        CreateUserRequest member2 = CreateUserRequest.builder()
                .username("ID2")
                .password("pass2")
                .name("name2")
                .email("a@gmail.com")
                .build();

        //when
        memberService.save(member1);

        //then
        assertThrows(IllegalStateException.class, () -> memberService.save(member2));
    }

}