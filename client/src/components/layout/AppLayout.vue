<template>
  <div class="app-layout">
    
    <TopNav />

    <!-- Main Content Area -->
    <div class="main-container">
      
      <Sidebar
        :collapsed="sidebarCollapsed"
        @toggle="sidebarCollapsed = !sidebarCollapsed"
      />

      <!-- Page Content -->
      <main class="page-content" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
        <!-- Breadcrumb Navigation -->
        <Breadcrumbs v-if="showBreadcrumbs" />

        <ErrorBoundary>
          <PrescriptionSkeleton />
          <router-view />
        </ErrorBoundary>

      </main>
    </div>

    <LoadingSpinner v-if="globalLoading" />
    
    <NotificationContainer />
  
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/store'
import TopNav from '@/components/layout/TopNav.vue'
import Sidebar from '@/components/layout/Sidebar.vue'
import Breadcrumbs from '@/components/layout/Breadcrumbs.vue'
import ErrorBoundary from '@/components/common/ErrorBoundary.vue'
import PrescriptionSkeleton from '@/components/pharmacy/PrescriptionSkeleton.vue'
import LoadingSpinner from '@/components/common/LoadingSpinner.vue'
import NotificationContainer from '@/components/common/NotificationContainer.vue'

import {
  MenuIcon,
  BellIcon,
  UserIcon,
  LogOutIcon,
  SettingsIcon,
  HomeIcon,
  UsersIcon,
  PillIcon,
  ClipboardIcon,
  CalendarIcon,
  BarChartIcon,
  FileTextIcon,
  ShoppingCartIcon,
  AlertCircleIcon
} from '@/components/icons'

const route = useRoute()
const authStore = useAuthStore()

const sidebarCollapsed = ref(false)
const globalLoading = ref(false)

const showBreadcrumbs = computed(() => {
  const hiddenRoutes = ['/', '/login', '/dashboard']
  return !hiddenRoutes.includes(route.path)
})

const navigationItems = [
  {
    label: 'Dashboard',
    icon: HomeIcon,
    route: '/dashboard',
    permission: null
  },
  {
    label: 'Patients',
    icon: UsersIcon,
    route: '/patients',
    permission: 'PATIENT_READ'
  },
  {
    label: 'Prescriptions',
    icon: ClipboardIcon,
    route: '/prescriptions',
    permission: 'PRESCRIPTION_READ'
  },
  {
    label: 'Medications',
    icon: PillIcon,
    route: '/medications',
    permission: 'MEDICATION_READ'
  },
  {
    label: 'Inventory',
    icon: ShoppingCartIcon,
    route: '/inventory',
    permission: 'INVENTORY_READ'
  },
  {
    label: 'Vaccinations',
    icon: CalendarIcon,
    route: '/vaccinations',
    permission: 'VACCINE_READ'
  },
  {
    label: 'Reports',
    icon: BarChartIcon,
    route: '/reports',
    permission: 'REPORT_READ'
  },
  {
    label: 'Insurance',
    icon: FileTextIcon,
    route: '/insurance',
    permission: 'INSURANCE_READ'
  }
]

const hasPermission = (permission) => {
  if (!permission) return true
  return authStore.userRole !== 'PHARMACIST' 
}

const filteredNavItems = computed(() => {
  return navigationItems.filter(item => hasPermission(item.permission))
})
const handleResize = () => {
  if (window.innerWidth < 768) {
    sidebarCollapsed.value = true
  }
}

onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.main-container {
  display: flex;
  min-height: calc(100vh - 64px);
}

.page-content {
  flex: 1;
  padding: 20px;
  margin-left: 250px;
  transition: margin-left 0.3s ease;
  position: relative;
}

.page-content.sidebar-collapsed {
  margin-left: 60px;
}

@media (max-width: 768px) {
  .page-content {
    margin-left: 0;
    padding: 15px;
  }

  .page-content.sidebar-collapsed {
    margin-left: 0;
  }
}

@media (max-width: 480px) {
  .page-content {
    padding: 10px;
  }
}
</style>
