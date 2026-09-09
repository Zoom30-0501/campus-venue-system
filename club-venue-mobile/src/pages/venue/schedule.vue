<template>
  <view class="page">
    <view v-if="list.length === 0" class="empty">该场地暂无排期</view>
    <view v-for="item in list" :key="item.scheduleId || item.activityId" class="card">
      <text class="title">{{ item.activityName }}</text>
      <text class="sub">开始：{{ item.startTime }}</text>
      <text class="sub">结束：{{ item.endTime }}</text>
      <view class="tag" :class="statusTag(item.status)">{{ item.status }}</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request.js'

const list = ref([])

function statusTag(status) {
  if (status === '已通过' || status === '进行中') return 'tag-success'
  if (status === '待审核' || status === '一级审核通过') return 'tag-warning'
  if (status === '已驳回') return 'tag-danger'
  if (status === '已结束') return 'tag-info'
  return 'tag-info'
}

onMounted(async () => {
  const pages = getCurrentPages()
  const venueId = pages[pages.length - 1].options?.venueId
  if (!venueId) return
  try {
    const res = await get('/api/venue/schedule/' + venueId)
    list.value = res.data || []
  } catch (e) {}
})
</script>

<style scoped>
.page { padding: 20rpx; background: var(--bg-primary); min-height: 100vh; }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 24rpx; margin-bottom: 16rpx; box-shadow: var(--shadow-sm); }
.title { font-size: 30rpx; font-weight: bold; color: var(--text-primary); display: block; margin-bottom: 8rpx; }
.sub { font-size: 24rpx; color: var(--text-secondary); display: block; margin-bottom: 4rpx; }
.tag { display: inline-block; font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; margin-top: 8rpx; }
.tag-success { background: var(--tag-success-bg); color: var(--tag-success-text); }
.tag-warning { background: var(--tag-warning-bg); color: var(--tag-warning-text); }
.tag-danger { background: var(--tag-danger-bg); color: var(--tag-danger-text); }
.tag-info { background: var(--tag-info-bg); color: var(--tag-info-text); }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 80rpx 0; }
</style>
