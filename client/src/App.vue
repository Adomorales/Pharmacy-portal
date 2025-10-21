<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/store'
import router from '@/router'

// Initialize auth store
const authStore = useAuthStore()

onMounted(() => {
  // Auth is already initialized in main.js, but we can add any additional setup here
  console.log('Pharmacy Portal initialized')
})
</script>

<template>
  <div id="app">
    <!-- Router will handle all the page rendering -->
    <router-view />
  </div>
</template>

<style>
/* Global styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: #2c3e50;
  background-color: #f8f9fa;
  min-height: 100vh;
}

/* Global button styles */
button {
  cursor: pointer;
  font-family: inherit;
  transition: all 0.2s ease;
}

button:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.3);
}

/* Global input styles */
input, select, textarea {
  font-family: inherit;
  font-size: 14px;
}

input:focus, select:focus, textarea:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

/* Utility classes */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

/* Loading animation */
@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

.loading {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* Scrollbar styling */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Print styles */
@media print {
  * {
    background: white !important;
    color: black !important;
    box-shadow: none !important;
    text-shadow: none !important;
  }

  button, .no-print {
    display: none !important;
  }

  a, a:visited {
    text-decoration: underline;
  }

  .page-break {
    page-break-before: always;
  }
}
</style>
