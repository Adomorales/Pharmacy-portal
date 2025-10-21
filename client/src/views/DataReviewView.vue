<template>
  <div class="data-review">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h1>Data Review</h1>
        <p>Review and approve submitted prescriptions</p>
      </div>
      <button @click="$router.push('/dashboard')" class="back-btn">Back to Dashboard</button>
    </div>

    <!-- Filter Controls -->
    <div class="filter-controls">
      <div class="filter-group">
        <label>Status:</label>
        <select v-model="statusFilter" @change="applyFilters" class="filter-select">
          <option value="">All Status</option>
          <option value="pending">Pending Review</option>
          <option value="approved">Approved</option>
          <option value="rejected">Rejected</option>
        </select>
      </div>

      <div class="filter-group">
        <label>Date Range:</label>
        <input
          type="date"
          v-model="dateFrom"
          @change="applyFilters"
          class="filter-input"
        />
        <span>to</span>
        <input
          type="date"
          v-model="dateTo"
          @change="applyFilters"
          class="filter-input"
        />
      </div>

      <div class="filter-group">
        <button @click="clearFilters" class="clear-filters-btn">Clear Filters</button>
      </div>
    </div>

    <!-- Prescription List -->
    <div class="prescription-list">
      <div v-if="filteredPrescriptions.length === 0" class="no-prescriptions">
        <p>No prescriptions found matching your criteria.</p>
      </div>

      <div v-else class="prescriptions-grid">
        <div
          v-for="prescription in filteredPrescriptions"
          :key="prescription.id"
          class="prescription-card"
          :class="{ 'urgent': prescription.isUrgent }"
        >
          <!-- Card Header -->
          <div class="card-header">
            <div class="prescription-id">Rx #{{ prescription.id }}</div>
            <div class="prescription-date">{{ formatDate(prescription.createdAt) }}</div>
          </div>

          <!-- Patient Info -->
          <div class="patient-info">
            <h3>{{ prescription.patient.firstName }} {{ prescription.patient.lastName }}</h3>
            <div class="patient-details">
              <span>DOB: {{ formatDate(prescription.patient.dateOfBirth) }}</span>
              <span>Phone: {{ prescription.patient.phone }}</span>
            </div>
          </div>

          <!-- Prescriber Info -->
          <div class="prescriber-info">
            <strong>Dr. {{ prescription.prescriber.name }}</strong>
            <div class="prescriber-details">
              <span>{{ prescription.prescriber.specialty }}</span>
              <span>NPI: {{ prescription.prescriber.npi }}</span>
            </div>
          </div>

          <!-- Medications -->
          <div class="medications-info">
            <div class="medications-count">
              {{ prescription.medications.length }} medication{{ prescription.medications.length !== 1 ? 's' : '' }}
            </div>
            <div class="medications-list">
              <div
                v-for="medication in prescription.medications.slice(0, 2)"
                :key="medication.name"
                class="medication-item"
              >
                <span class="med-name">{{ medication.name }}</span>
                <span class="med-strength">{{ medication.strength }}</span>
              </div>
              <div v-if="prescription.medications.length > 2" class="more-meds">
                +{{ prescription.medications.length - 2 }} more
              </div>
            </div>
          </div>

          <!-- Status and Actions -->
          <div class="card-footer">
            <div class="status-section">
              <span class="status-badge" :class="getStatusClass(prescription.status)">
                {{ getStatusText(prescription.status) }}
              </span>
              <span v-if="prescription.isUrgent" class="urgent-badge">Urgent</span>
            </div>

            <div class="action-buttons">
              <button @click="viewPrescription(prescription)" class="btn btn-outline">View Details</button>
              <button
                v-if="prescription.status === 'pending'"
                @click="approvePrescription(prescription)"
                class="btn btn-success"
              >
                Approve
              </button>
              <button
                v-if="prescription.status === 'pending'"
                @click="rejectPrescription(prescription)"
                class="btn btn-danger"
              >
                Reject
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Prescription Detail Modal -->
    <div v-if="selectedPrescription" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>Prescription Review</h2>
          <button @click="closeModal" class="close-btn">×</button>
        </div>

        <div class="modal-body">
          <div class="review-layout">
            <!-- Left Side - Prescription Details -->
            <div class="prescription-details">
              <div class="detail-section">
                <h3>Patient Information</h3>
                <div class="detail-card">
                  <div class="detail-row">
                    <span class="label">Name:</span>
                    <span class="value">{{ selectedPrescription.patient.firstName }} {{ selectedPrescription.patient.lastName }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="label">DOB:</span>
                    <span class="value">{{ formatDate(selectedPrescription.patient.dateOfBirth) }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="label">Phone:</span>
                    <span class="value">{{ selectedPrescription.patient.phone }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="label">Address:</span>
                    <span class="value">{{ formatAddress(selectedPrescription.patient.address) }}</span>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <h3>Prescriber Information</h3>
                <div class="detail-card">
                  <div class="detail-row">
                    <span class="label">Name:</span>
                    <span class="value">{{ selectedPrescription.prescriber.name }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="label">NPI:</span>
                    <span class="value">{{ selectedPrescription.prescriber.npi }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="label">Phone:</span>
                    <span class="value">{{ selectedPrescription.prescriber.phone }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="label">DEA:</span>
                    <span class="value">{{ selectedPrescription.prescriber.deaNumber }}</span>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <h3>Medications</h3>
                <div class="medications-detail">
                  <div
                    v-for="medication in selectedPrescription.medications"
                    :key="medication.name"
                    class="medication-detail-card"
                  >
                    <div class="med-header">
                      <strong>{{ medication.name }}</strong>
                      <span class="med-strength">{{ medication.strength }} {{ medication.form }}</span>
                    </div>
                    <div class="med-details">
                      <div class="detail-row">
                        <span class="label">SIG:</span>
                        <span class="value">{{ medication.sig }}</span>
                      </div>
                      <div class="detail-row">
                        <span class="label">Quantity:</span>
                        <span class="value">{{ medication.quantity }}</span>
                      </div>
                      <div class="detail-row">
                        <span class="label">Days Supply:</span>
                        <span class="value">{{ medication.daysSupply }}</span>
                      </div>
                      <div class="detail-row">
                        <span class="label">Refills:</span>
                        <span class="value">{{ medication.refills || 0 }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="detail-section">
                <h3>Insurance Information</h3>
                <div class="detail-card">
                  <div class="detail-row">
                    <span class="label">Insurance:</span>
                    <span class="value">{{ selectedPrescription.insurance?.name || 'No Insurance' }}</span>
                  </div>
                  <div class="detail-row">
                    <span class="label">Group #:</span>
                    <span class="value">{{ selectedPrescription.insurance?.groupNumber || 'N/A' }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Right Side - Prescription Image/Context -->
            <div class="prescription-context">
              <PrescriptionSkeleton />
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="closeModal" class="btn btn-secondary">Close</button>
          <button
            v-if="selectedPrescription.status === 'pending'"
            @click="approvePrescription(selectedPrescription)"
            class="btn btn-success"
          >
            Approve Prescription
          </button>
          <button
            v-if="selectedPrescription.status === 'pending'"
            @click="rejectPrescription(selectedPrescription)"
            class="btn btn-danger"
          >
            Reject Prescription
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PrescriptionSkeleton from '@/components/pharmacy/PrescriptionSkeleton.vue'

const router = useRouter()

// State
const statusFilter = ref('')
const dateFrom = ref('')
const dateTo = ref('')
const selectedPrescription = ref(null)

// Mock prescription data
const prescriptions = ref([
  {
    id: 1,
    patient: {
      firstName: 'John',
      lastName: 'Doe',
      dateOfBirth: '1980-05-15',
      phone: '(555) 123-4567',
      address: { street: '123 Main St', city: 'Anytown', state: 'CA', zipCode: '12345' }
    },
    prescriber: {
      name: 'Dr. Smith',
      specialty: 'Family Medicine',
      npi: '1234567890',
      phone: '(555) 987-6543',
      deaNumber: 'AS1234567'
    },
    medications: [
      {
        name: 'Lisinopril',
        strength: '10mg',
        form: 'Tablet',
        sig: 'Take 1 tablet by mouth daily',
        quantity: 30,
        daysSupply: 30,
        refills: 5
      }
    ],
    insurance: {
      name: 'Blue Cross Blue Shield',
      groupNumber: 'GRP123456'
    },
    status: 'pending',
    isUrgent: false,
    createdAt: '2024-01-15T10:30:00'
  },
  {
    id: 2,
    patient: {
      firstName: 'Jane',
      lastName: 'Smith',
      dateOfBirth: '1975-08-22',
      phone: '(555) 456-7890',
      address: { street: '456 Oak Ave', city: 'Somewhere', state: 'CA', zipCode: '67890' }
    },
    prescriber: {
      name: 'Dr. Johnson',
      specialty: 'Cardiology',
      npi: '0987654321',
      phone: '(555) 321-9876',
      deaNumber: 'BJ7654321'
    },
    medications: [
      {
        name: 'Metformin',
        strength: '500mg',
        form: 'Tablet',
        sig: 'Take 1 tablet twice daily with meals',
        quantity: 60,
        daysSupply: 30,
        refills: 11
      },
      {
        name: 'Atorvastatin',
        strength: '20mg',
        form: 'Tablet',
        sig: 'Take 1 tablet by mouth at bedtime',
        quantity: 30,
        daysSupply: 30,
        refills: 5
      }
    ],
    insurance: null,
    status: 'pending',
    isUrgent: true,
    createdAt: '2024-01-15T09:15:00'
  }
])

// Computed
const filteredPrescriptions = computed(() => {
  let filtered = prescriptions.value

  if (statusFilter.value) {
    filtered = filtered.filter(p => p.status === statusFilter.value)
  }

  if (dateFrom.value) {
    filtered = filtered.filter(p => new Date(p.createdAt) >= new Date(dateFrom.value))
  }

  if (dateTo.value) {
    filtered = filtered.filter(p => new Date(p.createdAt) <= new Date(dateTo.value))
  }

  return filtered
})

// Methods
const applyFilters = () => {
  // Filters are applied through computed property
}

const clearFilters = () => {
  statusFilter.value = ''
  dateFrom.value = ''
  dateTo.value = ''
}

const viewPrescription = (prescription) => {
  selectedPrescription.value = prescription
}

const closeModal = () => {
  selectedPrescription.value = null
}

const approvePrescription = async (prescription) => {
  try {
    // Update prescription status
    prescription.status = 'approved'

    // If modal is open, close it
    if (selectedPrescription.value?.id === prescription.id) {
      closeModal()
    }

    alert('Prescription approved successfully!')
  } catch (error) {
    console.error('Error approving prescription:', error)
    alert('Error approving prescription. Please try again.')
  }
}

const rejectPrescription = async (prescription) => {
  try {
    // Update prescription status
    prescription.status = 'rejected'

    // If modal is open, close it
    if (selectedPrescription.value?.id === prescription.id) {
      closeModal()
    }

    alert('Prescription rejected.')
  } catch (error) {
    console.error('Error rejecting prescription:', error)
    alert('Error rejecting prescription. Please try again.')
  }
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString()
}

const formatAddress = (address) => {
  if (!address) return 'N/A'
  return `${address.street}, ${address.city}, ${address.state} ${address.zipCode}`
}

const getStatusClass = (status) => {
  switch (status) {
    case 'pending': return 'status-warning'
    case 'approved': return 'status-success'
    case 'rejected': return 'status-danger'
    default: return 'status-secondary'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'pending': return 'Pending Review'
    case 'approved': return 'Approved'
    case 'rejected': return 'Rejected'
    default: return 'Unknown'
  }
}

onMounted(() => {
  // Load prescriptions from API
})
</script>

<style scoped>
.data-review {
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

.filter-controls {
  display: flex;
  gap: 20px;
  align-items: center;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-group label {
  color: #2c3e50;
  font-weight: 500;
  font-size: 14px;
}

.filter-select,
.filter-input {
  padding: 8px 12px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  font-size: 14px;
}

.clear-filters-btn {
  padding: 8px 16px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.clear-filters-btn:hover {
  background: #c0392b;
}

.prescriptions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.prescription-card {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
}

.prescription-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.prescription-card.urgent {
  border-left: 4px solid #e74c3c;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.prescription-id {
  font-weight: 600;
  color: #3498db;
}

.prescription-date {
  color: #7f8c8d;
  font-size: 12px;
}

.patient-info {
  margin-bottom: 16px;
}

.patient-info h3 {
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-size: 16px;
}

.patient-details {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #7f8c8d;
}

.prescriber-info {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f8f9fa;
}

.prescriber-info strong {
  color: #2c3e50;
}

.prescriber-details {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #7f8c8d;
  margin-top: 4px;
}

.medications-info {
  margin-bottom: 16px;
}

.medications-count {
  color: #7f8c8d;
  font-size: 12px;
  margin-bottom: 8px;
}

.medications-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.medication-item {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  padding: 4px 0;
}

.med-name {
  font-weight: 500;
  color: #2c3e50;
}

.med-strength {
  color: #7f8c8d;
}

.more-meds {
  color: #3498db;
  font-size: 11px;
  font-style: italic;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
}

.status-badge.status-success {
  background: #d4edda;
  color: #155724;
}

.status-badge.status-warning {
  background: #fff3cd;
  color: #856404;
}

.status-badge.status-danger {
  background: #f8d7da;
  color: #721c24;
}

.urgent-badge {
  padding: 4px 8px;
  background: #ffeaea;
  color: #c0392b;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

.btn-success {
  background: #27ae60;
  color: white;
}

.btn-success:hover:not(:disabled) {
  background: #229954;
}

.btn-danger {
  background: #e74c3c;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background: #c0392b;
}

.btn-secondary {
  background: #95a5a6;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background: #7f8c8d;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 1000px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 30px;
  border-bottom: 1px solid #e1e5e9;
}

.modal-header h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #7f8c8d;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background: #f8f9fa;
}

.modal-body {
  padding: 30px;
}

.review-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 30px;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.detail-card {
  background: #f8f9fa;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  padding: 16px;
}

.medications-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.medication-detail-card {
  background: #f8f9fa;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  padding: 16px;
}

.med-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.med-strength {
  color: #7f8c8d;
  font-size: 14px;
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

.prescription-context {
  position: sticky;
  top: 0;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 30px;
  border-top: 1px solid #e1e5e9;
  background: #f8f9fa;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .data-review {
    padding: 16px;
  }

  .filter-controls {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .prescriptions-grid {
    grid-template-columns: 1fr;
  }

  .review-layout {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .modal-content {
    margin: 10px;
    max-height: 95vh;
  }
}
</style>