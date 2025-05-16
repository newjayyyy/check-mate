package Capstone.checkmate;

import Capstone.checkmate.dto.ResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    // ✅ 날짜 기반 조회 API
    @GetMapping
    public List<Result> getResults(
            @RequestParam("start") LocalDate start,
            @RequestParam("end") LocalDate end
    ) {
        return resultService.getResultsBetween(start, end);
    }

    // ✅ 단건 저장 API
    /*@PostMapping
    public ResponseEntity<Long> saveResult(@RequestBody ResultDto dto) {
        Long savedId = resultService.saveResult(dto);
        return ResponseEntity.ok(savedId);
    }*/

    // ✅ 여러 개 저장용 API (올바르게 수정)
    @PostMapping
    public ResponseEntity<String> saveAllResults(@RequestBody List<ResultDto> resultDtoList) {
        for (ResultDto dto : resultDtoList) {
            resultService.saveResult(dto); // 기존 저장 로직 재사용
        }
        return ResponseEntity.ok("데이터 저장 완료!");
    }
}
