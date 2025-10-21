import { createRouter, createWebHistory } from 'vue-router'

// Import all views
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import DashboardView from '@/views/DashboardView.vue'
import DataEntryView from '@/views/DataEntryView.vue'
import DataReviewView from '@/views/DataReviewView.vue'
import ProductView from '@/views/ProductView.vue'
import ProductReviewView from '@/views/ProductReviewView.vue'
import VaccineView from '@/views/VaccineView.vue'
import NotificationsView from '@/views/NotificationsView.vue'
import NewPatientView from '@/views/NewPatientView.vue'
import InventoryView from '@/views/InventoryView.vue'
import ConfirmationView from '@/views/ConfirmationView.vue'
import InsuranceView from '@/views/InsuranceView.vue'
import RecallHazardousView from '@/views/RecallHazardousView.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: DashboardView,
    meta: { requiresAuth: true, permission: 'DASHBOARD_VIEW' }
  },
  {
    path: '/data-entry',
    name: 'data-entry',
    component: DataEntryView,
    meta: { requiresAuth: true, permission: 'PRESCRIPTION_CREATE' }
  },
  {
    path: '/data-review',
    name: 'data-review',
    component: DataReviewView,
    meta: { requiresAuth: true, permission: 'PRESCRIPTION_REVIEW' }
  },
  {
    path: '/product',
    name: 'product',
    component: ProductView,
    meta: { requiresAuth: true, permission: 'PRESCRIPTION_PROCESS' }
  },
  {
    path: '/product-review',
    name: 'product-review',
    component: ProductReviewView,
    meta: { requiresAuth: true, permission: 'PRESCRIPTION_REVIEW' }
  },
  {
    path: '/vaccine',
    name: 'vaccine',
    component: VaccineView,
    meta: { requiresAuth: true, permission: 'VACCINE_MANAGE' }
  },
  {
    path: '/notification',
    name: 'notification',
    component: NotificationsView,
    meta: { requiresAuth: true, permission: 'NOTIFICATION_VIEW' }
  },
  // Supporting pages
  {
    path: '/new-patient',
    name: 'new-patient',
    component: NewPatientView,
    meta: { requiresAuth: true, permission: 'PATIENT_CREATE' }
  },
  {
    path: '/new-prescription',
    name: 'new-prescription',
    component: NewPrescriptionView,
    meta: { requiresAuth: true, permission: 'PRESCRIPTION_CREATE' }
  },
  {
    path: '/inventory',
    name: 'inventory',
    component: InventoryView,
    meta: { requiresAuth: true, permission: 'INVENTORY_VIEW' }
  },
  {
    path: '/confirmation',
    name: 'confirmation',
    component: ConfirmationView,
    meta: { requiresAuth: true, permission: 'PRESCRIPTION_CONFIRM' }
  },
  {
    path: '/insurance',
    name: 'insurance',
    component: InsuranceView,
    meta: { requiresAuth: true, permission: 'INSURANCE_MANAGE' }
  },
  {
    path: '/recall-hazardous',
    name: 'recall-hazardous',
    component: RecallHazardousView,
    meta: { requiresAuth: true, permission: 'RECALL_VIEW' }
  }
]
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router