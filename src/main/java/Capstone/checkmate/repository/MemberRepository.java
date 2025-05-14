package Capstone.checkmate.repository;

import Capstone.checkmate.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUsername(String username);
    Optional<Member> findByName(String name);
}
