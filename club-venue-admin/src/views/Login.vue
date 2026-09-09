<template>
  <div class="login-page">
    <!-- Decorative gradient orbs -->
    <div class="orb orb-1"></div>
    <div class="orb orb-2"></div>
    <div class="orb orb-3"></div>

    <!-- Login Card -->
    <div class="login-card">
      <div class="login-header">
        <div class="card-dot"></div>
        <div class="card-title">社团场地管理系统</div>
        <div class="card-sub">高校社团活动 · 场地调度一站管理</div>
      </div>
      <el-form ref="form" :model="form" :rules="rules" @submit.native.prevent="submit">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="el-icon-user" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="el-icon-lock" show-password @keyup.enter.native="submit" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="submit" :loading="loading">
            登 录
          </el-button>
        </el-form-item>
        <div class="link-register">
          没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
      <div class="test-accounts">
        <div class="test-accounts-title">测试账号（点击自动填入）</div>
        <div class="test-accounts-list">
          <div class="test-account-item" v-for="acc in testAccounts" :key="acc.user" @click="fillAccount(acc)">
            <span class="test-account-role">{{ acc.role }}</span>
            <span class="test-account-info">{{ acc.user }} / 123456</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/api'

export default {
  name: 'Login',
  data() {
    return {
      form: { username: '', password: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false,
      testAccounts: [
        { role: '管理员', user: 'admin' },
        { role: '社团负责人', user: 'zhangsan' }
      ]
    }
  },
  methods: {
    fillAccount(acc) {
      this.form.username = acc.user
      this.form.password = '123456'
    },
    async submit() {
      try { await this.$refs.form.validate() } catch { return }
      this.loading = true
      try {
        const res = await request.post('/user/login', this.form)
        if (res.code === 200) {
          const userData = res.data
          this.$store.dispatch('login', {
            token: userData.token,
            userInfo: {
              userId: userData.userId, username: userData.username,
              nickname: userData.nickname, avatar: userData.avatar,
              role: userData.role, clubId: userData.clubId, clubName: userData.clubName
            }
          })
          this.$message.success('登录成功')
          this.$router.push(this.$route.query.redirect || '/')
        } else {
          this.$message.error(res.message || '登录失败')
        }
      } catch { this.$message.error('登录失败') }
      finally { this.loading = false }
    },
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #fbfbfd 0%, #f5f5f7 100%);
  position: relative;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'PingFang SC', sans-serif;
}

/* Decorative gradient orbs */
.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  pointer-events: none;
  z-index: 0;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(0, 0, 0, 0.04), transparent 70%);
  top: -200px;
  left: -100px;
  animation: float 20s ease-in-out infinite;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(0, 0, 0, 0.03), transparent 70%);
  bottom: -150px;
  right: -80px;
  animation: floatSlow 25s ease-in-out infinite;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(0, 0, 0, 0.02), transparent 70%);
  top: 50%;
  left: 60%;
  animation: float 30s ease-in-out infinite reverse;
}

/* Login Card — Glass Morphism */
.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(40px);
  -webkit-backdrop-filter: blur(40px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 20px;
  padding: 48px 40px 36px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.06), 0 0 0 0.5px rgba(0, 0, 0, 0.05);
  animation: cardEnter 0.8s cubic-bezier(0.25, 0.1, 0.25, 1) both;
}

/* Header */
.login-header {
  text-align: center;
  margin-bottom: 36px;
  animation: fadeInDown 0.6s cubic-bezier(0.25, 0.1, 0.25, 1) 0.1s both;
}

.card-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #1d1d1f;
  margin: 0 auto 24px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);
}

.card-title {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  letter-spacing: -0.5px;
  margin-bottom: 8px;
  line-height: 1.2;
}

.card-sub {
  font-size: 14px;
  color: #86868b;
  letter-spacing: 0.2px;
}

/* Form — stagger animation */
.login-card >>> .el-form-item {
  animation: fadeInUp 0.6s cubic-bezier(0.25, 0.1, 0.25, 1) both;
}
.login-card >>> .el-form-item:nth-child(1) { animation-delay: 0.2s; }
.login-card >>> .el-form-item:nth-child(2) { animation-delay: 0.3s; }
.login-card >>> .el-form-item:nth-child(3) { animation-delay: 0.4s; }

/* Input styling */
.login-card >>> .el-input__inner {
  background: rgba(0, 0, 0, 0.04) !important;
  border: 1px solid rgba(0, 0, 0, 0.06) !important;
  color: #1d1d1f !important;
  border-radius: 12px !important;
  height: 48px !important;
  font-size: 15px !important;
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1) !important;
}

.login-card >>> .el-input__inner::placeholder {
  color: #86868b !important;
  font-weight: 400;
}

.login-card >>> .el-input__inner:focus {
  border-color: #1d1d1f !important;
  box-shadow: 0 0 0 4px rgba(0, 0, 0, 0.06) !important;
  background: #fff !important;
}

.login-card >>> .el-input__prefix .el-icon-user,
.login-card >>> .el-input__prefix .el-icon-lock {
  color: #86868b;
  font-size: 16px;
}

/* Login button */
.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 4px;
  border-radius: 980px !important;
  background: #1d1d1f !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1) !important;
}

.login-btn:hover {
  transform: translateY(-2px) !important;
  background: #000 !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2) !important;
}

.login-btn:active {
  transform: scale(0.97) !important;
}

/* Register link */
.link-register {
  text-align: center;
  font-size: 13px;
  color: #86868b;
  margin-top: 8px;
  animation: fadeInUp 0.6s cubic-bezier(0.25, 0.1, 0.25, 1) 0.5s both;
}

.link-register a {
  color: #1d1d1f;
  text-decoration: none;
  font-weight: 500;
  transition: opacity 0.2s;
}

.link-register a:hover {
  opacity: 0.7;
}

/* Test accounts */
.test-accounts {
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
  animation: fadeInUp 0.6s cubic-bezier(0.25, 0.1, 0.25, 1) 0.55s both;
}

.test-accounts-title {
  text-align: center;
  font-size: 12px;
  color: #86868b;
  margin-bottom: 12px;
  letter-spacing: 0.3px;
}

.test-accounts-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.test-account-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.test-account-item:hover {
  background: rgba(0, 0, 0, 0.06);
  border-color: rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.test-account-item:active {
  transform: scale(0.98);
}

.test-account-role {
  font-size: 13px;
  font-weight: 500;
  color: #1d1d1f;
}

.test-account-info {
  font-size: 12px;
  color: #86868b;
  font-family: 'SF Mono', 'Menlo', 'Consolas', monospace;
}

/* Animations */
@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  33% { transform: translate(30px, -20px); }
  66% { transform: translate(-20px, 15px); }
}

@keyframes floatSlow {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-25px, 20px); }
}
</style>
