package com.oss.trade.controller;

import com.oss.trade.entity.TradeHistory;
import com.oss.trade.repository.TradeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeHistoryController {

    private final TradeHistoryRepository tradeHistoryRepository;

    @PostMapping
    public ResponseEntity<TradeHistory> record(@RequestBody TradeHistory history) {
        return ResponseEntity.ok(tradeHistoryRepository.save(history));
    }

    @GetMapping("/sold")
    public ResponseEntity<List<TradeHistory>> sold(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(tradeHistoryRepository.findBySellerIdOrderByTradedAtDesc(userId));
    }

    @GetMapping("/bought")
    public ResponseEntity<List<TradeHistory>> bought(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(tradeHistoryRepository.findByBuyerIdOrderByTradedAtDesc(userId));
    }

    // AI 추천 서비스가 거래 이력 조회용으로 호출
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<TradeHistory>> byBuyer(@PathVariable Long buyerId) {
        return ResponseEntity.ok(tradeHistoryRepository.findByBuyerIdOrderByTradedAtAsc(buyerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeHistory> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(
            tradeHistoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("거래 내역을 찾을 수 없습니다."))
        );
    }
}
