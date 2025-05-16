package Capstone.checkmate;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
