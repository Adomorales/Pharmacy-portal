<template>
  <div class="medication-search">
    <!-- Search Header -->
    <div class="search-header">
      <h3>Medication Search</h3>
      <div class="search-controls">
        <FormField
          v-model="searchQuery"
          placeholder="Search by name, generic name, or NDC..."
          :prefix-icon="SearchIcon"
          @input="handleSearch"
          class="search-field"
        />

        <div class="search-filters">
          <button
            v-for="filter in searchFilters"
            :key="filter.key"
            @click="toggleFilter(filter.key)"
            :class="{ active: activeFilters.includes(filter.key) }"
            class="filter-btn"
          >
            <component :is="filter.icon" />
            {{ filter.label }}
          </button>
        </div>
      </div>
    </div>

    <!-- Search Results -->
    <div class="search-results" v-if="searchResults.length > 0 || isSearching">
      <!-- Active Filters Display -->
      <div v-if="activeFilters.length > 0" class="active-filters">
        <span class="filter-label">Active filters:</span>
        <div class="filter-tags">
          <span
            v-for="filter in activeFilters"
            :key="filter"
            class="filter-tag"
          >
            {{ getFilterLabel(filter) }}
            <button @click="removeFilter(filter)" class="remove-filter">
              <XIcon />
            </button>
          </span>
        </div>
        <button @click="clearAllFilters" class="clear-filters">Clear all</button>
      </div>

      <!-- Loading State -->
      <div v-if="isSearching" class="search-loading">
        <LoadingBar :progress="searchProgress" />
        <span>Searching medications...</span>
      </div>

      <!-- Results -->
      <div v-else class="results-container">
        <div class="results-header">
          <span class="results-count">
            {{ searchResults.length }} medication{{ searchResults.length !== 1 ? 's' : '' }} found
          </span>
          <div class="results-actions">
            <button @click="toggleViewMode" class="view-toggle">
              <component :is="viewMode === 'list' ? GridIcon : ListIcon" />
              {{ viewMode === 'list' ? 'Grid' : 'List' }}
            </button>
          </div>
        </div>

        <!-- List View -->
        <div v-if="viewMode === 'list'" class="medication-list">
          <div
            v-for="medication in searchResults"
            :key="medication.id"
            class="medication-item"
            :class="{ 'low-stock': medication.stockLevel < medication.minStock }"
            @click="selectMedication(medication)"
          >
            <div class="medication-primary">
              <div class="medication-info">
                <h4 class="medication-name">{{ medication.name }}</h4>
                <p class="medication-generic">{{ medication.genericName }}</p>
                <div class="medication-details">
                  <span class="strength">{{ medication.strength }}</span>
                  <span class="form">{{ medication.form }}</span>
                  <span class="ndc">{{ medication.ndc }}</span>
                </div>
              </div>
            </div>

            <div class="medication-secondary">
              <div class="stock-info">
                <span class="stock-level" :class="getStockClass(medication.stockLevel)">
                  {{ medication.stockLevel }} {{ medication.unit }}
                </span>
                <span class="stock-status">{{ getStockStatus(medication) }}</span>
              </div>

              <div class="medication-actions">
                <button @click.stop="addToPrescription(medication)" class="action-btn primary">
                  <PlusIcon />
                  Add to Rx
                </button>
                <button @click.stop="viewDetails(medication)" class="action-btn secondary">
                  <EyeIcon />
                  Details
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Grid View -->
        <div v-else class="medication-grid">
          <div
            v-for="medication in searchResults"
            :key="medication.id"
            class="medication-card"
            :class="{ 'low-stock': medication.stockLevel < medication.minStock }"
            @click="selectMedication(medication)"
          >
            <div class="card-header">
              <h4 class="medication-name">{{ medication.name }}</h4>
              <div class="stock-badge" :class="getStockClass(medication.stockLevel)">
                {{ medication.stockLevel }} {{ medication.unit }}
              </div>
            </div>

            <div class="card-content">
              <p class="medication-generic">{{ medication.genericName }}</p>
              <div class="medication-details">
                <span class="strength">{{ medication.strength }}</span>
                <span class="form">{{ medication.form }}</span>
              </div>
              <div class="ndc">{{ medication.ndc }}</div>
            </div>

            <div class="card-actions">
              <button @click.stop="addToPrescription(medication)" class="action-btn primary">
                <PlusIcon />
                Add
              </button>
              <button @click.stop="viewDetails(medication)" class="action-btn secondary">
                <EyeIcon />
                View
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- No Results -->
    <div v-else-if="hasSearched && !isSearching" class="no-results">
      <SearchIcon class="no-results-icon" />
      <h4>No medications found</h4>
      <p>Try adjusting your search terms or filters</p>
      <button @click="clearSearch" class="btn btn-outline">Clear search</button>
    </div>

    <!-- Initial State -->
    <div v-else class="initial-state">
      <PillIcon class="initial-icon" />
      <h4>Search for medications</h4>
      <p>Enter a medication name, generic name, or NDC code to get started</p>
    </div>

    <!-- Medication Details Modal -->
    <MedicationModal
      v-if="selectedMedication"
      :medication="selectedMedication"
      @close="selectedMedication = null"
      @add-to-prescription="addToPrescription"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useDebounce } from '@/composables/useDebounce'
import FormField from '@/components/forms/FormField.vue'
import LoadingBar from '@/components/common/LoadingBar.vue'
import MedicationModal from '@/MedicationModal'

// Icons
import {
  SearchIcon,
  XIcon,
  FilterIcon,
  PillIcon,
  GridIcon,
  ListIcon,
  PlusIcon,
  EyeIcon
} from '@/components/icons'

// State
const searchQuery = ref('')
const isSearching = ref(false)
const searchProgress = ref(0)
const searchResults = ref([])
const hasSearched = ref(false)
const selectedMedication = ref(null)
const viewMode = ref('list')
const activeFilters = ref([])

// Search filters
const searchFilters = [
  { key: 'controlled', label: 'Controlled Substances', icon: PillIcon },
  { key: 'refrigerated', label: 'Refrigerated', icon: PillIcon },
  { key: 'low-stock', label: 'Low Stock', icon: PillIcon },
  { key: 'recalls', label: 'Active Recalls', icon: PillIcon }
]

// Computed
const debouncedSearchQuery = useDebounce(searchQuery, 500)

// Methods
const handleSearch = async () => {
  if (!searchQuery.value.trim()) {
    searchResults.value = []
    hasSearched.value = false
    return
  }

  isSearching.value = true
  searchProgress.value = 0
  hasSearched.value = true

  // Simulate search progress
  const progressInterval = setInterval(() => {
    searchProgress.value += 10
    if (searchProgress.value >= 90) {
      clearInterval(progressInterval)
    }
  }, 100)

  try {
    // Simulate API call - replace with actual service call
    await new Promise(resolve => setTimeout(resolve, 1000))

    // Mock search results - replace with actual API response
    searchResults.value = [
      {
        id: 1,
        name: 'Lisinopril',
        genericName: 'Lisinopril',
        strength: '10mg',
        form: 'Tablet',
        ndc: '00093-0017-01',
        stockLevel: 150,
        minStock: 50,
        unit: 'tablets',
        controlled: false,
        refrigerated: false
      },
      {
        id: 2,
        name: 'Amoxicillin',
        genericName: 'Amoxicillin Trihydrate',
        strength: '500mg',
        form: 'Capsule',
        ndc: '65862-0016-05',
        stockLevel: 25,
        minStock: 50,
        unit: 'capsules',
        controlled: false,
        refrigerated: false
      },
      {
        id: 3,
        name: 'Ibuprofen',
        genericName: 'Ibuprofen',
        strength: '800mg',
        form: 'Tablet',
        ndc: '00536-1325-01',
        stockLevel: 300,
        minStock: 100,
        unit: 'tablets',
        controlled: false,
        refrigerated: false
      }
    ].filter(med => {
      const query = searchQuery.value.toLowerCase()
      return (
        med.name.toLowerCase().includes(query) ||
        med.genericName.toLowerCase().includes(query) ||
        med.ndc.includes(query)
      )
    })

    searchProgress.value = 100
  } catch (error) {
    console.error('Search error:', error)
    searchResults.value = []
  } finally {
    clearInterval(progressInterval)
    setTimeout(() => {
      isSearching.value = false
      searchProgress.value = 0
    }, 300)
  }
}

const toggleFilter = (filterKey) => {
  const index = activeFilters.value.indexOf(filterKey)
  if (index > -1) {
    activeFilters.value.splice(index, 1)
  } else {
    activeFilters.value.push(filterKey)
  }
  handleSearch()
}

const removeFilter = (filterKey) => {
  activeFilters.value = activeFilters.value.filter(f => f !== filterKey)
  handleSearch()
}

const clearAllFilters = () => {
  activeFilters.value = []
  handleSearch()
}

const getFilterLabel = (filterKey) => {
  return searchFilters.find(f => f.key === filterKey)?.label || filterKey
}

const getStockClass = (stockLevel) => {
  if (stockLevel === 0) return 'out-of-stock'
  if (stockLevel < 50) return 'low-stock'
  return 'in-stock'
}

const getStockStatus = (medication) => {
  if (medication.stockLevel === 0) return 'Out of Stock'
  if (medication.stockLevel < medication.minStock) return 'Low Stock'
  return 'In Stock'
}

const selectMedication = (medication) => {
  selectedMedication.value = medication
}

const addToPrescription = (medication) => {
  // Emit event to add medication to current prescription
  console.log('Adding to prescription:', medication)
}

const viewDetails = (medication) => {
  selectMedication(medication)
}

const toggleViewMode = () => {
  viewMode.value = viewMode.value === 'list' ? 'grid' : 'list'
}

const clearSearch = () => {
  searchQuery.value = ''
  searchResults.value = []
  hasSearched.value = false
}

// Watch for search query changes
watch(debouncedSearchQuery, handleSearch)
</script>

<style scoped>
.medication-search {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.search-header {
  padding: 24px;
  border-bottom: 1px solid #e1e5e9;
}

.search-header h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.search-controls {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-field {
  flex: 1;
  max-width: 400px;
}

.search-filters {
  display: flex;
  gap: 8px;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: none;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.filter-btn:hover {
  border-color: #3498db;
}

.filter-btn.active {
  background: #e3f2fd;
  border-color: #3498db;
  color: #1976d2;
}

.active-filters {
  padding: 16px 24px;
  background: #f8f9fa;
  border-bottom: 1px solid #e1e5e9;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-label {
  color: #7f8c8d;
  font-size: 14px;
  font-weight: 500;
}

.filter-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  background: #3498db;
  color: white;
  border-radius: 12px;
  font-size: 12px;
}

.remove-filter {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  padding: 2px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-filter:hover {
  background: rgba(255, 255, 255, 0.2);
}

.clear-filters {
  background: none;
  border: none;
  color: #3498db;
  cursor: pointer;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
}

.clear-filters:hover {
  background: #e3f2fd;
}

.search-loading {
  padding: 40px 24px;
  text-align: center;
  color: #7f8c8d;
}

.results-container {
  padding: 24px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.results-count {
  color: #2c3e50;
  font-weight: 500;
}

.view-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: none;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.view-toggle:hover {
  border-color: #3498db;
}

.medication-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.medication-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.medication-item:hover {
  border-color: #3498db;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.1);
}

.medication-primary {
  flex: 1;
}

.medication-name {
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.medication-generic {
  margin: 0 0 8px 0;
  color: #7f8c8d;
  font-size: 14px;
}

.medication-details {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #95a5a6;
}

.medication-secondary {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stock-info {
  text-align: right;
}

.stock-level {
  display: block;
  font-weight: 600;
  font-size: 16px;
}

.stock-level.in-stock {
  color: #27ae60;
}

.stock-level.low-stock {
  color: #f39c12;
}

.stock-level.out-of-stock {
  color: #e74c3c;
}

.stock-status {
  display: block;
  font-size: 12px;
  color: #7f8c8d;
}

.medication-actions {
  display: flex;
  gap: 8px;
}

.medication-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.medication-card {
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.medication-card:hover {
  border-color: #3498db;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.card-content {
  margin-bottom: 16px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 12px;
}

.action-btn.primary {
  background: #3498db;
  color: white;
}

.action-btn.primary:hover {
  background: #2980b9;
}

.action-btn.secondary {
  background: #f8f9fa;
  color: #2c3e50;
  border: 1px solid #e1e5e9;
}

.action-btn.secondary:hover {
  background: #e1e5e9;
}

.no-results,
.initial-state {
  padding: 60px 24px;
  text-align: center;
  color: #7f8c8d;
}

.no-results-icon,
.initial-icon {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
  color: #bdc3c7;
}

.initial-state {
  background: #f8f9fa;
  border-radius: 8px;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .search-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .search-filters {
    justify-content: center;
  }

  .medication-grid {
    grid-template-columns: 1fr;
  }

  .medication-item {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .medication-secondary {
    justify-content: space-between;
  }
}
</style>
