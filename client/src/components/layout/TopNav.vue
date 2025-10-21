<template>
  <nav class="top-nav">
    <div class="nav-left">
      <!-- Mobile menu toggle -->
      <button
        class="mobile-menu-btn"
        @click="$emit('toggleSidebar')"
        aria-label="Toggle sidebar"
      >
        <MenuIcon />
      </button>

      <!-- Logo/Brand -->
      <div class="brand">
        <PharmacyIcon />
        <span class="brand-text">Pharmacy Portal</span>
      </div>
    </div>

    <div class="nav-center">
      <!-- Search Bar (optional) -->
      <div class="search-container" v-if="showSearch">
        <SearchBar
          v-model="searchQuery"
          @search="handleSearch"
          placeholder="Search patients, medications..."
        />
      </div>
    </div>

    <div class="nav-right">
      <!-- Quick Actions -->
      <div class="quick-actions">
        <button
          class="action-btn"
          @click="toggleSearch"
          :class="{ active: showSearch }"
          title="Search"
        >
          <SearchIcon />
        </button>

        <button class="action-btn" title="Notifications">
          <BellIcon />
          <span v-if="unreadNotifications > 0" class="notification-badge">
            {{ unreadNotifications }}
          </span>
        </button>

        <button class="action-btn" title="New Prescription" @click="newPrescription">
          <PlusIcon />
        </button>
      </div>

      <!-- User Menu -->
      <div class="user-menu">
        <div class="user-info" @click="toggleUserDropdown">
          <div class="user-avatar">
            <UserIcon />
          </div>
          <div class="user-details">
            <span class="user-name">{{ currentUser?.name || 'User' }}</span>
            <span class="user-role">{{ currentUser?.role || 'Staff' }}</span>
          </div>
          <ChevronDownIcon class="dropdown-arrow" :class="{ rotated: showUserDropdown }" />
        </div>

        <!-- User Dropdown -->
        <div v-if="showUserDropdown" class="user-dropdown">
          <button class="dropdown-item" @click="goToProfile">
            <UserIcon />
            Profile
          </button>
          <button class="dropdown-item" @click="goToSettings">
            <SettingsIcon />
            Settings
          </button>
          <div class="dropdown-divider"></div>
          <button class="dropdown-item logout" @click="logout">
            <LogOutIcon />
            Logout
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store'

// Component imports
import SearchBar from '@/components/common/SearchBar.vue'
import {
  MenuIcon,
  PharmacyIcon,
  SearchIcon,
  BellIcon,
  PlusIcon,
  UserIcon,
  ChevronDownIcon,
  SettingsIcon,
  LogOutIcon
} from '@/components/icons'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// Props
defineProps({
  sidebarCollapsed: {
    type: Boolean,
    default: false
  }
})

// Emits
defineEmits(['toggleSidebar'])

// State
const showSearch = ref(false)
const searchQuery = ref('')
const showUserDropdown = ref(false)
const unreadNotifications = ref(3) // This would come from store

// Computed
const currentUser = computed(() => authStore.currentUser)

// Methods
const toggleSearch = () => {
  showSearch.value = !showSearch.value
  if (showSearch.value) {
    // Focus search input after toggle
    setTimeout(() => {
      // Focus search input if SearchBar component exposes it
    }, 100)
  }
}

const toggleUserDropdown = () => {
  showUserDropdown.value = !showUserDropdown.value
}

const handleSearch = (query) => {
  // Implement search logic
  console.log('Searching for:', query)
  // Could emit to parent or handle routing
}

const newPrescription = () => {
  router.push('/prescriptions/new')
}

const goToProfile = () => {
  showUserDropdown.value = false
  router.push('/profile')
}

const goToSettings = () => {
  showUserDropdown.value = false
  router.push('/settings')
}

const logout = () => {
  showUserDropdown.value = false
  authStore.logout()
  router.push('/login')
}

// Close dropdown when clicking outside
const handleClickOutside = (event) => {
  const userMenu = event.target.closest('.user-menu')
  if (!userMenu) {
    showUserDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.top-nav {
  height: 64px;
  background: white;
  border-bottom: 1px solid #e1e5e9;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: #5a6c7d;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.mobile-menu-btn:hover {
  background-color: #f8f9fa;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  color: #2c3e50;
  font-size: 18px;
}

.brand-text {
  font-weight: 600;
}

.nav-center {
  flex: 1;
  max-width: 400px;
  margin: 0 40px;
}

.search-container {
  width: 100%;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.quick-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  position: relative;
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: #5a6c7d;
  border-radius: 4px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-btn:hover {
  background-color: #f8f9fa;
  color: #2c3e50;
}

.action-btn.active {
  background-color: #e3f2fd;
  color: #1976d2;
}

.notification-badge {
  position: absolute;
  top: 2px;
  right: 2px;
  background: #e74c3c;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 16px;
  text-align: center;
  font-weight: 600;
}

.user-menu {
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.user-info:hover {
  background-color: #f8f9fa;
}

.user-avatar {
  width: 36px;
  height: 36px;
  background: #3498db;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.user-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.user-name {
  font-weight: 500;
  color: #2c3e50;
  font-size: 14px;
}

.user-role {
  color: #7f8c8d;
  font-size: 12px;
}

.dropdown-arrow {
  transition: transform 0.2s;
  color: #7f8c8d;
}

.dropdown-arrow.rotated {
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 200px;
  z-index: 1000;
  margin-top: 8px;
}

.dropdown-item {
  width: 100%;
  padding: 12px 16px;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #2c3e50;
  transition: background-color 0.2s;
}

.dropdown-item:hover {
  background-color: #f8f9fa;
}

.dropdown-item.logout {
  color: #e74c3c;
}

.dropdown-item.logout:hover {
  background-color: #ffeaea;
}

.dropdown-divider {
  height: 1px;
  background: #e1e5e9;
  margin: 4px 0;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .top-nav {
    padding: 0 16px;
  }

  .mobile-menu-btn {
    display: flex;
  }

  .brand-text {
    display: none;
  }

  .nav-center {
    display: none;
  }

  .nav-right {
    gap: 8px;
  }

  .user-details {
    display: none;
  }

  .user-dropdown {
    right: -20px;
  }
}

@media (max-width: 480px) {
  .top-nav {
    padding: 0 12px;
  }

  .quick-actions {
    gap: 4px;
  }

  .action-btn {
    padding: 6px;
  }
}
</style>
