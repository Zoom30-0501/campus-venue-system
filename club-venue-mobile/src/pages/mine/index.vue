<template>
  <view class="page">
    <!-- 渐变头部 -->
    <view class="header">
      <view class="deco-ring"></view>
      <view class="avatar-wrap">
        <view class="avatar">{{ initialChar }}</view>
      </view>
      <text class="nickname">{{ displayName }}</text>
      <view class="role-badge">{{ displayRole }}</view>
    </view>

    <!-- 毛玻璃菜单 -->
    <view class="menu-list">
      <view class="menu-item" v-for="(m, idx) in menuItems" :key="idx" :class="'stagger-' + (idx + 1)" @click="goPage(m.url)">
        <view class="menu-left">
          <view class="menu-icon" :style="{ background: m.iconBg }">
            <text class="menu-icon-text" :style="{ color: m.iconColor }">{{ m.icon }}</text>
          </view>
          <text class="menu-label">{{ m.label }}</text>
        </view>
        <text class="arrow">›</text>
      </view>
    </view>

    <!-- 退出按钮 -->
    <view class="logout-wrap">
      <button class="logout-btn" @click="doLogout">退出登录</button>
    </view>
  </view>
</template>

<script>
import { clearToken, getUserInfo } from '@/utils/request.js'

export default {
  data() {
    return {
      displayName: '', displayRole: '普通用户', initialChar: 'U',
      menuItems: [
        { label: '编辑资料', icon: '编', url: '/pages/mine/profile', iconBg: 'rgba(0,0,0,0.05)', iconColor: '#1d1d1f' },
        { label: '我的活动', icon: '动', url: '/pages/mine/my-activities', iconBg: 'rgba(0,0,0,0.05)', iconColor: '#1d1d1f' },
        { label: '我的报名', icon: '报', url: '/pages/mine/my-enrollments', iconBg: 'rgba(0,0,0,0.05)', iconColor: '#1d1d1f' },
        { label: '签到记录', icon: '签', url: '/pages/mine/my-checkins', iconBg: 'rgba(0,0,0,0.05)', iconColor: '#1d1d1f' },
        { label: '我的建议', icon: '建', url: '/pages/mine/my-suggestions', iconBg: 'rgba(0,0,0,0.05)', iconColor: '#1d1d1f' },
        { label: '入团申请记录', icon: '申', url: '/pages/mine/my-applications', iconBg: 'rgba(0,0,0,0.05)', iconColor: '#1d1d1f' },
        { label: '提交建议', icon: '＋', url: '/pages/suggestion/add', iconBg: 'rgba(0,0,0,0.05)', iconColor: '#86868b' },
      ]
    }
  },
  methods: {
    goPage(url) { uni.navigateTo({ url }) },
    doLogout() {
      clearToken()
      uni.removeStorageSync('userInfo')
      uni.switchTab({ url: '/pages/index/index' })
    },
    readUser() {
      const u = getUserInfo()
      this.displayName = u.nickname || u.username || ''
      this.displayRole = u.role || '普通用户'
      const name = this.displayName
      this.initialChar = name ? name[0] : 'U'
    },
  },
  onShow() { this.readUser() },
  onLoad() { this.readUser() },
}
</script>

<style scoped>
.page { padding-bottom: 60rpx; background: #f5f5f7; min-height: 100vh; }

/* ═══ 渐变头部 ═══ */
.header {
  background: #1d1d1f;
  padding: 60rpx 30rpx 80rpx; text-align: center;
  position: relative; overflow: hidden;
}
.header::after {
  content: ''; position: absolute; bottom: -2rpx; left: 0; right: 0; height: 40rpx;
  background: #f5f5f7; border-radius: 40rpx 40rpx 0 0;
}
/* 装饰光环 */
.deco-ring {
  position: absolute; width: 300rpx; height: 300rpx; border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.15);
  top: -80rpx; right: -60rpx;
  animation: ringPulse 6s ease-in-out infinite;
}
@keyframes ringPulse {
  0%, 100% { transform: scale(1); opacity: 0.3 }
  50% { transform: scale(1.1); opacity: 0.15 }
}

.avatar-wrap {
  position: relative; z-index: 1;
  width: 120rpx; height: 120rpx; margin: 0 auto 20rpx;
  border-radius: 50%;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15), 0 0 0 4px rgba(255,255,255,0.2);
}
.avatar {
  width: 120rpx; height: 120rpx; line-height: 120rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.25);
  backdrop-filter: blur(10px); -webkit-backdrop-filter: blur(10px);
  font-size: 48rpx; font-weight: 700; color: #fff;
}
.nickname {
  position: relative; z-index: 1;
  font-size: 38rpx; font-weight: 700; display: block; color: #fff;
  letter-spacing: -0.3rpx;
}
.role-badge {
  display: inline-block; margin-top: 12rpx;
  font-size: 22rpx; padding: 6rpx 20rpx;
  border-radius: 980rpx;
  background: rgba(255,255,255,0.2);
  backdrop-filter: blur(10px); -webkit-backdrop-filter: blur(10px);
  color: #fff; font-weight: 500;
}

/* ═══ 毛玻璃菜单 ═══ */
.menu-list {
  margin: -20rpx 20rpx 0;
  background: rgba(255,255,255,0.8);
  backdrop-filter: saturate(200%) blur(20px); -webkit-backdrop-filter: saturate(200%) blur(20px);
  border: 0.5px solid rgba(0,0,0,0.04); border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0,0,0,0.04);
  position: relative; z-index: 1;
}
.menu-item {
  padding: 28rpx 24rpx; display: flex; align-items: center; justify-content: space-between;
  border-bottom: 0.5px solid rgba(0,0,0,0.05);
  transition: background 0.2s ease;
  animation: menuIn 0.4s cubic-bezier(0.25, 0.1, 0.25, 1) both;
}
.menu-item:last-child { border-bottom: none; }
.menu-item:active { background: rgba(0,0,0,0.03); }
@keyframes menuIn {
  from { opacity: 0; transform: translateX(-20rpx) }
  to { opacity: 1; transform: translateX(0) }
}
.stagger-1 { animation-delay: 0s }
.stagger-2 { animation-delay: 0.05s }
.stagger-3 { animation-delay: 0.10s }
.stagger-4 { animation-delay: 0.15s }
.stagger-5 { animation-delay: 0.20s }
.stagger-6 { animation-delay: 0.25s }
.stagger-7 { animation-delay: 0.30s }

.menu-left { display: flex; align-items: center; }
.menu-icon {
  width: 60rpx; height: 60rpx; border-radius: 16rpx;
  display: flex; align-items: center; justify-content: center;
  margin-right: 20rpx;
}
.menu-icon-text { font-size: 26rpx; font-weight: 700; }
.menu-label { font-size: 30rpx; color: #1d1d1f; font-weight: 500; }
.arrow { font-size: 36rpx; color: #c7c7cc; }

/* ═══ 退出按钮 ═══ */
.logout-wrap { padding: 40rpx 20rpx; }
.logout-btn {
  width: 100%; height: 96rpx; line-height: 96rpx;
  background: rgba(0,0,0,0.06);
  border: 0.5px solid rgba(0,0,0,0.08);
  color: #1d1d1f; font-size: 32rpx; font-weight: 600;
  border-radius: 980rpx;
  transition: all 0.2s ease;
}
.logout-btn:active { transform: scale(0.97); background: rgba(0,0,0,0.1); }
</style>
