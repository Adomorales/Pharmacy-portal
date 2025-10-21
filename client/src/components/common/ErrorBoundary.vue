<template>
  <div class="error-boundary">
    <slot v-if="!hasError" />

    <div v-else class="error-fallback">
      <div class="error-content">
        <div class="error-icon">
          <AlertTriangleIcon />
        </div>

        <div class="error-details">
          <h3 class="error-title">
            {{ errorTitle }}
          </h3>

          <p class="error-message">
            {{ errorMessage }}
          </p>

          <div class="error-actions">
            <button @click="retry" class="retry-btn">
              <RefreshIcon />
              Try Again
            </button>

            <button @click="reportError" class="report-btn" v-if="showReportButton">
              <FlagIcon />
              Report Issue
            </button>

            <button @click="reset" class="reset-btn">
              <HomeIcon />
              Go Home
            </button>
          </div>

          <details v-if="showDetails && error" class="error-details-section">
            <summary>Technical Details</summary>
            <pre class="error-stack">{{ error.stack }}</pre>
          </details>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onErrorCaptured, onMounted } from 'vue'
import {
  AlertTriangleIcon,
  RefreshIcon,
  FlagIcon,
  HomeIcon
} from '@/components/icons'

const props = defineProps({
  fallbackTitle: {
    type: String,
    default: 'Something went wrong'
  },
  fallbackMessage: {
    type: String,
    default: 'An unexpected error occurred. Please try again or contact support if the problem persists.'
  },
  showReportButton: {
    type: Boolean,
    default: true
  },
  showDetails: {
    type: Boolean,
    default: false
  },
  resetOnRouteChange: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['error', 'retry', 'reset'])

const hasError = ref(false)
const error = ref(null)
const errorInfo = ref(null)

const errorTitle = computed(() => {
  if (hasError.value) {
    // Customize title based on error type
    if (error.value?.response?.status === 404) {
      return 'Page Not Found'
    }
    if (error.value?.response?.status >= 500) {
      return 'Server Error'
    }
    if (error.value?.response?.status === 403) {
      return 'Access Denied'
    }
    return props.fallbackTitle
  }
  return ''
})

const errorMessage = computed(() => {
  if (hasError.value) {
    // Customize message based on error type
    if (error.value?.response?.status === 404) {
      return 'The page you\'re looking for doesn\'t exist or may have been moved.'
    }
    if (error.value?.response?.status >= 500) {
      return 'Our servers are experiencing issues. Please try again in a few minutes.'
    }
    if (error.value?.response?.status === 403) {
      return 'You don\'t have permission to access this resource.'
    }
    if (error.value?.response?.status === 401) {
      return 'Your session has expired. Please log in again.'
    }

    // Network errors
    if (error.value?.code === 'NETWORK_ERROR' || error.value?.message?.includes('Network')) {
      return 'Unable to connect to the server. Please check your internet connection.'
    }

    // Generic error
    return props.fallbackMessage
  }
  return ''
})

// Capture errors from child components
onErrorCaptured((err, instance, info) => {
  hasError.value = true
  error.value = err
  errorInfo.value = info

  console.error('ErrorBoundary caught an error:', err, info)

  // Emit error event for parent components
  emit('error', {
    error: err,
    componentInfo: info,
    timestamp: new Date().toISOString()
  })

  // Prevent error from propagating
  return false
})

// Reset error state
const reset = () => {
  hasError.value = false
  error.value = null
  errorInfo.value = null

  emit('reset')

  // Optionally navigate to home
  if (props.resetOnRouteChange) {
    router.push('/')
  }
}

// Retry the failed operation
const retry = () => {
  hasError.value = false
  error.value = null
  errorInfo.value = null

  emit('retry')

  // Force re-render of slot content
  nextTick(() => {
    // The slot will re-render automatically
  })
}

// Report error (could integrate with error reporting service)
const reportError = () => {
  const errorReport = {
    message: error.value?.message || 'Unknown error',
    stack: error.value?.stack,
    componentInfo: errorInfo.value,
    timestamp: new Date().toISOString(),
    userAgent: navigator.userAgent,
    url: window.location.href
  }

  // Here you could send to an error reporting service like Sentry
  console.log('Error report:', errorReport)

  // Show user feedback
  alert('Error report sent. Thank you for helping us improve!')
}

// Watch for route changes to reset error state
onMounted(() => {
  if (props.resetOnRouteChange) {
    // This would need to be implemented with Vue Router's navigation guards
    // For now, we'll handle it manually
  }
})
</script>

<style scoped>
.error-boundary {
  width: 100%;
  min-height: 100%;
}

.error-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 20px;
}

.error-content {
  max-width: 600px;
  width: 100%;
  text-align: center;
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.error-icon {
  font-size: 48px;
  color: #e74c3c;
  margin-bottom: 20px;
}

.error-title {
  color: #2c3e50;
  margin: 0 0 16px 0;
  font-size: 24px;
  font-weight: 600;
}

.error-message {
  color: #5a6c7d;
  margin: 0 0 24px 0;
  font-size: 16px;
  line-height: 1.5;
}

.error-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 24px;
}

.retry-btn,
.report-btn,
.reset-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.retry-btn {
  background: #3498db;
  color: white;
}

.retry-btn:hover {
  background: #2980b9;
}

.report-btn {
  background: #f39c12;
  color: white;
}

.report-btn:hover {
  background: #e67e22;
}

.reset-btn {
  background: #95a5a6;
  color: white;
}

.reset-btn:hover {
  background: #7f8c8d;
}

.error-details-section {
  text-align: left;
  margin-top: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
}

.error-details-section summary {
  cursor: pointer;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 12px;
}

.error-stack {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 12px;
  border-radius: 4px;
  font-size: 12px;
  white-space: pre-wrap;
  overflow-x: auto;
  max-height: 200px;
  overflow-y: auto;
}

/* Responsive design */
@media (max-width: 768px) {
  .error-fallback {
    padding: 10px;
  }

  .error-content {
    padding: 20px;
  }

  .error-actions {
    flex-direction: column;
    align-items: center;
  }

  .retry-btn,
  .report-btn,
  .reset-btn {
    width: 100%;
    max-width: 200px;
  }
}
</style>
