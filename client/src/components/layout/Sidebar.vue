<template>
  <aside class="sidebar" :class="{ 'collapsed': collapsed }">
    <nav class="sidebar-nav">
      <ul class="nav-list">
        <li v-for="item in filteredNavItems" :key="item.route" class="nav-item">
          <router-link
            :to="item.route"
            class="nav-link"
            :class="{ 'active': isActiveRoute(item.route) }"
          >
            <component :is="item.icon" class="nav-icon" />
            <span class="nav-label">{{ item.label }}</span>
            <span v-if="getBadgeCount(item.route)" class="nav-badge">
              {{ getBadgeCount(item.route) }}
            </span>
          </router-link>
        </li>
      </ul>
    </nav>

    <!-- Sidebar Footer -->
    <div class="sidebar-footer">
      <div class="footer-section">
        <button class="footer-btn" @click="toggleTheme" title="Toggle theme">
          <MoonIcon v-if="isDarkTheme" />
          <SunIcon v-else />
        </button>
        <button class="footer-btn" @click="openSettings" title="Settings">
          <SettingsIcon />
        </button>
      </div>

      <div class="footer-info">
        <small class="version">v1.0.0</small>
        <small class="status" :class="systemStatus.class">
          ● {{ systemStatus.text }}
        </small>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/store'

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
  MoonIcon,
  SunIcon,
  SettingsIcon
} from '@/components/icons'

// Props
defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

// Emits
defineEmits(['toggle'])

// Route
const route = useRoute()
const authStore = useAuthStore()

// State
const isDarkTheme = ref(false)
const systemStatus = ref({
  text: 'Online',
  class: 'status-online'
})

// Navigation items
const navigationItems = [
  {
    label: 'Dashboard',
    icon: HomeIcon,
    route: '/dashboard',
    permission: null,
    badge: null
  },
  {
    label: 'Patients',
    icon: UsersIcon,
    route: '/patients',
    permission: 'PATIENT_READ',
    badge: 'patients'
  },
  {
    label: 'Prescriptions',
    icon: ClipboardIcon,
    route: '/prescriptions',
    permission: 'PRESCRIPTION_READ',
    badge: 'prescriptions'
  },
  {
    label: 'Medications',
    icon: PillIcon,
    route: '/medications',
    permission: 'MEDICATION_READ',
    badge: 'medications'
  },
  {
    label: 'Inventory',
    icon: ShoppingCartIcon,
    route: '/inventory',
    permission: 'INVENTORY_READ',
    badge: 'inventory'
  },
  {
    label: 'Vaccinations',
    icon: CalendarIcon,
    route: '/vaccinations',
    permission: 'VACCINE_READ',
    badge: 'vaccinations'
  },
  {
    label: 'Reports',
    icon: BarChartIcon,
    route: '/reports',
    permission: 'REPORT_READ',
    badge: null
  },
  {
    label: 'Insurance',
    icon: FileTextIcon,
    route: '/insurance',
    permission: 'INSURANCE_READ',
    badge: null
  }
]

// Computed
const filteredNavItems = computed(() => {
  return navigationItems.filter(item => hasPermission(item.permission))
})

const hasPermission = (permission) => {
  if (!permission) return true
  // Implement permission checking based on user role
  const userRole = authStore.userRole
  const rolePermissions = {
    'ADMIN': ['PATIENT_READ', 'PRESCRIPTION_READ', 'MEDICATION_READ', 'INVENTORY_READ', 'VACCINE_READ', 'REPORT_READ', 'INSURANCE_READ'],
    'PHARMACIST': ['PATIENT_READ', 'PRESCRIPTION_READ', 'MEDICATION_READ', 'VACCINE_READ'],
    'TECHNICIAN': ['PATIENT_READ', 'PRESCRIPTION_READ'],
    'INTERN': ['PATIENT_READ']
  }

  return rolePermissions[userRole]?.includes(permission) || false
}

// Methods
const isActiveRoute = (routePath) => {
  if (routePath === '/dashboard') {
    return route.path === '/' || route.path === '/dashboard'
  }
  return route.path.startsWith(routePath)
}

const getBadgeCount = (routePath) => {
  // Mock badge counts - in real app, these would come from stores
  const badges = {
    '/patients': 12,
    '/prescriptions': 5,
    '/medications': 3,
    '/inventory': 2,
    '/vaccinations': 8
  }
  return badges[routePath] || null
}

const toggleTheme = () => {
  isDarkTheme.value = !isDarkTheme.value
  // Implement theme switching logic
  document.documentElement.classList.toggle('dark-theme')
}

const openSettings = () => {
  // Navigate to settings or open settings modal
  console.log('Open settings')
}

// Simulate system status updates
onMounted(() => {
  // In a real app, this would connect to a WebSocket or polling service
  setInterval(() => {
    // Simulate status changes
    if (Math.random() > 0.95) {
      systemStatus.value = {
        text: 'Maintenance',
        class: 'status-maintenance'
      }
    } else {
      systemStatus.value = {
        text: 'Online',
        class: 'status-online'
      }
    }
  }, 30000) // Update every 30 seconds
})
</script>

<style scoped>
.sidebar {
  width: 250px;
  height: calc(100vh - 64px);
  background: white;
  border-right: 1px solid #e1e5e9;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 64px;
  z-index: 90;
  transition: width 0.3s ease;
}

.sidebar.collapsed {
  width: 60px;
}

.sidebar-nav {
  flex: 1;
  padding: 16px 0;
  overflow-y: auto;
}

.nav-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-item {
  margin-bottom: 4px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: #5a6c7d;
  text-decoration: none;
  transition: all 0.2s;
  position: relative;
  border-left: 3px solid transparent;
}

.nav-link:hover {
  background-color: #f8f9fa;
  color: #2c3e50;
}

.nav-link.active {
  background-color: #e3f2fd;
  color: #1976d2;
  border-left-color: #1976d2;
}

.nav-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.nav-label {
  flex: 1;
  font-weight: 500;
  white-space: nowrap;
  opacity: 1;
  transition: opacity 0.3s ease;
}

.sidebar.collapsed .nav-label {
  opacity: 0;
  display: none;
}

.nav-badge {
  background: #e74c3c;
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 16px;
  text-align: center;
  font-weight: 600;
}

.sidebar-footer {
  border-top: 1px solid #e1e5e9;
  padding: 16px 20px;
  background: #f8f9fa;
}

.footer-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.footer-btn {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: #7f8c8d;
  border-radius: 4px;
  transition: all 0.2s;
}

.footer-btn:hover {
  background-color: #e1e5e9;
  color: #2c3e50;
}

.footer-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.version {
  color: #95a5a6;
  font-size: 10px;
}

.status {
  font-size: 10px;
  font-weight: 500;
}

.status-online {
  color: #27ae60;
}

.status-maintenance {
  color: #f39c12;
}

.sidebar.collapsed .sidebar-footer {
  padding: 16px 8px;
}

.sidebar.collapsed .footer-section {
  justify-content: center;
}

.sidebar.collapsed .footer-info {
  flex-direction: column;
  gap: 4px;
  text-align: center;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    z-index: 200;
  }

  .sidebar:not(.collapsed) {
    transform: translateX(0);
  }
}
</style>
