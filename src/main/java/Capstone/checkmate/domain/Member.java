package Capstone.checkmate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Email
    @Column(nullable = false, unique = true)
    private String email;
}
