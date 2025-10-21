<template>
  <div v-if="visible" class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>Stock Adjustment</h3>
        <button @click="close" class="close-btn">&times;</button>
      </div>

      <div class="modal-body">
        <div v-if="medication" class="medication-info">
          <h4>{{ medication.name }}</h4>
          <p>{{ medication.strength }} {{ medication.form }}</p>
          <p class="current-stock">Current Stock: {{ medication.currentStock }}</p>
        </div>

        <form @submit.prevent="submitAdjustment" class="adjustment-form">
          <div class="form-group">
            <label for="adjustmentType">Adjustment Type:</label>
            <select
              id="adjustmentType"
              v-model="adjustmentData.type"
              required
              class="form-select"
            >
              <option value="">Select Type</option>
              <option value="addition">Stock Addition</option>
              <option value="reduction">Stock Reduction</option>
              <option value="correction">Stock Correction</option>
              <option value="expiry">Expiry Removal</option>
              <option value="damage">Damage/Loss</option>
            </select>
          </div>

          <div class="form-group">
            <label for="quantity">Quantity:</label>
            <input
              id="quantity"
              v-model.number="adjustmentData.quantity"
              type="number"
              min="1"
              required
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label for="reason">Reason:</label>
            <select
              id="reason"
              v-model="adjustmentData.reason"
              required
              class="form-select"
            >
              <option value="">Select Reason</option>
              <option value="received">Received new stock</option>
              <option value="returned">Patient returned</option>
              <option value="damaged">Damaged/lost</option>
              <option value="expired">Expired medication</option>
              <option value="correction">Inventory correction</option>
              <option value="transfer">Transfer in/out</option>
              <option value="recall">Product recall</option>
              <option value="other">Other</option>
            </select>
          </div>

          <div class="form-group">
            <label for="notes">Notes:</label>
            <textarea
              id="notes"
              v-model="adjustmentData.notes"
              rows="3"
              class="form-textarea"
              placeholder="Additional details about this adjustment..."
            ></textarea>
          </div>

          <div class="form-group">
            <label for="reference">Reference/Lot Number:</label>
            <input
              id="reference"
              v-model="adjustmentData.reference"
              type="text"
              class="form-input"
              placeholder="Lot number, invoice number, etc."
            />
          </div>
        </form>
      </div>

      <div class="modal-footer">
        <button @click="close" class="btn btn-secondary">Cancel</button>
        <button @click="submitAdjustment" :disabled="!isValid" class="btn btn-primary">
          Save Adjustment
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue'

const props = defineProps({
  medication: {
    type: Object,
    default: null
  },
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'save'])

const adjustmentData = ref({
  type: '',
  quantity: '',
  reason: '',
  notes: '',
  reference: ''
})

const isValid = computed(() => {
  return adjustmentData.value.type && adjustmentData.value.quantity > 0 && adjustmentData.value.reason
})

const handleOverlayClick = (event) => {
  if (event.target === event.currentTarget) {
    close()
  }
}

const close = () => {
  emit('close')
  // Reset form
  adjustmentData.value = {
    type: '',
    quantity: '',
    reason: '',
    notes: '',
    reference: ''
  }
}

const submitAdjustment = () => {
  if (isValid.value) {
    emit('save', {
      medication: props.medication,
      ...adjustmentData.value
    })
    close()
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e1e5e9;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #7f8c8d;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f8f9fa;
  color: #2c3e50;
}

.modal-body {
  padding: 24px;
}

.medication-info {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #3498db;
}

.medication-info h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
}

.medication-info p {
  margin: 0 0 4px 0;
  color: #7f8c8d;
  font-size: 14px;
}

.current-stock {
  color: #2c3e50 !important;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #2c3e50;
  font-weight: 500;
  font-size: 14px;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid #e1e5e9;
  background: #f8f9fa;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-secondary {
  background: white;
  color: #7f8c8d;
  border: 1px solid #e1e5e9;
}

.btn-secondary:hover:not(:disabled) {
  background: #f8f9fa;
  color: #2c3e50;
}

.btn-primary {
  background: #3498db;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2980b9;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: 20px;
  }

  .modal-footer {
    flex-direction: column-reverse;
  }

  .btn {
    width: 100%;
  }
}
</style>
