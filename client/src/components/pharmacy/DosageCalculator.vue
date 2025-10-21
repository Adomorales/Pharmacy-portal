<template>
  <div class="dosage-calculator">
    <!-- Header -->
    <div class="calculator-header">
      <h3>Dosage Calculator</h3>
      <p class="calculator-subtitle">Calculate medication dosages based on patient parameters</p>
    </div>

    <!-- Medication Selection -->
    <div class="medication-selection">
      <FormField
        v-model="selectedMedication"
        type="select"
        label="Select Medication"
        :options="medicationOptions"
        placeholder="Choose a medication"
        @change="onMedicationChange"
      />
    </div>

    <!-- Patient Parameters -->
    <div class="patient-parameters">
      <h4>Patient Information</h4>

      <div class="parameters-grid">
        <FormField
          v-model="patient.age"
          type="number"
          label="Age"
          placeholder="Years"
          :min="0"
          :max="150"
          class="param-field"
        />

        <FormField
          v-model="patient.weight"
          type="number"
          label="Weight"
          placeholder="kg"
          :min="0"
          :max="500"
          :step="0.1"
          class="param-field"
        />

        <FormField
          v-model="patient.height"
          type="number"
          label="Height"
          placeholder="cm"
          :min="0"
          :max="300"
          class="param-field"
        />

        <FormField
          v-model="patient.creatinineClearance"
          type="number"
          label="Creatinine Clearance"
          placeholder="mL/min"
          :min="0"
          :max="200"
          :step="0.1"
          class="param-field"
        />
      </div>

      <div class="patient-options">
        <label class="patient-option">
          <input
            type="radio"
            value="adult"
            v-model="patient.category"
          />
          Adult
        </label>
        <label class="patient-option">
          <input
            type="radio"
            value="pediatric"
            v-model="patient.category"
          />
          Pediatric
        </label>
        <label class="patient-option">
          <input
            type="radio"
            value="geriatric"
            v-model="patient.category"
          />
          Geriatric
        </label>
      </div>

      <div class="condition-flags">
        <label class="condition-flag">
          <input
            type="checkbox"
            v-model="patient.conditions.renal"
          />
          Renal Impairment
        </label>
        <label class="condition-flag">
          <input
            type="checkbox"
            v-model="patient.conditions.hepatic"
          />
          Hepatic Impairment
        </label>
        <label class="condition-flag">
          <input
            type="checkbox"
            v-model="patient.conditions.obese"
          />
          Obese (BMI > 30)
        </label>
      </div>
    </div>

    <!-- Calculation Method -->
    <div v-if="calculationMethod" class="calculation-method">
      <h4>Calculation Method</h4>
      <div class="method-info">
        <div class="method-details">
          <strong>{{ calculationMethod.name }}</strong>
          <p>{{ calculationMethod.description }}</p>
        </div>
        <div class="method-formula">
          <strong>Formula:</strong>
          <div class="formula">{{ calculationMethod.formula }}</div>
        </div>
      </div>
    </div>

    <!-- Dosage Results -->
    <div v-if="dosageResult" class="dosage-results">
      <h4>Dosage Calculation</h4>

      <div class="results-summary">
        <div class="result-primary">
          <div class="calculated-dose">
            <span class="dose-value">{{ formatNumber(dosageResult.dose) }}</span>
            <span class="dose-unit">{{ dosageResult.unit }}</span>
          </div>
          <div class="frequency">{{ dosageResult.frequency }}</div>
        </div>

        <div class="result-details">
          <div class="detail-item">
            <span class="detail-label">Total Daily Dose:</span>
            <span class="detail-value">{{ formatNumber(dosageResult.totalDaily) }} {{ dosageResult.unit }}/day</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">BSA:</span>
            <span class="detail-value">{{ formatNumber(dosageResult.bsa) }} m²</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">Creatinine Clearance:</span>
            <span class="detail-value">{{ formatNumber(dosageResult.creatinineClearance) }} mL/min</span>
          </div>
        </div>
      </div>

      <!-- Dose Adjustments -->
      <div v-if="dosageResult.adjustments.length > 0" class="dose-adjustments">
        <h5>Dose Adjustments</h5>
        <div class="adjustments-list">
          <div
            v-for="adjustment in dosageResult.adjustments"
            :key="adjustment.reason"
            class="adjustment-item"
          >
            <div class="adjustment-reason">{{ adjustment.reason }}</div>
            <div class="adjustment-change">
              {{ adjustment.original }} → {{ adjustment.adjusted }} {{ dosageResult.unit }}
            </div>
          </div>
        </div>
      </div>

      <!-- Warnings -->
      <div v-if="dosageResult.warnings.length > 0" class="calculation-warnings">
        <h5>⚠️ Warnings</h5>
        <div class="warnings-list">
          <div
            v-for="warning in dosageResult.warnings"
            :key="warning"
            class="warning-item"
          >
            {{ warning }}
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Calculations -->
    <div class="quick-calculations">
      <h4>Quick Calculations</h4>

      <div class="quick-calc-grid">
        <button
          v-for="calc in quickCalculations"
          :key="calc.key"
          @click="quickCalculate(calc)"
          class="quick-calc-btn"
          :disabled="!canUseQuickCalc(calc)"
        >
          <div class="calc-icon">
            <component :is="calc.icon" />
          </div>
          <div class="calc-info">
            <div class="calc-title">{{ calc.title }}</div>
            <div class="calc-description">{{ calc.description }}</div>
          </div>
        </button>
      </div>
    </div>

    <!-- References -->
    <div v-if="references.length > 0" class="calculator-references">
      <h4>References</h4>
      <ul class="references-list">
        <li v-for="ref in references" :key="ref.id">
          <a :href="ref.url" target="_blank">{{ ref.title }}</a>
          <span class="ref-year">({{ ref.year }})</span>
        </li>
      </ul>
    </div>

    <!-- Action Buttons -->
    <div class="calculator-actions">
      <button @click="calculateDosage" :disabled="isCalculating" class="btn btn-primary">
        <span v-if="isCalculating" class="btn-loader"></span>
        Calculate Dosage
      </button>

      <button @click="resetCalculator" class="btn btn-secondary">
        Reset
      </button>

      <button
        v-if="dosageResult"
        @click="addToPrescription"
        class="btn btn-success"
      >
        Add to Prescription
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import FormField from '@/components/forms/FormField.vue'
import { CalculatorIcon, ActivityIcon, UserIcon, AlertTriangleIcon } from '@/components/icons'

// State
const selectedMedication = ref('')
const isCalculating = ref(false)
const patient = ref({
  age: '',
  weight: '',
  height: '',
  creatinineClearance: '',
  category: 'adult',
  conditions: {
    renal: false,
    hepatic: false,
    obese: false
  }
})

const dosageResult = ref(null)

// Medication options (mock data - replace with actual medications)
const medicationOptions = [
  { value: 'vancomycin', label: 'Vancomycin' },
  { value: 'gentamicin', label: 'Gentamicin' },
  { value: 'amoxicillin', label: 'Amoxicillin' },
  { value: 'ibuprofen', label: 'Ibuprofen' }
]

// Calculation methods for different medications
const calculationMethods = {
  vancomycin: {
    name: 'Vancomycin (Trough-based dosing)',
    description: 'Based on patient weight and renal function',
    formula: 'Loading dose: 25-30 mg/kg, Maintenance dose: 15-20 mg/kg q8-12h',
    category: 'antibiotic'
  },
  gentamicin: {
    name: 'Gentamicin (Once daily dosing)',
    description: 'High-dose extended interval dosing for adults',
    formula: '5-7 mg/kg IV q24h',
    category: 'antibiotic'
  },
  amoxicillin: {
    name: 'Amoxicillin (Standard dosing)',
    description: 'Based on weight and indication',
    formula: '20-90 mg/kg/day divided q8-12h',
    category: 'antibiotic'
  }
}

// Quick calculation options
const quickCalculations = [
  {
    key: 'bsa',
    title: 'Body Surface Area',
    description: 'Calculate BSA using Mosteller formula',
    icon: ActivityIcon,
    requires: ['height', 'weight']
  },
  {
    key: 'creatinine',
    title: 'Creatinine Clearance',
    description: 'Estimate using Cockcroft-Gault formula',
    icon: CalculatorIcon,
    requires: ['age', 'weight', 'creatinine']
  },
  {
    key: 'bmi',
    title: 'BMI Calculation',
    description: 'Body Mass Index calculation',
    icon: UserIcon,
    requires: ['height', 'weight']
  }
]

// References
const references = [
  { id: 1, title: 'Lexicomp Drug Information', url: 'https://example.com/lexicomp', year: '2024' },
  { id: 2, title: 'Micromedex', url: 'https://example.com/micromedex', year: '2024' }
]

// Computed
const calculationMethod = computed(() => {
  return selectedMedication.value ? calculationMethods[selectedMedication.value] : null
})

const canCalculate = computed(() => {
  return selectedMedication.value && patient.value.weight && patient.value.age
})

const canUseQuickCalc = (calc) => {
  return calc.requires.every(req => patient.value[req])
}

// Methods
const onMedicationChange = () => {
  dosageResult.value = null
}

const calculateDosage = async () => {
  if (!canCalculate.value) return

  isCalculating.value = true

  try {
    // Simulate calculation - replace with actual calculation logic
    await new Promise(resolve => setTimeout(resolve, 1000))

    // Mock calculation result - replace with actual calculation
    const weight = parseFloat(patient.value.weight)
    const age = parseInt(patient.value.age)

    let baseDose = 0
    let frequency = ''

    switch (selectedMedication.value) {
      case 'vancomycin':
        baseDose = weight * 15 // 15 mg/kg maintenance dose
        frequency = 'q12h'
        break
      case 'gentamicin':
        baseDose = weight * 5 // 5 mg/kg once daily
        frequency = 'q24h'
        break
      case 'amoxicillin':
        baseDose = weight * 40 // 40 mg/kg/day divided doses
        frequency = 'q8h'
        break
      case 'ibuprofen':
        baseDose = weight * 10 // 10 mg/kg per dose
        frequency = 'q6-8h'
        break
    }

    // Calculate BSA if height is provided
    let bsa = null
    if (patient.value.height && patient.value.weight) {
      bsa = calculateBSA(patient.value.height, patient.value.weight)
    }

    // Calculate creatinine clearance if needed
    let creatinineClearance = null
    if (patient.value.age && patient.value.weight && patient.value.creatinineClearance) {
      creatinineClearance = parseFloat(patient.value.creatinineClearance)
    }

    dosageResult.value = {
      dose: baseDose,
      unit: 'mg',
      frequency: frequency,
      totalDaily: baseDose * (24 / parseInt(frequency.replace('q', '').replace('h', ''))),
      bsa: bsa,
      creatinineClearance: creatinineClearance,
      adjustments: [],
      warnings: []
    }

    // Add warnings based on conditions
    if (patient.value.conditions.renal) {
      dosageResult.value.warnings.push('Dose adjustment may be required for renal impairment')
    }

    if (patient.value.conditions.obese && ['vancomycin', 'gentamicin'].includes(selectedMedication.value)) {
      dosageResult.value.adjustments.push({
        reason: 'Obesity adjustment',
        original: baseDose,
        adjusted: baseDose * 0.8
      })
    }

  } catch (error) {
    console.error('Calculation error:', error)
  } finally {
    isCalculating.value = false
  }
}

const calculateBSA = (height, weight) => {
  // Mosteller formula: BSA = sqrt(height * weight / 3600)
  const heightM = height / 100
  return Math.sqrt((heightM * weight) / 3600)
}

const quickCalculate = (calc) => {
  switch (calc.key) {
    case 'bsa':
      const bsa = calculateBSA(patient.value.height, patient.value.weight)
      alert(`BSA: ${bsa.toFixed(2)} m²`)
      break
    case 'bmi':
      const heightM = patient.value.height / 100
      const bmi = patient.value.weight / (heightM * heightM)
      alert(`BMI: ${bmi.toFixed(1)} kg/m²`)
      break
  }
}

const resetCalculator = () => {
  selectedMedication.value = ''
  patient.value = {
    age: '',
    weight: '',
    height: '',
    creatinineClearance: '',
    category: 'adult',
    conditions: {
      renal: false,
      hepatic: false,
      obese: false
    }
  }
  dosageResult.value = null
}

const addToPrescription = () => {
  if (dosageResult.value) {
    // Emit event to add calculated dosage to prescription
    console.log('Adding calculated dosage to prescription:', dosageResult.value)
  }
}

const formatNumber = (num) => {
  if (num === null || num === undefined) return 'N/A'
  return Number(num).toFixed(1)
}

// Watch for medication changes
watch(selectedMedication, () => {
  dosageResult.value = null
})
</script>

<style scoped>
.dosage-calculator {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.calculator-header {
  padding: 24px;
  border-bottom: 1px solid #e1e5e9;
  background: #f8f9fa;
}

.calculator-header h3 {
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.calculator-subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.medication-selection,
.patient-parameters,
.calculation-method,
.dosage-results,
.quick-calculations {
  padding: 24px;
  border-bottom: 1px solid #e1e5e9;
}

.medication-selection h4,
.patient-parameters h4,
.calculation-method h4,
.dosage-results h4,
.quick-calculations h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.parameters-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.param-field {
  margin-bottom: 0;
}

.patient-options {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.patient-option {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #2c3e50;
}

.patient-option input[type="radio"] {
  margin: 0;
}

.condition-flags {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.condition-flag {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #2c3e50;
}

.condition-flag input[type="checkbox"] {
  margin: 0;
}

.method-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.method-details p {
  margin: 8px 0 0 0;
  color: #7f8c8d;
  font-size: 14px;
  line-height: 1.4;
}

.method-formula {
  background: white;
  border-radius: 6px;
  padding: 12px;
}

.formula {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #2c3e50;
  margin-top: 8px;
}

.results-summary {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.result-primary {
  text-align: center;
}

.calculated-dose {
  margin-bottom: 8px;
}

.dose-value {
  font-size: 32px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
}

.dose-unit {
  font-size: 16px;
  color: #7f8c8d;
  margin-left: 4px;
}

.frequency {
  color: #3498db;
  font-weight: 500;
  font-size: 14px;
}

.result-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.detail-label {
  color: #7f8c8d;
}

.detail-value {
  color: #2c3e50;
  font-weight: 500;
}

.dose-adjustments,
.calculation-warnings {
  margin-top: 16px;
}

.dose-adjustments h5,
.calculation-warnings h5 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 600;
}

.adjustments-list,
.warnings-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.adjustment-item,
.warning-item {
  padding: 12px;
  border-radius: 6px;
  font-size: 13px;
}

.adjustment-item {
  background: #e3f2fd;
  border-left: 3px solid #3498db;
}

.warning-item {
  background: #fff3cd;
  border-left: 3px solid #f39c12;
  color: #856404;
}

.quick-calculations {
  border-bottom: none;
}

.quick-calc-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.quick-calc-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.quick-calc-btn:hover:not(:disabled) {
  background: #e1e5e9;
  border-color: #3498db;
}

.quick-calc-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.calc-icon {
  width: 32px;
  height: 32px;
  background: #3498db;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.calc-info {
  flex: 1;
}

.calc-title {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
  margin-bottom: 2px;
}

.calc-description {
  color: #7f8c8d;
  font-size: 12px;
  line-height: 1.3;
}

.calculator-references {
  padding: 24px;
  border-bottom: none;
}

.calculator-references h4 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.references-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.references-list li {
  padding: 8px 0;
  font-size: 13px;
  color: #2c3e50;
}

.references-list a {
  color: #3498db;
  text-decoration: none;
}

.references-list a:hover {
  text-decoration: underline;
}

.ref-year {
  color: #7f8c8d;
}

.calculator-actions {
  padding: 24px;
  display: flex;
  gap: 12px;
  justify-content: center;
  background: #f8f9fa;
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

.btn-success {
  background: #27ae60;
  color: white;
}

.btn-success:hover:not(:disabled) {
  background: #229954;
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
  .parameters-grid {
    grid-template-columns: 1fr;
  }

  .patient-options {
    flex-direction: column;
    gap: 8px;
  }

  .condition-flags {
    flex-direction: column;
    gap: 8px;
  }

  .method-info {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .results-summary {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .quick-calc-grid {
    grid-template-columns: 1fr;
  }

  .calculator-actions {
    flex-direction: column;
  }
}
</style>
