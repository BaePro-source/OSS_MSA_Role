package com.oss.trade.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "trade_history")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TradeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Long buyerId;

    private int price;

    @Column(nullable = false)
    private String productTitle;

    private String category;

    @CreationTimestamp
    private LocalDateTime tradedAt;
}
