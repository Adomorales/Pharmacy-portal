// Main store file - Pinia stores are defined in separate modules
// Import and export all stores here for easy access

export { useAuthStore } from '@/store/auth'
export { usePatientStore } from '@/store/patients'
export { usePrescriptionStore } from '@/store/prescriptions'

// Future stores can be added here:
// export { useMedicationStore } from '@/store/medications'
// export { useVaccineStore } from '@/store/vaccines'
// export { useInsuranceStore } from '@/store/insurance'