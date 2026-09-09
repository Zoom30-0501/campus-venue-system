<template>
  <view class="page">
    <view v-if="list.length === 0" class="empty">暂无成员</view>
    <view v-for="item in list" :key="item.memberId || item.userId" class="card">
      <view class="card-left">
        <view class="avatar">{{ (item.username || item.userName || '用')[0] }}</view>
      </view>
      <view class="card-body">
        <text class="name">{{ item.username || item.userName }}</text>
        <text class="sub">{{ item.phone || '' }}</text>
      </view>
      <view class="tag" :class="roleClass(item.role)">{{ item.role }}</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request.js'

const list = ref([])

function roleClass(role) {
  if (role === '社长' || role === '社长/管理员') return 'tag-primary'
  if (role === '管理员') return 'tag-success'
  return 'tag-info'
}

onMounted(async () => {
  const pages = getCurrentPages()
  const clubId = pages[pages.length - 1].options?.clubId
  if (!clubId) return
  try {
    const res = await get('/api/club-member/list/' + clubId)
    list.value = res.data || []
  } catch (e) {}
})
</script>

<style scoped>
.page { padding: 20rpx; background: var(--bg-primary); min-height: 100vh; }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 20rpx 24rpx; margin-bottom: 12rpx; display: flex; align-items: center; box-shadow: var(--shadow-sm); }
.card-left { margin-right: 16rpx; }
.avatar { width: 72rpx; height: 72rpx; line-height: 72rpx; border-radius: 50%; background: rgba(0,0,0,0.05); color: var(--accent); text-align: center; font-size: 28rpx; font-weight: bold; }
.card-body { flex: 1; }
.name { font-size: 28rpx; font-weight: bold; color: var(--text-primary); display: block; margin-bottom: 2rpx; }
.sub { font-size: 24rpx; color: var(--text-muted); display: block; }
.tag { display: inline-block; font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; }
.tag-primary { background: rgba(0,0,0,0.05); color: var(--accent); }
.tag-success { background: var(--tag-success-bg); color: var(--tag-success-text); }
.tag-info { background: var(--tag-info-bg); color: var(--tag-info-text); }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 80rpx 0; }
</style>
