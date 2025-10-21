package com.pharmacy.controller;

import com.pharmacy.dao.NotificationsDao;
import com.pharmacy.dto.NotificationsDto;
import com.pharmacy.dto.WorkflowRequestDto;
import com.pharmacy.model.enums.NotificationStatus;
import com.pharmacy.model.Notifications;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin
public class NotificationsController {

    private final NotificationsDao dao;

    public NotificationsController(NotificationsDao dao) {
        this.dao = dao;
    }

    @GetMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Notifications getNotificationById(@PathVariable long notificationId) {
        try {
            Notifications notification = dao.getById(notificationId);
            return notification;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to get notification by ID", e);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Notifications createNotification(@Valid @RequestBody NotificationsDto notificationDto) {
        try {
            Notifications createdNotification = dao.create(notificationDto);
            return createdNotification;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to create notification", e);
        }
    }

    @PutMapping("/{notificationId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Notifications updateNotificationStatus( @PathVariable long notificationId, @RequestParam NotificationStatus status) {
        try {
            Notifications updatedNotification = dao.updateNotificationStatus(notificationId, status);
            return updatedNotification;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to update notification status", e);
        }
    }

    @PutMapping("/{notificationId}/mark-sent")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Notifications markNotificationAsSent(@PathVariable long notificationId) {
        try {
            Notifications notification = dao.markNotificationAsSent(notificationId);
            return notification;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to mark notification as sent", e);
        }
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public boolean deleteNotification(@PathVariable long notificationId) {
        try {
            dao.deleteById(notificationId);
            return true;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to delete notification", e);
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<NotificationsDto> getPendingNotifications() {
        try {
            List<NotificationsDto> notifications = dao.getPendingNotifications();
            return notifications;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to get pending notifications", e);
        }
    }

    @GetMapping("/case/{caseId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<NotificationsDto> getNotificationsByCaseId(@PathVariable long caseId) {
        try {
            List<NotificationsDto> notifications = dao.getNotificationsByCaseId(caseId);
            return notifications;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to get notifications by case ID", e);
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<NotificationsDto> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        try {
            List<NotificationsDto> notifications = dao.getNotificationsByStatus(status);
            return notifications;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to get notifications by status", e);
        }
    }

    @PutMapping("/{notificationId}/advance-workflow")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Notifications advanceWorkflow( @PathVariable long notificationId, @Valid @RequestBody WorkflowRequestDto workflowRequest) {
        try {
            Notifications updatedNotification = dao.advanceWorkflow(notificationId, workflowRequest.getUserId());
            return updatedNotification;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to advance workflow", e);
        }
    }

    @PutMapping("/{notificationId}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Notifications rejectToPreviousStage( @PathVariable long notificationId, @Valid @RequestBody WorkflowRequestDto workflowRequest) {
        try {
            Notifications updatedNotification = dao.rejectToPreviousStage( notificationId, workflowRequest.getReason(), workflowRequest.getUserId());
            return updatedNotification;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to reject to previous stage", e);
        }
    }

    @PutMapping("/{notificationId}/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Notifications assignToUser( @PathVariable long notificationId, @Valid @RequestBody WorkflowRequestDto workflowRequest) {
        try {
            Notifications updatedNotification = dao.assignToUser( notificationId, workflowRequest.getAssignedUserId(), workflowRequest.getAssignedByUserId());
            return updatedNotification;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to assign to user", e);
        }
    }

    @PutMapping("/{notificationId}/notes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Notifications addNotes( @PathVariable long notificationId, @Valid @RequestBody WorkflowRequestDto workflowRequest) {
        try {
            Notifications updatedNotification = dao.addNotes( notificationId, workflowRequest.getNotes(), workflowRequest.getUserId());
            return updatedNotification;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to add notes", e);
        }
    }

    @PutMapping("/{notificationId}/priority")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Notifications markAsPriority( @PathVariable long notificationId, @Valid @RequestBody WorkflowRequestDto workflowRequest) {
        try {
            Notifications updatedNotification = dao.markAsPriority( notificationId, workflowRequest.getIsPriority(), workflowRequest.getUserId());
            return updatedNotification;
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Failed to mark as priority", e);
        }
    }

} 

