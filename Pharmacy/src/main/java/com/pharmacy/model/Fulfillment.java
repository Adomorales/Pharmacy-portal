package com.pharmacy.model;

import com.pharmacy.model.enums.FulfillmentStatus;
import java.time.OffsetDateTime;
import java.util.Objects;

public class Fulfillment {
    private long fulfillmentId;
    private long prescriptionId;
    private Long productId;
    private Long lotId;
    private int quantityDispensed;
    private OffsetDateTime filledAt;
    private Long filledByUserId;
    private Long verifiedByPharmacistId;
    private FulfillmentStatus status;

    public Fulfillment() {}

    public long getFulfillmentId() {
        return fulfillmentId;
    }

    public void setFulfillmentId(long fulfillmentId) {
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Fulfillment that = (Fulfillment) o;
        return fulfillmentId == that.fulfillmentId && prescriptionId == that.prescriptionId && quantityDispensed == that.quantityDispensed && Objects.equals(productId, that.productId) && Objects.equals(lotId, that.lotId) && Objects.equals(filledAt, that.filledAt) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fulfillmentId, prescriptionId, productId, lotId, quantityDispensed, filledAt, status);
    }

    @Override
    public String toString() {
        return "Fulfillment{" +
                "fulfillmentId=" + fulfillmentId +
                ", prescriptionId=" + prescriptionId +
                ", productId=" + productId +
                ", lotId=" + lotId +
                ", quantityDispensed=" + quantityDispensed +
                ", filledAt=" + filledAt +
                ", status=" + status +
                '}';
    }
}
