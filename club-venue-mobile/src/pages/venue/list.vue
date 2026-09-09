<template>
  <view class="page">
    <!-- 顶部渐变背景 -->
    <view class="page-hero">
      <text class="hero-title">场地</text>
      <text class="hero-sub">浏览可用场地并预约</text>
    </view>

    <view class="card-list">
      <view v-if="list.length === 0" class="empty">暂无场地</view>
      <view v-for="(item, idx) in list" :key="item.venueId" class="card" :class="'stagger-' + (idx + 1)" @click="goDetail(item.venueId)">
        <view class="card-body">
          <view class="row">
            <text class="name">{{ item.venueName }}</text>
            <text class="status" :class="item.status === '可用' ? 's-ok' : 's-busy'">{{ item.status || '可用' }}</text>
          </view>
          <text class="sub">编号：{{ item.venueNo || '-' }}</text>
        </view>
        <text class="arrow">›</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request.js'
const list = ref([])
function goDetail(id) { uni.navigateTo({ url: '/pages/venue/detail?id=' + id }) }
onMounted(async () => {
  try { const res = await get('/api/venue/list'); list.value = res.data || [] } catch (e) {}
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
  display: flex; align-items: center;
  box-shadow: 0 4px 24px rgba(0,0,0,0.04);
  transition: transform 0.2s ease;
  animation: cardIn 0.5s cubic-bezier(0.25, 0.1, 0.25, 1) both;
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

.card-body { flex: 1; }
.row { display: flex; align-items: center; justify-content: space-between; }
.name { font-size: 30rpx; font-weight: 600; color: #1d1d1f; }
.status { font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 980rpx; font-weight: 500; }
.s-ok { background: rgba(0,0,0,0.05); color: #1d1d1f; }
.s-busy { background: rgba(0,0,0,0.05); color: #424245; }
.sub { font-size: 24rpx; color: #86868b; margin-top: 8rpx; display: block; }
.arrow { font-size: 36rpx; color: #c7c7cc; }
.empty { text-align: center; color: #86868b; font-size: 26rpx; padding: 80rpx 0; }
</style>
