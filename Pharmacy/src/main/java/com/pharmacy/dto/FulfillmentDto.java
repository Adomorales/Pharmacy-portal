package com.pharmacy.dto;

import com.pharmacy.model.enums.FulfillmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.OffsetDateTime;

public class FulfillmentDto {
    private long fulfillmentId;
    @NotNull
    @Positive
    private long prescriptionId;
    private Long productId;
    private Long lotId;
    @Positive
    private int quantityDispensed;
    private OffsetDateTime filledAt;
    private Long filledByUserId;
    private Long verifiedByPharmacistId;
    private FulfillmentStatus status;

    public FulfillmentDto() {}

    public long getfulfillmentId() {
        return fulfillmentId;
    }

    public void setfulfillmentId(long fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
    }

    public long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getLotId() {
        return lotId;
    }

    public void setLotId(Long lotId) {
        this.lotId = lotId;
    }

    public int getQuantityDispensed() {
        return quantityDispensed;
    }

    public void setQuantityDispensed(int quantityDispensed) {
        this.quantityDispensed = quantityDispensed;
    }

    public OffsetDateTime getFilledAt() {
        return filledAt;
    }

    public void setFilledAt(OffsetDateTime filledAt) {
        this.filledAt = filledAt;
    }

    public Long getFilledByUserId() {
        return filledByUserId;
    }

    public void setFilledByUserId(Long filledByUserId) {
        this.filledByUserId = filledByUserId;
    }

    public Long getVerifiedByPharmacistId() {
        return verifiedByPharmacistId;
    }

    public void setVerifiedByPharmacistId(Long verifiedByPharmacistId) {
        this.verifiedByPharmacistId = verifiedByPharmacistId;
    }

    public FulfillmentStatus getStatus() {
        return status;
    }

    public void setStatus(FulfillmentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FulfillmentDto{" +
                "fulfillmentId=" + fulfillmentId +
                ", prescriptionId=" + prescriptionId +
                ", productId=" + productId +
                ", lotId=" + lotId +
                ", quantityDispensed=" + quantityDispensed +
                ", status=" + status +
                '}';
    }
}
