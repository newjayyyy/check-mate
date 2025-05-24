package Capstone.checkmate.repository;

import Capstone.checkmate.domain.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaskDetectionRepository extends JpaRepository<Inspection, Long> {
    // 필요시 커스텀 메서드 추가
}