package com.pharmacy.dto;

import com.pharmacy.model.enums.ReviewStatus;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public class ProductReviewDto {

    private long productReviewId;
    @Positive
    private long productId;
    @Positive
    private long reviewerUserId;
    @NotBlank
    private String comment;
    @PastOrPresent
    private LocalDateTime reviewedAt;
    @NotNull
    private ReviewStatus status;

    public ProductReviewDto() {}

    public ProductReviewDto(long productReviewId, long productId, long reviewerUserId, String comment, LocalDateTime reviewedAt, ReviewStatus status) {
        this.productReviewId = productReviewId;
        this.productId = productId;
        this.reviewerUserId = reviewerUserId;
        this.comment = comment;
        this.reviewedAt = reviewedAt;
        this.status = status;
    }

    public long getProductReviewId() {
        return productReviewId;
    }

    public void setProductReviewId(long productReviewId) {
        this.productReviewId = productReviewId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getReviewerUserId() {
        return reviewerUserId;
    }

    public void setReviewerUserId(long reviewerUserId) {
        this.reviewerUserId = reviewerUserId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

}
