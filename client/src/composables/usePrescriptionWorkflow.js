import { ref, computed, watch } from 'vue'
import { usePrescriptionStore } from '@/store'

// Global prescription workflow state
const prescriptionWorkflow = ref({
  currentStep: 'patient', // patient, prescriber, medications, review
  isDirty: false,
  lastSaved: null,
  autoSaveEnabled: true
})

const workflowSteps = [
  { key: 'patient', label: 'Patient Information', required: true },
  { key: 'prescriber', label: 'Prescriber Details', required: true },
  { key: 'medications', label: 'Medications', required: true },
  { key: 'review', label: 'Review & Submit', required: false }
]

export const usePrescriptionWorkflow = () => {
  const prescriptionStore = usePrescriptionStore()

  const currentPrescription = computed(() => prescriptionStore.currentPrescription)

  const currentStepIndex = computed(() => {
    return workflowSteps.findIndex(step => step.key === prescriptionWorkflow.value.currentStep)
  })

  const completedSteps = computed(() => {
    return workflowSteps.filter(step => {
      switch (step.key) {
        case 'patient': return !!currentPrescription.value?.patient
        case 'prescriber': return !!currentPrescription.value?.prescriber
        case 'medications': return !!currentPrescription.value?.medications?.length
        case 'review': return false // Review is never "completed" until submitted
        default: return false
      }
    })
  })

  const canProceedToStep = (stepKey) => {
    const stepIndex = workflowSteps.findIndex(s => s.key === stepKey)
    const requiredSteps = workflowSteps.slice(0, stepIndex).filter(s => s.required)

    return requiredSteps.every(step => {
      return completedSteps.value.some(completed => completed.key === step.key)
    })
  }

  const goToStep = (stepKey) => {
    if (canProceedToStep(stepKey) || stepKey === prescriptionWorkflow.value.currentStep) {
      prescriptionWorkflow.value.currentStep = stepKey
      return true
    }
    return false
  }

  const nextStep = () => {
    const currentIndex = currentStepIndex.value
    if (currentIndex < workflowSteps.length - 1) {
      const nextStepKey = workflowSteps[currentIndex + 1].key
      if (goToStep(nextStepKey)) {
        return nextStepKey
      }
    }
    return null
  }

  const previousStep = () => {
    const currentIndex = currentStepIndex.value
    if (currentIndex > 0) {
      const prevStepKey = workflowSteps[currentIndex - 1].key
      prescriptionWorkflow.value.currentStep = prevStepKey
      return prevStepKey
    }
    return null
  }

  const markDirty = () => {
    prescriptionWorkflow.value.isDirty = true
  }

  const markClean = () => {
    prescriptionWorkflow.value.isDirty = false
    prescriptionWorkflow.value.lastSaved = new Date()
  }

  const autoSave = async () => {
    if (prescriptionWorkflow.value.autoSaveEnabled &&
        prescriptionWorkflow.value.isDirty &&
        currentPrescription.value) {
      try {
        await prescriptionStore.updatePrescription(currentPrescription.value.id, currentPrescription.value)
        markClean()
      } catch (error) {
        console.error('Auto-save failed:', error)
      }
    }
  }

  // Auto-save every 30 seconds if dirty
  let autoSaveInterval = null
  watch(() => prescriptionWorkflow.value.isDirty, (isDirty) => {
    if (isDirty && prescriptionWorkflow.value.autoSaveEnabled) {
      if (autoSaveInterval) clearInterval(autoSaveInterval)
      autoSaveInterval = setInterval(autoSave, 30000)
    } else {
      if (autoSaveInterval) {
        clearInterval(autoSaveInterval)
        autoSaveInterval = null
      }
    }
  })

  return {
    // State
    prescriptionWorkflow: readonly(prescriptionWorkflow),
    workflowSteps: readonly(workflowSteps),
    currentStep: computed(() => prescriptionWorkflow.value.currentStep),
    currentStepIndex,
    completedSteps,
    currentPrescription,

    // Actions
    goToStep,
    nextStep,
    previousStep,
    canProceedToStep,
    markDirty,
    markClean,
    autoSave,

    // Utilities
    isStepCompleted: (stepKey) => completedSteps.value.some(s => s.key === stepKey),
    isStepCurrent: (stepKey) => prescriptionWorkflow.value.currentStep === stepKey,
    getStepProgress: () => ({
      completed: completedSteps.value.length,
      total: workflowSteps.length,
      percentage: Math.round((completedSteps.value.length / workflowSteps.length) * 100)
    })
  }
}
