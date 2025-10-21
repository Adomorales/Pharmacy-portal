import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from '@/router'
import { useAuthStore } from '@/store'
import '@/style.css'
import App from '@/App.vue'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// Initialize auth state and set up router guards
const authStore = useAuthStore()

// Set up router guards after auth store is available
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/dashboard')
  } else if (to.meta.requiresAuth && to.meta.permission && !authStore.hasPermission(to.meta.permission)) {
    alert('You do not have permission to access this page.')
    next('/dashboard')
  } else {
    next()
  }
})

authStore.initializeAuth()

app.mount('#app')
