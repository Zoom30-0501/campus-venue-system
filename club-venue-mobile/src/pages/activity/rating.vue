<template>
  <view class="page">
    <!-- 评价列表 -->
    <view v-if="list.length === 0 && !loading" class="empty">暂无评价</view>
    <view v-for="item in list" :key="item.ratingId" class="card">
      <view class="rating-header">
        <text class="user">{{ item.username || item.userName || '匿名用户' }}</text>
        <view class="stars">
          <text v-for="n in 5" :key="n" class="star" :class="n <= item.score ? 'star-fill' : 'star-empty'">★</text>
        </view>
      </view>
      <text class="content">{{ item.content }}</text>
      <text class="time">{{ item.createTime || item.createdAt }}</text>
    </view>

    <!-- 写评价按钮 -->
    <view class="btn-wrap">
      <button class="btn-primary" @click="showForm = true">写评价</button>
    </view>

    <!-- 写评价弹窗 -->
    <view v-if="showForm" class="overlay" @click="showForm = false">
      <view class="form-card" @click.stop>
        <text class="form-title">写评价</text>
        <view class="star-picker">
          <text v-for="n in 5" :key="n" class="star big" :class="n <= score ? 'star-fill' : 'star-empty'" @click="score = n">★</text>
        </view>
        <textarea v-model="content" class="textarea" placeholder="请输入评价内容..." maxlength="500" />
        <text class="counter">{{ content.length }}/500</text>
        <view class="form-btns">
          <button class="btn-cancel" @click="showForm = false">取消</button>
          <button class="btn-submit" @click="handleSubmit">提交</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, post, getUserInfo } from '@/utils/request.js'

let activityId = null
const list = ref([])
const loading = ref(true)
const showForm = ref(false)
const score = ref(5)
const content = ref('')

async function loadRatings() {
  try {
    const res = await get('/api/activity-rating/byActivity/' + activityId)
    list.value = res.data || []
  } catch (e) {}
  loading.value = false
}

async function handleSubmit() {
  if (!content.value.trim()) { uni.showToast({ title: '请输入评价内容', icon: 'none' }); return }
  try {
    const u = getUserInfo()
    const res = await post('/api/activity-rating', {
      activityId: activityId,
      userId: u.userId,
      score: score.value,
      content: content.value
    })
    if (res.code === 200) {
      uni.showToast({ title: '评价成功', icon: 'success' })
      showForm.value = false
      content.value = ''
      score.value = 5
      loadRatings()
    } else {
      uni.showToast({ title: res.message || '评价失败', icon: 'none' })
    }
  } catch (e) { uni.showToast({ title: '评价失败', icon: 'none' }) }
}

onMounted(() => {
  const pages = getCurrentPages()
  activityId = pages[pages.length - 1].options?.activityId
  if (activityId) loadRatings()
})
</script>

<style scoped>
.page { padding: 20rpx; padding-bottom: 120rpx; background: var(--bg-primary); min-height: 100vh; }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 24rpx; margin-bottom: 16rpx; box-shadow: var(--shadow-sm); }
.rating-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.user { font-size: 28rpx; font-weight: bold; color: var(--text-primary); }
.stars { display: flex; gap: 4rpx; }
.star { font-size: 28rpx; }
.star-fill { color: #1d1d1f; }
.star-empty { color: var(--text-muted); }
.content { font-size: 26rpx; color: var(--text-secondary); display: block; margin-bottom: 8rpx; line-height: 1.6; }
.time { font-size: 22rpx; color: var(--text-muted); }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 80rpx 0; }
.btn-wrap { position: fixed; bottom: 0; left: 0; right: 0; padding: 20rpx; background: var(--card-bg); border-top: 1px solid var(--card-border); }
.btn-primary { width: 100%; height: 88rpx; line-height: 88rpx; background: #1d1d1f; color: #fff; font-size: 32rpx; border-radius: 12rpx; border: none; }
.overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
.form-card { background: var(--bg-secondary); border-radius: 16rpx; padding: 40rpx 30rpx; width: 620rpx; }
.form-title { font-size: 32rpx; font-weight: bold; color: var(--text-primary); display: block; text-align: center; margin-bottom: 24rpx; }
.star-picker { text-align: center; margin-bottom: 24rpx; }
.star.big { font-size: 48rpx; padding: 0 8rpx; }
.textarea { width: 100%; height: 240rpx; font-size: 28rpx; color: var(--text-primary); background: var(--bg-tertiary); border-radius: 8rpx; padding: 16rpx; box-sizing: border-box; }
.counter { text-align: right; font-size: 24rpx; color: var(--text-muted); display: block; margin-top: 8rpx; }
.form-btns { display: flex; gap: 20rpx; margin-top: 24rpx; }
.btn-cancel { flex: 1; height: 80rpx; line-height: 80rpx; background: var(--bg-tertiary); color: var(--text-secondary); font-size: 28rpx; border-radius: 10rpx; border: none; }
.btn-submit { flex: 1; height: 80rpx; line-height: 80rpx; background: #1d1d1f; color: #fff; font-size: 28rpx; border-radius: 10rpx; border: none; }
</style>
