package com.pharmacy.model.enums;

/**
 * Represents the status of a prescription within its current workflow stage.
 * Different statuses are available depending on the current stage.
 */
public enum WorkflowStatus {
    // Data Entry Statuses
    DATA_ENTRY_PENDING("Data Entry - Awaiting Input", WorkflowStage.DATA_ENTRY),
    DATA_ENTRY_IN_PROGRESS("Data Entry - Currently Being Processed", WorkflowStage.DATA_ENTRY),
    DATA_ENTRY_COMPLETED("Data Entry - Completed, Ready for Review", WorkflowStage.DATA_ENTRY),

    // Data Review Statuses
    DATA_REVIEW_PENDING("Data Review - Awaiting Pharmacist Review", WorkflowStage.DATA_REVIEW),
    DATA_REVIEW_IN_PROGRESS("Data Review - Under Pharmacist Review", WorkflowStage.DATA_REVIEW),
    DATA_REVIEW_APPROVED("Data Review - Approved by Pharmacist", WorkflowStage.DATA_REVIEW),
    DATA_REVIEW_REJECTED("Data Review - Rejected by Pharmacist", WorkflowStage.DATA_REVIEW),

    // Product Statuses
    PRODUCT_PENDING("Product Selection - Awaiting Product Selection", WorkflowStage.PRODUCT),
    PRODUCT_IN_PROGRESS("Product Selection - Currently Selecting Products", WorkflowStage.PRODUCT),
    PRODUCT_SELECTED("Product Selection - Products Selected", WorkflowStage.PRODUCT),
    PRODUCT_OUT_OF_STOCK("Product Selection - Items Need to be Ordered", WorkflowStage.PRODUCT),
    PRODUCT_FULFILLED("Product Selection - Ready for Review", WorkflowStage.PRODUCT),

    // Product Review Statuses
    PRODUCT_REVIEW_PENDING("Product Review - Awaiting Final Check", WorkflowStage.PRODUCT_REVIEW),
    PRODUCT_REVIEW_IN_PROGRESS("Product Review - Under Final Review", WorkflowStage.PRODUCT_REVIEW),
    PRODUCT_REVIEW_APPROVED("Product Review - Approved for Dispensing", WorkflowStage.PRODUCT_REVIEW),
    PRODUCT_REVIEW_REJECTED("Product Review - Rejected, Return to Product Selection", WorkflowStage.PRODUCT_REVIEW),

    // Notifications Statuses
    NOTIFICATIONS_PENDING("Notifications - Ready to Send to Patient", WorkflowStage.NOTIFICATIONS),
    NOTIFICATIONS_SENT("Notifications - Sent to Patient", WorkflowStage.NOTIFICATIONS),
    NOTIFICATIONS_FAILED("Notifications - Failed to Send", WorkflowStage.NOTIFICATIONS),

    // Final Statuses
    COMPLETED("Prescription Completed Successfully", WorkflowStage.COMPLETED),
    CANCELLED("Prescription Cancelled", WorkflowStage.CANCELLED);

    private final String description;
    private final WorkflowStage stage;

    WorkflowStatus(String description, WorkflowStage stage) {
        this.description = description;
        this.stage = stage;
    }

    public String getDescription() {
        return description;
    }

    public WorkflowStage getStage() {
        return stage;
    }

    /**
     * Check if this status can transition to another status
     */
    public boolean canTransitionTo(WorkflowStatus targetStatus) {
        // Define valid transitions based on workflow rules
        switch (this) {
            case DATA_ENTRY_COMPLETED:
                return targetStatus == DATA_REVIEW_PENDING;

            case DATA_REVIEW_APPROVED:
                return targetStatus == PRODUCT_PENDING;

            case DATA_REVIEW_REJECTED:
                return targetStatus == DATA_ENTRY_PENDING;

            case PRODUCT_FULFILLED:
                return targetStatus == PRODUCT_REVIEW_PENDING;

            case PRODUCT_REVIEW_APPROVED:
                return targetStatus == NOTIFICATIONS_PENDING;

            case PRODUCT_REVIEW_REJECTED:
                return targetStatus == PRODUCT_PENDING;

            case NOTIFICATIONS_SENT:
                return targetStatus == COMPLETED;

            case NOTIFICATIONS_FAILED:
                return targetStatus == NOTIFICATIONS_PENDING;

            default:
                return false;
        }
    }
}
