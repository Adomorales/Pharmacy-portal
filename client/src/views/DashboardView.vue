<template>
  <div class="dashboard">
    <!-- Header -->
    <div class="dashboard-header">
      <h1>Pharmacy Portal</h1>
      <div class="user-info">
        <span>Welcome, {{ currentUser?.firstName }} {{ currentUser?.lastName }}</span>
        <button @click="logout" class="logout-btn">Logout</button>
      </div>
    </div>

    <!-- Main Buttons Section -->
    <div class="main-buttons-section">
      <h2>Main Functions</h2>
      <div class="main-buttons">
        <button
          v-for="button in mainButtons"
          :key="button.key"
          @click="navigateTo(button.route)"
          class="main-button"
          :class="{ 'disabled': !hasPermission(button.permission) }"
        >
          <div class="button-icon">
            <component :is="button.icon" />
          </div>
          <div class="button-content">
            <div class="button-title">{{ button.title }}</div>
            <div class="button-subtitle">{{ button.description }}</div>
          </div>
          <div class="button-count" :class="getCountClass(button.count)">
            {{ button.count }}
          </div>
        </button>
      </div>
    </div>

    <!-- Secondary Buttons Section -->
    <div class="secondary-buttons-section">
      <h2>Quick Actions</h2>
      <div class="secondary-buttons">
        <button
          v-for="button in secondaryButtons"
          :key="button.key"
          @click="navigateTo(button.route)"
          class="secondary-button"
          :class="{ 'disabled': !hasPermission(button.permission) }"
        >
          <div class="button-icon">
            <component :is="button.icon" />
          </div>
          <div class="button-title">{{ button.title }}</div>
        </button>
      </div>
    </div>

    <!-- Prescription Skeleton (always visible) -->
    <PrescriptionSkeleton />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store'
import PrescriptionSkeleton from '@/components/pharmacy/PrescriptionSkeleton.vue'

// Icons
import {
  UserPlusIcon,
  FileTextIcon,
  PackageIcon,
  CheckCircleIcon,
  ShieldIcon,
  BellIcon,
  ClipboardListIcon,
  UsersIcon,
  ArchiveIcon,
  CreditCardIcon,
  AlertTriangleIcon,
  ActivityIcon
} from '@/components/icons'

const router = useRouter()
const authStore = useAuthStore()

// Main buttons configuration
const mainButtons = [
  {
    key: 'data-entry',
    title: 'Data Entry',
    description: 'Enter patient and prescription data',
    route: '/data-entry',
    icon: ClipboardListIcon,
    permission: 'PRESCRIPTION_CREATE',
    count: 12
  },
  {
    key: 'data-review',
    title: 'Data Review',
    description: 'Review and approve prescriptions',
    route: '/data-review',
    icon: CheckCircleIcon,
    permission: 'PRESCRIPTION_REVIEW',
    count: 8
  },
  {
    key: 'product',
    title: 'Product',
    description: 'Process medications for pickup',
    route: '/product',
    icon: PackageIcon,
    permission: 'PRESCRIPTION_PROCESS',
    count: 5
  },
  {
    key: 'product-review',
    title: 'Product Review',
    description: 'Review processed medications',
    route: '/product-review',
    icon: FileTextIcon,
    permission: 'PRESCRIPTION_REVIEW',
    count: 3
  },
  {
    key: 'vaccine',
    title: 'Vaccine',
    description: 'Manage vaccine appointments',
    route: '/vaccine',
    icon: ShieldIcon,
    permission: 'VACCINE_MANAGE',
    count: 7
  },
  {
    key: 'notification',
    title: 'Notification',
    description: 'View system notifications',
    route: '/notification',
    icon: BellIcon,
    permission: 'NOTIFICATION_VIEW',
    count: 15
  }
]

// Secondary buttons configuration (2x3 grid)
const secondaryButtons = [
  {
    key: 'new-prescription',
    title: 'New Prescription',
    route: '/new-prescription',
    icon: FileTextIcon,
    permission: 'PRESCRIPTION_CREATE'
  },
  {
    key: 'new-patient',
    title: 'New Patient',
    route: '/new-patient',
    icon: UserPlusIcon,
    permission: 'PATIENT_CREATE'
  },
  {
    key: 'inventory',
    title: 'Inventory',
    route: '/inventory',
    icon: ArchiveIcon,
    permission: 'INVENTORY_VIEW'
  },
  {
    key: 'confirmation',
    title: 'Confirmation',
    route: '/confirmation',
    icon: CheckCircleIcon,
    permission: 'PRESCRIPTION_CONFIRM'
  },
  {
    key: 'insurance',
    title: 'Insurance',
    route: '/insurance',
    icon: CreditCardIcon,
    permission: 'INSURANCE_MANAGE'
  },
  {
    key: 'recall-hazardous',
    title: 'Recall/Hazardous',
    route: '/recall-hazardous',
    icon: AlertTriangleIcon,
    permission: 'RECALL_VIEW'
  }
]

// Computed
const currentUser = computed(() => authStore.currentUser)

// Methods
const navigateTo = (route) => {
  if (route) {
    router.push(route)
  }
}

const hasPermission = (permission) => {
  return authStore.hasPermission(permission)
}

const getCountClass = (count) => {
  const minutes = 5 // Mock calculation - replace with actual time logic
  if (minutes <= 5) return 'new'
  if (minutes <= 10) return 'recent'
  return 'old'
}

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.dashboard-header h1 {
  margin: 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #7f8c8d;
}

.logout-btn {
  padding: 8px 16px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.logout-btn:hover {
  background: #c0392b;
}

.main-buttons-section,
.secondary-buttons-section {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.main-buttons-section h2,
.secondary-buttons-section h2 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  font-weight: 600;
}

.main-buttons {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.main-button {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: white;
  border: 2px solid #e1e5e9;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
  position: relative;
}

.main-button:hover:not(.disabled) {
  border-color: #3498db;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.15);
  transform: translateY(-2px);
}

.main-button.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.button-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(45deg, #00d4aa, #00a0ff);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.button-content {
  flex: 1;
}

.button-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.button-subtitle {
  font-size: 14px;
  color: #7f8c8d;
}

.button-count {
  padding: 8px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 700;
  min-width: 40px;
  text-align: center;
}

.button-count.new {
  background: #d4edda;
  color: #155724;
}

.button-count.recent {
  background: #fff3cd;
  color: #856404;
}

.button-count.old {
  background: #f8d7da;
  color: #721c24;
}

.secondary-buttons {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.secondary-button {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: white;
  border: 2px solid #e1e5e9;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
}

.secondary-button:hover:not(.disabled) {
  border-color: #3498db;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.15);
  transform: translateY(-2px);
}

.secondary-button.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.secondary-button .button-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(45deg, #ffd700, #ff6b35);
}

.secondary-button .button-title {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
    gap: 20px;
  }

  .dashboard-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .secondary-buttons {
    grid-template-columns: repeat(2, 1fr);
  }

  .main-button {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .button-content {
    text-align: center;
  }
}
</style>
