package com.pharmacy.dto;

import com.pharmacy.model.enums.ReviewStatus;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DataReviewDto {

    private long dataReviewId;
    @Positive
    private long dataEntryId;
    @Positive
    private long reviewerUserId;

    private String notes;
    private LocalDateTime reviewedAt;

    @NotNull
    private ReviewStatus status;

    public DataReviewDto() {}

    public DataReviewDto(long dataReviewId, long dataEntryId, long reviewerUserId, String notes, LocalDateTime reviewedAt, ReviewStatus status) {
        this.dataReviewId = dataReviewId;
        this.dataEntryId = dataEntryId;
        this.reviewerUserId = reviewerUserId;
        this.notes = notes;
        this.reviewedAt = reviewedAt;
        this.status = status;
    }

    public long getDataReviewId() {
        return dataReviewId;
    }

    public void setDataReviewId(long dataReviewId) {
        this.dataReviewId = dataReviewId;
    }

    public long getDataEntryId() {
        return dataEntryId;
    }

    public void setDataEntryId(long dataEntryId) {
        this.dataEntryId = dataEntryId;
    }

    public long getReviewerUserId() {
        return reviewerUserId;
    }

    public void setReviewerUserId(long reviewerUserId) {
        this.reviewerUserId = reviewerUserId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
