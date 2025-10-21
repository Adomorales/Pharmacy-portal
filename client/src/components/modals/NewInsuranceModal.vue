<template>
  <div v-if="visible" class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>Add New Insurance</h3>
        <button @click="close" class="close-btn">&times;</button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="submitInsurance" class="insurance-form">
          <div class="form-group">
            <label for="insuranceName">Insurance Provider Name:</label>
            <input
              id="insuranceName"
              v-model="insuranceData.name"
              type="text"
              required
              class="form-input"
              placeholder="e.g., Blue Cross Blue Shield"
            />
          </div>

          <div class="form-group">
            <label for="insuranceType">Insurance Type:</label>
            <select
              id="insuranceType"
              v-model="insuranceData.type"
              required
              class="form-select"
            >
              <option value="">Select Type</option>
              <option value="PPO">PPO (Preferred Provider Organization)</option>
              <option value="HMO">HMO (Health Maintenance Organization)</option>
              <option value="Medicare">Medicare</option>
              <option value="Medicaid">Medicaid</option>
              <option value="Commercial">Commercial</option>
              <option value="WorkersComp">Workers Compensation</option>
              <option value="Other">Other</option>
            </select>
          </div>

          <div class="form-group">
            <label for="policyNumber">Policy Number:</label>
            <input
              id="policyNumber"
              v-model="insuranceData.policyNumber"
              type="text"
              required
              class="form-input"
              placeholder="Policy or member ID"
            />
          </div>

          <div class="form-group">
            <label for="groupNumber">Group Number:</label>
            <input
              id="groupNumber"
              v-model="insuranceData.groupNumber"
              type="text"
              class="form-input"
              placeholder="Group or plan number (if applicable)"
            />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="bin">BIN:</label>
              <input
                id="bin"
                v-model="insuranceData.bin"
                type="text"
                class="form-input"
                placeholder="Bank Identification Number"
              />
            </div>

            <div class="form-group">
              <label for="pcn">PCN:</label>
              <input
                id="pcn"
                v-model="insuranceData.pcn"
                type="text"
                class="form-input"
                placeholder="Processor Control Number"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="relationship">Patient Relationship to Insured:</label>
            <select
              id="relationship"
              v-model="insuranceData.relationship"
              required
              class="form-select"
            >
              <option value="">Select Relationship</option>
              <option value="self">Self</option>
              <option value="spouse">Spouse</option>
              <option value="child">Child</option>
              <option value="parent">Parent</option>
              <option value="other">Other</option>
            </select>
          </div>

          <div v-if="insuranceData.relationship !== 'self'" class="dependent-info">
            <h4>Insured Person Information</h4>

            <div class="form-row">
              <div class="form-group">
                <label for="insuredFirstName">First Name:</label>
                <input
                  id="insuredFirstName"
                  v-model="insuranceData.insuredFirstName"
                  type="text"
                  class="form-input"
                />
              </div>

              <div class="form-group">
                <label for="insuredLastName">Last Name:</label>
                <input
                  id="insuredLastName"
                  v-model="insuranceData.insuredLastName"
                  type="text"
                  class="form-input"
                />
              </div>
            </div>

            <div class="form-group">
              <label for="insuredDob">Date of Birth:</label>
              <input
                id="insuredDob"
                v-model="insuranceData.insuredDob"
                type="date"
                class="form-input"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="effectiveDate">Effective Date:</label>
            <input
              id="effectiveDate"
              v-model="insuranceData.effectiveDate"
              type="date"
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label for="notes">Notes:</label>
            <textarea
              id="notes"
              v-model="insuranceData.notes"
              rows="3"
              class="form-textarea"
              placeholder="Additional notes or special coverage details..."
            ></textarea>
          </div>
        </form>
      </div>

      <div class="modal-footer">
        <button @click="close" class="btn btn-secondary">Cancel</button>
        <button @click="submitInsurance" :disabled="!isValid" class="btn btn-primary">
          Add Insurance
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'save'])

const insuranceData = ref({
  name: '',
  type: '',
  policyNumber: '',
  groupNumber: '',
  bin: '',
  pcn: '',
  relationship: '',
  insuredFirstName: '',
  insuredLastName: '',
  insuredDob: '',
  effectiveDate: '',
  notes: ''
})

const isValid = computed(() => {
  return insuranceData.value.name &&
         insuranceData.value.type &&
         insuranceData.value.policyNumber &&
         insuranceData.value.relationship
})

const handleOverlayClick = (event) => {
  if (event.target === event.currentTarget) {
    close()
  }
}

const close = () => {
  emit('close')
  // Reset form
  insuranceData.value = {
    name: '',
    type: '',
    policyNumber: '',
    groupNumber: '',
    bin: '',
    pcn: '',
    relationship: '',
    insuredFirstName: '',
    insuredLastName: '',
    insuredDob: '',
    effectiveDate: '',
    notes: ''
  }
}

const submitInsurance = () => {
  if (isValid.value) {
    emit('save', { ...insuranceData.value })
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
  max-width: 600px;
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

.insurance-form h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid #e1e5e9;
  padding-bottom: 8px;
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
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

.dependent-info {
  margin-top: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #3498db;
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

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
