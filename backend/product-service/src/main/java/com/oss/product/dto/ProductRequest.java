package com.oss.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank
    private String title;

    private String description;

    @Min(0)
    private int price;

    private String category;
    private String imageUrl;
    private double latitude;
    private double longitude;
}
