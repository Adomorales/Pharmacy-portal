<template>
  <form @submit.prevent="handleSubmit" class="patient-form">
    <div class="form-header">
      <h2>{{ isEditing ? 'Edit Patient' : 'New Patient' }}</h2>
      <p class="form-description">
        {{ isEditing ? 'Update patient information' : 'Enter patient details to create a new patient record' }}
      </p>
    </div>

    <div class="form-sections">
      <!-- Personal Information Section -->
      <div class="form-section">
        <h3>Personal Information</h3>

        <div class="form-row">
          <FormField
            v-model="form.firstName"
            label="First Name"
            placeholder="Enter first name"
            required
            :error-message="errors.firstName"
            class="form-col"
          />

          <FormField
            v-model="form.lastName"
            label="Last Name"
            placeholder="Enter last name"
            required
            :error-message="errors.lastName"
            class="form-col"
          />
        </div>

        <div class="form-row">
          <FormField
            v-model="form.dateOfBirth"
            type="date"
            label="Date of Birth"
            required
            :error-message="errors.dateOfBirth"
            :max="today"
            class="form-col"
          />

          <FormField
            v-model="form.gender"
            type="select"
            label="Gender"
            :options="genderOptions"
            placeholder="Select gender"
            :error-message="errors.gender"
            class="form-col"
          />
        </div>

        <FormField
          v-model="form.email"
          type="email"
          label="Email Address"
          placeholder="patient@example.com"
          :error-message="errors.email"
        />

        <div class="form-row">
          <FormField
            v-model="form.phone"
            type="tel"
            label="Phone Number"
            placeholder="(555) 123-4567"
            :error-message="errors.phone"
            class="form-col"
          />

          <FormField
            v-model="form.emergencyContact"
            type="tel"
            label="Emergency Contact"
            placeholder="(555) 987-6543"
            :error-message="errors.emergencyContact"
            class="form-col"
          />
        </div>
      </div>

      <!-- Address Information Section -->
      <div class="form-section">
        <h3>Address Information</h3>

        <FormField
          v-model="form.address.street"
          label="Street Address"
          placeholder="123 Main Street"
          :error-message="errors['address.street']"
        />

        <div class="form-row">
          <FormField
            v-model="form.address.city"
            label="City"
            placeholder="Enter city"
            :error-message="errors['address.city']"
            class="form-col"
          />

          <FormField
            v-model="form.address.state"
            label="State"
            placeholder="Enter state"
            :error-message="errors['address.state']"
            class="form-col"
          />

          <FormField
            v-model="form.address.zipCode"
            label="ZIP Code"
            placeholder="12345"
            :error-message="errors['address.zipCode']"
            class="form-col"
          />
        </div>
      </div>

      <!-- Medical Information Section -->
      <div class="form-section">
        <h3>Medical Information</h3>

        <FormField
          v-model="form.medicalRecordNumber"
          label="Medical Record Number"
          placeholder="MRN123456"
          :error-message="errors.medicalRecordNumber"
        />

        <FormField
          v-model="form.insuranceInfo"
          type="textarea"
          label="Insurance Information"
          placeholder="Primary insurance provider and policy number"
          :rows="3"
          :error-message="errors.insuranceInfo"
        />

        <FormField
          v-model="form.allergies"
          type="textarea"
          label="Known Allergies"
          placeholder="List any known drug or food allergies"
          :rows="3"
          :error-message="errors.allergies"
        />

        <FormField
          v-model="form.currentMedications"
          type="textarea"
          label="Current Medications"
          placeholder="List current medications and dosages"
          :rows="3"
          :error-message="errors.currentMedications"
        />
      </div>

      <!-- Emergency Contact Section -->
      <div class="form-section">
        <h3>Emergency Contact</h3>

        <div class="form-row">
          <FormField
            v-model="form.emergencyContactName"
            label="Emergency Contact Name"
            placeholder="Enter emergency contact name"
            :error-message="errors.emergencyContactName"
            class="form-col"
          />

          <FormField
            v-model="form.emergencyContactPhone"
            type="tel"
            label="Emergency Contact Phone"
            placeholder="(555) 987-6543"
            :error-message="errors.emergencyContactPhone"
            class="form-col"
          />
        </div>

        <FormField
          v-model="form.emergencyContactRelationship"
          label="Relationship"
          placeholder="Spouse, Parent, Sibling, etc."
          :error-message="errors.emergencyContactRelationship"
        />
      </div>
    </div>

    <!-- Form Actions -->
    <div class="form-actions">
      <button type="button" class="btn btn-secondary" @click="resetForm" :disabled="loading">
        Reset
      </button>

      <div class="primary-actions">
        <button type="button" class="btn btn-outline" @click="saveDraft" :disabled="loading" v-if="!isEditing">
          Save as Draft
        </button>

        <button type="submit" class="btn btn-primary" :disabled="loading || !isValid">
          <span v-if="loading" class="btn-loader"></span>
          {{ isEditing ? 'Update Patient' : 'Create Patient' }}
        </button>
      </div>
    </div>

    <!-- Unsaved Changes Warning -->
    <div v-if="hasUnsavedChanges" class="unsaved-warning">
      You have unsaved changes. Make sure to save before leaving.
    </div>
  </form>
</template>

<script setup>
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePatientStore } from '@/store'
import PatientService from '@/services/PatientService'
import FormField from './FormField.vue'

// Icons would be imported here

const route = useRoute()
const router = useRouter()
const patientStore = usePatientStore()

// Props
const props = defineProps({
  patientId: {
    type: [String, Number],
    default: null
  }
})

// State
const loading = ref(false)
const form = reactive({
  firstName: '',
  lastName: '',
  dateOfBirth: '',
  gender: '',
  email: '',
  phone: '',
  emergencyContact: '',
  address: {
    street: '',
    city: '',
    state: '',
    zipCode: ''
  },
  medicalRecordNumber: '',
  insuranceInfo: '',
  allergies: '',
  currentMedications: '',
  emergencyContactName: '',
  emergencyContactPhone: '',
  emergencyContactRelationship: ''
})

const errors = ref({})
const initialFormData = ref(null)

// Options
const genderOptions = [
  { value: 'M', label: 'Male' },
  { value: 'F', label: 'Female' },
  { value: 'O', label: 'Other' },
  { value: 'P', label: 'Prefer not to say' }
]

// Computed
const isEditing = computed(() => !!props.patientId)

const today = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const hasUnsavedChanges = computed(() => {
  if (!initialFormData.value) return false
  return JSON.stringify(form) !== JSON.stringify(initialFormData.value)
})

const isValid = computed(() => {
  return form.firstName && form.lastName && form.dateOfBirth
})

// Methods
const loadPatient = async (patientId) => {
  try {
    loading.value = true
    const patient = await patientStore.fetchPatientById(patientId)

    // Populate form with patient data
    Object.keys(form).forEach(key => {
      if (key === 'address' && patient[key]) {
        form.address = { ...patient[key] }
      } else if (patient[key] !== undefined) {
        form[key] = patient[key]
      }
    })

    initialFormData.value = JSON.parse(JSON.stringify(form))
  } catch (error) {
    console.error('Error loading patient:', error)
    // Handle error - maybe redirect or show error message
  } finally {
    loading.value = false
  }
}

const validateForm = () => {
  errors.value = {}

  // Required field validation
  if (!form.firstName.trim()) {
    errors.value.firstName = 'First name is required'
  }

  if (!form.lastName.trim()) {
    errors.value.lastName = 'Last name is required'
  }

  if (!form.dateOfBirth) {
    errors.value.dateOfBirth = 'Date of birth is required'
  }

  // Email validation
  if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.value.email = 'Please enter a valid email address'
  }

  // Phone validation
  if (form.phone && !/^[\+]?[1-9][\d]{0,15}$/.test(form.phone.replace(/[\s\-\(\)]/g, ''))) {
    errors.value.phone = 'Please enter a valid phone number'
  }

  return Object.keys(errors.value).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  try {
    loading.value = true
    let result

    if (isEditing.value) {
      result = await patientStore.updatePatient(props.patientId, form)
    } else {
      result = await patientStore.createPatient(form)
    }

    // Success - redirect or show success message
    router.push(`/patients/${result.patientId || result.id}`)

  } catch (error) {
    console.error('Error saving patient:', error)
    // Handle error - show notification or set form errors
    if (error.response?.data?.errors) {
      errors.value = { ...errors.value, ...error.response.data.errors }
    }
  } finally {
    loading.value = false
  }
}

const saveDraft = async () => {
  try {
    loading.value = true
    await patientStore.createPatient({ ...form, status: 'draft' })
    // Show success message
  } catch (error) {
    console.error('Error saving draft:', error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  if (isEditing.value && initialFormData.value) {
    Object.assign(form, JSON.parse(JSON.stringify(initialFormData.value)))
  } else {
    Object.keys(form).forEach(key => {
      if (typeof form[key] === 'object' && form[key] !== null) {
        form[key] = {}
      } else {
        form[key] = ''
      }
    })
  }
  errors.value = {}
}

// Watch for route changes to load patient data
watch(() => props.patientId, (newId) => {
  if (newId) {
    loadPatient(newId)
  }
}, { immediate: true })

// Initialize form if editing
onMounted(() => {
  if (isEditing.value) {
    loadPatient(props.patientId)
  } else {
    initialFormData.value = JSON.parse(JSON.stringify(form))
  }
})
</script>

<style scoped>
.patient-form {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.form-header {
  margin-bottom: 32px;
  text-align: center;
}

.form-header h2 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.form-description {
  margin: 0;
  color: #7f8c8d;
  font-size: 16px;
}

.form-sections {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.form-section {
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  padding: 24px;
}

.form-section h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.form-col {
  flex: 1;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e1e5e9;
}

.primary-actions {
  display: flex;
  gap: 12px;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #3498db;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2980b9;
}

.btn-secondary {
  background: #95a5a6;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background: #7f8c8d;
}

.btn-outline {
  background: transparent;
  color: #3498db;
  border: 1px solid #3498db;
}

.btn-outline:hover:not(:disabled) {
  background: #3498db;
  color: white;
}

.btn-loader {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.unsaved-warning {
  background: #fff3cd;
  color: #856404;
  padding: 12px 16px;
  border-radius: 6px;
  border: 1px solid #ffeaa7;
  margin-top: 20px;
  font-size: 14px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Mobile responsive */
@media (max-width: 768px) {
  .patient-form {
    margin: 0;
    padding: 20px;
    border-radius: 0;
  }

  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .form-actions {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .primary-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
