<template>
  <view class="page">
    <view v-if="list.length === 0" class="empty">暂无入团申请记录</view>
    <view v-for="item in list" :key="item.id" class="card">
      <text class="title">申请社团：{{ item.clubName || '未知' }}</text>
      <text class="sub">申请理由：{{ item.reason }}</text>
      <text class="sub">申请时间：{{ item.createTime }}</text>
      <view :class="['tag', statusClass(item.status)]">{{ item.status }}</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, getUserInfo } from '@/utils/request.js'
const list = ref([])
function statusClass(status) {
  const map = { '已通过': 'tag-success', '待审核': 'tag-warning', '已驳回': 'tag-danger' }
  return map[status] || 'tag-info'
}
onMounted(async () => {
  const u = getUserInfo()
  if (!u || !u.userId) return
  try {
    const res = await get('/api/club-application/my/' + u.userId)
    list.value = res.data || []
  } catch (e) {}
})
</script>

<style scoped>
.page { padding: 20rpx; background: var(--bg-primary); min-height: 100vh; }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 24rpx; margin-bottom: 16rpx; }
.title { font-size: 30rpx; font-weight: bold; color: var(--text-primary); display: block; margin-bottom: 8rpx; }
.sub { font-size: 24rpx; color: var(--text-secondary); display: block; margin-bottom: 4rpx; }
.tag { display: inline-block; font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; margin-top: 8rpx; }
.tag-success { background: rgba(0,0,0,0.05); color: #1d1d1f; }
.tag-warning { background: rgba(0,0,0,0.05); color: #424245; }
.tag-danger { background: rgba(0,0,0,0.05); color: #1d1d1f; }
.tag-info { background: rgba(134,134,139,0.08); color: #86868b; }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 80rpx 0; }
</style>
