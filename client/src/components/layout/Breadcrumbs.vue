<template>
  <nav class="breadcrumbs" v-if="breadcrumbs.length > 0">
    <ol class="breadcrumb-list">
      <li v-for="(crumb, index) in breadcrumbs" :key="crumb.path" class="breadcrumb-item">
        <router-link
          v-if="index < breadcrumbs.length - 1"
          :to="crumb.path"
          class="breadcrumb-link"
        >
          <component :is="crumb.icon" v-if="crumb.icon" class="breadcrumb-icon" />
          {{ crumb.label }}
        </router-link>

        <span v-else class="breadcrumb-current">
          <component :is="crumb.icon" v-if="crumb.icon" class="breadcrumb-icon" />
          {{ crumb.label }}
        </span>

        <ChevronRightIcon
          v-if="index < breadcrumbs.length - 1"
          class="breadcrumb-separator"
        />
      </li>
    </ol>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

// Component imports
import {
  HomeIcon,
  UsersIcon,
  PillIcon,
  ClipboardIcon,
  CalendarIcon,
  BarChartIcon,
  FileTextIcon,
  ShoppingCartIcon,
  AlertCircleIcon,
  ChevronRightIcon
} from '@/components/icons'

// Route
const route = useRoute()

// Breadcrumb mapping
const routeBreadcrumbs = {
  '/': {
    label: 'Dashboard',
    icon: HomeIcon
  },
  '/dashboard': {
    label: 'Dashboard',
    icon: HomeIcon
  },
  '/patients': {
    label: 'Patients',
    icon: UsersIcon
  },
  '/patients/new': {
    label: 'New Patient',
    icon: UsersIcon
  },
  '/patients/:id': {
    label: 'Patient Details',
    icon: UsersIcon
  },
  '/patients/:id/edit': {
    label: 'Edit Patient',
    icon: UsersIcon
  },
  '/prescriptions': {
    label: 'Prescriptions',
    icon: ClipboardIcon
  },
  '/prescriptions/new': {
    label: 'New Prescription',
    icon: ClipboardIcon
  },
  '/prescriptions/:id': {
    label: 'Prescription Details',
    icon: ClipboardIcon
  },
  '/medications': {
    label: 'Medications',
    icon: PillIcon
  },
  '/medications/:id': {
    label: 'Medication Details',
    icon: PillIcon
  },
  '/inventory': {
    label: 'Inventory',
    icon: ShoppingCartIcon
  },
  '/vaccinations': {
    label: 'Vaccinations',
    icon: CalendarIcon
  },
  '/vaccinations/new': {
    label: 'New Appointment',
    icon: CalendarIcon
  },
  '/reports': {
    label: 'Reports',
    icon: BarChartIcon
  },
  '/insurance': {
    label: 'Insurance',
    icon: FileTextIcon
  },
  '/settings': {
    label: 'Settings',
    icon: SettingsIcon
  }
}

// Generate dynamic breadcrumbs based on route
const breadcrumbs = computed(() => {
  const crumbs = []
  const pathSegments = route.path.split('/').filter(Boolean)

  // Add home/dashboard crumb
  if (route.path !== '/') {
    crumbs.push(routeBreadcrumbs['/'] || { label: 'Dashboard', icon: HomeIcon })
  }

  // Build breadcrumbs from path segments
  let currentPath = ''
  pathSegments.forEach((segment, index) => {
    currentPath += `/${segment}`

    // Handle dynamic routes (like :id)
    const breadcrumbKey = Object.keys(routeBreadcrumbs).find(key => {
      const keySegments = key.split('/').filter(Boolean)
      if (keySegments.length !== pathSegments.length) return false

      return keySegments.every((keySegment, keyIndex) => {
        if (keySegment.startsWith(':')) return true
        return keySegment === pathSegments[keyIndex]
      })
    })

    if (breadcrumbKey) {
      const breadcrumb = routeBreadcrumbs[breadcrumbKey]

      // For dynamic routes, customize label based on params
      let label = breadcrumb.label
      let icon = breadcrumb.icon

      if (breadcrumbKey.includes('/:id')) {
        const param = route.params.id
        if (param) {
          // You could fetch entity name from store here
          label = `${label} #${param}`
        }
      }

      crumbs.push({
        path: currentPath,
        label,
        icon
      })
    }
  })

  return crumbs
})

// Helper function to get entity name for dynamic routes
const getEntityName = (type, id) => {
  // This would typically fetch from store
  switch (type) {
    case 'patient':
      return `Patient #${id}`
    case 'prescription':
      return `Prescription #${id}`
    case 'medication':
      return `Medication #${id}`
    default:
      return `${type} #${id}`
  }
}
</script>

<style scoped>
.breadcrumbs {
  margin-bottom: 20px;
  padding: 12px 0;
}

.breadcrumb-list {
  display: flex;
  align-items: center;
  list-style: none;
  margin: 0;
  padding: 0;
  flex-wrap: wrap;
  gap: 4px;
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.breadcrumb-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  color: #7f8c8d;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.2s;
  font-size: 14px;
}

.breadcrumb-link:hover {
  background-color: #f8f9fa;
  color: #2c3e50;
}

.breadcrumb-current {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  color: #2c3e50;
  font-weight: 500;
  font-size: 14px;
  background-color: #e3f2fd;
  border-radius: 6px;
}

.breadcrumb-icon {
  width: 16px;
  height: 16px;
}

.breadcrumb-separator {
  color: #bdc3c7;
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .breadcrumbs {
    margin-bottom: 16px;
    padding: 8px 0;
  }

  .breadcrumb-list {
    gap: 2px;
  }

  .breadcrumb-link,
  .breadcrumb-current {
    padding: 4px 8px;
    font-size: 13px;
  }

  .breadcrumb-icon {
    width: 14px;
    height: 14px;
  }

  .breadcrumb-separator {
    width: 14px;
    height: 14px;
  }
}
</style>
