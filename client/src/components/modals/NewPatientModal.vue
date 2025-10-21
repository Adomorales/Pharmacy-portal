<template>
  <div v-if="visible" class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>Add New Patient</h3>
        <button @click="close" class="close-btn">&times;</button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="submitPatient" class="patient-form">
          <div class="form-row">
            <div class="form-group">
              <label for="firstName">First Name:</label>
              <input
                id="firstName"
                v-model="patientData.firstName"
                type="text"
                required
                class="form-input"
                placeholder="Enter first name"
              />
            </div>

            <div class="form-group">
              <label for="lastName">Last Name:</label>
              <input
                id="lastName"
                v-model="patientData.lastName"
                type="text"
                required
                class="form-input"
                placeholder="Enter last name"
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="dateOfBirth">Date of Birth:</label>
              <input
                id="dateOfBirth"
                v-model="patientData.dateOfBirth"
                type="date"
                required
                class="form-input"
              />
            </div>

            <div class="form-group">
              <label for="gender">Gender:</label>
              <select
                id="gender"
                v-model="patientData.gender"
                required
                class="form-select"
              >
                <option value="">Select Gender</option>
                <option value="male">Male</option>
                <option value="female">Female</option>
                <option value="other">Other</option>
                <option value="prefer-not-to-say">Prefer not to say</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label for="phone">Phone Number:</label>
            <input
              id="phone"
              v-model="patientData.phone"
              type="tel"
              required
              class="form-input"
              placeholder="(555) 123-4567"
            />
          </div>

          <div class="form-group">
            <label for="email">Email:</label>
            <input
              id="email"
              v-model="patientData.email"
              type="email"
              class="form-input"
              placeholder="patient@example.com"
            />
          </div>

          <div class="form-group">
            <label for="address">Address:</label>
            <textarea
              id="address"
              v-model="patientData.address"
              rows="3"
              class="form-textarea"
              placeholder="Street address, city, state, ZIP code"
            ></textarea>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="emergencyContact">Emergency Contact:</label>
              <input
                id="emergencyContact"
                v-model="patientData.emergencyContact"
                type="text"
                class="form-input"
                placeholder="Contact name"
              />
            </div>

            <div class="form-group">
              <label for="emergencyPhone">Emergency Phone:</label>
              <input
                id="emergencyPhone"
                v-model="patientData.emergencyPhone"
                type="tel"
                class="form-input"
                placeholder="(555) 987-6543"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="allergies">Allergies:</label>
            <textarea
              id="allergies"
              v-model="patientData.allergies"
              rows="3"
              class="form-textarea"
              placeholder="List any known allergies or adverse reactions"
            ></textarea>
          </div>

          <div class="form-group">
            <label for="medicalConditions">Medical Conditions:</label>
            <textarea
              id="medicalConditions"
              v-model="patientData.medicalConditions"
              rows="3"
              class="form-textarea"
              placeholder="Current medical conditions or relevant medical history"
            ></textarea>
          </div>

          <div class="form-group">
            <label for="notes">Notes:</label>
            <textarea
              id="notes"
              v-model="patientData.notes"
              rows="3"
              class="form-textarea"
              placeholder="Additional notes or special considerations"
            ></textarea>
          </div>
        </form>
      </div>

      <div class="modal-footer">
        <button @click="close" class="btn btn-secondary">Cancel</button>
        <button @click="submitPatient" :disabled="!isValid" class="btn btn-primary">
          Add Patient
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

const patientData = ref({
  firstName: '',
  lastName: '',
  dateOfBirth: '',
  gender: '',
  phone: '',
  email: '',
  address: '',
  emergencyContact: '',
  emergencyPhone: '',
  allergies: '',
  medicalConditions: '',
  notes: ''
})

const isValid = computed(() => {
  return patientData.value.firstName &&
         patientData.value.lastName &&
         patientData.value.dateOfBirth &&
         patientData.value.gender &&
         patientData.value.phone
})

const handleOverlayClick = (event) => {
  if (event.target === event.currentTarget) {
    close()
  }
}

const close = () => {
  emit('close')
  // Reset form
  patientData.value = {
    firstName: '',
    lastName: '',
    dateOfBirth: '',
    gender: '',
    phone: '',
    email: '',
    address: '',
    emergencyContact: '',
    emergencyPhone: '',
    allergies: '',
    medicalConditions: '',
    notes: ''
  }
}

const submitPatient = () => {
  if (isValid.value) {
    emit('save', { ...patientData.value })
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
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

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
