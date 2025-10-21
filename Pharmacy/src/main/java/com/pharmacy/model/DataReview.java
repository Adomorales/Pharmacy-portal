package com.pharmacy.model;

import com.pharmacy.model.enums.ReviewStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class DataReview {

    private long dataReviewId;
    private long dataEntryId;
    private long reviewerUserId;

    private String notes;
    private LocalDateTime reviewedAt;

    private ReviewStatus status;

    public DataReview() {}

    public DataReview(long dataReviewId, long dataEntryId, long reviewerUserId, String notes, LocalDateTime reviewedAt, ReviewStatus status) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DataReview that = (DataReview) o;
        return dataReviewId == that.dataReviewId && dataEntryId == that.dataEntryId && reviewerUserId == that.reviewerUserId && Objects.equals(notes, that.notes) && Objects.equals(reviewedAt, that.reviewedAt) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataReviewId, dataEntryId, reviewerUserId, notes, reviewedAt, status);
    }

    @Override
    public String toString() {
        return "DataReview{" +
                "dataReviewId=" + dataReviewId +
                ", dataEntryId=" + dataEntryId +
                ", reviewerUserId=" + reviewerUserId +
                ", notes='" + notes + '\'' +
                ", reviewedAt=" + reviewedAt +
                ", status=" + status +
                '}';
    }

}
