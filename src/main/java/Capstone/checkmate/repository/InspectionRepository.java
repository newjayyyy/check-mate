package Capstone.checkmate.repository;

import Capstone.checkmate.domain.Model;
import Capstone.checkmate.domain.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    // 기간별 검사 조회
    List<Inspection> findAllByUploadedAtBetween(LocalDateTime from, LocalDateTime to);
    
    List<Inspection> findAllByModel(Model model);

}
