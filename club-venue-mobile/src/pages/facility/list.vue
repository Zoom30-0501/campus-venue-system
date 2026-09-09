<template>
  <view class="page">
    <view v-if="list.length === 0" class="empty">暂无设施</view>
    <view v-for="item in list" :key="item.facilityId" class="card">
      <view class="card-body">
        <text class="name">{{ item.facilityName }}</text>
        <text class="sub">所属场地：{{ item.venueName || '未指定' }}</text>
      </view>
      <view class="tag" :class="statusClass(item.status)">{{ item.status }}</view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request.js'

const list = ref([])

function statusClass(status) {
  if (status === '正常') return 'tag-success'
  if (status === '维修中') return 'tag-warning'
  if (status === '损坏') return 'tag-danger'
  return 'tag-info'
}

async function loadData() {
  try {
    const res = await get('/api/facility/list')
    list.value = res.data || []
  } catch (e) {}
}

onMounted(() => { loadData() })
</script>

<style scoped>
.page { padding: 20rpx; background: var(--bg-primary); min-height: 100vh; }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 24rpx; margin-bottom: 16rpx; display: flex; align-items: center; justify-content: space-between; box-shadow: var(--shadow-sm); }
.card-body { flex: 1; }
.name { font-size: 30rpx; font-weight: bold; color: var(--text-primary); display: block; margin-bottom: 4rpx; }
.sub { font-size: 24rpx; color: var(--text-secondary); display: block; }
.tag { display: inline-block; font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; }
.tag-success { background: var(--tag-success-bg); color: var(--tag-success-text); }
.tag-warning { background: var(--tag-warning-bg); color: var(--tag-warning-text); }
.tag-danger { background: var(--tag-danger-bg); color: var(--tag-danger-text); }
.tag-info { background: var(--tag-info-bg); color: var(--tag-info-text); }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 80rpx 0; }
</style>
