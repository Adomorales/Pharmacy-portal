package com.pharmacy.model.enums;

/**
 * Represents the current workflow stage of a prescription in the pharmacy system.
 * Prescriptions flow through these stages: Data Entry → Data Review → Product → Product Review → Notifications
 */
public enum WorkflowStage {
    DATA_ENTRY("Data Entry - Patient/Prescriber/Medication Selection"),
    DATA_REVIEW("Data Review - Pharmacist Verification"),
    PRODUCT("Product - Inventory Selection and Fulfillment"),
    PRODUCT_REVIEW("Product Review - Final Pharmacist Check"),
    NOTIFICATIONS("Notifications - Patient Communication"),
    COMPLETED("Completed - Workflow Finished"),
    CANCELLED("Cancelled - Workflow Terminated");

    private final String description;

    WorkflowStage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
