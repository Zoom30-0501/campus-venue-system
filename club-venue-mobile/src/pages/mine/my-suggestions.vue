<template>
  <view class="page">
    <view v-if="list.length === 0" class="empty">暂无建议记录</view>
    <view v-for="item in list" :key="item.suggestionId" class="card">
      <text class="title">{{ item.content }}</text>
      <text class="sub">提交时间：{{ item.createTime }}</text>
      <view class="tag" :class="'tag-' + item.status">{{ item.status === '已采纳' ? '已采纳' : item.status === '已关闭' ? '已关闭' : '待处理' }}</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, getUserInfo } from '@/utils/request.js'
const list = ref([])
onMounted(async () => {
  try { const u=getUserInfo(); if(u.userId){ const res=await get('/api/suggestion/user/'+u.userId); list.value=res.data||[] } } catch(e){}
})
</script>

<style scoped>
.page { padding: 20rpx; background: var(--bg-primary); min-height: 100vh; }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 24rpx; margin-bottom: 16rpx; box-shadow: var(--shadow-sm); }
.title { font-size: 28rpx; color: var(--text-primary); display: block; margin-bottom: 6rpx; }
.sub { font-size: 24rpx; color: var(--text-secondary); display: block; }
.tag { display: inline-block; font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; margin-top: 8rpx; }
.tag-已采纳 { background: var(--tag-success-bg); color: var(--tag-success-text); }
.tag-已关闭 { background: var(--tag-info-bg); color: var(--tag-info-text); }
.tag-待处理 { background: var(--tag-warning-bg); color: var(--tag-warning-text); }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 80rpx 0; }
</style>
