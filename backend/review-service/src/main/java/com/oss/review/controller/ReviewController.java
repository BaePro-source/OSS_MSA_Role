package com.oss.review.controller;

import com.oss.review.dto.ReviewRequest;
import com.oss.review.entity.Review;
import com.oss.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> create(
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody ReviewRequest req
    ) {
        return ResponseEntity.ok(reviewService.create(userId, req));
    }

    @GetMapping("/user/{targetId}")
    public ResponseEntity<List<Review>> getByUser(@PathVariable Long targetId) {
        return ResponseEntity.ok(reviewService.getByTarget(targetId));
    }

    @GetMapping("/user/{targetId}/summary")
    public ResponseEntity<Map<String, Object>> summary(@PathVariable Long targetId) {
        return ResponseEntity.ok(reviewService.getSummary(targetId));
    }

    // 관리자용
    @GetMapping("/low-rated")
    public ResponseEntity<List<Object[]>> lowRated(
        @RequestParam(defaultValue = "2.0") double threshold
    ) {
        return ResponseEntity.ok(reviewService.getLowRatedUsers(threshold));
    }
}
