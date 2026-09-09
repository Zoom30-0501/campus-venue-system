<template>
  <view class="page">
    <view v-if="!venue" class="empty">加载中...</view>
    <template v-else>
      <view class="header-banner">
        <text class="venue-name">{{ venue.venueName }}</text>
        <text class="venue-no">编号：{{ venue.venueNo }}</text>
        <text class="venue-status" :class="venue.status === '可用' ? 's-ok' : 's-busy'">{{ venue.status || '可用' }}</text>
      </view>
      <view class="section">
        <view class="section-title">场地排期</view>
        <view v-if="schedules.length === 0" class="empty">本周暂无排期</view>
        <view v-for="s in schedules" :key="s.scheduleId" class="card">
          <text class="card-title">{{ s.title || s.activityName || '占用' }}</text>
          <text class="card-sub">{{ s.startTime }} ~ {{ s.endTime }}</text>
        </view>
      </view>
      <view class="btn-group">
        <button class="apply-btn" @click="applyVenue">申请使用此场地</button>
      </view>
      <view class="section">
        <view class="section-title">场地设施</view>
        <view v-if="facilities.length === 0" class="empty">暂无设施</view>
        <view v-for="f in facilities" :key="f.facilityId" class="facility-item">
          <text>{{ f.facilityName }}</text>
          <text :class="f.status === '正常' ? 's-ok' : 's-busy'">{{ f.status }}</text>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request.js'
const venue = ref(null); const schedules = ref([]); const facilities = ref([])
function applyVenue() { const id = venue.value?.venueId; if (id) uni.navigateTo({ url: '/pages/activity/apply?venueId=' + id }) }
onMounted(async () => {
  const pages = getCurrentPages(); const id = pages[pages.length - 1].options?.id
  if (!id) return
  try {
    const [vRes, sRes] = await Promise.all([
      get('/api/venue/' + id), get('/api/venue/' + id + '/schedule').catch(() => ({ data: [] }))
    ])
    venue.value = vRes.data; schedules.value = sRes.data || []; facilities.value = vRes.data?.facilities || []
  } catch (e) {}
})
</script>

<style scoped>
.page { padding-bottom: 30rpx; background: var(--bg-primary); min-height: 100vh; }
.header-banner { background: var(--header-bg); padding: 40rpx 30rpx; text-align: center; border-bottom: 1px solid var(--border-color); }
.venue-name { font-size: 36rpx; font-weight: bold; display: block; color: var(--text-primary); }
.venue-no { font-size: 24rpx; color: var(--text-muted); display: block; margin-top: 8rpx; }
.venue-status { display: inline-block; font-size: 24rpx; padding: 6rpx 20rpx; border-radius: 20rpx; margin-top: 12rpx; }
.s-ok { background: var(--tag-success-bg); color: var(--tag-success-text); }
.s-busy { background: var(--tag-danger-bg); color: var(--tag-danger-text); }
.section { padding: 20rpx; }
.section-title { font-size: 30rpx; font-weight: bold; color: var(--text-primary); margin-bottom: 16rpx; }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; box-shadow: var(--shadow-sm); }
.card-title { font-size: 28rpx; font-weight: bold; color: var(--text-primary); display: block; }
.card-sub { font-size: 24rpx; color: var(--text-secondary); display: block; margin-top: 6rpx; }
.facility-item { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 8rpx; padding: 20rpx 24rpx; margin-bottom: 8rpx; display: flex; justify-content: space-between; font-size: 28rpx; color: var(--text-primary); }
.btn-group { padding: 20rpx; }
.apply-btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #1d1d1f; color: #fff; font-size: 32rpx; border-radius: 12rpx; border: none; }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 30rpx; }
</style>
