<template>
  <view class="page">
    <view class="form-card">
      <!-- 活动信息展示 -->
      <view class="target-info">
        <text class="label">报名活动</text>
        <text class="target-name">{{ activityName }}</text>
        <text class="target-sub">活动ID：{{ activityId }}</text>
      </view>

      <!-- 用户信息展示 -->
      <view class="user-info">
        <view class="info-row">
          <text class="info-label">报名人</text>
          <text class="info-value">{{ userInfo.nickname || userInfo.username || '未知' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">所属社团</text>
          <text class="info-value">{{ userInfo.clubName || '未加入社团' }}</text>
        </view>
      </view>

      <!-- 报名须知 -->
      <view class="notice">
        <text class="notice-title">报名须知：</text>
        <text class="notice-text">1. 报名后需等待活动负责人审核通过；</text>
        <text class="notice-text">2. 审核通过后请按时参加活动；</text>
        <text class="notice-text">3. 活动现场需签到确认；</text>
        <text class="notice-text">4. 如有特殊情况需取消报名。</text>
      </view>

      <button class="submit-btn" @click="handleSubmit" :disabled="submitting">
        {{ submitting ? '提交中...' : '确认报名' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { post, getUserInfo } from '@/utils/request.js'

let activityId = null
const activityName = ref('')
const userInfo = ref({})
const submitting = ref(false)

async function handleSubmit() {
  const u = userInfo.value
  if (!u || !u.userId) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    const res = await post('/api/activity-registration', {
      activityId: Number(activityId),
      userId: u.userId
    })
    if (res.code === 200) {
      uni.showToast({ title: res.message || '报名成功', icon: 'success' })
      setTimeout(() => uni.navigateBack(), 1500)
    } else {
      uni.showToast({ title: res.message || '报名失败', icon: 'none' })
    }
  } catch (e) {
    uni.showToast({ title: '网络异常，请重试', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  userInfo.value = getUserInfo()
  const pages = getCurrentPages()
  const opts = pages[pages.length - 1].options || {}
  activityId = opts.activityId
  activityName.value = opts.activityName || ''
})
</script>

<style scoped>
.page { padding: 20rpx; background: var(--bg-primary); min-height: 100vh; }
.form-card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 16rpx; padding: 32rpx 28rpx; box-shadow: var(--shadow-sm); }
.target-info { text-align: center; padding-bottom: 32rpx; border-bottom: 1rpx solid var(--border-color); }
.label { font-size: 24rpx; color: var(--text-muted); display: block; }
.target-name { font-size: 38rpx; font-weight: bold; color: var(--accent); display: block; margin-top: 8rpx; }
.target-sub { font-size: 24rpx; color: var(--text-secondary); display: block; margin-top: 4rpx; }
.user-info { padding: 24rpx 0; border-bottom: 1rpx solid var(--border-color); }
.info-row { display: flex; justify-content: space-between; padding: 12rpx 0; }
.info-label { font-size: 26rpx; color: var(--text-secondary); }
.info-value { font-size: 26rpx; color: var(--text-primary); font-weight: bold; }
.notice { background: rgba(0,0,0,0.03); border: 1rpx solid rgba(0,0,0,0.06); border-radius: 10rpx; padding: 20rpx; margin: 24rpx 0; }
.notice-title { font-size: 26rpx; font-weight: bold; color: var(--accent); display: block; margin-bottom: 8rpx; }
.notice-text { font-size: 24rpx; color: var(--text-secondary); display: block; line-height: 40rpx; }
.submit-btn { width: 100%; height: 96rpx; line-height: 96rpx; background: #1d1d1f; color: #fff; font-size: 34rpx; font-weight: bold; border-radius: 16rpx; border: none; margin-top: 10rpx; letter-spacing: 2rpx; }
.submit-btn[disabled] { opacity: 0.5; }
</style>
