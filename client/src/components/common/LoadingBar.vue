<template>
  <div class="loading-bar-container">
    <div class="loading-bar" :class="{ 'inline': inline }">
      <div
        class="loading-progress"
        :style="{ width: `${progress}%` }"
      ></div>
    </div>

    <div v-if="showText" class="loading-text">
      <span class="loading-message">{{ message }}</span>
      <span v-if="showPercentage" class="loading-percentage">{{ Math.round(progress) }}%</span>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

// Props
const props = defineProps({
  progress: {
    type: Number,
    default: 0
  },
  message: {
    type: String,
    default: 'Loading...'
  },
  showText: {
    type: Boolean,
    default: true
  },
  showPercentage: {
    type: Boolean,
    default: true
  },
  inline: {
    type: Boolean,
    default: false
  },
  height: {
    type: String,
    default: '4px'
  },
  color: {
    type: String,
    default: '#3498db'
  },
  backgroundColor: {
    type: String,
    default: '#e1e5e9'
  }
})

// State
const internalProgress = ref(props.progress)

// Watch for progress changes
watch(() => props.progress, (newProgress) => {
  internalProgress.value = newProgress
})
</script>

<style scoped>
.loading-bar-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.loading-bar {
  width: 100%;
  height: 4px;
  background: #e1e5e9;
  border-radius: 2px;
  overflow: hidden;
  position: relative;
}

.loading-bar.inline {
  height: 6px;
  width: 200px;
}

.loading-progress {
  height: 100%;
  background: #3498db;
  border-radius: 2px;
  transition: width 0.3s ease;
  position: relative;
}

.loading-progress::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  background-image: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.4),
    transparent
  );
  animation: loading-shimmer 2s infinite;
}

.loading-text {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #7f8c8d;
}

.loading-message {
  font-weight: 500;
}

.loading-percentage {
  background: #f8f9fa;
  padding: 2px 6px;
  border-radius: 12px;
  font-weight: 600;
  color: #2c3e50;
}

@keyframes loading-shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

/* Variations */
.loading-bar.small {
  height: 2px;
}

.loading-bar.large {
  height: 8px;
}

.loading-bar.rounded {
  border-radius: 4px;
}

.loading-bar.rounded .loading-progress {
  border-radius: 4px;
}

/* Color variations */
.loading-bar.success .loading-progress {
  background: #27ae60;
}

.loading-bar.warning .loading-progress {
  background: #f39c12;
}

.loading-bar.error .loading-progress {
  background: #e74c3c;
}
</style>
