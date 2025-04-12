package Capstone.checkmate.controller;

import Capstone.checkmate.dto.CreateUserRequest;
import Capstone.checkmate.dto.CreateUserResponse;
import Capstone.checkmate.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public CreateUserResponse signup(@RequestBody @Valid CreateUserRequest request) {
        Long id = memberService.save(request);
        return new CreateUserResponse(id);
    }


}
