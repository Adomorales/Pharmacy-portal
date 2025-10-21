<template>
  <div class="form-field" :class="{ 'has-error': hasError, 'disabled': disabled }">
    <!-- Label -->
    <label v-if="label" class="field-label" :for="inputId">
      {{ label }}
      <span v-if="required" class="required-indicator">*</span>
    </label>

    <!-- Input Field -->
    <div class="field-input-container">
      <!-- Prefix Icon -->
      <div v-if="$slots.prefix || prefixIcon" class="field-prefix">
        <slot name="prefix">
          <component :is="prefixIcon" />
        </slot>
      </div>

      <!-- Main Input -->
      <input
        v-if="type === 'text' || type === 'email' || type === 'password' || type === 'number' || type === 'tel' || type === 'url' || type === 'search'"
        :id="inputId"
        v-model="internalValue"
        :type="inputType"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :required="required"
        :min="min"
        :max="max"
        :step="step"
        :pattern="pattern"
        :autocomplete="autocomplete"
        :class="inputClass"
        @input="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"
        @keydown="handleKeydown"
        ref="inputRef"
      />

      <textarea
        v-else-if="type === 'textarea'"
        :id="inputId"
        v-model="internalValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :required="required"
        :rows="rows"
        :maxlength="maxlength"
        :class="inputClass"
        @input="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"
        ref="inputRef"
      ></textarea>

      <select
        v-else-if="type === 'select'"
        :id="inputId"
        v-model="internalValue"
        :disabled="disabled"
        :required="required"
        :class="inputClass"
        @change="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"
        ref="inputRef"
      >
        <option v-if="placeholder" value="" disabled>{{ placeholder }}</option>
        <option v-for="option in options" :key="option.value" :value="option.value">
          {{ option.label }}
        </option>
      </select>

      <!-- Suffix Icon -->
      <div v-if="$slots.suffix || suffixIcon || showPasswordToggle" class="field-suffix">
        <button
          v-if="showPasswordToggle && type === 'password'"
          type="button"
          class="password-toggle"
          @click="togglePasswordVisibility"
          :disabled="disabled"
          aria-label="Toggle password visibility"
        >
          <EyeIcon v-if="inputType === 'password'" />
          <EyeOffIcon v-else />
        </button>
        <slot name="suffix">
          <component :is="suffixIcon" />
        </slot>
      </div>
    </div>

    <!-- Help Text -->
    <div v-if="helpText" class="field-help">{{ helpText }}</div>

    <!-- Error Message -->
    <div v-if="hasError" class="field-error">
      {{ errorMessage }}
    </div>

    <!-- Success Message -->
    <div v-if="showSuccess && isValid" class="field-success">
      <CheckIcon />
      {{ successMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import {
  EyeIcon,
  EyeOffIcon,
  CheckIcon
} from '@/components/icons'

// Props
const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  label: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'text',
    validator: (value) => ['text', 'email', 'password', 'number', 'tel', 'url', 'search', 'textarea', 'select'].includes(value)
  },
  placeholder: {
    type: String,
    default: ''
  },
  required: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  },
  readonly: {
    type: Boolean,
    default: false
  },
  options: {
    type: Array,
    default: () => []
  },
  rows: {
    type: Number,
    default: 3
  },
  maxlength: {
    type: Number,
    default: null
  },
  min: {
    type: [Number, String],
    default: null
  },
  max: {
    type: [Number, String],
    default: null
  },
  step: {
    type: [Number, String],
    default: null
  },
  pattern: {
    type: String,
    default: null
  },
  autocomplete: {
    type: String,
    default: null
  },
  helpText: {
    type: String,
    default: ''
  },
  errorMessage: {
    type: String,
    default: ''
  },
  successMessage: {
    type: String,
    default: ''
  },
  showSuccess: {
    type: Boolean,
    default: false
  },
  prefixIcon: {
    type: [String, Object],
    default: null
  },
  suffixIcon: {
    type: [String, Object],
    default: null
  },
  showPasswordToggle: {
    type: Boolean,
    default: false
  },
  rules: {
    type: Array,
    default: () => []
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'blur', 'focus', 'input', 'change'])

// State
const internalValue = ref(props.modelValue)
const inputType = ref(props.type)
const inputRef = ref(null)
const validationTimeout = ref(null)

// Computed
const inputId = computed(() => `field-${Math.random().toString(36).substr(2, 9)}`)

const hasError = computed(() => {
  return props.errorMessage && props.errorMessage.length > 0
})

const isValid = computed(() => {
  if (!props.showSuccess) return false
  return props.successMessage && props.successMessage.length > 0
})

const inputClass = computed(() => {
  return {
    'has-prefix': props.prefixIcon || props.$slots.prefix,
    'has-suffix': props.suffixIcon || props.showPasswordToggle || props.$slots.suffix,
    'field-input': true
  }
})

// Watch for external value changes
watch(() => props.modelValue, (newValue) => {
  internalValue.value = newValue
})

// Methods
const handleInput = (event) => {
  const value = event.target.value
  internalValue.value = value
  emit('update:modelValue', value)
  emit('input', value)

  // Validate on input if rules provided
  if (props.rules.length > 0) {
    clearTimeout(validationTimeout.value)
    validationTimeout.value = setTimeout(() => {
      validateField()
    }, 300)
  }
}

const handleBlur = (event) => {
  emit('blur', event)
  validateField()
}

const handleFocus = (event) => {
  emit('focus', event)
}

const handleKeydown = (event) => {
  // Custom key handling if needed
}

const togglePasswordVisibility = () => {
  inputType.value = inputType.value === 'password' ? 'text' : 'password'
}

const validateField = () => {
  // Implement validation logic based on rules
  // This would integrate with a validation library like Vuelidate
}

const focus = () => {
  nextTick(() => {
    inputRef.value?.focus()
  })
}

const select = () => {
  nextTick(() => {
    inputRef.value?.select()
  })
}

// Expose methods
defineExpose({
  focus,
  select,
  validate: validateField,
  inputElement: inputRef
})
</script>

<style scoped>
.form-field {
  margin-bottom: 20px;
}

.field-label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #2c3e50;
  font-size: 14px;
}

.required-indicator {
  color: #e74c3c;
  margin-left: 4px;
}

.field-input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.field-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  font-size: 14px;
  color: #2c3e50;
  background: white;
  transition: all 0.2s;
}

.field-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.field-input:disabled {
  background-color: #f8f9fa;
  color: #7f8c8d;
  cursor: not-allowed;
}

.field-input:readonly {
  background-color: #f8f9fa;
  color: #5a6c7d;
}

.field-input.has-prefix {
  padding-left: 40px;
}

.field-input.has-suffix {
  padding-right: 40px;
}

.field-input.error {
  border-color: #e74c3c;
  box-shadow: 0 0 0 3px rgba(231, 76, 60, 0.1);
}

.field-input.success {
  border-color: #27ae60;
  box-shadow: 0 0 0 3px rgba(39, 174, 96, 0.1);
}

.field-prefix,
.field-suffix {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  color: #7f8c8d;
  pointer-events: none;
  z-index: 1;
}

.field-prefix {
  left: 12px;
}

.field-suffix {
  right: 12px;
}

.password-toggle {
  pointer-events: auto;
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: #7f8c8d;
  border-radius: 4px;
  transition: all 0.2s;
}

.password-toggle:hover {
  background-color: #f8f9fa;
  color: #2c3e50;
}

.field-help {
  margin-top: 6px;
  font-size: 12px;
  color: #7f8c8d;
}

.field-error {
  margin-top: 6px;
  font-size: 12px;
  color: #e74c3c;
  display: flex;
  align-items: center;
  gap: 4px;
}

.field-success {
  margin-top: 6px;
  font-size: 12px;
  color: #27ae60;
  display: flex;
  align-items: center;
  gap: 4px;
}

.has-error .field-input {
  border-color: #e74c3c;
}

.disabled .field-input {
  background-color: #f8f9fa;
  color: #7f8c8d;
  cursor: not-allowed;
}

/* Textarea specific styles */
.field-input[type="textarea"] {
  resize: vertical;
  min-height: 80px;
}

/* Select specific styles */
.field-input select {
  cursor: pointer;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .form-field {
    margin-bottom: 16px;
  }

  .field-input {
    padding: 14px 16px;
    font-size: 16px; /* Prevent zoom on iOS */
  }

  .field-input.has-prefix {
    padding-left: 44px;
  }

  .field-input.has-suffix {
    padding-right: 44px;
  }

  .field-prefix,
  .field-suffix {
    font-size: 16px;
  }
}
</style>
