import { defineStore } from 'pinia'
import PrescriptionService from '@/services/PrescriptionService'

export const usePrescriptionStore = defineStore('prescriptions', {
  state: () => ({
    prescriptions: [],
    currentPrescription: null,
    loading: false,
    error: null
  }),

  getters: {
    allPrescriptions: (state) => state.prescriptions,
    prescriptionById: (state) => (id) => state.prescriptions.find(p => p.id === id),
    prescriptionsByPatient: (state) => (patientId) =>
      state.prescriptions.filter(p => p.patientId === patientId),
    prescriptionsByPrescriber: (state) => (prescriberId) =>
      state.prescriptions.filter(p => p.prescriberId === prescriberId)
  },

  actions: {
    async fetchPrescriptionById(id) {
      this.loading = true
      this.error = null

      try {
        const prescription = await PrescriptionService.getById(id)
        this.currentPrescription = prescription.data || prescription
        return this.currentPrescription
      } catch (error) {
        this.error = error.message || 'Failed to fetch prescription'
        console.error('Error fetching prescription:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchPrescriptionsByPatient(patientId) {
      this.loading = true
      this.error = null

      try {
        const prescriptions = await PrescriptionService.listByPatient(patientId)
        // Update or merge with existing prescriptions
        prescriptions.data?.forEach(prescription => {
          const existingIndex = this.prescriptions.findIndex(p => p.id === prescription.id)
          if (existingIndex !== -1) {
            this.prescriptions[existingIndex] = prescription
          } else {
            this.prescriptions.push(prescription)
          }
        })
        return prescriptions.data || prescriptions
      } catch (error) {
        this.error = error.message || 'Failed to fetch prescriptions'
        console.error('Error fetching prescriptions:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async createPrescription(prescriptionData) {
      this.loading = true
      this.error = null

      try {
        const newPrescription = await PrescriptionService.create(prescriptionData)
        this.prescriptions.push(newPrescription.data || newPrescription)
        return newPrescription.data || newPrescription
      } catch (error) {
        this.error = error.message || 'Failed to create prescription'
        console.error('Error creating prescription:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async updatePrescriptionStatus(id, status) {
      this.loading = true
      this.error = null

      try {
        const updatedPrescription = await PrescriptionService.updateStatus(id, status)

        // Update in local state
        const index = this.prescriptions.findIndex(p => p.id === id)
        if (index !== -1) {
          this.prescriptions[index] = { ...this.prescriptions[index], ...updatedPrescription.data || updatedPrescription }
        }

        if (this.currentPrescription && this.currentPrescription.id === id) {
          this.currentPrescription = { ...this.currentPrescription, ...updatedPrescription.data || updatedPrescription }
        }

        return updatedPrescription.data || updatedPrescription
      } catch (error) {
        this.error = error.message || 'Failed to update prescription status'
        console.error('Error updating prescription status:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async deletePrescription(id) {
      this.loading = true
      this.error = null

      try {
        await PrescriptionService.delete(id)

        // Remove from local state
        this.prescriptions = this.prescriptions.filter(p => p.id !== id)

        if (this.currentPrescription && this.currentPrescription.id === id) {
          this.currentPrescription = null
        }
      } catch (error) {
        this.error = error.message || 'Failed to delete prescription'
        console.error('Error deleting prescription:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    clearCurrentPrescription() {
      this.currentPrescription = null
    }
  }
})
