<template>
  <div class="inventory-status">
    <!-- Header -->
    <div class="status-header">
      <h3>Inventory Status</h3>
      <div class="status-actions">
        <button @click="refreshInventory" :disabled="isRefreshing" class="refresh-btn">
          <span v-if="isRefreshing" class="btn-loader"></span>
          Refresh
        </button>
        <button @click="exportReport" class="export-btn">
          Export Report
        </button>
      </div>
    </div>

    <!-- Status Overview Cards -->
    <div class="status-overview">
      <div class="overview-card">
        <div class="card-icon">
          <PackageIcon class="icon" />
        </div>
        <div class="card-content">
          <div class="card-value">{{ totalMedications }}</div>
          <div class="card-label">Total Medications</div>
        </div>
      </div>

      <div class="overview-card warning">
        <div class="card-icon">
          <AlertTriangleIcon class="icon" />
        </div>
        <div class="card-content">
          <div class="card-value">{{ lowStockCount }}</div>
          <div class="card-label">Low Stock Items</div>
        </div>
      </div>

      <div class="overview-card danger">
        <div class="card-icon">
          <XCircleIcon class="icon" />
        </div>
        <div class="card-content">
          <div class="card-value">{{ outOfStockCount }}</div>
          <div class="card-label">Out of Stock</div>
        </div>
      </div>

      <div class="overview-card success">
        <div class="card-icon">
          <CheckCircleIcon class="icon" />
        </div>
        <div class="card-content">
          <div class="card-value">{{ wellStockedCount }}</div>
          <div class="card-label">Well Stocked</div>
        </div>
      </div>
    </div>

    <!-- Filters and Search -->
    <div class="inventory-controls">
      <div class="search-filter">
        <FormField
          v-model="searchQuery"
          placeholder="Search medications..."
          :prefix-icon="SearchIcon"
          @input="handleSearch"
          class="search-field"
        />
      </div>

      <div class="status-filters">
        <button
          v-for="filter in statusFilters"
          :key="filter.key"
          @click="toggleStatusFilter(filter.key)"
          :class="{ active: activeStatusFilters.includes(filter.key) }"
          class="status-filter-btn"
        >
          {{ filter.label }}
          <span class="filter-count">({{ getFilterCount(filter.key) }})</span>
        </button>
      </div>
    </div>

    <!-- Inventory Table -->
    <div class="inventory-table-container">
      <table class="inventory-table">
        <thead>
          <tr>
            <th>Medication</th>
            <th>Strength</th>
            <th>Form</th>
            <th>NDC</th>
            <th>Current Stock</th>
            <th>Min Stock</th>
            <th>Status</th>
            <th>Expiry Date</th>
            <th>Location</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="medication in filteredInventory"
            :key="medication.id"
            class="inventory-row"
            :class="getRowClass(medication)"
          >
            <td class="medication-cell">
              <div class="medication-info">
                <strong>{{ medication.name }}</strong>
                <span class="generic-name">{{ medication.genericName }}</span>
              </div>
            </td>
            <td>{{ medication.strength }}</td>
            <td>{{ medication.form }}</td>
            <td class="ndc-cell">{{ medication.ndc }}</td>
            <td class="stock-cell">
              <span class="stock-value" :class="getStockValueClass(medication)">
                {{ medication.currentStock }}
              </span>
            </td>
            <td>{{ medication.minStock }}</td>
            <td>
              <span class="status-badge" :class="getStatusClass(medication)">
                {{ getStatusText(medication) }}
              </span>
            </td>
            <td class="expiry-cell" :class="getExpiryClass(medication)">
              {{ formatDate(medication.expiryDate) }}
              <span v-if="isExpiringSoon(medication)" class="expiry-warning">
                Expires soon
              </span>
            </td>
            <td>{{ medication.location }}</td>
            <td class="actions-cell">
              <div class="row-actions">
                <button
                  v-if="medication.currentStock <= medication.minStock"
                  @click="reorderMedication(medication)"
                  class="action-btn reorder"
                  title="Reorder"
                >
                  <ShoppingCartIcon />
                </button>
                <button @click="adjustStock(medication)" class="action-btn adjust" title="Adjust Stock">
                  <EditIcon />
                </button>
                <button @click="viewHistory(medication)" class="action-btn history" title="View History">
                  <HistoryIcon />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Low Stock Alerts -->
    <div v-if="lowStockItems.length > 0" class="low-stock-alerts">
      <h4>⚠️ Low Stock Alerts</h4>
      <div class="alerts-list">
        <div
          v-for="item in lowStockItems"
          :key="item.id"
          class="alert-item"
        >
          <div class="alert-info">
            <strong>{{ item.name }}</strong>
            <span>{{ item.strength }} {{ item.form }}</span>
          </div>
          <div class="alert-details">
            <span class="current-stock">Stock: {{ item.currentStock }}/{{ item.minStock }}</span>
            <span class="days-until">Reorder needed</span>
          </div>
          <button @click="reorderMedication(item)" class="reorder-now-btn">
            Reorder Now
          </button>
        </div>
      </div>
    </div>

    <!-- Expiry Warnings -->
    <div v-if="expiringSoonItems.length > 0" class="expiry-warnings">
      <h4>⏰ Expiry Warnings</h4>
      <div class="warnings-list">
        <div
          v-for="item in expiringSoonItems"
          :key="item.id"
          class="warning-item"
        >
          <div class="warning-info">
            <strong>{{ item.name }}</strong>
            <span>{{ item.strength }} {{ item.form }}</span>
          </div>
          <div class="warning-details">
            <span class="expiry-date">Expires: {{ formatDate(item.expiryDate) }}</span>
            <span class="lot-number">Lot: {{ item.lotNumber }}</span>
          </div>
          <div class="warning-actions">
            <button @click="markForReturn(item)" class="return-btn">
              Mark for Return
            </button>
            <button @click="extendExpiry(item)" class="extend-btn">
              Request Extension
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Stock Adjustment Modal -->
    <StockAdjustmentModal
      v-if="showStockModal"
      :medication="selectedMedication"
      @close="closeStockModal"
      @save="saveStockAdjustment"
    />

    <!-- Reorder Modal -->
    <ReorderModal
      v-if="showReorderModal"
      :medication="selectedMedication"
      @close="closeReorderModal"
      @reorder="processReorder"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import FormField from '@/components/forms/FormField.vue'
import StockAdjustmentModal from '@/StockAdjustmentModal'
import ReorderModal from '@/ReorderModal'
import {
  PackageIcon,
  AlertTriangleIcon,
  XCircleIcon,
  CheckCircleIcon,
  SearchIcon,
  ShoppingCartIcon,
  EditIcon,
  HistoryIcon
} from '@/components/icons'

const searchQuery = ref('')
const isRefreshing = ref(false)
const showStockModal = ref(false)
const showReorderModal = ref(false)
const selectedMedication = ref(null)
const activeStatusFilters = ref([])

const inventory = ref([
  {
    id: 1,
    name: 'Lisinopril',
    genericName: 'Lisinopril',
    strength: '10mg',
    form: 'Tablet',
    ndc: '00093-0017-01',
    currentStock: 45,
    minStock: 50,
    expiryDate: '2024-12-15',
    lotNumber: 'LIS20241215',
    location: 'Shelf A-1'
  },
  {
    id: 2,
    name: 'Amoxicillin',
    genericName: 'Amoxicillin Trihydrate',
    strength: '500mg',
    form: 'Capsule',
    ndc: '65862-0016-05',
    currentStock: 0,
    minStock: 50,
    expiryDate: '2024-08-30',
    lotNumber: 'AMX20240830',
    location: 'Shelf B-2'
  },
  {
    id: 3,
    name: 'Ibuprofen',
    genericName: 'Ibuprofen',
    strength: '800mg',
    form: 'Tablet',
    ndc: '00536-1325-01',
    currentStock: 250,
    minStock: 100,
    expiryDate: '2025-03-20',
    lotNumber: 'IBU20250320',
    location: 'Shelf C-3'
  }
])

const statusFilters = [
  { key: 'low-stock', label: 'Low Stock' },
  { key: 'out-of-stock', label: 'Out of Stock' },
  { key: 'expiring-soon', label: 'Expiring Soon' },
  { key: 'well-stocked', label: 'Well Stocked' }
]

const filteredInventory = computed(() => {
  let filtered = inventory.value


  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(item =>
      item.name.toLowerCase().includes(query) ||
      item.genericName.toLowerCase().includes(query) ||
      item.ndc.includes(query)
    )
  }

  
  if (activeStatusFilters.value.length > 0) {
    filtered = filtered.filter(item => {
      return activeStatusFilters.value.some(filter => {
        switch (filter) {
          case 'low-stock':
            return item.currentStock > 0 && item.currentStock <= item.minStock
          case 'out-of-stock':
            return item.currentStock === 0
          case 'expiring-soon':
            return isExpiringSoon(item)
          case 'well-stocked':
            return item.currentStock > item.minStock
          default:
            return false
        }
      })
    })
  }

  return filtered
})

const totalMedications = computed(() => inventory.value.length)

const lowStockCount = computed(() =>
  inventory.value.filter(item => item.currentStock > 0 && item.currentStock <= item.minStock).length
)

const outOfStockCount = computed(() =>
  inventory.value.filter(item => item.currentStock === 0).length
)

const wellStockedCount = computed(() =>
  inventory.value.filter(item => item.currentStock > item.minStock).length
)

const lowStockItems = computed(() =>
  inventory.value.filter(item => item.currentStock > 0 && item.currentStock <= item.minStock)
)

const expiringSoonItems = computed(() =>
  inventory.value.filter(item => isExpiringSoon(item))
)


const handleSearch = () => {}

const toggleStatusFilter = (filterKey) => {
  const index = activeStatusFilters.value.indexOf(filterKey)
  if (index > -1) {
    activeStatusFilters.value.splice(index, 1)
  } else {
    activeStatusFilters.value.push(filterKey)
  }
}

const getFilterCount = (filterKey) => {
  switch (filterKey) {
    case 'low-stock':
      return lowStockCount.value
    case 'out-of-stock':
      return outOfStockCount.value
    case 'expiring-soon':
      return expiringSoonItems.value.length
    case 'well-stocked':
      return wellStockedCount.value
    default:
      return 0
  }
}

const getRowClass = (medication) => {
  if (medication.currentStock === 0) return 'out-of-stock'
  if (medication.currentStock <= medication.minStock) return 'low-stock'
  if (isExpiringSoon(medication)) return 'expiring-soon'
  return 'well-stocked'
}

const getStockValueClass = (medication) => {
  if (medication.currentStock === 0) return 'out-of-stock'
  if (medication.currentStock <= medication.minStock) return 'low-stock'
  return 'well-stocked'
}

const getStatusClass = (medication) => {
  if (medication.currentStock === 0) return 'status-danger'
  if (medication.currentStock <= medication.minStock) return 'status-warning'
  return 'status-success'
}

const getStatusText = (medication) => {
  if (medication.currentStock === 0) return 'Out of Stock'
  if (medication.currentStock <= medication.minStock) return 'Low Stock'
  return 'Well Stocked'
}

const getExpiryClass = (medication) => {
  return isExpiringSoon(medication) ? 'expiring-soon' : ''
}

const isExpiringSoon = (medication) => {
  const expiryDate = new Date(medication.expiryDate)
  const thirtyDaysFromNow = new Date()
  thirtyDaysFromNow.setDate(thirtyDaysFromNow.getDate() + 30)
  return expiryDate <= thirtyDaysFromNow
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString()
}

const refreshInventory = async () => {
  isRefreshing.value = true
  try {

    await new Promise(resolve => setTimeout(resolve, 1000))
  
  } catch (error) {
    console.error('Error refreshing inventory:', error)
  } finally {
    isRefreshing.value = false
  }
}

const exportReport = () => {
  console.log('Exporting inventory report...')
}

const reorderMedication = (medication) => {
  selectedMedication.value = medication
  showReorderModal.value = true
}

const adjustStock = (medication) => {
  selectedMedication.value = medication
  showStockModal.value = true
}

const viewHistory = (medication) => {
  console.log('Viewing history for:', medication.name)
}

const markForReturn = (medication) => {
  console.log('Marking for return:', medication.name)
}

const extendExpiry = (medication) => {
  console.log('Requesting extension for:', medication.name)
}

const closeStockModal = () => {
  showStockModal.value = false
  selectedMedication.value = null
}

const closeReorderModal = () => {
  showReorderModal.value = false
  selectedMedication.value = null
}

const saveStockAdjustment = (adjustment) => {
  console.log('Saving stock adjustment:', adjustment)
  closeStockModal()
}

const processReorder = (order) => {
  console.log('Processing reorder:', order)
  closeReorderModal()
}

onMounted(() => {})
</script>

<style scoped>
.inventory-status {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e1e5e9;
}

.status-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.status-actions {
  display: flex;
  gap: 12px;
}

.refresh-btn,
.export-btn {
  padding: 8px 16px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  background: white;
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.refresh-btn:hover,
.export-btn:hover {
  border-color: #3498db;
  color: #3498db;
}

.refresh-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.status-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  padding: 24px;
  border-bottom: 1px solid #e1e5e9;
}

.overview-card {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.2s;
}

.overview-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.overview-card.warning {
  border-left: 4px solid #f39c12;
}

.overview-card.danger {
  border-left: 4px solid #e74c3c;
}

.overview-card.success {
  border-left: 4px solid #27ae60;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.overview-card .card-icon {
  background: #f8f9fa;
}

.overview-card.warning .card-icon {
  background: #fef5e7;
  color: #f39c12;
}

.overview-card.danger .card-icon {
  background: #ffeaea;
  color: #e74c3c;
}

.overview-card.success .card-icon {
  background: #e8f5e8;
  color: #27ae60;
}

.icon {
  width: 24px;
  height: 24px;
}

.card-content {
  flex: 1;
}

.card-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 4px;
}

.card-label {
  color: #7f8c8d;
  font-size: 14px;
}

.inventory-controls {
  padding: 20px 24px;
  border-bottom: 1px solid #e1e5e9;
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-filter {
  flex: 1;
  max-width: 300px;
}

.status-filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.status-filter-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: none;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  white-space: nowrap;
}

.status-filter-btn:hover {
  border-color: #3498db;
}

.status-filter-btn.active {
  background: #e3f2fd;
  border-color: #3498db;
  color: #1976d2;
}

.filter-count {
  color: #7f8c8d;
  font-size: 11px;
}

.inventory-table-container {
  overflow-x: auto;
}

.inventory-table {
  width: 100%;
  border-collapse: collapse;
}

.inventory-table th {
  padding: 16px 12px;
  text-align: left;
  background: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #e1e5e9;
  white-space: nowrap;
}

.inventory-table td {
  padding: 16px 12px;
  border-bottom: 1px solid #f8f9fa;
  vertical-align: middle;
}

.inventory-row {
  transition: background-color 0.2s;
}

.inventory-row:hover {
  background-color: #f8f9fa;
}

.medication-cell {
  min-width: 200px;
}

.medication-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.generic-name {
  color: #7f8c8d;
  font-size: 12px;
}

.ndc-cell {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #7f8c8d;
}

.stock-cell {
  text-align: center;
}

.stock-value {
  font-weight: 600;
  font-size: 16px;
}

.stock-value.well-stocked {
  color: #27ae60;
}

.stock-value.low-stock {
  color: #f39c12;
}

.stock-value.out-of-stock {
  color: #e74c3c;
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

.expiry-cell {
  min-width: 100px;
}

.expiry-cell.expiring-soon {
  color: #f39c12;
  font-weight: 500;
}

.expiry-warning {
  display: block;
  font-size: 10px;
  color: #e67e22;
  margin-top: 2px;
}

.actions-cell {
  text-align: center;
  min-width: 120px;
}

.row-actions {
  display: flex;
  gap: 6px;
  justify-content: center;
}

.action-btn {
  padding: 6px;
  border: 1px solid #e1e5e9;
  border-radius: 4px;
  background: none;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn:hover {
  border-color: #3498db;
}

.action-btn.reorder {
  color: #27ae60;
}

.action-btn.adjust {
  color: #3498db;
}

.action-btn.history {
  color: #7f8c8d;
}

.low-stock-alerts,
.expiry-warnings {
  padding: 24px;
  border-top: 1px solid #e1e5e9;
}

.low-stock-alerts h4,
.expiry-warnings h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.alerts-list,
.warnings-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.alert-item,
.warning-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  gap: 16px;
}

.alert-info,
.warning-info {
  flex: 1;
}

.alert-info strong,
.warning-info strong {
  display: block;
  color: #2c3e50;
  margin-bottom: 2px;
}

.alert-info span,
.warning-info span {
  color: #7f8c8d;
  font-size: 12px;
}

.alert-details,
.warning-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 11px;
  color: #7f8c8d;
}

.warning-actions {
  display: flex;
  gap: 8px;
}

.reorder-now-btn,
.return-btn,
.extend-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 11px;
  transition: all 0.2s;
}

.reorder-now-btn {
  background: #27ae60;
  color: white;
}

.reorder-now-btn:hover {
  background: #229954;
}

.return-btn {
  background: #f39c12;
  color: white;
}

.return-btn:hover {
  background: #e67e22;
}

.extend-btn {
  background: #3498db;
  color: white;
}

.extend-btn:hover {
  background: #2980b9;
}

.btn-loader {
  width: 14px;
  height: 14px;
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
  .status-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .status-overview {
    grid-template-columns: repeat(2, 1fr);
  }

  .inventory-controls {
    flex-direction: column;
    align-items: stretch;
  }

  .status-filters {
    justify-content: center;
  }

  .alert-item,
  .warning-item {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .warning-actions {
    justify-content: center;
  }
}
</style>
