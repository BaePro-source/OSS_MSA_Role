package com.oss.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {
    private Long tradeId;

    @NotNull
    private Long targetId;

    @Min(1) @Max(5)
    private int rating;

    private String content;
}
