<template>
  <view class="root">
    <!-- ═══ 未登录：Apple 风格全屏登录 ═══ -->
    <view v-if="!loggedIn" class="login-full">
      <!-- 渐变装饰圆 -->
      <view class="deco-circle c1"></view>
      <view class="deco-circle c2"></view>
      <view class="deco-circle c3"></view>

      <view class="login-card">
        <view class="card-dot"></view>
        <text class="card-title">社团场地管理系统</text>
        <text class="card-sub">高校社团活动 · 场地调度一站管理</text>
        <input v-model="username" placeholder="用户名" class="my-input" />
        <input v-model="password" type="password" placeholder="密码" class="my-input" />
        <button class="my-btn" @click="doLogin" :disabled="loading">{{ loading ? '登录中...' : '登 录' }}</button>
        <view class="my-link" @click="goRegister">没有账号？立即注册</view>
      </view>
      <view class="test-accounts">
        <text class="test-accounts-title">测试账号（点击自动填入）</text>
        <view class="test-accounts-list">
          <view class="test-account-item" v-for="acc in testAccounts" :key="acc.user" @click="fillAccount(acc)">
            <text class="test-account-role">{{ acc.role }}</text>
            <text class="test-account-info">{{ acc.user }} / 123456</text>
          </view>
        </view>
      </view>
    </view>

    <!-- ═══ 已登录：Apple 风格仪表盘 ═══ -->
    <view v-else class="home-page">
      <!-- 顶部渐变背景区域 -->
      <view class="hero-banner">
        <text class="hero-greeting">欢迎回来</text>
        <text class="hero-sub">社团场地管理平台</text>
      </view>

      <!-- 统计卡片 2x2 网格 -->
      <view class="stats-grid">
        <view class="stat-card" v-for="(s, idx) in statList" :key="s.label" :class="'stagger-' + (idx + 1)">
          <view class="stat-icon" :style="{ background: s.iconBg }">
            <text class="stat-icon-text">{{ s.iconChar }}</text>
          </view>
          <text class="stat-num">{{ s.value }}</text>
          <text class="stat-label">{{ s.label }}</text>
        </view>
      </view>

      <!-- 快捷入口 -->
      <view class="section">
        <view class="section-title">快捷入口</view>
        <view class="grid">
          <view class="grid-item" @click="goTab('/pages/club/list')">
            <view class="grid-icon gi-club"><text class="gi-text">社</text></view>
            <text class="grid-label">社团</text>
          </view>
          <view class="grid-item" @click="goTab('/pages/venue/list')">
            <view class="grid-icon gi-venue"><text class="gi-text">场</text></view>
            <text class="grid-label">场地</text>
          </view>
          <view class="grid-item" @click="goTab('/pages/activity/list')">
            <view class="grid-icon gi-activity"><text class="gi-text">活</text></view>
            <text class="grid-label">活动</text>
          </view>
          <view class="grid-item" @click="goApply">
            <view class="grid-icon gi-apply"><text class="gi-text">申</text></view>
            <text class="grid-label">申请</text>
          </view>
        </view>
      </view>

      <!-- 近期活动 -->
      <view class="section">
        <view class="section-title">近期活动</view>
        <view v-if="activities.length === 0" class="empty">暂无活动</view>
        <view v-for="(item, idx) in activities" :key="item.activityId" class="act-card" :class="'stagger-' + (idx + 1)" @click="goDetail('/pages/activity/detail', item.activityId)">
          <view class="act-header">
            <text class="act-title">{{ item.activityName }}</text>
            <view class="tag" :class="'tag-' + item.status">{{ item.status }}</view>
          </view>
          <text class="act-sub">🕐 {{ item.startTime }}</text>
          <text class="act-sub">📍 {{ item.venueName || '未指定' }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get, post, setToken, setUserInfo, getUserInfo } from '@/utils/request.js'

const loggedIn = ref(false)
const username = ref('')
const password = ref('')
const loading = ref(false)
const stats = ref({ clubCount: 0, venueCount: 0, activityCount: 0, checkInCount: 0 })
const activities = ref([])
const testAccounts = [
  { role: '管理员', user: 'admin' },
  { role: '社团负责人', user: 'zhangsan' },
  { role: '普通用户', user: 'testuser' }
]

function fillAccount(acc) {
  username.value = acc.user
  password.value = '123456'
}

function goTab(url) { uni.switchTab({ url }) }
function goApply() { uni.navigateTo({ url: '/pages/activity/apply' }) }
function goDetail(url, id) { uni.navigateTo({ url: url + '?id=' + id }) }
function goRegister() { uni.navigateTo({ url: '/pages/register/register' }) }

async function doLogin() {
  if (!username.value || !password.value) return uni.showToast({ title: '请输入账号和密码', icon: 'none' })
  loading.value = true
  try {
    const res = await post('/api/user/login', { username: username.value, password: password.value })
    if (res && res.code === 200) {
      setToken(res.data.token)
      setUserInfo({
        userId: res.data.userId, username: res.data.username, nickname: res.data.nickname,
        avatar: res.data.avatar, role: res.data.role, clubId: res.data.clubId, clubName: res.data.clubName
      })
      loggedIn.value = true
      loadData()
    } else {
      uni.showToast({ title: (res && res.message) || '登录失败', icon: 'none' })
    }
  } catch (e) { uni.showToast({ title: '连接失败', icon: 'none' }) }
  finally { loading.value = false }
}

async function loadData() {
  try {
    const [clubRes, venueRes, actRes] = await Promise.all([
      get('/api/club/list', {}, true), get('/api/venue/list', {}, true), get('/api/activity/list', {}, true)
    ])
    stats.value.clubCount = clubRes?.data?.length || 0
    stats.value.venueCount = venueRes?.data?.length || 0
    stats.value.activityCount = actRes?.data?.length || 0
    activities.value = (actRes?.data || []).slice(0, 5)
  } catch (e) {}
}

function checkLogin() {
  const u = getUserInfo()
  if (u && u.userId) { loggedIn.value = true; loadData() }
  else { loggedIn.value = false; username.value = ''; password.value = '' }
}

onShow(() => { checkLogin() })

const statList = computed(() => [
  { label: '社团', value: stats.value.clubCount, iconChar: '社', iconBg: 'rgba(0,0,0,0.06)' },
  { label: '场地', value: stats.value.venueCount, iconChar: '场', iconBg: 'rgba(0,0,0,0.06)' },
  { label: '活动', value: stats.value.activityCount, iconChar: '活', iconBg: 'rgba(0,0,0,0.06)' },
  { label: '签到', value: stats.value.checkInCount, iconChar: '签', iconBg: 'rgba(0,0,0,0.06)' },
])
</script>

<style scoped>
/* ═══ 登录页 - Apple 风格 ═══ */
.login-full {
  height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center;
  position: fixed; top: 0; left: 0; width: 100vw; z-index: 999; overflow: hidden;
  background: linear-gradient(160deg, #f5f5f7 0%, #e8e8ed 40%, #f0f0f5 100%);
}
/* 浮动装饰圆 - Apple 配色 */
.deco-circle { position: absolute; border-radius: 50%; z-index: 0; }
.deco-circle.c1 {
  width: 300rpx; height: 300rpx;
  background: radial-gradient(circle, rgba(0,0,0,0.06), transparent 70%);
  top: 8%; right: -60rpx;
  animation: floatApple 10s ease-in-out infinite;
}
.deco-circle.c2 {
  width: 400rpx; height: 400rpx;
  background: radial-gradient(circle, rgba(0,0,0,0.04), transparent 70%);
  bottom: 5%; left: -100rpx;
  animation: floatApple 14s ease-in-out infinite reverse;
}
.deco-circle.c3 {
  width: 200rpx; height: 200rpx;
  background: radial-gradient(circle, rgba(0,0,0,0.03), transparent 70%);
  top: 45%; left: 15%;
  animation: floatApple 8s ease-in-out infinite 2s;
}
@keyframes floatApple {
  0%, 100% { transform: translateY(0) scale(1) }
  50% { transform: translateY(-40rpx) scale(1.08) }
}

.login-card {
  position: relative; z-index: 1; width: 620rpx;
  background: rgba(255,255,255,0.72);
  backdrop-filter: saturate(200%) blur(40px); -webkit-backdrop-filter: saturate(200%) blur(40px);
  border: 0.5px solid rgba(0,0,0,0.06); border-radius: 24rpx;
  padding: 56rpx 40rpx 40rpx;
  box-shadow: 0 24px 80px rgba(0,0,0,0.08), 0 0 1px rgba(0,0,0,0.1);
  animation: cardEnterApple 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) both;
}
@keyframes cardEnterApple {
  from { opacity: 0; transform: translateY(40rpx) scale(0.96) }
  to { opacity: 1; transform: translateY(0) scale(1) }
}
.card-dot {
  width: 16rpx; height: 16rpx; border-radius: 50%;
  background: #1d1d1f;
  margin: 0 auto 24rpx;
  box-shadow: 0 0 20rpx rgba(0,0,0,0.15);
}
.card-title {
  font-size: 44rpx; font-weight: 700; color: #1d1d1f;
  text-align: center; display: block; letter-spacing: -0.5rpx;
}
.card-sub {
  font-size: 26rpx; color: #86868b; text-align: center;
  display: block; margin: 12rpx 0 40rpx; letter-spacing: 0.5rpx;
}
.my-input {
  width: 100%; height: 96rpx;
  border: 0.5px solid rgba(0,0,0,0.1); border-radius: 14rpx;
  padding: 0 28rpx; font-size: 30rpx;
  background: rgba(255,255,255,0.6); color: #1d1d1f;
  margin-bottom: 20rpx; box-sizing: border-box;
  transition: all 0.3s ease;
}
.my-input:focus {
  border-color: #1d1d1f;
  box-shadow: 0 0 0 3px rgba(0,0,0,0.06);
  background: rgba(255,255,255,0.9);
}
.my-btn {
  width: 100%; height: 96rpx; line-height: 96rpx;
  background: #1d1d1f;
  color: #fff; font-size: 34rpx; font-weight: 600;
  border-radius: 980rpx; border: none;
  letter-spacing: 2rpx;
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
}
.my-btn:active { transform: scale(0.97); opacity: 0.9; }
.my-btn[disabled] { opacity: 0.5; }
.my-link { text-align: center; color: #1d1d1f; font-size: 26rpx; margin-top: 28rpx; }
/* Test accounts */
.test-accounts {
  position: relative; z-index: 1;
  width: 620rpx; margin-top: 32rpx;
  animation: cardEnterApple 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) 0.3s both;
}
.test-accounts-title {
  display: block; text-align: center;
  font-size: 24rpx; color: #86868b; margin-bottom: 16rpx;
}
.test-accounts-list {
  display: flex; flex-direction: column; gap: 12rpx;
}
.test-account-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 20rpx 28rpx; border-radius: 16rpx;
  background: rgba(255,255,255,0.6);
  backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
  border: 0.5px solid rgba(0,0,0,0.06);
  transition: all 0.25s cubic-bezier(0.25, 0.1, 0.25, 1);
}
.test-account-item:active {
  transform: scale(0.97);
  background: rgba(255,255,255,0.9);
}
.test-account-role {
  font-size: 26rpx; font-weight: 500; color: #1d1d1f;
}
.test-account-info {
  font-size: 24rpx; color: #86868b;
}

/* ═══ 首页 - Apple 风格仪表盘 ═══ */
.home-page {
  min-height: 100vh; background: #f5f5f7;
  padding-bottom: 40rpx;
}

/* Hero Banner */
.hero-banner {
  background: #1d1d1f;
  padding: 60rpx 32rpx 80rpx;
  position: relative; overflow: hidden;
}
.hero-banner::after {
  content: ''; position: absolute; bottom: -2rpx; left: 0; right: 0; height: 40rpx;
  background: #f5f5f7; border-radius: 40rpx 40rpx 0 0;
}
.hero-greeting { display: block; font-size: 44rpx; font-weight: 700; color: #fff; letter-spacing: -0.5rpx; }
.hero-sub { display: block; font-size: 28rpx; color: rgba(255,255,255,0.8); margin-top: 8rpx; }

/* 统计卡片 2x2 */
.stats-grid {
  display: flex; flex-wrap: wrap; gap: 16rpx;
  padding: 0 20rpx; margin-top: -40rpx; position: relative; z-index: 1;
}
.stat-card {
  width: calc(50% - 8rpx); background: rgba(255,255,255,0.8);
  backdrop-filter: saturate(200%) blur(20px); -webkit-backdrop-filter: saturate(200%) blur(20px);
  border: 0.5px solid rgba(0,0,0,0.04); border-radius: 20rpx;
  padding: 28rpx 24rpx; box-sizing: border-box;
  box-shadow: 0 4px 24px rgba(0,0,0,0.04);
  animation: statEnter 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) both;
}
@keyframes statEnter {
  from { opacity: 0; transform: translateY(30rpx) scale(0.92) }
  to { opacity: 1; transform: translateY(0) scale(1) }
}
.stat-icon {
  width: 56rpx; height: 56rpx; border-radius: 14rpx;
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 16rpx;
}
.stat-icon-text { font-size: 26rpx; font-weight: 700; }
.stat-num { display: block; font-size: 48rpx; font-weight: 700; color: #1d1d1f; letter-spacing: -1rpx; }
.stat-label { font-size: 24rpx; color: #86868b; margin-top: 4rpx; display: block; }

/* 交错动画 */
.stagger-1 { animation-delay: 0s }
.stagger-2 { animation-delay: 0.08s }
.stagger-3 { animation-delay: 0.16s }
.stagger-4 { animation-delay: 0.24s }

/* Section */
.section { padding: 0 20rpx; margin-top: 32rpx; }
.section-title {
  font-size: 34rpx; font-weight: 700; color: #1d1d1f;
  margin-bottom: 20rpx; letter-spacing: -0.3rpx;
}

/* 快捷入口 */
.grid { display: flex; gap: 16rpx; }
.grid-item {
  flex: 1; text-align: center;
  background: rgba(255,255,255,0.8);
  backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
  border: 0.5px solid rgba(0,0,0,0.04);
  border-radius: 20rpx; padding: 24rpx 0;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  transition: transform 0.2s ease;
}
.grid-item:active { transform: scale(0.96); }
.grid-icon {
  width: 88rpx; height: 88rpx; border-radius: 22rpx;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 12rpx;
}
.gi-text { font-size: 34rpx; font-weight: 700; }
.gi-club, .gi-venue, .gi-activity, .gi-apply { background: rgba(0,0,0,0.05); }
.gi-club .gi-text, .gi-venue .gi-text, .gi-activity .gi-text, .gi-apply .gi-text { color: #1d1d1f; }
.grid-label { font-size: 24rpx; color: #86868b; }

/* 活动卡片 */
.act-card {
  background: rgba(255,255,255,0.8);
  backdrop-filter: saturate(200%) blur(20px); -webkit-backdrop-filter: saturate(200%) blur(20px);
  border: 0.5px solid rgba(0,0,0,0.04); border-radius: 20rpx;
  padding: 28rpx; margin-bottom: 16rpx;
  box-shadow: 0 4px 24px rgba(0,0,0,0.04);
  transition: transform 0.2s ease;
  animation: actEnter 0.5s cubic-bezier(0.25, 0.1, 0.25, 1) both;
}
.act-card:active { transform: scale(0.98); }
@keyframes actEnter {
  from { opacity: 0; transform: translateY(20rpx) }
  to { opacity: 1; transform: translateY(0) }
}
.act-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12rpx; }
.act-title { font-size: 30rpx; font-weight: 600; color: #1d1d1f; flex: 1; margin-right: 12rpx; }
.act-sub { font-size: 24rpx; color: #86868b; display: block; margin-bottom: 4rpx; }

/* Tags */
.tag { display: inline-block; font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 980rpx; }
.tag-已通过,.tag-进行中 { background: rgba(0,0,0,0.05); color: #1d1d1f; }
.tag-待审核 { background: rgba(0,0,0,0.05); color: #424245; }
.tag-已驳回 { background: rgba(0,0,0,0.05); color: #1d1d1f; }
.tag-已结束 { background: rgba(134,134,139,0.12); color: #86868b; }
.tag-一级审核通过 { background: rgba(0,0,0,0.05); color: #424245; }

.empty { text-align: center; color: #86868b; font-size: 26rpx; padding: 60rpx 0; }
</style>
