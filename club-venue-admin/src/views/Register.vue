<template>
  <div class="register-page">
    <!-- 装饰元素：渐变旋转块 -->
    <div class="deco-block d1"></div>
    <div class="deco-block d2"></div>

    <div class="register-card">
      <div class="register-header">
        <div class="card-dot"></div>
        <div class="card-title">注册账号</div>
        <div class="card-sub">加入社团场地管理平台</div>
      </div>
      <el-form ref="form" :model="form" :rules="rules" @submit.native.prevent="submit">
        <el-form-item prop="username">
          <el-input v-model="form.username" size="small" placeholder="用户名" prefix-icon="el-icon-user" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" size="small" type="password" placeholder="密码（至少6位）" prefix-icon="el-icon-lock" show-password />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" size="small" placeholder="昵称" prefix-icon="el-icon-edit-outline" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" class="register-btn" @click="submit" :loading="loading">注 册</el-button>
        </el-form-item>
        <div class="link-login">
          已有账号？<router-link to="/login">去登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from '@/api'

export default {
  name: 'Register',
  data() {
    return {
      form: { username: '', password: '', nickname: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码至少6位', trigger: 'blur' }
        ],
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    async submit() {
      try { await this.$refs.form.validate() } catch { return }
      this.loading = true
      try {
        const res = await request.post('/user/register', this.form)
        if (res.code === 200) { this.$message.success('注册成功，请登录'); this.$router.push('/login') }
        else { this.$message.error(res.message || '注册失败') }
      } catch { this.$message.error('注册失败') }
      finally { this.loading = false }
    },
  }
}
</script>

<style scoped>
.register-page {
  height: 100vh; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #f5f3ff 0%, #f0f9ff 50%, #f8fafc 100%);
  position: relative; overflow: hidden;
}
/* 装饰旋转块 */
.deco-block { position: absolute; border-radius: 30px; opacity: 0.2; z-index: 0; }
.deco-block.d1 { width: 360px; height: 360px; background: linear-gradient(135deg, rgba(0,0,0,0.06), rgba(0,0,0,0.02)); top: -100px; right: -100px; transform: rotate(15deg); animation: spin1 20s linear infinite; }
.deco-block.d2 { width: 250px; height: 250px; background: linear-gradient(225deg, rgba(0,0,0,0.04), rgba(0,0,0,0.01)); bottom: -60px; left: -60px; transform: rotate(-10deg); animation: spin2 25s linear infinite; }
@keyframes spin1 { from { transform: rotate(0deg) } to { transform: rotate(360deg) } }
@keyframes spin2 { from { transform: rotate(0deg) } to { transform: rotate(-360deg) } }

.register-card {
  position: relative; z-index: 1; width: 380px;
  background: rgba(255,255,255,0.9); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(0,0,0,0.06); border-radius: 12px; padding: 36px 32px 28px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.08);
}
.register-header { text-align: center; margin-bottom: 28px; }
.card-dot { width: 10px; height: 10px; border-radius: 50%; background: #1d1d1f; margin: 0 auto 14px; box-shadow: 0 0 16px rgba(0,0,0,0.15); }
.card-title { font-size: 22px; font-weight: 700; color: #1e293b; letter-spacing: 1px; margin-bottom: 6px; }
.card-sub { font-size: 12px; color: #64748b; letter-spacing: 0.5px; }
.register-card >>> .el-input__inner { background: #f8fafc !important; border: 1px solid #e2e8f0 !important; color: #1e293b !important; border-radius: 6px; height: 40px; }
.register-card >>> .el-input__inner::placeholder { color: #94a3b8 !important; }
.register-card >>> .el-input__inner:focus { border-color: #1d1d1f !important; box-shadow: 0 0 0 2px rgba(0,0,0,0.06) !important; }
.register-card >>> .el-input__prefix i { color: #94a3b8; }
.register-btn { width: 100%; height: 40px; font-size: 14px; letter-spacing: 4px; border-radius: 6px; }
.link-login { text-align: center; font-size: 12px; color: #94a3b8; margin-top: 8px; }
.link-login a { color: #1d1d1f; text-decoration: none; transition: opacity .2s; }
.link-login a:hover { opacity: 0.8; }
</style>
