<template>
  <view class="page">
    <view v-if="list.length === 0" class="empty">暂无签到记录</view>
    <view v-for="item in list" :key="item.id" class="card">
      <text class="title">活动签到 #{{ item.activityId }}</text>
      <text class="sub">签到时间：{{ item.checkInTime || item.createTime }}</text>
      <text class="sub" :class="item.verified ? 'ok' : 'warn'">{{ item.verified ? '已核验' : '未核验' }}</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, getUserInfo } from '@/utils/request.js'
const list = ref([])
onMounted(async () => {
  try { const u=getUserInfo(); if(u.userId){ const res=await get('/api/checkin/user/'+u.userId); list.value=res.data||[] } } catch(e){}
})
</script>

<style scoped>
.page { padding: 20rpx; background: var(--bg-primary); min-height: 100vh; }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 24rpx; margin-bottom: 16rpx; box-shadow: var(--shadow-sm); }
.title { font-size: 28rpx; font-weight: bold; color: var(--text-primary); display: block; margin-bottom: 6rpx; }
.sub { font-size: 24rpx; color: var(--text-secondary); display: block; margin-bottom: 4rpx; }
.ok { color: var(--tag-success-text); }
.warn { color: var(--tag-warning-text); }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 80rpx 0; }
</style>
