<template>
  <div id="register">
    <div class="register-container">
      <form @submit.prevent="register" class="register-form">
        <div class="welcome-section">
          <h1><span class="highlight">Create Account</span></h1>
          <p class="subtitle">Join our pharmacy management system</p>
        </div>

        <div id="fields" class="form-fields">
          <div class="form-row">
            <div class="input-group">
              <input
                type="text"
                id="firstName"
                v-model="user.firstName"
                required
                class="form-input"
                :class="{ 'has-value': user.firstName }"
              />
              <label for="firstName" class="input-label">First Name</label>
            </div>

            <div class="input-group">
              <input
                type="text"
                id="lastName"
                v-model="user.lastName"
                required
                class="form-input"
                :class="{ 'has-value': user.lastName }"
              />
              <label for="lastName" class="input-label">Last Name</label>
            </div>
          </div>

          <div class="input-group">
            <input
              type="email"
              id="email"
              v-model="user.email"
              required
              class="form-input"
              :class="{ 'has-value': user.email }"
            />
            <label for="email" class="input-label">Email</label>
          </div>

          <div class="input-group">
            <input
              type="text"
              id="username"
              v-model="user.username"
              required
              class="form-input"
              :class="{ 'has-value': user.username }"
            />
            <label for="username" class="input-label">Username</label>
          </div>

          <div class="input-group">
            <input
              type="password"
              id="password"
              v-model="user.password"
              required
              class="form-input"
              :class="{ 'has-value': user.password }"
            />
            <label for="password" class="input-label">Password</label>
          </div>

          <div class="input-group">
            <input
              type="password"
              id="confirmPassword"
              v-model="user.confirmPassword"
              required
              class="form-input"
              :class="{ 'has-value': user.confirmPassword }"
            />
            <label for="confirmPassword" class="input-label">Confirm Password</label>
          </div>

          <div class="form-row">
            <div class="input-group">
              <select
                id="role"
                v-model="user.role"
                required
                class="form-input"
                :class="{ 'has-value': user.role }"
              >
                <option value="">Select Role</option>
                <option value="PHARMACIST">Pharmacist</option>
                <option value="TECHNICIAN">Pharmacy Technician</option>
                <option value="ADMIN">Administrator</option>
              </select>
              <label for="role" class="input-label">Role</label>
            </div>

            <div class="input-group">
              <input
                type="text"
                id="licenseNumber"
                v-model="user.licenseNumber"
                class="form-input"
                :class="{ 'has-value': user.licenseNumber }"
                :required="user.role === 'PHARMACIST'"
              />
              <label for="licenseNumber" class="input-label">
                License Number {{ user.role === 'PHARMACIST' ? '(Required)' : '(Optional)' }}
              </label>
            </div>
          </div>

          <div class="form-actions">
            <button class="submit-btn" type="submit">
              Create Account
            </button>
          </div>
        </div>

        <div class="divider">
          <hr/>
        </div>

        <div class="login-section">
          <span class="login-text">Already have an account?</span>
          <router-link :to="{ name: 'login' }" class="link-btn">Sign in!</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store'

const router = useRouter()
const authStore = useAuthStore()

const user = reactive({
  firstName: '',
  lastName: '',
  email: '',
  username: '',
  password: '',
  confirmPassword: '',
  role: '',
  licenseNumber: ''
})

const register = async () => {
  if (user.password !== user.confirmPassword) {
    alert('Passwords do not match')
    return
  }

  try {
    await authStore.register(user)

    alert('Account created successfully!')
    router.push('/login')
  } catch (error) {
    console.error('Registration error:', error)

    if (error.response?.status === 400) {
      alert(error.response.data.message || 'Registration failed. Please check your information.')
    } else {
      alert('Registration failed. Please try again.')
    }
  }
}

onMounted(() => {
  // Initialize auth state from localStorage
  authStore.initializeAuth()
})
</script>

<style scoped>
#register {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  width: 100%;
  max-width: 500px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.welcome-section {
  text-align: center;
}

.welcome-section h1 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 28px;
  font-weight: 300;
}

.highlight {
  background: linear-gradient(45deg, #00d4aa, #00a0ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 600;
}

.subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.form-fields {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.input-group {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 15px 0;
  border: none;
  border-bottom: 2px solid #e1e5e9;
  background: transparent;
  font-size: 16px;
  color: #2c3e50;
  transition: all 0.3s ease;
  outline: none;
}

.form-input:focus {
  border-bottom-color: #00d4aa;
}

.form-input.has-value {
  border-bottom-color: #00a0ff;
}

.input-label {
  position: absolute;
  top: 15px;
  left: 0;
  color: #7f8c8d;
  font-size: 16px;
  transition: all 0.3s ease;
  pointer-events: none;
}

.form-input:focus + .input-label,
.form-input.has-value + .input-label {
  top: -10px;
  font-size: 12px;
  color: #00d4aa;
  font-weight: 600;
}

.form-actions {
  margin-top: 10px;
}

.submit-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(45deg, #00d4aa, #00a0ff);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 212, 170, 0.3);
}

.submit-btn:active {
  transform: translateY(0);
}

.divider {
  margin: 20px 0;
}

.divider hr {
  border: none;
  height: 1px;
  background: linear-gradient(90deg, transparent, #e1e5e9, transparent);
}

.login-section {
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-text {
  color: #7f8c8d;
  font-size: 14px;
}

.link-btn {
  color: #00a0ff;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
  padding: 5px 10px;
  border-radius: 15px;
}

.link-btn:hover {
  background: linear-gradient(45deg, #ffd700, #ff6b35);
  color: white;
  transform: scale(1.05);
}

/* Mobile responsive */
@media (max-width: 768px) {
  .register-container {
    padding: 30px 20px;
    margin: 20px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .welcome-section h1 {
    font-size: 24px;
  }
}
</style>
