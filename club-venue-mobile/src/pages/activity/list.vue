<template>
  <view class="page">
    <!-- 顶部渐变背景 -->
    <view class="page-hero">
      <text class="hero-title">活动</text>
      <text class="hero-sub">发现精彩活动并报名参与</text>
    </view>

    <view class="card-list">
      <view v-if="list.length === 0" class="empty">暂无活动</view>
      <view v-for="(item, idx) in list" :key="item.activityId" class="card" :class="'stagger-' + (idx + 1)" @click="goDetail(item.activityId)">
        <text class="title">{{ item.activityName }}</text>
        <text class="sub">🕐 {{ item.startTime }} ~ {{ item.endTime }}</text>
        <text class="sub">📍 {{ item.venueName || '未指定' }} | 🏠 {{ item.clubName || '未知' }}</text>
        <view class="card-footer">
          <view class="tag" :class="'tag-' + item.status">{{ item.status }}</view>
          <button class="enroll-btn" @click.stop="goEnroll(item)">报名参加</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, getUserInfo } from '@/utils/request.js'
const list = ref([])
const userInfo = ref({})

function goDetail(id) { uni.navigateTo({ url: '/pages/activity/detail?id=' + id }) }

function goEnroll(item) {
  const u = userInfo.value
  if (!u || !u.userId) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  uni.navigateTo({ url: '/pages/activity/enroll?activityId=' + item.activityId + '&activityName=' + encodeURIComponent(item.activityName) })
}

onMounted(async () => {
  userInfo.value = getUserInfo()
  try { const res = await get('/api/activity/list'); list.value = res.data || [] } catch (e) {}
})
</script>

<style scoped>
.page { min-height: 100vh; background: #f5f5f7; padding-bottom: 40rpx; }
.page-hero {
  background: #1d1d1f;
  padding: 48rpx 32rpx 64rpx; position: relative; overflow: hidden;
}
.page-hero::after {
  content: ''; position: absolute; bottom: -2rpx; left: 0; right: 0; height: 32rpx;
  background: #f5f5f7; border-radius: 32rpx 32rpx 0 0;
}
.hero-title { display: block; font-size: 44rpx; font-weight: 700; color: #fff; letter-spacing: -0.5rpx; }
.hero-sub { display: block; font-size: 26rpx; color: rgba(255,255,255,0.8); margin-top: 8rpx; }

.card-list { padding: 0 20rpx; margin-top: -24rpx; position: relative; z-index: 1; }
.card {
  background: rgba(255,255,255,0.8);
  backdrop-filter: saturate(200%) blur(20px); -webkit-backdrop-filter: saturate(200%) blur(20px);
  border: 0.5px solid rgba(0,0,0,0.04); border-radius: 20rpx;
  padding: 28rpx; margin-bottom: 16rpx;
  box-shadow: 0 4px 24px rgba(0,0,0,0.04);
  transition: transform 0.2s ease;
  animation: cardIn 0.5s cubic-bezier(0.25, 0.1, 0.25, 1) both;
  position: relative;
}
.card:active { transform: scale(0.98); }
@keyframes cardIn {
  from { opacity: 0; transform: translateY(24rpx) }
  to { opacity: 1; transform: translateY(0) }
}
.stagger-1 { animation-delay: 0s }
.stagger-2 { animation-delay: 0.06s }
.stagger-3 { animation-delay: 0.12s }
.stagger-4 { animation-delay: 0.18s }
.stagger-5 { animation-delay: 0.24s }
.stagger-6 { animation-delay: 0.30s }
.stagger-7 { animation-delay: 0.36s }
.stagger-8 { animation-delay: 0.42s }

.title { font-size: 30rpx; font-weight: 600; color: #1d1d1f; display: block; margin-bottom: 10rpx; }
.sub { font-size: 24rpx; color: #86868b; display: block; margin-bottom: 4rpx; }
.card-footer { display: flex; align-items: center; justify-content: space-between; margin-top: 16rpx; }
.tag { display: inline-block; font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 980rpx; font-weight: 500; }
.tag-已通过, .tag-进行中 { background: rgba(0,0,0,0.05); color: #1d1d1f; }
.tag-待审核 { background: rgba(0,0,0,0.05); color: #424245; }
.tag-已驳回 { background: rgba(0,0,0,0.05); color: #1d1d1f; }
.tag-已结束 { background: rgba(134,134,139,0.12); color: #86868b; }
.tag-一级审核通过 { background: rgba(0,0,0,0.05); color: #424245; }
.enroll-btn {
  height: 56rpx; line-height: 56rpx; padding: 0 28rpx;
  background: #1d1d1f;
  color: #fff; font-size: 24rpx; font-weight: 500;
  border-radius: 980rpx; border: none;
  transition: all 0.2s ease;
}
.enroll-btn:active { transform: scale(0.95); }
.empty { text-align: center; color: #86868b; font-size: 26rpx; padding: 80rpx 0; }
</style>
