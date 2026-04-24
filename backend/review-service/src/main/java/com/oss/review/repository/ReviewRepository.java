package com.oss.review.repository;

import com.oss.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTargetIdOrderByCreatedAtDesc(Long targetId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetId = :targetId")
    Double avgRatingByTarget(@Param("targetId") Long targetId);

    // 낮은 평점 사용자 조회 (관리자용)
    @Query("SELECT r.targetId, AVG(r.rating) as avg FROM Review r GROUP BY r.targetId HAVING AVG(r.rating) < :threshold")
    List<Object[]> findLowRatedUsers(@Param("threshold") double threshold);
}
