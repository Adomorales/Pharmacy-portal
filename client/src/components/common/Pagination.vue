<template>
  <div class="pagination">
    <!-- Previous Button -->
    <button
      class="pagination-btn pagination-prev"
      @click="$emit('page-change', currentPage - 1)"
      :disabled="currentPage <= 1"
      aria-label="Previous page"
    >
      <ChevronLeftIcon />
      Previous
    </button>

    <!-- Page Numbers -->
    <div v-if="showPageNumbers" class="pagination-pages">
      <!-- First Page -->
      <button
        v-if="showFirstPage"
        class="pagination-btn pagination-number"
        @click="$emit('page-change', 1)"
        :class="{ active: currentPage === 1 }"
      >
        1
      </button>

      <!-- First Ellipsis -->
      <span v-if="showFirstEllipsis" class="pagination-ellipsis">...</span>

      <!-- Page Range -->
      <button
        v-for="page in visiblePages"
        :key="page"
        class="pagination-btn pagination-number"
        @click="$emit('page-change', page)"
        :class="{ active: currentPage === page }"
      >
        {{ page }}
      </button>

      <!-- Last Ellipsis -->
      <span v-if="showLastEllipsis" class="pagination-ellipsis">...</span>

      <!-- Last Page -->
      <button
        v-if="showLastPage"
        class="pagination-btn pagination-number"
        @click="$emit('page-change', totalPages)"
        :class="{ active: currentPage === totalPages }"
      >
        {{ totalPages }}
      </button>
    </div>

    <!-- Next Button -->
    <button
      class="pagination-btn pagination-next"
      @click="$emit('page-change', currentPage + 1)"
      :disabled="currentPage >= totalPages"
      aria-label="Next page"
    >
      Next
      <ChevronRightIcon />
    </button>

    <!-- Page Size Selector -->
    <div v-if="showPageSizeSelector" class="page-size-selector">
      <select
        :value="pageSize"
        @change="$emit('page-size-change', parseInt($event.target.value))"
        class="page-size-select"
      >
        <option v-for="size in pageSizeOptions" :key="size" :value="size">
          {{ size }} per page
        </option>
      </select>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ChevronLeftIcon, ChevronRightIcon } from '@/components/icons'

// Props
const props = defineProps({
  currentPage: {
    type: Number,
    required: true
  },
  totalPages: {
    type: Number,
    required: true
  },
  showPageNumbers: {
    type: Boolean,
    default: true
  },
  showPageSizeSelector: {
    type: Boolean,
    default: false
  },
  pageSize: {
    type: Number,
    default: 10
  },
  pageSizeOptions: {
    type: Array,
    default: () => [5, 10, 20, 50, 100]
  },
  maxVisiblePages: {
    type: Number,
    default: 7
  }
})

// Emits
defineEmits(['page-change', 'page-size-change'])

// Computed
const showFirstPage = computed(() => {
  return props.totalPages > 1 && !props.visiblePages.includes(1)
})

const showLastPage = computed(() => {
  return props.totalPages > 1 && !props.visiblePages.includes(props.totalPages)
})

const showFirstEllipsis = computed(() => {
  return props.visiblePages[0] > 2
})

const showLastEllipsis = computed(() => {
  return props.visiblePages[props.visiblePages.length - 1] < props.totalPages - 1
})

const visiblePages = computed(() => {
  if (!props.showPageNumbers || props.totalPages <= props.maxVisiblePages) {
    return []
  }

  const pages = []
  const halfVisible = Math.floor(props.maxVisiblePages / 2)
  let startPage = Math.max(2, props.currentPage - halfVisible)
  let endPage = Math.min(props.totalPages - 1, props.currentPage + halfVisible)

  // Adjust range if we're near the beginning or end
  if (props.currentPage <= halfVisible) {
    endPage = props.maxVisiblePages - 1
  } else if (props.currentPage >= props.totalPages - halfVisible) {
    startPage = props.totalPages - props.maxVisiblePages + 2
  }

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i)
  }

  return pages
})
</script>

<style scoped>
.pagination {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.pagination-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  color: #2c3e50;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 40px;
  justify-content: center;
}

.pagination-btn:hover:not(:disabled) {
  border-color: #3498db;
  color: #3498db;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  color: #7f8c8d;
}

.pagination-btn.active {
  background: #3498db;
  border-color: #3498db;
  color: white;
}

.pagination-prev,
.pagination-next {
  min-width: 100px;
}

.pagination-pages {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-number {
  min-width: 40px;
}

.pagination-ellipsis {
  padding: 0 8px;
  color: #7f8c8d;
  font-size: 14px;
}

.page-size-selector {
  margin-left: auto;
}

.page-size-select {
  padding: 8px 12px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
  background: white;
  color: #2c3e50;
  font-size: 14px;
  cursor: pointer;
}

.page-size-select:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

/* Mobile responsive */
@media (max-width: 768px) {
  .pagination {
    justify-content: center;
  }

  .pagination-pages {
    order: 3;
    width: 100%;
    justify-content: center;
    margin-top: 8px;
  }

  .page-size-selector {
    margin-left: 0;
    margin-top: 8px;
  }

  .pagination-prev,
  .pagination-next {
    min-width: 80px;
    font-size: 13px;
  }
}
</style>
