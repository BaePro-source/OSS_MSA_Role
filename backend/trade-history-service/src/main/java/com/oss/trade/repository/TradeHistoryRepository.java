package com.oss.trade.repository;

import com.oss.trade.entity.TradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeHistoryRepository extends JpaRepository<TradeHistory, Long> {
    List<TradeHistory> findBySellerIdOrderByTradedAtDesc(Long sellerId);
    List<TradeHistory> findByBuyerIdOrderByTradedAtDesc(Long buyerId);
    List<TradeHistory> findByBuyerIdOrderByTradedAtAsc(Long buyerId);  // AI 추천용
}
