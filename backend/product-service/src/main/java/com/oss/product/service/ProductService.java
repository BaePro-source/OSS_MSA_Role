package com.oss.product.service;

import com.oss.product.dto.ProductRequest;
import com.oss.product.entity.Product;
import com.oss.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // km 단위 반경을 위도/경도 delta 로 근사 변환 (1도 ≈ 111km)
    private static final double KM_PER_DEGREE = 111.0;

    public List<Product> findAll() {
        return productRepository.findAllByOrderByCreatedAtDesc();
    }

    public Product create(Long sellerId, ProductRequest req) {
        Product product = Product.builder()
            .sellerId(sellerId)
            .title(req.getTitle())
            .description(req.getDescription())
            .price(req.getPrice())
            .category(req.getCategory())
            .imageUrl(req.getImageUrl())
            .latitude(req.getLatitude())
            .longitude(req.getLongitude())
            .build();
        return productRepository.save(product);
    }

    public List<Product> findNearby(double lat, double lng, double radiusKm) {
        double delta = radiusKm / KM_PER_DEGREE;
        return productRepository.findNearby(lat, lng, delta);
    }

    public List<Product> search(String keyword) {
        return productRepository
            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(
                keyword, keyword);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("물품을 찾을 수 없습니다."));
    }

    public List<Product> findBySeller(Long sellerId) {
        return productRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
    }

    public Product update(Long productId, Long sellerId, ProductRequest req) {
        Product product = findById(productId);
        if (!product.getSellerId().equals(sellerId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }
        product.setTitle(req.getTitle());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setCategory(req.getCategory());
        product.setImageUrl(req.getImageUrl());
        return productRepository.save(product);
    }

    public void delete(Long productId, Long sellerId) {
        Product product = findById(productId);
        if (!product.getSellerId().equals(sellerId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }
        productRepository.delete(product);
    }

    public Product updateStatus(Long productId, Product.Status status) {
        Product product = findById(productId);
        product.setStatus(status);
        return productRepository.save(product);
    }
}
