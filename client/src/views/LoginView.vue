<template>
  <div id="login">
    <div class="login-container">
      <form @submit.prevent="login" class="login-form">
        <div class="welcome-section">
          <h1><span class="highlight">Welcome Back</span></h1>
        </div>

        <div id="fields" class="form-fields">
          <div class="input-group">
            <input
              type="text"
              id="username"
              v-model="user.username"
              required
              autofocus
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

          <div class="form-actions">
            <button class="submit-btn" type="submit">
              Sign in
            </button>
          </div>
        </div>

        <div class="divider">
          <hr/>
        </div>

        <div class="register-section">
          <span class="register-text">Need an account?</span>
          <router-link :to="{ name: 'register' }" class="link-btn">Create Account!</router-link>
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
  username: '',
  password: ''
})

const login = async () => {
  try {
    await authStore.login(user.username, user.password)

    if (authStore.isAuthenticated) {
      router.push('/dashboard')
    }
  } catch (error) {
    console.error('Login error:', error)

    if (error.response?.status === 401) {
      if (error.response.data?.message?.includes('password')) {
        alert('Password incorrect')
      } else {
        alert('Incorrect username and password')
      }
    } else {
      alert('Login failed. Please try again.')
    }
  }
}

onMounted(() => {
  // Initialize auth state from localStorage
  authStore.initializeAuth()
})
</script>

<style scoped>
#login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  width: 100%;
  max-width: 400px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.welcome-section {
  text-align: center;
}

.welcome-section h1 {
  margin: 0;
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

.form-fields {
  display: flex;
  flex-direction: column;
  gap: 25px;
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

.register-section {
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.register-text {
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
  .login-container {
    padding: 30px 20px;
    margin: 20px;
  }

  .welcome-section h1 {
    font-size: 24px;
  }
}
</style>
