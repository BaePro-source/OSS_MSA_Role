package com.oss.product.repository;

import com.oss.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySellerIdOrderByCreatedAtDesc(Long sellerId);

    List<Product> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(
        String title, String description);

    // 위치 기반 조회: 반경 내 물품 (SQLite는 지리 함수 미지원 → 간단한 위도/경도 범위 쿼리)
    @Query("SELECT p FROM Product p WHERE p.status = 'ON_SALE' " +
           "AND ABS(p.latitude - :lat) < :delta " +
           "AND ABS(p.longitude - :lng) < :delta " +
           "ORDER BY p.createdAt DESC")
    List<Product> findNearby(@Param("lat") double lat,
                              @Param("lng") double lng,
                              @Param("delta") double delta);

    List<Product> findByCategoryOrderByCreatedAtDesc(String category);
}
