<template>
  <view class="page">
    <!-- 活动头部 -->
    <view class="header-banner">
      <text class="activity-name">{{ activityName }}</text>
      <view v-if="checkedIn" class="checked-badge">已签到</view>
      <text v-if="checkedIn && checkinTime" class="checkin-time">签到时间：{{ checkinTime }}</text>
    </view>

    <!-- 签到统计 -->
    <view class="count-card">
      <text class="count-num">{{ checkinCount }}</text>
      <text class="count-label">人已签到</text>
    </view>

    <!-- 签到按钮 -->
    <view class="btn-wrap" v-if="!checkedIn">
      <button class="btn-checkin" @click="doCheckin">立即签到</button>
    </view>

    <!-- 签到记录列表 -->
    <view class="list-title">签到记录</view>
    <view v-if="records.length === 0" class="empty">暂无签到记录</view>
    <view v-for="item in records" :key="item.checkinId" class="card">
      <text class="user">{{ item.username || item.userName || '匿名用户' }}</text>
      <text class="time">{{ item.checkinTime || item.createTime }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, post, getUserInfo } from '@/utils/request.js'

let activityId = null
const activityName = ref('')
const checkedIn = ref(false)
const checkinTime = ref('')
const checkinCount = ref(0)
const records = ref([])

async function loadData() {
  try {
    const res = await get('/api/checkin/count/' + activityId)
    checkinCount.value = res.data || 0
  } catch (e) {}
  // 加载签到记录列表
  try {
    const res = await get('/api/checkin/list/' + activityId)
    records.value = res.data || []
    const u = getUserInfo()
    const mine = records.value.find(r => r.userId === u.userId)
    if (mine) {
      checkedIn.value = true
      checkinTime.value = mine.checkinTime || mine.createTime || ''
    }
  } catch (e) {}
}

async function doCheckin() {
  try {
    const u = getUserInfo()
    const res = await post('/api/checkin', { activityId: activityId, userId: u.userId })
    if (res.code === 200) {
      uni.showToast({ title: '签到成功', icon: 'success' })
      checkedIn.value = true
      const now = new Date()
      checkinTime.value = now.getFullYear() + '-' + 
        String(now.getMonth()+1).padStart(2,'0') + '-' + 
        String(now.getDate()).padStart(2,'0') + ' ' +
        String(now.getHours()).padStart(2,'0') + ':' +
        String(now.getMinutes()).padStart(2,'0') + ':' +
        String(now.getSeconds()).padStart(2,'0')
      checkinCount.value++
      loadData()
    } else {
      uni.showToast({ title: res.message || '签到失败', icon: 'none' })
    }
  } catch (e) { uni.showToast({ title: '签到失败', icon: 'none' }) }
}

onMounted(() => {
  const pages = getCurrentPages()
  const opts = pages[pages.length - 1].options || {}
  activityId = opts.activityId
  activityName.value = opts.activityName || ''
  if (activityId) loadData()
})
</script>

<style scoped>
.page { padding-bottom: 30rpx; background: var(--bg-primary); min-height: 100vh; }
.header-banner { background: var(--header-bg); padding: 40rpx 30rpx; text-align: center; border-bottom: 1px solid var(--border-color); }
.activity-name { font-size: 36rpx; font-weight: bold; color: var(--text-primary); display: block; }
.checked-badge { display: inline-block; margin-top: 16rpx; font-size: 24rpx; padding: 6rpx 24rpx; border-radius: 20rpx; background: var(--tag-success-bg); color: var(--tag-success-text); }
.checkin-time { font-size: 24rpx; color: var(--text-secondary); display: block; margin-top: 8rpx; }
.count-card { margin: 20rpx; padding: 30rpx; background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; text-align: center; box-shadow: var(--shadow-sm); }
.count-num { font-size: 56rpx; font-weight: bold; color: var(--accent); display: block; }
.count-label { font-size: 26rpx; color: var(--text-secondary); margin-top: 4rpx; display: block; }
.btn-wrap { padding: 0 20rpx; margin-bottom: 20rpx; }
.btn-checkin { width: 100%; height: 96rpx; line-height: 96rpx; background: #1d1d1f; color: #fff; font-size: 34rpx; border-radius: 14rpx; border: none; }
.list-title { font-size: 28rpx; font-weight: bold; color: var(--text-primary); padding: 20rpx 20rpx 10rpx; }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 20rpx 24rpx; margin: 0 20rpx 12rpx; display: flex; justify-content: space-between; align-items: center; box-shadow: var(--shadow-sm); }
.user { font-size: 28rpx; color: var(--text-primary); }
.time { font-size: 24rpx; color: var(--text-muted); }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 60rpx 0; }
</style>
