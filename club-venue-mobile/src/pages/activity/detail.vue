<template>
  <view class="page">
    <view v-if="!activity" class="empty">加载中...</view>
    <template v-else>
      <view class="header-banner">
        <text class="name">{{ activity.activityName }}</text>
        <view class="tag" :class="'tag-' + activity.status">{{ activity.status }}</view>
      </view>
      <view class="card">
        <view class="info-row"><text class="label">申请理由</text><text class="value">{{ activity.reason || '无' }}</text></view>
        <view class="info-row"><text class="label">活动时间</text><text class="value">{{ activity.startTime }} ~ {{ activity.endTime }}</text></view>
        <view class="info-row"><text class="label">社团</text><text class="value">{{ activity.clubName || '未知' }}</text></view>
        <view class="info-row"><text class="label">场地</text><text class="value">{{ activity.venueName || '未指定' }}</text></view>
      </view>
      <view class="btn-group">
        <button v-if="activity.status === '进行中'" class="btn btn-primary" @click="checkIn">签到打卡</button>
        <button class="btn btn-enroll" @click="goEnroll">报名参加活动</button>
      </view>
      <!-- 评价 -->
      <view class="section">
        <view class="section-title">活动评价</view>
        <view v-if="ratings.length === 0" class="empty">暂无评价</view>
        <view v-for="r in ratings" :key="r.id" class="rating-item">
          <view class="rating-header">
            <text class="rating-user">{{ r.userName || '匿名' }}</text>
            <text class="rating-score">{{ '★'.repeat(r.score) }}{{ '☆'.repeat(5 - r.score) }}</text>
          </view>
          <text class="rating-comment">{{ r.comment || '无评论' }}</text>
        </view>
        <button class="btn btn-small" @click="goRate" style="margin-top:16rpx">写评价</button>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, post, getUserInfo } from '@/utils/request.js'
const activity = ref(null)
const ratings = ref([])

function goEnroll() {
  uni.navigateTo({ url: '/pages/activity/enroll?activityId=' + activity.value.activityId + '&activityName=' + encodeURIComponent(activity.value.activityName) })
}
function goRate() {
  uni.navigateTo({ url: '/pages/activity/rating?activityId=' + activity.value.activityId })
}

async function checkIn() {
  try {
    const u = getUserInfo()
    const res = await post('/api/checkin', { activityId: activity.value.activityId, userId: u.userId })
    if (res.code === 200) { uni.showToast({ title: '签到成功', icon: 'success' }) }
    else { uni.showToast({ title: res.message || '签到失败', icon: 'none' }) }
  } catch (e) { uni.showToast({ title: '签到失败', icon: 'none' }) }
}

onMounted(async () => {
  const pages = getCurrentPages(); const id = pages[pages.length - 1].options?.id
  if (!id) return
  try {
    const res = await get('/api/activity/' + id)
    activity.value = res.data
    const ratingRes = await get('/api/activity-rating/list/' + id).catch(() => ({ data: [] }))
    ratings.value = ratingRes.data || []
  } catch (e) {}
})
</script>

<style scoped>
.page { padding-bottom: 30rpx; background: var(--bg-primary); min-height: 100vh; }
.header-banner { background: var(--header-bg); padding: 40rpx 30rpx; text-align: center; border-bottom: 1px solid var(--border-color); }
.name { font-size: 36rpx; font-weight: bold; display: block; color: var(--text-primary); }
.tag { display: inline-block; font-size: 24rpx; padding: 6rpx 20rpx; border-radius: 20rpx; margin-top: 12rpx; background: rgba(0,0,0,0.05); color: var(--accent); }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 24rpx; margin: 20rpx; box-shadow: var(--shadow-sm); }
.info-row { display: flex; padding: 16rpx 0; border-bottom: 1rpx solid var(--border-color); }
.label { font-size: 28rpx; color: var(--text-secondary); width: 150rpx; }
.value { font-size: 28rpx; color: var(--text-primary); flex: 1; }
.btn-group { padding: 20rpx; display: flex; gap: 16rpx; }
.btn { flex: 1; height: 88rpx; line-height: 88rpx; border-radius: 12rpx; font-size: 30rpx; border: none; font-weight: bold; }
.btn-primary { background: #1d1d1f; color: #fff; }
.btn-enroll { background: #1d1d1f; color: #fff; }
.btn-small { height: 64rpx; line-height: 64rpx; font-size: 26rpx; background: rgba(0,0,0,0.05); color: #1d1d1f; border: 1px solid rgba(0,0,0,0.1); border-radius: 8rpx; width: 200rpx; }
.section { padding: 20rpx; }
.section-title { font-size: 30rpx; font-weight: bold; color: var(--text-primary); margin-bottom: 16rpx; }
.rating-item { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 8rpx; padding: 20rpx; margin-bottom: 10rpx; }
.rating-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8rpx; }
.rating-user { font-size: 26rpx; color: var(--text-primary); font-weight: bold; }
.rating-score { font-size: 24rpx; color: #1d1d1f; }
.rating-comment { font-size: 26rpx; color: var(--text-secondary); }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 80rpx; }
</style>
