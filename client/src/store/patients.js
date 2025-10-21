import { defineStore } from 'pinia'
import PatientService from '@/services/PatientService'

export const usePatientStore = defineStore('patients', {
  state: () => ({
    patients: [],
    currentPatient: null,
    loading: false,
    error: null,
    searchResults: []
  }),

  getters: {
    allPatients: (state) => state.patients,
    patientById: (state) => (id) => state.patients.find(patient => patient.patientId === id),
    hasPatients: (state) => state.patients.length > 0
  },

  actions: {
    async fetchAllPatients() {
      this.loading = true
      this.error = null

      try {
        const patients = await PatientService.getAllPatients()
        this.patients = patients.data || patients
      } catch (error) {
        this.error = error.message || 'Failed to fetch patients'
        console.error('Error fetching patients:', error)
      } finally {
        this.loading = false
      }
    },

    async fetchPatientById(patientId) {
      this.loading = true
      this.error = null

      try {
        const patient = await PatientService.getPatientById(patientId)
        this.currentPatient = patient.data || patient
        return this.currentPatient
      } catch (error) {
        this.error = error.message || 'Failed to fetch patient'
        console.error('Error fetching patient:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async createPatient(patientData) {
      this.loading = true
      this.error = null

      try {
        const newPatient = await PatientService.createPatient(patientData)
        this.patients.push(newPatient.data || newPatient)
        return newPatient.data || newPatient
      } catch (error) {
        this.error = error.message || 'Failed to create patient'
        console.error('Error creating patient:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async updatePatient(patientId, patientData) {
      this.loading = true
      this.error = null

      try {
        const updatedPatient = await PatientService.updatePatient(patientId, patientData)

        // Update in local state
        const index = this.patients.findIndex(p => p.patientId === patientId)
        if (index !== -1) {
          this.patients[index] = updatedPatient.data || updatedPatient
        }

        if (this.currentPatient && this.currentPatient.patientId === patientId) {
          this.currentPatient = updatedPatient.data || updatedPatient
        }

        return updatedPatient.data || updatedPatient
      } catch (error) {
        this.error = error.message || 'Failed to update patient'
        console.error('Error updating patient:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async deletePatient(patientId) {
      this.loading = true
      this.error = null

      try {
        await PatientService.deletePatient(patientId)

        // Remove from local state
        this.patients = this.patients.filter(p => p.patientId !== patientId)

        if (this.currentPatient && this.currentPatient.patientId === patientId) {
          this.currentPatient = null
        }
      } catch (error) {
        this.error = error.message || 'Failed to delete patient'
        console.error('Error deleting patient:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async searchPatients(query) {
      this.loading = true
      this.error = null

      try {
        const results = await PatientService.searchPatients(query)
        this.searchResults = results.data || results
        return this.searchResults
      } catch (error) {
        this.error = error.message || 'Failed to search patients'
        console.error('Error searching patients:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async lookupPatientByContact(firstName, lastName, phone) {
      this.loading = true
      this.error = null

      try {
        const patient = await PatientService.lookupPatientByContact(firstName, lastName, phone)
        return patient.data || patient
      } catch (error) {
        this.error = error.message || 'Failed to lookup patient'
        console.error('Error looking up patient:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    clearSearchResults() {
      this.searchResults = []
    },

    clearCurrentPatient() {
      this.currentPatient = null
    }
  }
})
