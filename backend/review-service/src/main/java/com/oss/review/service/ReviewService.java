package com.oss.review.service;

import com.oss.review.dto.ReviewRequest;
import com.oss.review.entity.Review;
import com.oss.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review create(Long reviewerId, ReviewRequest req) {
        Review review = Review.builder()
            .tradeId(req.getTradeId())
            .reviewerId(reviewerId)
            .targetId(req.getTargetId())
            .rating(req.getRating())
            .content(req.getContent())
            .build();
        return reviewRepository.save(review);
    }

    public List<Review> getByTarget(Long targetId) {
        return reviewRepository.findByTargetIdOrderByCreatedAtDesc(targetId);
    }

    public Map<String, Object> getSummary(Long targetId) {
        Double avg = reviewRepository.avgRatingByTarget(targetId);
        long count = reviewRepository.findByTargetIdOrderByCreatedAtDesc(targetId).size();
        return Map.of(
            "targetId", targetId,
            "averageRating", avg == null ? 0.0 : avg,
            "reviewCount", count
        );
    }

    public List<Object[]> getLowRatedUsers(double threshold) {
        return reviewRepository.findLowRatedUsers(threshold);
    }
}
