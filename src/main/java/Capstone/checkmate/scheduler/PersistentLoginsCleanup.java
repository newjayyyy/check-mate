package Capstone.checkmate.scheduler;

import java.time.LocalDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PersistentLoginsCleanup {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 매시 정각마다 7일 지난 remember-me 토큰 삭제
     */
    @Scheduled(cron = "0 0 * * * *")
    public void deleteExpiredTokens() {
        // 7일 전 시점
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);

        // 삭제 쿼리 실행
        int deleted = jdbcTemplate.update(
                "DELETE FROM persistent_logins WHERE last_used < ?",
                cutoff
        );

        // 삭제된 건수 로깅
        if (deleted > 0) {
            log.info("Deleted {} expired remember-me tokens", deleted);
        }
    }
}
