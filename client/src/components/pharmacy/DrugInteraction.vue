<template>
  <div class="drug-interaction">
    <!-- Header -->
    <div class="interaction-header">
      <h3>Drug Interaction Checker</h3>
      <p class="interaction-subtitle">Check for potential drug interactions and contraindications</p>
    </div>

    <!-- Drug Selection -->
    <div class="drug-selection">
      <div class="selection-section">
        <h4>Add Medications</h4>
        <div class="drug-input-container">
          <FormField
            v-model="currentDrugSearch"
            placeholder="Search for medication..."
            :prefix-icon="SearchIcon"
            @input="searchMedications"
            class="drug-search-field"
          />

          <!-- Search Results -->
          <div v-if="medicationSearchResults.length > 0" class="search-results">
            <button
              v-for="medication in medicationSearchResults"
              :key="medication.id"
              @click="addMedication(medication)"
              class="search-result-item"
            >
              <div class="medication-info">
                <strong>{{ medication.name }}</strong>
                <span class="generic">{{ medication.genericName }}</span>
              </div>
              <PlusIcon class="add-icon" />
            </button>
          </div>
        </div>
      </div>

      <!-- Selected Medications -->
      <div class="selected-medications">
        <h4>Selected Medications ({{ selectedMedications.length }})</h4>

        <div v-if="selectedMedications.length === 0" class="empty-selection">
          <PillIcon class="empty-icon" />
          <p>Add medications to check for interactions</p>
        </div>

        <div v-else class="medications-list">
          <div
            v-for="(medication, index) in selectedMedications"
            :key="medication.id"
            class="selected-medication"
          >
            <div class="medication-info">
              <div class="medication-details">
                <strong class="medication-name">{{ medication.name }}</strong>
                <span class="generic-name">{{ medication.genericName }}</span>
                <span class="strength" v-if="medication.strength">{{ medication.strength }}</span>
              </div>
              <button @click="removeMedication(index)" class="remove-btn">
                <XIcon />
              </button>
            </div>
          </div>
        </div>

        <button
          v-if="selectedMedications.length >= 2"
          @click="checkInteractions"
          :disabled="isChecking"
          class="check-btn"
        >
          <span v-if="isChecking" class="btn-loader"></span>
          Check for Interactions
        </button>
      </div>
    </div>

    <!-- Patient Context -->
    <div v-if="selectedMedications.length > 0" class="patient-context">
      <h4>Patient Information</h4>
      <div class="context-options">
        <label class="context-option">
          <input
            type="radio"
            value="adult"
            v-model="patientContext.age"
            @change="updateInteractions"
          />
          Adult (18+ years)
        </label>
        <label class="context-option">
          <input
            type="radio"
            value="pediatric"
            v-model="patientContext.age"
            @change="updateInteractions"
          />
          Pediatric (< 18 years)
        </label>
        <label class="context-option">
          <input
            type="radio"
            value="geriatric"
            v-model="patientContext.age"
            @change="updateInteractions"
          />
          Geriatric (65+ years)
        </label>
      </div>

      <div class="context-options">
        <label class="context-option">
          <input
            type="checkbox"
            v-model="patientContext.pregnant"
            @change="updateInteractions"
          />
          Pregnant
        </label>
        <label class="context-option">
          <input
            type="checkbox"
            v-model="patientContext.renalImpairment"
            @change="updateInteractions"
          />
          Renal Impairment
        </label>
        <label class="context-option">
          <input
            type="checkbox"
            v-model="patientContext.hepaticImpairment"
            @change="updateInteractions"
          />
          Hepatic Impairment
        </label>
      </div>
    </div>

    <!-- Interaction Results -->
    <div v-if="interactions.length > 0" class="interaction-results">
      <div class="results-header">
        <h4>Interaction Results</h4>
        <div class="results-summary">
          <span class="severity-count" :class="`severity-${getMaxSeverity()}`">
            {{ getSeverityText(getMaxSeverity()) }}
          </span>
        </div>
      </div>

      <div class="interactions-list">
        <div
          v-for="interaction in interactions"
          :key="interaction.id"
          class="interaction-item"
          :class="`severity-${interaction.severity}`"
        >
          <div class="interaction-header">
            <div class="severity-badge" :class="`severity-${interaction.severity}`">
              <AlertTriangleIcon />
              {{ getSeverityText(interaction.severity) }}
            </div>
            <div class="interaction-drugs">
              <span class="drug-1">{{ interaction.drug1 }}</span>
              <span class="interaction-symbol">+</span>
              <span class="drug-2">{{ interaction.drug2 }}</span>
            </div>
          </div>

          <div class="interaction-details">
            <h5>{{ interaction.description }}</h5>
            <p>{{ interaction.clinicalSignificance }}</p>

            <div class="interaction-info">
              <div class="info-section">
                <strong>Mechanism:</strong>
                <span>{{ interaction.mechanism }}</span>
              </div>

              <div class="info-section">
                <strong>Management:</strong>
                <span>{{ interaction.management }}</span>
              </div>
            </div>

            <div v-if="interaction.references.length > 0" class="interaction-references">
              <strong>References:</strong>
              <ul>
                <li v-for="ref in interaction.references" :key="ref">
                  <a :href="ref.url" target="_blank">{{ ref.title }}</a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- No Interactions -->
    <div v-else-if="hasChecked && interactions.length === 0" class="no-interactions">
      <CheckCircleIcon class="success-icon" />
      <h4>No interactions found</h4>
      <p>The selected medications appear to be safe to use together.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import FormField from '@/components/forms/FormField.vue'
import { SearchIcon, XIcon, PlusIcon, AlertTriangleIcon, CheckCircleIcon, PillIcon } from '@/components/icons'

// State
const currentDrugSearch = ref('')
const medicationSearchResults = ref([])
const selectedMedications = ref([])
const isChecking = ref(false)
const hasChecked = ref(false)
const interactions = ref([])
const patientContext = ref({
  age: 'adult',
  pregnant: false,
  renalImpairment: false,
  hepaticImpairment: false
})

// Computed
const canCheckInteractions = computed(() => selectedMedications.value.length >= 2)

// Methods
const searchMedications = async (query) => {
  if (!query.trim()) {
    medicationSearchResults.value = []
    return
  }

  // Mock search - replace with actual API call
  medicationSearchResults.value = [
    { id: 1, name: 'Warfarin', genericName: 'Warfarin Sodium', strength: '5mg' },
    { id: 2, name: 'Aspirin', genericName: 'Acetylsalicylic Acid', strength: '81mg' },
    { id: 3, name: 'Ibuprofen', genericName: 'Ibuprofen', strength: '400mg' },
    { id: 4, name: 'Amoxicillin', genericName: 'Amoxicillin Trihydrate', strength: '500mg' }
  ].filter(med =>
    med.name.toLowerCase().includes(query.toLowerCase()) ||
    med.genericName.toLowerCase().includes(query.toLowerCase())
  )
}

const addMedication = (medication) => {
  if (!selectedMedications.value.find(m => m.id === medication.id)) {
    selectedMedications.value.push(medication)
  }
  currentDrugSearch.value = ''
  medicationSearchResults.value = []
}

const removeMedication = (index) => {
  selectedMedications.value.splice(index, 1)
  interactions.value = []
  hasChecked.value = false
}

const checkInteractions = async () => {
  if (selectedMedications.value.length < 2) return

  isChecking.value = true

  try {
    // Simulate API call - replace with actual service call
    await new Promise(resolve => setTimeout(resolve, 1500))

    // Mock interactions - replace with actual API response
    interactions.value = [
      {
        id: 1,
        drug1: 'Warfarin',
        drug2: 'Aspirin',
        severity: 'major',
        description: 'Increased risk of bleeding',
        clinicalSignificance: 'Concurrent use may increase the risk of bleeding due to additive anticoagulant effects.',
        mechanism: 'Pharmacodynamic interaction - both drugs affect platelet function and coagulation',
        management: 'Monitor INR closely and adjust warfarin dose as needed. Consider alternative analgesic.',
        references: [
          { title: 'Warfarin and Aspirin Interaction', url: 'https://example.com/ref1' }
        ]
      },
      {
        id: 2,
        drug1: 'Amoxicillin',
        drug2: 'Warfarin',
        severity: 'moderate',
        description: 'Possible reduction in warfarin efficacy',
        clinicalSignificance: 'Antibiotics may alter gut flora and reduce vitamin K production, potentially affecting warfarin anticoagulation.',
        mechanism: 'Alteration of gut flora affecting vitamin K absorption',
        management: 'Monitor INR more frequently during antibiotic therapy and for several days after discontinuation.',
        references: [
          { title: 'Antibiotic-Warfarin Interactions', url: 'https://example.com/ref2' }
        ]
      }
    ]

    hasChecked.value = true
  } catch (error) {
    console.error('Error checking interactions:', error)
  } finally {
    isChecking.value = false
  }
}

const updateInteractions = () => {
  if (hasChecked.value) {
    checkInteractions()
  }
}

const getMaxSeverity = () => {
  const severities = interactions.value.map(i => i.severity)
  if (severities.includes('major')) return 'major'
  if (severities.includes('moderate')) return 'moderate'
  if (severities.includes('minor')) return 'minor'
  return 'unknown'
}

const getSeverityText = (severity) => {
  const texts = {
    major: 'Major Interaction',
    moderate: 'Moderate Interaction',
    minor: 'Minor Interaction',
    unknown: 'Unknown Severity'
  }
  return texts[severity] || 'Unknown'
}

// Watch for medication changes
watch(selectedMedications, () => {
  interactions.value = []
  hasChecked.value = false
})
</script>

<style scoped>
.drug-interaction {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.interaction-header {
  padding: 24px;
  border-bottom: 1px solid #e1e5e9;
  background: #f8f9fa;
}

.interaction-header h3 {
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.interaction-subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.drug-selection {
  padding: 24px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.selection-section h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.drug-input-container {
  position: relative;
}

.drug-search-field {
  margin-bottom: 8px;
}

.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e1e5e9;
  border-top: none;
  border-radius: 0 0 6px 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-height: 200px;
  overflow-y: auto;
  z-index: 100;
}

.search-result-item {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border: none;
  background: none;
  text-align: left;
  cursor: pointer;
  transition: background-color 0.2s;
  border-bottom: 1px solid #f8f9fa;
}

.search-result-item:hover {
  background-color: #f8f9fa;
}

.search-result-item:last-child {
  border-bottom: none;
}

.medication-info {
  flex: 1;
}

.medication-name {
  display: block;
  color: #2c3e50;
  font-weight: 500;
}

.generic {
  display: block;
  color: #7f8c8d;
  font-size: 12px;
}

.add-icon {
  color: #27ae60;
}

.selected-medications {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  height: fit-content;
}

.selected-medications h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.empty-selection {
  text-align: center;
  color: #7f8c8d;
  padding: 20px;
}

.empty-icon {
  width: 32px;
  height: 32px;
  margin-bottom: 8px;
  color: #bdc3c7;
}

.medications-list {
  margin-bottom: 16px;
}

.selected-medication {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 8px;
}

.selected-medication:last-child {
  margin-bottom: 0;
}

.medication-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.medication-details {
  flex: 1;
}

.medication-name {
  display: block;
  color: #2c3e50;
  font-weight: 500;
  margin-bottom: 2px;
}

.generic-name {
  display: block;
  color: #7f8c8d;
  font-size: 12px;
  margin-bottom: 2px;
}

.strength {
  display: block;
  color: #95a5a6;
  font-size: 11px;
}

.remove-btn {
  background: none;
  border: none;
  color: #e74c3c;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.remove-btn:hover {
  background-color: #ffeaea;
}

.check-btn {
  width: 100%;
  padding: 12px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.check-btn:hover:not(:disabled) {
  background: #2980b9;
}

.check-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}

.patient-context {
  padding: 24px;
  border-top: 1px solid #e1e5e9;
  background: #f8f9fa;
}

.patient-context h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.context-options {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.context-option {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #2c3e50;
}

.context-option input[type="radio"],
.context-option input[type="checkbox"] {
  margin: 0;
}

.interaction-results {
  padding: 24px;
  border-top: 1px solid #e1e5e9;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.results-header h4 {
  margin: 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.severity-count {
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.severity-major {
  background: #ffeaea;
  color: #c0392b;
}

.severity-moderate {
  background: #fff3cd;
  color: #856404;
}

.severity-minor {
  background: #e8f5e8;
  color: #2d5a2d;
}

.interactions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.interaction-item {
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  padding: 16px;
  background: white;
}

.interaction-item.severity-major {
  border-left: 4px solid #e74c3c;
}

.interaction-item.severity-moderate {
  border-left: 4px solid #f39c12;
}

.interaction-item.severity-minor {
  border-left: 4px solid #27ae60;
}

.interaction-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.severity-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
}

.severity-badge.severity-major {
  background: #ffeaea;
  color: #c0392b;
}

.severity-badge.severity-moderate {
  background: #fff3cd;
  color: #856404;
}

.severity-badge.severity-minor {
  background: #e8f5e8;
  color: #2d5a2d;
}

.interaction-drugs {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.drug-1,
.drug-2 {
  font-weight: 600;
  color: #2c3e50;
}

.interaction-symbol {
  color: #7f8c8d;
}

.interaction-details h5 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
}

.interaction-details p {
  margin: 0 0 12px 0;
  color: #5a6c7d;
  font-size: 13px;
  line-height: 1.4;
}

.interaction-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 12px;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-section strong {
  color: #2c3e50;
  font-size: 12px;
  font-weight: 600;
}

.info-section span {
  color: #5a6c7d;
  font-size: 12px;
  line-height: 1.3;
}

.interaction-references {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e1e5e9;
}

.interaction-references strong {
  color: #2c3e50;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 8px;
  display: block;
}

.interaction-references ul {
  margin: 0;
  padding-left: 20px;
}

.interaction-references li {
  margin-bottom: 4px;
  font-size: 11px;
}

.interaction-references a {
  color: #3498db;
  text-decoration: none;
}

.interaction-references a:hover {
  text-decoration: underline;
}

.no-interactions {
  padding: 40px 24px;
  text-align: center;
  color: #27ae60;
}

.success-icon {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
}

.btn-loader {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Mobile responsive */
@media (max-width: 768px) {
  .drug-selection {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .context-options {
    flex-direction: column;
    gap: 8px;
  }

  .interaction-info {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .results-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
}
</style>
