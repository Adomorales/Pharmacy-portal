<template>
  <div class="data-table-container">
    <!-- Table Header -->
    <div class="table-header">
      <div class="table-title">
        <h3>{{ title }}</h3>
        <span v-if="subtitle" class="table-subtitle">{{ subtitle }}</span>
      </div>

      <div class="table-actions">
        <!-- Search -->
        <div class="search-box" v-if="searchable">
          <SearchIcon class="search-icon" />
          <input
            v-model="searchQuery"
            type="text"
            :placeholder="searchPlaceholder"
            class="search-input"
            @input="handleSearch"
          />
          <button v-if="searchQuery" class="clear-search" @click="clearSearch">
            <XIcon />
          </button>
        </div>

        <!-- Filters -->
        <button
          v-if="filterable"
          class="filter-btn"
          @click="toggleFilters"
          :class="{ active: showFilters }"
        >
          <FilterIcon />
          Filters
          <span v-if="activeFilterCount > 0" class="filter-count">{{ activeFilterCount }}</span>
        </button>

        <!-- View Options -->
        <div class="view-options">
          <button
            class="view-btn"
            :class="{ active: viewMode === 'table' }"
            @click="setViewMode('table')"
            title="Table view"
          >
            <TableIcon />
          </button>
          <button
            class="view-btn"
            :class="{ active: viewMode === 'cards' }"
            @click="setViewMode('cards')"
            title="Card view"
          >
            <GridIcon />
          </button>
        </div>

        <!-- Custom Actions -->
        <slot name="table-actions"></slot>
      </div>
    </div>

    <!-- Filters Panel -->
    <div v-if="showFilters && filterable" class="filters-panel">
      <slot name="filters" :filters="filters" :updateFilter="updateFilter">
        <!-- Default filters if no custom slot provided -->
        <div class="default-filters">
          <FormField
            v-for="filter in availableFilters"
            :key="filter.key"
            :label="filter.label"
            :type="filter.type"
            :options="filter.options"
            :value="filters[filter.key]"
            @input="value => updateFilter(filter.key, value)"
          />
        </div>
      </slot>
    </div>

    <!-- Table Content -->
    <div class="table-content" :class="{ 'card-view': viewMode === 'cards' }">
      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <span>Loading {{ title.toLowerCase() }}...</span>
      </div>

      <!-- Empty State -->
      <div v-else-if="filteredData.length === 0" class="empty-state">
        <slot name="empty-state">
          <div class="default-empty">
            <SearchIcon class="empty-icon" />
            <h4>No {{ title.toLowerCase() }} found</h4>
            <p>{{ emptyMessage }}</p>
            <button v-if="creatable" class="btn btn-primary" @click="$emit('create-new')">
              Create New {{ title.slice(0, -1) }}
            </button>
          </div>
        </slot>
      </div>

      <!-- Table View -->
      <div v-else-if="viewMode === 'table'" class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th
                v-for="column in visibleColumns"
                :key="column.key"
                class="table-header-cell"
                :class="{ 'sortable': column.sortable }"
                @click="column.sortable && handleSort(column.key)"
              >
                <div class="header-content">
                  <span>{{ column.label }}</span>
                  <div v-if="column.sortable" class="sort-indicator">
                    <ChevronUpIcon v-if="sortBy === column.key && sortOrder === 'asc'" />
                    <ChevronDownIcon v-else-if="sortBy === column.key && sortOrder === 'desc'" />
                    <ChevronsUpDownIcon v-else />
                  </div>
                </div>
              </th>
              <th v-if="actions.length > 0" class="actions-header">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="item in paginatedData"
              :key="getItemKey(item)"
              class="table-row"
              :class="{ 'selected': selectedItems.includes(getItemKey(item)) }"
              @click="handleRowClick(item)"
            >
              <td
                v-for="column in visibleColumns"
                :key="column.key"
                class="table-cell"
              >
                <slot
                  :name="`cell-${column.key}`"
                  :item="item"
                  :value="getNestedValue(item, column.key)"
                >
                  {{ formatCellValue(getNestedValue(item, column.key), column) }}
                </slot>
              </td>
              <td v-if="actions.length > 0" class="actions-cell">
                <div class="row-actions">
                  <button
                    v-for="action in actions"
                    :key="action.key"
                    class="action-btn"
                    :class="action.class"
                    :title="action.label"
                    @click.stop="handleAction(action, item)"
                    :disabled="action.disabled && action.disabled(item)"
                  >
                    <component :is="action.icon" />
                    <span v-if="!action.iconOnly">{{ action.label }}</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Card View -->
      <div v-else class="cards-view">
        <div
          v-for="item in paginatedData"
          :key="getItemKey(item)"
          class="data-card"
          :class="{ 'selected': selectedItems.includes(getItemKey(item)) }"
          @click="handleRowClick(item)"
        >
          <slot name="card" :item="item">
            <!-- Default card layout -->
            <div class="card-header">
              <h4 class="card-title">{{ getNestedValue(item, 'title') || getNestedValue(item, 'name') }}</h4>
              <div class="card-badges">
                <span v-for="badge in getItemBadges(item)" :key="badge.key" class="badge" :class="badge.class">
                  {{ badge.label }}
                </span>
              </div>
            </div>
            <div class="card-content">
              <div v-for="field in cardFields" :key="field.key" class="card-field">
                <span class="field-label">{{ field.label }}:</span>
                <span class="field-value">{{ formatCellValue(getNestedValue(item, field.key), field) }}</span>
              </div>
            </div>
            <div v-if="actions.length > 0" class="card-actions">
              <button
                v-for="action in actions.slice(0, 2)"
                :key="action.key"
                class="action-btn"
                :class="action.class"
                @click.stop="handleAction(action, item)"
              >
                <component :is="action.icon" />
                {{ action.label }}
              </button>
            </div>
          </slot>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="showPagination && totalPages > 1" class="table-footer">
      <div class="pagination-info">
        Showing {{ startItem }}-{{ endItem }} of {{ totalItems }} {{ title.toLowerCase() }}
      </div>

      <Pagination
        :current-page="currentPage"
        :total-pages="totalPages"
        :show-page-numbers="showPageNumbers"
        @page-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import FormField from './FormField.vue'
import Pagination from './Pagination.vue'
import {
  SearchIcon,
  XIcon,
  FilterIcon,
  TableIcon,
  GridIcon,
  ChevronUpIcon,
  ChevronDownIcon,
  ChevronsUpDownIcon
} from '@/components/icons'

// Props
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  },
  data: {
    type: Array,
    default: () => []
  },
  columns: {
    type: Array,
    default: () => []
  },
  actions: {
    type: Array,
    default: () => []
  },
  searchable: {
    type: Boolean,
    default: true
  },
  filterable: {
    type: Boolean,
    default: false
  },
  sortable: {
    type: Boolean,
    default: true
  },
  selectable: {
    type: Boolean,
    default: false
  },
  creatable: {
    type: Boolean,
    default: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  searchPlaceholder: {
    type: String,
    default: 'Search...'
  },
  emptyMessage: {
    type: String,
    default: 'No items found matching your criteria.'
  },
  itemsPerPage: {
    type: Number,
    default: 10
  },
  cardFields: {
    type: Array,
    default: () => []
  },
  availableFilters: {
    type: Array,
    default: () => []
  },
  showPagination: {
    type: Boolean,
    default: true
  },
  showPageNumbers: {
    type: Boolean,
    default: true
  },
  getItemKey: {
    type: Function,
    default: (item) => item.id
  }
})

// Emits
const emit = defineEmits([
  'search',
  'filter',
  'sort',
  'select',
  'action',
  'create-new',
  'page-change'
])

// State
const searchQuery = ref('')
const showFilters = ref(false)
const filters = ref({})
const sortBy = ref('')
const sortOrder = ref('asc')
const currentPage = ref(1)
const selectedItems = ref([])
const viewMode = ref('table')

// Computed
const visibleColumns = computed(() => {
  return props.columns.filter(col => !col.hidden)
})

const filteredData = computed(() => {
  let data = [...props.data]

  // Apply search
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    data = data.filter(item =>
      props.columns.some(col => {
        const value = getNestedValue(item, col.key)
        return String(value).toLowerCase().includes(query)
      })
    )
  }

  // Apply filters
  Object.keys(filters.value).forEach(key => {
    const filterValue = filters.value[key]
    if (filterValue) {
      data = data.filter(item => {
        const value = getNestedValue(item, key)
        return String(value).toLowerCase().includes(String(filterValue).toLowerCase())
      })
    }
  })

  // Apply sorting
  if (sortBy.value) {
    data.sort((a, b) => {
      const aVal = getNestedValue(a, sortBy.value)
      const bVal = getNestedValue(b, sortBy.value)

      let comparison = 0
      if (aVal < bVal) comparison = -1
      if (aVal > bVal) comparison = 1

      return sortOrder.value === 'desc' ? -comparison : comparison
    })
  }

  return data
})

const totalItems = computed(() => filteredData.value.length)

const totalPages = computed(() => Math.ceil(totalItems.value / props.itemsPerPage))

const startItem = computed(() => {
  return (currentPage.value - 1) * props.itemsPerPage + 1
})

const endItem = computed(() => {
  return Math.min(currentPage.value * props.itemsPerPage, totalItems.value)
})

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * props.itemsPerPage
  return filteredData.value.slice(start, start + props.itemsPerPage)
})

const activeFilterCount = computed(() => {
  return Object.values(filters.value).filter(value => value && value.toString().trim()).length
})

// Methods
const getNestedValue = (obj, path) => {
  return path.split('.').reduce((current, key) => current?.[key], obj)
}

const formatCellValue = (value, column) => {
  if (column.formatter) {
    return column.formatter(value)
  }

  if (value === null || value === undefined) {
    return column.placeholder || '-'
  }

  return value
}

const getItemBadges = (item) => {
  // Return badges for card view
  return []
}

const handleSearch = () => {
  currentPage.value = 1
  emit('search', searchQuery.value)
}

const clearSearch = () => {
  searchQuery.value = ''
  handleSearch()
}

const toggleFilters = () => {
  showFilters.value = !showFilters.value
}

const updateFilter = (key, value) => {
  filters.value[key] = value
  currentPage.value = 1
  emit('filter', filters.value)
}

const handleSort = (column) => {
  if (sortBy.value === column) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = column
    sortOrder.value = 'asc'
  }
  emit('sort', { column, order: sortOrder.value })
}

const handleRowClick = (item) => {
  if (props.selectable) {
    const key = props.getItemKey(item)
    const index = selectedItems.value.indexOf(key)
    if (index > -1) {
      selectedItems.value.splice(index, 1)
    } else {
      selectedItems.value.push(key)
    }
    emit('select', selectedItems.value)
  }
}

const handleAction = (action, item) => {
  emit('action', { action: action.key, item })
}

const handlePageChange = (page) => {
  currentPage.value = page
  emit('page-change', page)
}

const setViewMode = (mode) => {
  viewMode.value = mode
}

// Watch for data changes to reset pagination
watch(() => props.data, () => {
  currentPage.value = 1
})
</script>

<style scoped>
.data-table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e1e5e9;
}

.table-title h3 {
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.table-subtitle {
  color: #7f8c8d;
  font-size: 14px;
}

.table-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  padding: 8px 12px 8px 36px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  font-size: 14px;
  width: 200px;
  transition: all 0.2s;
}

.search-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.search-icon {
  position: absolute;
  left: 12px;
  color: #7f8c8d;
  width: 16px;
  height: 16px;
}

.clear-search {
  position: absolute;
  right: 8px;
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: #7f8c8d;
  border-radius: 4px;
}

.clear-search:hover {
  background-color: #f8f9fa;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: none;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.filter-btn:hover {
  border-color: #3498db;
}

.filter-btn.active {
  background: #e3f2fd;
  border-color: #3498db;
  color: #1976d2;
}

.filter-count {
  background: #e74c3c;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 16px;
  text-align: center;
}

.view-options {
  display: flex;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  overflow: hidden;
}

.view-btn {
  padding: 8px 12px;
  background: none;
  border: none;
  cursor: pointer;
  color: #7f8c8d;
  transition: all 0.2s;
}

.view-btn:hover {
  background-color: #f8f9fa;
}

.view-btn.active {
  background: #3498db;
  color: white;
}

.filters-panel {
  padding: 20px 24px;
  border-bottom: 1px solid #e1e5e9;
  background: #f8f9fa;
}

.default-filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.table-content {
  min-height: 200px;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

.empty-icon {
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
  color: #bdc3c7;
}

.default-empty h4 {
  margin: 0 0 8px 0;
  color: #2c3e50;
}

.default-empty p {
  margin: 0 0 20px 0;
  text-align: center;
}

.table-wrapper {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.table-header-cell {
  padding: 16px 12px;
  text-align: left;
  background: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #e1e5e9;
  white-space: nowrap;
}

.table-header-cell.sortable {
  cursor: pointer;
  user-select: none;
}

.table-header-cell.sortable:hover {
  background: #e1e5e9;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-indicator {
  opacity: 0.5;
}

.table-row {
  border-bottom: 1px solid #f8f9fa;
  transition: background-color 0.2s;
}

.table-row:hover {
  background-color: #f8f9fa;
}

.table-cell {
  padding: 16px 12px;
  vertical-align: middle;
}

.actions-header {
  width: 120px;
}

.actions-cell {
  text-align: center;
}

.row-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn {
  padding: 6px 8px;
  background: none;
  border: 1px solid #e1e5e9;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.action-btn:hover {
  border-color: #3498db;
  color: #3498db;
}

.cards-view {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px 24px;
}

.data-card {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.data-card:hover {
  border-color: #3498db;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.15);
}

.data-card.selected {
  border-color: #3498db;
  background: #e3f2fd;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.card-title {
  margin: 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.card-badges {
  display: flex;
  gap: 8px;
}

.badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 500;
  text-transform: uppercase;
}

.badge.success {
  background: #d4edda;
  color: #155724;
}

.badge.warning {
  background: #fff3cd;
  color: #856404;
}

.card-content {
  margin-bottom: 16px;
}

.card-field {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.field-label {
  color: #7f8c8d;
  font-weight: 500;
}

.field-value {
  color: #2c3e50;
  font-weight: 400;
}

.card-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-top: 1px solid #e1e5e9;
  background: #f8f9fa;
}

.pagination-info {
  color: #7f8c8d;
  font-size: 14px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Mobile responsive */
@media (max-width: 768px) {
  .table-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .table-actions {
    justify-content: space-between;
  }

  .search-input {
    width: 150px;
  }

  .cards-view {
    grid-template-columns: 1fr;
    padding: 16px 20px;
  }

  .table-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
}
</style>
