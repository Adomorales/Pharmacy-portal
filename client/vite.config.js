import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import path from 'node:path'

const _filename = fileURLToPath(import.meta.url);
const _dirname = path.dirname(_filename);

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@' : fileURLToPath(new URL('./src', import.meta.url)),
      '@components'             : path.resolve(_dirname, './src/components'),
      '@services'               : path.resolve(_dirname, './src/services'),
      '@views'                  : path.resolve(_dirname, './src/views'),
      '@assets'                 : path.resolve(_dirname, './src/assets'),
      '@utils'                  : path.resolve(_dirname, './src/utils'),
      '@store'                  : path.resolve(_dirname, './src/store'),
      '@router'                 : path.resolve(_dirname, './src/router'),
      '@composables'            : path.resolve(_dirname, './src/composables'),
      '@/StockAdjustmentModal'  : path.resolve(_dirname, './src/components/pharmacy/modals/StockAdjustmentModal.vue'),
      '@/ReorderModal'          : path.resolve(_dirname, './src/components/pharmacy/modals/ReorderModal.vue'),
      '@/InventoryStatus'       : path.resolve(_dirname, './src/components/pharmacy/InventoryStatus.vue'),
      '@/MedicationSearch'      : path.resolve(_dirname, './src/components/pharmacy/MedicationSearch.vue'),
      '@/MedicationModal'       : path.resolve(_dirname, './src/components/pharmacy/MedicationModal.vue'),
      '@/UserManagement'        : path.resolve(_dirname, './src/components/admin/UserManagement.vue'),
      '@/RoleManagement'        : path.resolve(_dirname, './src/components/admin/RoleManagement.vue'),
      '@/AuditLogs'             : path.resolve(_dirname, './src/components/admin/AuditLogs.vue'),
      '@/Settings'              : path.resolve(_dirname, './src/components/admin/Settings.vue'),
      '@/Dashboard'             : path.resolve(_dirname, './src/components/common/Dashboard.vue'),
      '@/LoadingBar'            : path.resolve(_dirname, './src/components/common/LoadingBar.vue'),
      '@/FormField'             : path.resolve(_dirname, './src/components/forms/FormField.vue'),
      '@/Pagination'            : path.resolve(_dirname, './src/components/common/Pagination.vue'),
      '@/Notification'          : path.resolve(_dirname, './src/components/common/Notification.vue')

    }
  },
  server: {
    port: 5175,
    host: true,
    hmr: {
      port: 5175
    }
  },
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'axios'],
    force: true
  },
  build: {
    rollupOptions: {
      external: []
    }
  }
})
