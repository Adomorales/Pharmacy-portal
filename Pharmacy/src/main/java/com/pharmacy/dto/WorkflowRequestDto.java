package com.pharmacy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WorkflowRequestDto {

    @NotBlank(message = "User ID is required")
    private String userId;

    private String reason;
    private String assignedUserId;
    private String assignedByUserId;

    @NotBlank(message = "Notes cannot be blank")
    private String notes;

    @NotNull(message = "Priority flag is required")
    private Boolean isPriority;

    // Default constructor
    public WorkflowRequestDto() {}

    // Constructor for operations that only need userId
    public WorkflowRequestDto(String userId) {
        this.userId = userId;
    }

    // Constructor for add notes and mark priority operations
    public static WorkflowRequestDto forNotes(String userId, String notes) {
        WorkflowRequestDto dto = new WorkflowRequestDto();
        dto.setUserId(userId);
        dto.setNotes(notes);
        return dto;
    }

    public static WorkflowRequestDto forPriority(String userId, Boolean isPriority) {
        WorkflowRequestDto dto = new WorkflowRequestDto();
        dto.setUserId(userId);
        dto.setIsPriority(isPriority);
        return dto;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(String assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public String getAssignedByUserId() {
        return assignedByUserId;
    }

    public void setAssignedByUserId(String assignedByUserId) {
        this.assignedByUserId = assignedByUserId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsPriority() {
        return isPriority;
    }

    public void setIsPriority(Boolean isPriority) {
        this.isPriority = isPriority;
    }
} 
