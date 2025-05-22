package Capstone.checkmate.repository;

import Capstone.checkmate.domain.Inspection;
import Capstone.checkmate.domain.InspectionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionImageRepository extends JpaRepository<InspectionImage, Long> {

    // Inspection 기반 이미지 조회
    List<InspectionImage> findAllByInspection(Inspection inspection);
}
