package com.oss.review.controller;

import com.oss.review.dto.ReviewRequest;
import com.oss.review.entity.Review;
import com.oss.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "리뷰", description = "리뷰 작성, 평점 조회 및 저신뢰 사용자 탐지")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "인증 필요 (X-User-Id 헤더)")
    @PostMapping
    public ResponseEntity<Review> create(
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody ReviewRequest req
    ) {
        return ResponseEntity.ok(reviewService.create(userId, req));
    }

    @Operation(summary = "사용자별 리뷰 목록 조회")
    @GetMapping("/user/{targetId}")
    public ResponseEntity<List<Review>> getByUser(@PathVariable Long targetId) {
        return ResponseEntity.ok(reviewService.getByTarget(targetId));
    }

    @Operation(summary = "평균 평점 및 총 리뷰 수 조회")
    @GetMapping("/user/{targetId}/summary")
    public ResponseEntity<Map<String, Object>> summary(@PathVariable Long targetId) {
        return ResponseEntity.ok(reviewService.getSummary(targetId));
    }

    @Operation(summary = "저평점 사용자 목록 조회", description = "threshold 이하 평균 평점 사용자 반환 (관리자용)")
    @GetMapping("/low-rated")
    public ResponseEntity<List<Object[]>> lowRated(
        @RequestParam(defaultValue = "2.0") double threshold
    ) {
        return ResponseEntity.ok(reviewService.getLowRatedUsers(threshold));
    }
}
