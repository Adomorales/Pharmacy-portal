package com.pharmacy.model.enums;

public enum ReviewStatus {

    PENDING,
    APPROVED,
    REJECTED;

    public static ReviewStatus fromDbValue(String dbValue) {
        if (dbValue == null) {
            throw new IllegalArgumentException("Fail");
        }

        switch (dbValue.toLowerCase()) {
            case "pending"      :    return PENDING;
            case "approved"     :   return APPROVED;
            case "rejected"     :   return REJECTED;
            default:
                throw new IllegalArgumentException("Unknown");
        }
    }
}
