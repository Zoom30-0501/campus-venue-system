<template>
  <view class="page">
    <!-- 顶部渐变背景 -->
    <view class="page-hero">
      <text class="hero-title">社团</text>
      <text class="hero-sub">发现并加入感兴趣的社团</text>
    </view>

    <view class="card-list">
      <view v-if="list.length === 0" class="empty">暂无社团</view>
      <view v-for="(item, idx) in list" :key="item.clubId" class="card" :class="'stagger-' + (idx + 1)" @click="goDetail(item.clubId)">
        <view class="card-left">
          <view class="avatar">{{ item.clubName ? item.clubName[0] : '社' }}</view>
        </view>
        <view class="card-body">
          <text class="name">{{ item.clubName }}</text>
          <text class="sub">类别：{{ item.category || '未分类' }}</text>
          <text class="sub">负责人：{{ item.leaderName || '未知' }}</text>
        </view>
        <text class="arrow">›</text>
        <button class="join-btn" @click.stop="goApply(item)">申请加入</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, getUserInfo } from '@/utils/request.js'

const list = ref([])
const userInfo = ref({})

function goDetail(id) { uni.navigateTo({ url: '/pages/club/detail?id=' + id }) }

function goApply(item) {
  const u = userInfo.value
  if (!u || !u.userId) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  uni.navigateTo({ url: '/pages/club/apply?clubId=' + item.clubId + '&clubName=' + encodeURIComponent(item.clubName) })
}

onMounted(async () => {
  userInfo.value = getUserInfo()
  try {
    const res = await get('/api/club/list')
    list.value = res.data || []
  } catch (e) {}
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

.card-left { margin-right: 20rpx; }
.avatar {
  width: 88rpx; height: 88rpx; line-height: 88rpx; border-radius: 22rpx;
  background: rgba(0,0,0,0.05); color: #1d1d1f;
  text-align: center; font-size: 34rpx; font-weight: 700;
}
.card-body { flex: 1; min-width: 0; }
.name { font-size: 30rpx; font-weight: 600; color: #1d1d1f; display: block; margin-bottom: 6rpx; }
.sub { font-size: 24rpx; color: #86868b; display: block; margin-bottom: 2rpx; }
.arrow { font-size: 36rpx; color: #c7c7cc; margin-right: 16rpx; }
.join-btn {
  position: absolute; right: 28rpx; bottom: 24rpx;
  height: 56rpx; line-height: 56rpx; padding: 0 24rpx;
  background: #1d1d1f;
  color: #fff; font-size: 24rpx; font-weight: 500;
  border-radius: 980rpx; border: none;
  transition: all 0.2s ease;
}
.join-btn:active { transform: scale(0.95); }
.empty { text-align: center; color: #86868b; font-size: 26rpx; padding: 80rpx 0; }
</style>
