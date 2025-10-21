package com.pharmacy.dao;

import com.pharmacy.dto.NotificationsDto;
import com.pharmacy.model.Notifications;
import com.pharmacy.model.enums.NotificationStatus;

import java.util.List;

public interface NotificationsDao {

    Notifications getById(long notificationId);

    List<Notifications> listPending();

    Notifications enqueue(Notifications n);

    Notifications markSent(long notificationId);

    Notifications updateStatus(long notificationId, NotificationStatus status);

    Notifications updatePriority(long notificationId, boolean isPriority);

    Notifications updateNotes(long notificationId, String notes);

    Notifications updateAssignedUser(long notificationId, long assignedUserId);

    Notifications updateAssignedBy(long notificationId, long assignedByUserId);

    Notifications updateReason(long notificationId, String reason);

    Notifications updateWorkflow(long notificationId, String workflow);

    Notifications updateWorkflowStage(long notificationId, String workflowStage);

    Notifications create(NotificationsDto dto);

    boolean deleteById(long notificationId);

    List<NotificationsDto> getPendingNotifications();

    List<NotificationsDto> getNotificationsByCaseId(long caseId);

    List<NotificationsDto> getNotificationsByStatus(NotificationStatus status);

    Notifications advanceWorkflow(long notificationId, long userId);

    Notifications rejectToPreviousStage(long notificationId, String reason, long userId);

    Notifications assignToUser(long notificationId, long assignedUserId, long assignedByUserId);

    Notifications addNotes(long notificationId, String notes, long userId);

    Notifications markAsPriority(long notificationId, boolean isPriority, long userId);
