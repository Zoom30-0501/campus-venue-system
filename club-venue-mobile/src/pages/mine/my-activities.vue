<template>
  <view class="page">
    <view v-if="list.length === 0" class="empty"><text class="empty-text">暂无活动记录</text><button class="apply-link" @click="goApply">去申请场地</button></view>
    <view v-for="item in list" :key="item.activityId" class="card" @click="goDetail(item.activityId)">
      <text class="title">{{ item.activityName }}</text>
      <text class="sub">{{ item.startTime }} ~ {{ item.endTime }}</text>
      <view class="tag" :class="'tag-' + item.status">{{ item.status }}</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, getUserInfo } from '@/utils/request.js'
const list = ref([])
function goApply() { uni.navigateTo({ url:'/pages/activity/apply' }) }
function goDetail(id) { uni.navigateTo({ url:'/pages/activity/detail?id='+id }) }
onMounted(async () => {
  try { const u = getUserInfo(); if (u.userId) { const res=await get('/api/activity/list?userId='+u.userId); list.value=res.data||[] } } catch (e) {}
})
</script>

<style scoped>
.page { padding: 20rpx; background: var(--bg-primary); min-height: 100vh; }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 24rpx; margin-bottom: 16rpx; box-shadow: var(--shadow-sm); }
.title { font-size: 28rpx; font-weight: bold; color: var(--text-primary); display: block; margin-bottom: 6rpx; }
.sub { font-size: 24rpx; color: var(--text-secondary); display: block; }
.tag { display: inline-block; font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; margin-top: 8rpx; }
.tag-已通过, .tag-进行中 { background: var(--tag-success-bg); color: var(--tag-success-text); }
.tag-待审核 { background: var(--tag-warning-bg); color: var(--tag-warning-text); }
.tag-已驳回 { background: var(--tag-danger-bg); color: var(--tag-danger-text); }
.tag-已结束 { background: var(--tag-info-bg); color: var(--tag-info-text); }
.tag-一级审核通过 { background: var(--tag-warning-bg); color: var(--tag-warning-text); }
.empty { text-align: center; padding: 80rpx 0; }
.empty-text { color: var(--text-muted); font-size: 26rpx; display: block; margin-bottom: 20rpx; }
.apply-link { display: inline-block; padding: 12rpx 32rpx; background: #1d1d1f; color: #fff; font-size: 26rpx; border-radius: 8rpx; border: none; }
</style>
