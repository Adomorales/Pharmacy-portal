<template>
  <div class="search-bar">
    <div class="search-input-container">
      <SearchIcon class="search-icon" />
      <input
        v-model="internalValue"
        type="text"
        :placeholder="placeholder"
        class="search-input"
        @input="handleInput"
        @keydown.enter="handleSearch"
        @keydown.esc="clearSearch"
        ref="searchInput"
      />
      <button
        v-if="internalValue"
        class="clear-btn"
        @click="clearSearch"
        aria-label="Clear search"
      >
        <XIcon />
      </button>
    </div>

    <!-- Search Suggestions (optional) -->
    <div v-if="showSuggestions && suggestions.length > 0" class="search-suggestions">
      <div
        v-for="suggestion in suggestions"
        :key="suggestion.id"
        class="suggestion-item"
        @click="selectSuggestion(suggestion)"
      >
        <component :is="suggestion.icon" class="suggestion-icon" />
        <div class="suggestion-content">
          <div class="suggestion-title">{{ suggestion.title }}</div>
          <div class="suggestion-subtitle">{{ suggestion.subtitle }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'

// Component imports
import { SearchIcon, XIcon } from '@/components/icons'

// Props
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: 'Search...'
  },
  debounceMs: {
    type: Number,
    default: 300
  },
  suggestions: {
    type: Array,
    default: () => []
  },
  showSuggestions: {
    type: Boolean,
    default: true
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'search', 'suggestion-select'])

// State
const internalValue = ref(props.modelValue)
const searchTimeout = ref(null)
const searchInput = ref(null)

// Computed
const hasValue = computed(() => internalValue.value.trim().length > 0)

// Watch for external value changes
watch(() => props.modelValue, (newValue) => {
  internalValue.value = newValue
})

// Methods
const handleInput = () => {
  // Clear previous timeout
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }

  // Debounce search
  searchTimeout.value = setTimeout(() => {
    emit('update:modelValue', internalValue.value)
    if (hasValue.value) {
      emit('search', internalValue.value)
    }
  }, props.debounceMs)
}

const handleSearch = () => {
  if (hasValue.value) {
    emit('search', internalValue.value)
  }
}

const clearSearch = () => {
  internalValue.value = ''
  emit('update:modelValue', '')
  emit('search', '')

  // Focus input after clearing
  nextTick(() => {
    searchInput.value?.focus()
  })
}

const selectSuggestion = (suggestion) => {
  internalValue.value = suggestion.title
  emit('update:modelValue', suggestion.title)
  emit('suggestion-select', suggestion)
}

const focus = () => {
  searchInput.value?.focus()
}

// Expose focus method
defineExpose({
  focus
})
</script>

<style scoped>
.search-bar {
  position: relative;
  width: 100%;
}

.search-input-container {
  position: relative;
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  padding: 0 16px;
  transition: all 0.2s;
}

.search-input-container:focus-within {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
  background: white;
}

.search-icon {
  color: #7f8c8d;
  margin-right: 12px;
  width: 18px;
  height: 18px;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 12px 0;
  font-size: 14px;
  color: #2c3e50;
  outline: none;
}

.search-input::placeholder {
  color: #95a5a6;
}

.clear-btn {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: #7f8c8d;
  border-radius: 4px;
  transition: all 0.2s;
  margin-left: 8px;
}

.clear-btn:hover {
  background-color: #e1e5e9;
  color: #2c3e50;
}

.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e1e5e9;
  border-top: none;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
  max-height: 300px;
  overflow-y: auto;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-bottom: 1px solid #f8f9fa;
}

.suggestion-item:hover {
  background-color: #f8f9fa;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-icon {
  width: 16px;
  height: 16px;
  color: #7f8c8d;
  flex-shrink: 0;
}

.suggestion-content {
  flex: 1;
}

.suggestion-title {
  font-weight: 500;
  color: #2c3e50;
  font-size: 14px;
  margin-bottom: 2px;
}

.suggestion-subtitle {
  color: #7f8c8d;
  font-size: 12px;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .search-input-container {
    padding: 0 12px;
  }

  .search-input {
    padding: 10px 0;
    font-size: 16px; /* Prevent zoom on iOS */
  }

  .search-icon {
    width: 20px;
    height: 20px;
    margin-right: 10px;
  }
}
</style>
