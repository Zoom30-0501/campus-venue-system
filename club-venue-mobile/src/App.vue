<template>
  <view class="app">
    <view v-if="showSplash" class="splash">
      <view class="splash-icon">C</view>
      <text class="splash-title">社团活动场地管理</text>
    </view>
    <view v-else class="splash-fade">
      <router-view />
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const showSplash = ref(true)

onMounted(() => {
  setTimeout(() => { showSplash.value = false }, 1500)
})
</script>

<style>
/* ═════════════════════════════════════════════
 *  Apple-Inspired Design System — Mobile
 * ═════════════════════════════════════════════ */
:root {
  --bg-primary: #f5f5f7;
  --bg-secondary: #ffffff;
  --bg-tertiary: #f0f0f2;
  --text-primary: #1d1d1f;
  --text-secondary: #424245;
  --text-muted: #86868b;
  --border-color: rgba(0, 0, 0, 0.06);
  --card-bg: rgba(255, 255, 255, 0.8);
  --card-border: rgba(255, 255, 255, 0.6);
  --accent: #1d1d1f;
  --accent2: #1d1d1f;
  --tag-success-bg: rgba(0,0,0,0.05);
  --tag-success-text: #1d1d1f;
  --tag-warning-bg: rgba(0,0,0,0.05);
  --tag-warning-text: #424245;
  --tag-danger-bg: rgba(0,0,0,0.05);
  --tag-danger-text: #1d1d1f;
  --tag-info-bg: rgba(134,134,139,0.08);
  --tag-info-text: #86868b;
  --header-bg: linear-gradient(180deg, rgba(0,0,0,0.03) 0%, transparent 100%);
  --menu-bg: rgba(255, 255, 255, 0.8);
  --menu-border: rgba(0, 0, 0, 0.04);
  --shadow-sm: 0 2px 12px rgba(0, 0, 0, 0.04);
  --shadow-md: 0 8px 32px rgba(0, 0, 0, 0.06);
  --glass-bg: rgba(255, 255, 255, 0.72);
  --glass-blur: blur(20px);
  --radius-card: 20rpx;
  --radius-btn: 980rpx;
  --ease-smooth: cubic-bezier(0.25, 0.1, 0.25, 1);
}

page {
  background: var(--bg-primary);
  font-family: -apple-system, 'SF Pro Display', 'SF Pro Text', 'Helvetica Neue', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: var(--text-primary);
  -webkit-font-smoothing: antialiased;
}

.brand-gradient {
  background: linear-gradient(135deg, var(--accent), var(--accent2));
}

/* Glass Card */
.page-card {
  background: var(--card-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-sm);
}

/* Tags */
.tag-success { background: var(--tag-success-bg); color: var(--tag-success-text); }
.tag-warning { background: var(--tag-warning-bg); color: var(--tag-warning-text); }
.tag-danger  { background: var(--tag-danger-bg);  color: var(--tag-danger-text); }
.tag-info    { background: var(--tag-info-bg);    color: var(--tag-info-text); }
.tag-primary { background: rgba(0,0,0,0.05);  color: var(--accent); }

/* Splash — Apple Style */
.splash {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: #f5f5f7;
}

.splash-icon {
  width: 80px;
  height: 80px;
  border-radius: 22px;
  background: #1d1d1f;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: #fff;
  font-weight: 700;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  animation: splashPulse 1.5s ease-in-out;
}

.splash-title {
  color: #1d1d1f;
  font-size: 20px;
  font-weight: 600;
  margin-top: 20px;
  letter-spacing: -0.3px;
}

.splash-fade {
  animation: fadeIn 0.4s ease both;
}

@keyframes splashPulse {
  0% { transform: scale(0.8); opacity: 0; }
  50% { transform: scale(1.05); opacity: 1; }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Reveal animation classes */
.reveal {
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.6s var(--ease-smooth), transform 0.6s var(--ease-smooth);
}
.reveal.visible {
  opacity: 1;
  transform: translateY(0);
}
</style>
