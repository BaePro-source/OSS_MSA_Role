package com.oss.trade.controller;

import com.oss.trade.entity.TradeHistory;
import com.oss.trade.repository.TradeHistoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "거래 이력", description = "거래 기록 및 조회")
@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeHistoryController {

    private final TradeHistoryRepository tradeHistoryRepository;

    @Operation(summary = "거래 이력 기록")
    @PostMapping
    public ResponseEntity<TradeHistory> record(@RequestBody TradeHistory history) {
        return ResponseEntity.ok(tradeHistoryRepository.save(history));
    }

    @Operation(summary = "내 판매 이력 조회")
    @GetMapping("/sold")
    public ResponseEntity<List<TradeHistory>> sold(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(tradeHistoryRepository.findBySellerIdOrderByTradedAtDesc(userId));
    }

    @Operation(summary = "내 구매 이력 조회")
    @GetMapping("/bought")
    public ResponseEntity<List<TradeHistory>> bought(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(tradeHistoryRepository.findByBuyerIdOrderByTradedAtDesc(userId));
    }

    @Operation(summary = "구매자별 이력 조회", description = "AI 추천 서비스가 내부적으로 호출")
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<TradeHistory>> byBuyer(@PathVariable Long buyerId) {
        return ResponseEntity.ok(tradeHistoryRepository.findByBuyerIdOrderByTradedAtAsc(buyerId));
    }

    @Operation(summary = "거래 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<TradeHistory> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(
            tradeHistoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("거래 내역을 찾을 수 없습니다."))
        );
    }
}
