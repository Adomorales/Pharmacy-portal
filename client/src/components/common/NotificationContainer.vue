<template>
  <div class="notification-container">
    <TransitionGroup name="notification" tag="div">
      <div
        v-for="notification in notifications"
        :key="notification.id"
        class="notification"
        :class="notification.type"
      >
        <div class="notification-icon">
          <component :is="getIcon(notification.type)" />
        </div>

        <div class="notification-content">
          <div class="notification-title">{{ notification.title }}</div>
          <div class="notification-message">{{ notification.message }}</div>
        </div>

        <button
          class="notification-close"
          @click="removeNotification(notification.id)"
          aria-label="Close notification"
        >
          <XIcon />
        </button>

        <!-- Progress bar for auto-dismiss -->
        <div
          v-if="notification.duration"
          class="notification-progress"
          :style="{ animationDuration: `${notification.duration}ms` }"
        ></div>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'

// Component imports
import {
  CheckCircleIcon,
  AlertCircleIcon,
  AlertTriangleIcon,
  InfoIcon,
  XIcon
} from '@/components/icons'

// State
const notifications = ref([])

// Notification types and icons
const notificationTypes = {
  success: CheckCircleIcon,
  error: AlertCircleIcon,
  warning: AlertTriangleIcon,
  info: InfoIcon
}

// Methods
const getIcon = (type) => {
  return notificationTypes[type] || InfoIcon
}

const addNotification = (notification) => {
  const id = Date.now() + Math.random()
  const newNotification = {
    id,
    type: notification.type || 'info',
    title: notification.title || '',
    message: notification.message || '',
    duration: notification.duration || 5000,
    persistent: notification.persistent || false,
    actions: notification.actions || []
  }

  notifications.value.push(newNotification)

  // Auto-remove if not persistent
  if (!newNotification.persistent && newNotification.duration > 0) {
    setTimeout(() => {
      removeNotification(id)
    }, newNotification.duration)
  }

  return id
}

const removeNotification = (id) => {
  const index = notifications.value.findIndex(n => n.id === id)
  if (index > -1) {
    notifications.value.splice(index, 1)
  }
}

const clearAll = () => {
  notifications.value = []
}

// Global notification service
const notificationService = {
  success: (message, options = {}) => addNotification({ ...options, type: 'success', message }),
  error: (message, options = {}) => addNotification({ ...options, type: 'error', message }),
  warning: (message, options = {}) => addNotification({ ...options, type: 'warning', message }),
  info: (message, options = {}) => addNotification({ ...options, type: 'info', message }),
  remove: removeNotification,
  clearAll
}

// Make service globally available
window.$notification = notificationService

// Expose methods for external use
defineExpose({
  addNotification,
  removeNotification,
  clearAll,
  service: notificationService
})
</script>

<style scoped>
.notification-container {
  position: fixed;
  top: 80px;
  right: 20px;
  z-index: 1000;
  max-width: 400px;
  width: 100%;
}

.notification {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  margin-bottom: 12px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-left: 4px solid;
  overflow: hidden;
  animation: slideInRight 0.3s ease;
}

.notification.success {
  border-left-color: #27ae60;
}

.notification.error {
  border-left-color: #e74c3c;
}

.notification.warning {
  border-left-color: #f39c12;
}

.notification.info {
  border-left-color: #3498db;
}

.notification-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
  margin-top: 2px;
}

.notification.success .notification-icon {
  color: #27ae60;
}

.notification.error .notification-icon {
  color: #e74c3c;
}

.notification.warning .notification-icon {
  color: #f39c12;
}

.notification.info .notification-icon {
  color: #3498db;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
  margin-bottom: 4px;
}

.notification-message {
  color: #5a6c7d;
  font-size: 13px;
  line-height: 1.4;
}

.notification-close {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: #7f8c8d;
  border-radius: 4px;
  transition: all 0.2s;
  flex-shrink: 0;
}

.notification-close:hover {
  background-color: #f8f9fa;
  color: #2c3e50;
}

.notification-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  background: currentColor;
  animation: progress linear;
  opacity: 0.3;
}

/* Animations */
@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes progress {
  from {
    width: 100%;
  }
  to {
    width: 0%;
  }
}

.notification-enter-active {
  animation: slideInRight 0.3s ease;
}

.notification-leave-active {
  animation: slideInRight 0.3s ease reverse;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .notification-container {
    top: 70px;
    right: 10px;
    left: 10px;
    max-width: none;
  }

  .notification {
    margin-bottom: 8px;
  }
}
</style>
