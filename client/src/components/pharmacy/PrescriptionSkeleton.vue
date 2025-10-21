<template>
  <div class="prescription-skeleton" :class="{ 'collapsed': isCollapsed, 'expanded': !isCollapsed }">
    <!-- Toggle Button -->
    <div class="skeleton-header" @click="toggleCollapse">
      <div class="header-content">
        <PrescriptionIcon />
        <span class="title">{{ isEditing ? 'Editing Prescription' : 'New Prescription' }}</span>
        <StatusBadge :status="prescription?.status || 'draft'" />
      </div>
      <button class="toggle-btn" @click.stop="toggleCollapse">
        {{ isCollapsed ? '→' : '←' }}
      </button>
    </div>

    <!-- Expanded Content -->
    <div v-if="!isCollapsed" class="skeleton-content">
      <!-- Prescription Overview -->
      <div class="prescription-overview">
        <div class="patient-info" v-if="prescription?.patient">
          <h4>Patient</h4>
          <div class="info-item">
            <UserIcon />
            <span>{{ prescription.patient.firstName }} {{ prescription.patient.lastName }}</span>
          </div>
          <div class="info-item">
            <CalendarIcon />
            <span>DOB: {{ formatDate(prescription.patient.dateOfBirth) }}</span>
          </div>
          <div class="info-item" v-if="prescription.patient.phone">
            <PhoneIcon />
            <span>{{ prescription.patient.phone }}</span>
          </div>
        </div>

        <div class="medication-info" v-if="prescription?.medications?.length">
          <h4>Medications</h4>
          <div v-for="med in prescription.medications" :key="med.id" class="medication-item">
            <PillIcon />
            <div class="med-details">
              <strong>{{ med.name }}</strong>
              <span class="dosage">{{ med.dosage }} {{ med.frequency }}</span>
              <span class="sig" v-if="med.sig">{{ med.sig }}</span>
            </div>
          </div>
        </div>

        <div class="prescriber-info" v-if="prescription?.prescriber">
          <h4>Prescriber</h4>
          <div class="info-item">
            <StethoscopeIcon />
            <span>{{ prescription.prescriber.firstName }} {{ prescription.prescriber.lastName }}</span>
          </div>
          <div class="info-item">
            <BadgeIcon />
            <span>NPI: {{ prescription.prescriber.npi }}</span>
          </div>
        </div>
      </div>

      <!-- Progress Indicator -->
      <div class="progress-section">
        <h4>Progress</h4>
        <div class="progress-steps">
          <div
            v-for="step in workflowSteps"
            :key="step.key"
            class="step"
            :class="{
              'completed': isStepCompleted(step.key),
              'current': isStepCurrent(step.key),
              'pending': isStepPending(step.key)
            }"
          >
            <div class="step-icon">
              <component :is="step.icon" />
            </div>
            <span class="step-label">{{ step.label }}</span>
            <div class="step-status">
              <CheckIcon v-if="isStepCompleted(step.key)" />
              <ClockIcon v-else-if="isStepCurrent(step.key)" />
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="quick-actions">
        <button @click="editPatient" class="action-btn" :disabled="!prescription?.patient">
          <UserIcon />
          {{ prescription?.patient ? 'Edit Patient' : 'Add Patient' }}
        </button>
        <button @click="addMedication" class="action-btn" :disabled="!canAddMedication">
          <PlusIcon />
          Add Medication
        </button>
        <button @click="reviewPrescription" class="action-btn" :disabled="!isComplete">
          <EyeIcon />
          Review & Submit
        </button>
      </div>

      <!-- Warnings/Alerts -->
      <div v-if="warnings.length" class="warnings-section">
        <h4>⚠️ Attention Needed</h4>
        <div v-for="warning in warnings" :key="warning.id" class="warning-item">
          <AlertTriangleIcon />
          <span>{{ warning.message }}</span>
        </div>
      </div>

      <!-- Timestamps -->
      <div class="timestamps">
        <small v-if="prescription?.createdAt">
          Created: {{ formatDateTime(prescription.createdAt) }}
        </small>
        <small v-if="prescription?.updatedAt">
          Updated: {{ formatDateTime(prescription.updatedAt) }}
        </small>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { usePrescriptionStore } from '@/store'
import { usePrescriptionWorkflow } from '@/composables/usePrescriptionWorkflow'
import {
  PrescriptionIcon, UserIcon, CalendarIcon, PhoneIcon, PillIcon,
  StethoscopeIcon, BadgeIcon, CheckIcon, ClockIcon, PlusIcon,
  EyeIcon, AlertTriangleIcon
} from '@/components/icons'

const prescriptionStore = usePrescriptionStore()
const workflow = usePrescriptionWorkflow()
const isCollapsed = ref(false)

const prescription = computed(() => prescriptionStore.currentPrescription)

const workflowSteps = [
  { key: 'patient', label: 'Patient Info', icon: 'UserIcon' },
  { key: 'prescriber', label: 'Prescriber', icon: 'StethoscopeIcon' },
  { key: 'medications', label: 'Medications', icon: 'PillIcon' },
  { key: 'review', label: 'Review', icon: 'EyeIcon' }
]

const warnings = computed(() => {
  const alerts = []

  if (!prescription.value?.patient) {
    alerts.push({ id: 'no-patient', message: 'Patient information required' })
  }

  if (!prescription.value?.prescriber) {
    alerts.push({ id: 'no-prescriber', message: 'Prescriber information required' })
  }

  if (!prescription.value?.medications?.length) {
    alerts.push({ id: 'no-medications', message: 'At least one medication required' })
  }

  return alerts
})

const isComplete = computed(() => warnings.value.length === 0)

const canAddMedication = computed(() => prescription.value?.patient && prescription.value?.prescriber)

const isStepCompleted = (step) => {
  return workflow.isStepCompleted(step)
}

const isStepCurrent = (step) => {
  return workflow.currentStep.value === step
}

const isStepPending = (step) => !isStepCompleted(step) && !isStepCurrent(step)

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const editPatient = () => {
  workflow.goToStep('patient')
  console.log('Edit patient')
}

const addMedication = () => {
  if (canAddMedication.value) {
    workflow.goToStep('medications')
    console.log('Add medication')
  }
}

const reviewPrescription = () => {
  if (isComplete.value) {
    workflow.goToStep('review')
    console.log('Review prescription')
  }
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString()
}

const formatDateTime = (date) => {
  return new Date(date).toLocaleString()
}

// Watch for prescription changes and expand if new prescription is created
watch(() => prescription.value?.id, (newId, oldId) => {
  if (newId && !oldId) {
    isCollapsed.value = false
  }
})
</script>

<style scoped>
.prescription-skeleton {
  position: fixed;
  right: 20px;
  top: 100px;
  width: 350px;
  max-height: 80vh;
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  z-index: 1000;
}

.skeleton-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e1e5e9;
  cursor: pointer;
  border-radius: 12px 12px 0 0;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title {
  font-weight: 600;
  color: #2c3e50;
}

.skeleton-content {
  padding: 16px;
  max-height: 60vh;
  overflow-y: auto;
}

.prescription-overview {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.patient-info,
.medication-info,
.prescriber-info {
  background: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
}

.patient-info h4,
.medication-info h4,
.prescriber-info h4 {
  margin: 0 0 8px 0;
  color: #34495e;
  font-size: 14px;
  font-weight: 600;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
  font-size: 13px;
  color: #5a6c7d;
}

.medication-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 8px;
  background: white;
  border-radius: 6px;
  margin-bottom: 6px;
}

.med-details {
  flex: 1;
}

.dosage {
  display: block;
  font-size: 12px;
  color: #7f8c8d;
  margin-top: 2px;
}

.sig {
  display: block;
  font-style: italic;
  font-size: 11px;
  color: #95a5a6;
  margin-top: 2px;
}

.progress-section {
  margin-bottom: 16px;
}

.progress-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
}

.progress-steps::before {
  content: '';
  position: absolute;
  top: 12px;
  left: 0;
  right: 0;
  height: 2px;
  background: #ecf0f1;
  z-index: 1;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  position: relative;
  z-index: 2;
  background: white;
  padding: 0 4px;
}

.step-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.step.completed .step-icon {
  background: #27ae60;
  color: white;
}

.step.current .step-icon {
  background: #3498db;
  color: white;
}

.step.pending .step-icon {
  background: #bdc3c7;
  color: #7f8c8d;
}

.step-label {
  font-size: 10px;
  text-align: center;
  color: #7f8c8d;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}

.action-btn:hover:not(:disabled) {
  background: #2980b9;
}

.action-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}

.warnings-section {
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 16px;
}

.warning-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #856404;
  margin-bottom: 4px;
}

.timestamps {
  border-top: 1px solid #ecf0f1;
  padding-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.timestamps small {
  color: #95a5a6;
  font-size: 11px;
}

/* Collapsed state */
.collapsed {
  width: 60px;
}

.collapsed .skeleton-content {
  display: none;
}

.collapsed .skeleton-header {
  padding: 12px;
  justify-content: center;
}

.collapsed .header-content {
  flex-direction: column;
  gap: 4px;
}

.collapsed .title {
  display: none;
}

.toggle-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: #7f8c8d;
  padding: 4px;
}

@media (max-width: 768px) {
  .prescription-skeleton {
    position: fixed;
    right: 10px;
    top: 10px;
    width: calc(100vw - 20px);
    max-height: 70vh;
  }
}
</style>
