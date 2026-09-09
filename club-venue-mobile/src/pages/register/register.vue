<template>
  <view class="register-page">
    <!-- Canvas几何图案背景 -->
    <canvas canvas-id="regCanvas" class="bg-canvas"></canvas>
    <!-- 浮动装饰 -->
    <view class="deco deco-1"></view>
    <view class="deco deco-2"></view>

    <view class="form-card">
      <view class="card-dot"></view>
      <text class="header">创建账号</text>
      <view class="input-group">
        <input v-model="form.username" placeholder="请输入账号" class="input" />
      </view>
      <view class="input-group">
        <input v-model="form.password" type="password" placeholder="密码（不少于6位）" class="input" />
      </view>
      <view class="input-group">
        <input v-model="form.nickname" placeholder="请输入昵称" class="input" />
      </view>
      <button class="btn" @click="handleRegister">注 册</button>
      <view class="link-login" @click="goLogin">已有账号？去登录</view>
    </view>
  </view>
</template>

<script setup>
import { reactive, onMounted, onBeforeUnmount } from 'vue'
import { post } from '@/utils/request.js'

const form = reactive({ username: '', password: '', nickname: '' })

function goLogin() { uni.navigateBack() }

// Canvas 六边形网格动画（区别于登录页的波浪）
let animId = null
function initCanvas() {
  // #ifdef H5
  const canvas = document.querySelector('.register-page .bg-canvas')
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  let w, h
  const resize = () => {
    w = window.innerWidth; h = window.innerHeight
    const dpr = window.devicePixelRatio || 1
    canvas.width = w * dpr; canvas.height = h * dpr
    canvas.style.width = w + 'px'; canvas.style.height = h + 'px'
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
  }
  resize()
  window.addEventListener('resize', resize)

  const hexSize = 40
  let offset = 0

  function draw() {
    ctx.clearRect(0, 0, w, h)
    // 六边形网格
    ctx.strokeStyle = 'rgba(0,0,0,0.05)'
    ctx.lineWidth = 1.5
    const dx = hexSize * 1.5
    const dy = hexSize * Math.sqrt(3)
    for (let row = -1; row < Math.ceil(h/dy)+1; row++) {
      for (let col = -1; col < Math.ceil(w/dx)+1; col++) {
        const cx = col * dx + (row%2===0 ? 0 : dx/2)
        const cy = row * dy + offset % dy
        if (cy < -hexSize || cy > h + hexSize) continue
        ctx.beginPath()
        for (let i=0; i<6; i++) {
          const angle = Math.PI/6 + i * Math.PI/3
          const px = cx + hexSize * Math.cos(angle)
          const py = cy + hexSize * Math.sin(angle)
          if (i===0) ctx.moveTo(px, py); else ctx.lineTo(px, py)
        }
        ctx.closePath(); ctx.stroke()
        // 中心点渐变
        if ((row+col)%3===0) {
          ctx.beginPath(); ctx.arc(cx, cy, 2.5, 0, Math.PI*2)
          ctx.fillStyle = 'rgba(0,0,0,0.08)'; ctx.fill()
        }
      }
    }
    offset += 0.15
    animId = requestAnimationFrame(draw)
  }
  draw()

  // 保存引用
  canvas._animId = animId
  canvas._resize = resize
  // #endif
}

onMounted(() => { setTimeout(initCanvas, 50) })
onBeforeUnmount(() => {
  // #ifdef H5
  const canvas = document.querySelector('.register-page .bg-canvas')
  if (canvas) {
    if (canvas._animId) cancelAnimationFrame(canvas._animId)
    if (canvas._resize) window.removeEventListener('resize', canvas._resize)
  }
  // #endif
})

async function handleRegister() {
  if (!form.username || !form.password || !form.nickname) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' }); return
  }
  if (form.password.length < 6) {
    uni.showToast({ title: '密码不少于6位', icon: 'none' }); return
  }
  try {
    const res = await post('/api/user/register', form)
    if (res.code === 200) { uni.showToast({ title: '注册成功', icon: 'success' }); setTimeout(() => uni.navigateBack(), 1500) }
    else { uni.showToast({ title: res.message || '注册失败', icon: 'none' }) }
  } catch (e) { uni.showToast({ title: '注册失败', icon: 'none' }) }
}
</script>

<style scoped>
/* ═══ 注册页 - 浅色主题，六边形网格Canvas + 装饰元素 ═══ */
.register-page {
  min-height: 100vh; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #f5f5f7 0%, #f0f0f5 50%, #f8f8fa 100%);
  position: relative; overflow: hidden;
}
.bg-canvas { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 0; }
/* 不对称装饰块 */
.deco { position: absolute; border-radius: 30rpx; z-index: 0; opacity: 0.25; }
.deco-1 { width: 300rpx; height: 300rpx; background: linear-gradient(135deg, rgba(0,0,0,0.06), rgba(0,0,0,0.02)); top: -60rpx; right: -60rpx; transform: rotate(15deg); animation: decoRotate1 20s linear infinite; }
.deco-2 { width: 200rpx; height: 200rpx; background: linear-gradient(225deg, rgba(0,0,0,0.04), rgba(0,0,0,0.01)); bottom: -40rpx; left: -40rpx; transform: rotate(-10deg); animation: decoRotate2 25s linear infinite; }
@keyframes decoRotate1 { from { transform: rotate(0deg) } to { transform: rotate(360deg) } }
@keyframes decoRotate2 { from { transform: rotate(0deg) } to { transform: rotate(-360deg) } }

.form-card {
  position: relative; z-index: 1; width: 640rpx;
  background: rgba(255,255,255,0.9); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(0,0,0,0.06); border-radius: 16rpx;
  padding: 48rpx 36rpx 36rpx; box-shadow: 0 20px 60px rgba(0,0,0,0.08);
}
.card-dot { width: 12rpx; height: 12rpx; border-radius: 50%; background: #1d1d1f; margin: 0 auto 20rpx; box-shadow: 0 0 16rpx rgba(0,0,0,0.15); }
.header { text-align: center; font-size: 36rpx; font-weight: bold; color: #1e293b; margin-bottom: 32rpx; display: block; letter-spacing: 2rpx; }
.input-group { margin-bottom: 20rpx; }
.input { width: 100%; height: 88rpx; border: 1px solid #e2e8f0; border-radius: 12rpx; padding: 0 24rpx; font-size: 28rpx; box-sizing: border-box; background: #f8fafc; color: #1e293b; }
.input:focus { border-color: #1d1d1f; }
.btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #1d1d1f; color: #fff; font-size: 32rpx; font-weight: bold; border-radius: 12rpx; border: none; margin-top: 24rpx; letter-spacing: 4rpx; }
.link-login { text-align: center; color: #1d1d1f; font-size: 26rpx; margin-top: 20rpx; }
</style>
