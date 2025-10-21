<template>
  <div class="data-entry">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h1>Data Entry</h1>
        <p>Enter and process prescription information</p>
      </div>
      <button @click="$router.push('/dashboard')" class="back-btn">Back to Dashboard</button>
    </div>

    <!-- Progress Steps -->
    <div class="progress-steps">
      <div
        v-for="(step, index) in steps"
        :key="step.key"
        class="step"
        :class="{ active: currentStep === index, completed: currentStep > index }"
      >
        <div class="step-number">{{ index + 1 }}</div>
        <div class="step-title">{{ step.title }}</div>
        <div class="step-indicator"></div>
      </div>
    </div>

    <!-- Step Content -->
    <div class="step-content">
      <!-- Patient Step -->
      <div v-if="currentStep === 0" class="step-panel">
        <div class="panel-header">
          <h2>Patient Information</h2>
          <p>Search for existing patient or create new patient record</p>
        </div>

        <div class="panel-content">
          <!-- Patient Search -->
          <div class="search-section">
            <FormField
              v-model="patientSearch"
              placeholder="Search patients by name, DOB, or phone..."
              :prefix-icon="SearchIcon"
              @input="searchPatients"
              class="patient-search"
            />

            <!-- Search Results -->
            <div v-if="patientSearchResults.length > 0" class="search-results">
              <div
                v-for="patient in patientSearchResults"
                :key="patient.id"
                @click="selectPatient(patient)"
                class="patient-result"
              >
                <div class="patient-info">
                  <strong>{{ patient.firstName }} {{ patient.lastName }}</strong>
                  <div class="patient-details">
                    <span>DOB: {{ formatDate(patient.dateOfBirth) }}</span>
                    <span>Phone: {{ patient.phone }}</span>
                  </div>
                </div>
                <div class="patient-actions">
                  <button class="select-btn">Select</button>
                </div>
              </div>
            </div>

            <!-- No Results -->
            <div v-else-if="patientSearch && !isSearchingPatients" class="no-results">
              <p>No patients found matching your search.</p>
              <button @click="showNewPatient = true" class="btn btn-primary">Create New Patient</button>
            </div>
          </div>

          <!-- Selected Patient Info -->
          <div v-if="selectedPatient" class="selected-patient">
            <h3>Selected Patient</h3>
            <div class="patient-card">
              <div class="patient-header">
                <div class="patient-name">{{ selectedPatient.firstName }} {{ selectedPatient.lastName }}</div>
                <button @click="clearSelectedPatient" class="clear-btn">×</button>
              </div>
              <div class="patient-details">
                <div class="detail-row">
                  <span class="label">Date of Birth:</span>
                  <span class="value">{{ formatDate(selectedPatient.dateOfBirth) }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">Phone:</span>
                  <span class="value">{{ selectedPatient.phone }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">Address:</span>
                  <span class="value">{{ formatAddress(selectedPatient.address) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Prescriber Step -->
      <div v-if="currentStep === 1" class="step-panel">
        <div class="panel-header">
          <h2>Prescriber Information</h2>
          <p>Select the prescribing physician or healthcare provider</p>
        </div>

        <div class="panel-content">
          <!-- Prescriber List -->
          <div class="prescriber-list">
            <div
              v-for="prescriber in prescribers"
              :key="prescriber.id"
              @click="selectPrescriber(prescriber)"
              class="prescriber-item"
              :class="{ selected: selectedPrescriber?.id === prescriber.id }"
            >
              <div class="prescriber-info">
                <strong>{{ prescriber.name }}</strong>
                <div class="prescriber-details">
                  <span>{{ prescriber.specialty }}</span>
                  <span>NPI: {{ prescriber.npi }}</span>
                  <span>{{ prescriber.phone }}</span>
                </div>
              </div>
              <div class="prescriber-status">
                <span class="status-badge" :class="getPrescriberStatusClass(prescriber)">
                  {{ getPrescriberStatusText(prescriber) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Medication Step -->
      <div v-if="currentStep === 2" class="step-panel">
        <div class="panel-header">
          <h2>Medication Information</h2>
          <p>Select medications and enter prescription details</p>
        </div>

        <div class="panel-content">
          <!-- Medication Search and Selection -->
          <MedicationSearch @add-to-prescription="addMedicationToPrescription" />

          <!-- Selected Medications -->
          <div v-if="prescription.medications.length > 0" class="selected-medications">
            <h3>Selected Medications</h3>
            <div class="medications-list">
              <div
                v-for="(medication, index) in prescription.medications"
                :key="index"
                class="medication-item"
              >
                <div class="medication-info">
                  <strong>{{ medication.name }}</strong>
                  <span>{{ medication.strength }} {{ medication.form }}</span>
                </div>
                <div class="medication-details">
                  <div class="sig-input">
                    <label>SIG:</label>
                    <input
                      v-model="medication.sig"
                      placeholder="Take 1 tablet by mouth daily"
                      class="sig-field"
                    />
                  </div>
                  <div class="quantity-input">
                    <label>Quantity:</label>
                    <input
                      v-model.number="medication.quantity"
                      type="number"
                      min="1"
                      class="quantity-field"
                    />
                  </div>
                  <div class="days-input">
                    <label>Days Supply:</label>
                    <input
                      v-model.number="medication.daysSupply"
                      type="number"
                      min="1"
                      class="days-field"
                    />
                  </div>
                </div>
                <button @click="removeMedication(index)" class="remove-med-btn">×</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Insurance Step -->
      <div v-if="currentStep === 3" class="step-panel">
        <div class="panel-header">
          <h2>Insurance Information</h2>
          <p>Select patient's insurance coverage</p>
        </div>

        <div class="panel-content">
          <!-- Insurance Search -->
          <div class="insurance-section">
            <FormField
              v-model="insuranceSearch"
              placeholder="Search insurance plans..."
              :prefix-icon="SearchIcon"
              @input="searchInsurance"
              class="insurance-search"
            />

            <!-- Insurance Options -->
            <div v-if="insuranceOptions.length > 0" class="insurance-options">
              <div
                v-for="insurance in insuranceOptions"
                :key="insurance.id"
                @click="selectInsurance(insurance)"
                class="insurance-option"
              >
                <div class="insurance-info">
                  <strong>{{ insurance.name }}</strong>
                  <span>{{ insurance.type }}</span>
                  <span>{{ insurance.groupNumber }}</span>
                </div>
                <div class="insurance-status">
                  <span class="status-badge success">Active</span>
                </div>
              </div>
            </div>

            <!-- No Insurance -->
            <div v-if="!hasInsurance" class="no-insurance">
              <p>Patient has no insurance coverage</p>
              <button @click="showNewInsurance = true" class="btn btn-outline">Add Insurance</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Review Step -->
      <div v-if="currentStep === 4" class="step-panel">
        <div class="panel-header">
          <h2>Review & Submit</h2>
          <p>Review all information before submitting</p>
        </div>

        <div class="panel-content">
          <div class="review-sections">
            <!-- Patient Review -->
            <div class="review-section">
              <h3>Patient Information</h3>
              <div class="review-card">
                <div class="review-row">
                  <span class="label">Name:</span>
                  <span class="value">{{ selectedPatient?.firstName }} {{ selectedPatient?.lastName }}</span>
                </div>
                <div class="review-row">
                  <span class="label">DOB:</span>
                  <span class="value">{{ formatDate(selectedPatient?.dateOfBirth) }}</span>
                </div>
                <div class="review-row">
                  <span class="label">Phone:</span>
                  <span class="value">{{ selectedPatient?.phone }}</span>
                </div>
              </div>
            </div>

            <!-- Prescriber Review -->
            <div class="review-section">
              <h3>Prescriber Information</h3>
              <div class="review-card">
                <div class="review-row">
                  <span class="label">Name:</span>
                  <span class="value">{{ selectedPrescriber?.name }}</span>
                </div>
                <div class="review-row">
                  <span class="label">NPI:</span>
                  <span class="value">{{ selectedPrescriber?.npi }}</span>
                </div>
                <div class="review-row">
                  <span class="label">Phone:</span>
                  <span class="value">{{ selectedPrescriber?.phone }}</span>
                </div>
              </div>
            </div>

            <!-- Medications Review -->
            <div class="review-section">
              <h3>Medications</h3>
              <div class="review-card">
                <div
                  v-for="medication in prescription.medications"
                  :key="medication.name"
                  class="medication-review"
                >
                  <div class="med-name">{{ medication.name }} {{ medication.strength }}</div>
                  <div class="med-details">
                    <span>SIG: {{ medication.sig }}</span>
                    <span>Qty: {{ medication.quantity }}</span>
                    <span>Days: {{ medication.daysSupply }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Insurance Review -->
            <div class="review-section">
              <h3>Insurance</h3>
              <div class="review-card">
                <div class="review-row">
                  <span class="label">Coverage:</span>
                  <span class="value">{{ selectedInsurance?.name || 'No Insurance' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Navigation Buttons -->
    <div class="navigation-buttons">
      <button
        v-if="currentStep > 0"
        @click="previousStep"
        class="btn btn-secondary"
      >
        Previous
      </button>

      <div class="spacer"></div>

      <button
        v-if="currentStep < steps.length - 1"
        @click="nextStep"
        :disabled="!canProceed"
        class="btn btn-primary"
      >
        Next
      </button>

      <button
        v-if="currentStep === steps.length - 1"
        @click="submitPrescription"
        :disabled="!canSubmit"
        class="btn btn-success"
      >
        Submit Prescription
      </button>
    </div>

    <!-- New Patient Modal -->
    <NewPatientModal
      v-if="showNewPatient"
      @close="showNewPatient = false"
      @save="handleNewPatient"
    />

    <!-- New Insurance Modal -->
    <NewInsuranceModal
      v-if="showNewInsurance"
      @close="showNewInsurance = false"
      @save="handleNewInsurance"
    />

    <!-- Prescription Context -->
    <PrescriptionSkeleton />
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import FormField from '@/components/forms/FormField.vue'
import MedicationSearch from '@/components/pharmacy/MedicationSearch.vue'
import PrescriptionSkeleton from '@/components/pharmacy/PrescriptionSkeleton.vue'
import NewPatientModal from '@/components/modals/NewPatientModal'
import NewInsuranceModal from '@/components/modals/NewInsuranceModal'

// Icons
import { SearchIcon } from '@/components/icons'

const router = useRouter()

// State
const currentStep = ref(0)
const patientSearch = ref('')
const insuranceSearch = ref('')
const selectedPatient = ref(null)
const selectedPrescriber = ref(null)
const selectedInsurance = ref(null)
const prescription = reactive({
  medications: []
})
const showNewPatient = ref(false)
const showNewInsurance = ref(false)
const isSearchingPatients = ref(false)
const patientSearchResults = ref([])
const insuranceOptions = ref([])

// Steps configuration
const steps = [
  { key: 'patient', title: 'Patient' },
  { key: 'prescriber', title: 'Prescriber' },
  { key: 'medication', title: 'Medication' },
  { key: 'insurance', title: 'Insurance' },
  { key: 'review', title: 'Review' }
]

// Mock data
const prescribers = ref([
  {
    id: 1,
    name: 'Dr. John Smith',
    specialty: 'Family Medicine',
    npi: '1234567890',
    phone: '(555) 123-4567',
    status: 'active'
  },
  {
    id: 2,
    name: 'Dr. Sarah Johnson',
    specialty: 'Internal Medicine',
    npi: '0987654321',
    phone: '(555) 987-6543',
    status: 'active'
  }
])

// Computed
const canProceed = computed(() => {
  switch (currentStep.value) {
    case 0: return !!selectedPatient
    case 1: return !!selectedPrescriber
    case 2: return prescription.medications.length > 0
    case 3: return true // Insurance is optional
    default: return false
  }
})

const canSubmit = computed(() => {
  return selectedPatient && selectedPrescriber && prescription.medications.length > 0
})

// Methods
const searchPatients = async (query) => {
  if (!query.trim()) {
    patientSearchResults.value = []
    return
  }

  isSearchingPatients.value = true

  // Mock search - replace with actual API call
  setTimeout(() => {
    patientSearchResults.value = [
      {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        dateOfBirth: '1980-05-15',
        phone: '(555) 123-4567',
        address: { street: '123 Main St', city: 'Anytown', state: 'CA', zipCode: '12345' }
      }
    ].filter(patient =>
      `${patient.firstName} ${patient.lastName}`.toLowerCase().includes(query.toLowerCase())
    )
    isSearchingPatients.value = false
  }, 500)
}

const selectPatient = (patient) => {
  selectedPatient.value = patient
  patientSearch.value = ''
  patientSearchResults.value = []
}

const clearSelectedPatient = () => {
  selectedPatient.value = null
}

const selectPrescriber = (prescriber) => {
  selectedPrescriber.value = prescriber
}

const addMedicationToPrescription = (medication) => {
  prescription.medications.push({
    ...medication,
    sig: '',
    quantity: '',
    daysSupply: ''
  })
}

const removeMedication = (index) => {
  prescription.medications.splice(index, 1)
}

const searchInsurance = (query) => {
  if (!query.trim()) {
    insuranceOptions.value = []
    return
  }

  // Mock search - replace with actual API call
  insuranceOptions.value = [
    {
      id: 1,
      name: 'Blue Cross Blue Shield',
      type: 'PPO',
      groupNumber: 'GRP123456'
    }
  ].filter(insurance =>
    insurance.name.toLowerCase().includes(query.toLowerCase())
  )
}

const selectInsurance = (insurance) => {
  selectedInsurance.value = insurance
  insuranceSearch.value = ''
  insuranceOptions.value = []
}

const nextStep = () => {
  if (currentStep.value < steps.length - 1) {
    currentStep.value++
  }
}

const previousStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const submitPrescription = async () => {
  try {
    // Submit prescription - replace with actual API call
    console.log('Submitting prescription:', {
      patient: selectedPatient.value,
      prescriber: selectedPrescriber.value,
      medications: prescription.medications,
      insurance: selectedInsurance.value
    })

    // Navigate to data review
    router.push('/data-review')
  } catch (error) {
    console.error('Error submitting prescription:', error)
    alert('Error submitting prescription. Please try again.')
  }
}

const handleNewPatient = (patient) => {
  selectedPatient.value = patient
  showNewPatient.value = false
}

const handleNewInsurance = (insurance) => {
  selectedInsurance.value = insurance
  showNewInsurance.value = false
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString()
}

const formatAddress = (address) => {
  if (!address) return ''
  return `${address.street}, ${address.city}, ${address.state} ${address.zipCode}`
}

const getPrescriberStatusClass = (prescriber) => {
  return prescriber.status === 'active' ? 'status-success' : 'status-warning'
}

const getPrescriberStatusText = (prescriber) => {
  return prescriber.status === 'active' ? 'Active' : 'Inactive'
}

onMounted(() => {
  // Load initial data
})
</script>

<style scoped>
.data-entry {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content h1 {
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.header-content p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.back-btn {
  padding: 10px 20px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.back-btn:hover {
  background: #2980b9;
}

.progress-steps {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 20px 0;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  opacity: 0.5;
  transition: opacity 0.3s;
}

.step.active {
  opacity: 1;
}

.step.completed {
  opacity: 0.8;
}

.step-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e1e5e9;
  color: #7f8c8d;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  transition: all 0.3s;
}

.step.active .step-number {
  background: #3498db;
  color: white;
}

.step.completed .step-number {
  background: #27ae60;
  color: white;
}

.step-title {
  font-size: 12px;
  color: #7f8c8d;
  text-align: center;
}

.step-indicator {
  width: 40px;
  height: 2px;
  background: #e1e5e9;
  transition: background-color 0.3s;
}

.step.active ~ .step .step-indicator,
.step.completed .step-indicator {
  background: #27ae60;
}

.step-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-height: 600px;
}

.step-panel {
  padding: 30px;
}

.panel-header {
  margin-bottom: 30px;
  text-align: center;
}

.panel-header h2 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.panel-header p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.search-section {
  max-width: 600px;
  margin: 0 auto;
}

.patient-search {
  margin-bottom: 20px;
}

.search-results {
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  max-height: 300px;
  overflow-y: auto;
}

.patient-result {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f8f9fa;
  cursor: pointer;
  transition: background-color 0.2s;
}

.patient-result:hover {
  background-color: #f8f9fa;
}

.patient-result:last-child {
  border-bottom: none;
}

.patient-details {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #7f8c8d;
  margin-top: 4px;
}

.select-btn {
  padding: 6px 12px;
  background: #27ae60;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.selected-patient {
  margin-top: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.selected-patient h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
}

.patient-card {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  padding: 16px;
}

.patient-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.patient-name {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.clear-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #e74c3c;
  cursor: pointer;
  padding: 4px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.detail-row .label {
  color: #7f8c8d;
  font-weight: 500;
}

.detail-row .value {
  color: #2c3e50;
}

.prescriber-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.prescriber-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.prescriber-item:hover {
  border-color: #3498db;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.1);
}

.prescriber-item.selected {
  border-color: #27ae60;
  background: #f8fff8;
}

.prescriber-details {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #7f8c8d;
  margin-top: 4px;
}

.selected-medications {
  margin-top: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.selected-medications h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
}

.medications-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.medication-item {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.medication-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.medication-details {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 16px;
  margin-top: 12px;
}

.sig-input,
.quantity-input,
.days-input {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sig-input label,
.quantity-input label,
.days-input label {
  font-size: 12px;
  color: #7f8c8d;
  font-weight: 500;
}

.sig-field,
.quantity-field,
.days-field {
  padding: 8px;
  border: 1px solid #e1e5e9;
  border-radius: 4px;
  font-size: 14px;
}

.remove-med-btn {
  background: none;
  border: none;
  color: #e74c3c;
  font-size: 18px;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.remove-med-btn:hover {
  background: #ffeaea;
}

.insurance-options {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.insurance-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.insurance-option:hover {
  border-color: #3498db;
}

.review-sections {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.review-section h3 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 16px;
}

.review-card {
  background: #f8f9fa;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  padding: 16px;
}

.review-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.medication-review {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e1e5e9;
}

.medication-review:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.med-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.med-details {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #7f8c8d;
}

.navigation-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.spacer {
  flex: 1;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn:disabled {
  opacity: 0.5;
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

.btn-success {
  background: #27ae60;
  color: white;
}

.btn-success:hover:not(:disabled) {
  background: #229954;
}

.btn-outline {
  background: transparent;
  color: #3498db;
  border: 1px solid #3498db;
}

.btn-outline:hover {
  background: #3498db;
  color: white;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .data-entry {
    padding: 16px;
  }

  .progress-steps {
    flex-wrap: wrap;
    gap: 12px;
  }

  .review-sections {
    grid-template-columns: 1fr;
  }

  .medication-details {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .navigation-buttons {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
