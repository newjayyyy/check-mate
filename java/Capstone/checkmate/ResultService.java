package Capstone.checkmate;

import Capstone.checkmate.dto.ResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResultService {

    private final ResultRepository resultRepository;

    public List<Result> getResultsBetween(LocalDate start, LocalDate end) {
        return resultRepository.findByCreatedAtBetween(start.atStartOfDay(), end.plusDays(1).atStartOfDay());
    }

    @Transactional
    public Long saveResult(ResultDto dto) {
        Result result = Result.create(dto); // 팩토리 메서드 사용
        resultRepository.save(result);
        return result.getId();
    }
    
 

}
