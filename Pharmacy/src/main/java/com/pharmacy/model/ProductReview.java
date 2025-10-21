package com.pharmacy.model;

import com.pharmacy.model.enums.ReviewStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class ProductReview {

    private long productReviewId;
    private long productId;
    private long reviewerUserId;
    private String comment;
    private LocalDateTime reviewedAt;
    private ReviewStatus status;

    public ProductReview() {}

    public ProductReview(long productReviewId, long productId, long reviewerUserId, String comment, LocalDateTime reviewedAt, ReviewStatus status) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductReview that = (ProductReview) o;
        return productReviewId == that.productReviewId && productId == that.productId && reviewerUserId == that.reviewerUserId && Objects.equals(comment, that.comment) && Objects.equals(reviewedAt, that.reviewedAt) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productReviewId, productId, reviewerUserId, comment, reviewedAt, status);
    }

    @Override
    public String toString() {
        return "ProductReview{" +
                "productReviewId=" + productReviewId +
                ", productId=" + productId +
                ", reviewerUserId=" + reviewerUserId +
                ", comment='" + comment + '\'' +
                ", reviewedAt=" + reviewedAt +
                ", status=" + status +
                '}';
    }

}
