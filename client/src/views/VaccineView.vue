<template>
  <div class="vaccine">
    <!-- Header -->
    <div class="page-header">
      <div class="header-content">
        <h1>Vaccine</h1>
        <p>Manage vaccine appointments and administration</p>
      </div>
      <button @click="$router.push('/dashboard')" class="back-btn">Back to Dashboard</button>
    </div>

    <!-- Vaccine management interface will be implemented here -->
    <div class="vaccine-content">
      <div class="prescription-context">
        <PrescriptionSkeleton />
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PrescriptionSkeleton from '@/components/pharmacy/PrescriptionSkeleton.vue'

const router = useRouter()

onMounted(() => {
  // Initialize vaccine management
})
</script>

<style scoped>
.vaccine {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 20px 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content h1 {
  margin: 0 0 4px 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.header-content p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.back-btn {
  padding: 10px 20px;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.back-btn:hover {
  background: #2980b9;
}

.vaccine-content {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 600px;
}
</style>
