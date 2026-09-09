<template>
  <view class="page">
    <view class="form">
      <view class="target-club">
        <text class="label">申请加入</text>
        <text class="club-name">{{ clubName }}</text>
      </view>
      <textarea v-model="reason" class="textarea" placeholder="请输入申请理由..." maxlength="500" />
      <text class="counter">{{ reason.length }}/500</text>
      <button class="btn" @click="handleSubmit">提交申请</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { post, getUserInfo } from '@/utils/request.js'

let clubId = null
const clubName = ref('')
const reason = ref('')

async function handleSubmit() {
  if (!reason.value.trim()) { uni.showToast({ title: '请输入申请理由', icon: 'none' }); return }
  try {
    const u = getUserInfo()
    const res = await post('/api/club-application', {
      clubId: clubId,
      userId: u.userId,
      reason: reason.value
    })
    if (res.code === 200) {
      uni.showToast({ title: '申请已提交，等待审批', icon: 'success' })
      setTimeout(() => uni.navigateBack(), 1500)
    } else {
      uni.showToast({ title: res.message || '提交失败', icon: 'none' })
    }
  } catch (e) { uni.showToast({ title: '提交失败', icon: 'none' }) }
}

// 使用 onLoad 方式获取参数以兼容 Options API 和 Composition API
import { onMounted } from 'vue'
onMounted(() => {
  const pages = getCurrentPages()
  const opts = pages[pages.length - 1].options || {}
  clubId = opts.clubId
  clubName.value = opts.clubName || ''
})
</script>

<style scoped>
.page { padding: 20rpx; background: var(--bg-primary); min-height: 100vh; }
.form { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 30rpx; box-shadow: var(--shadow-sm); }
.target-club { margin-bottom: 24rpx; }
.label { font-size: 26rpx; color: var(--text-muted); display: block; }
.club-name { font-size: 36rpx; font-weight: bold; color: var(--accent); display: block; margin-top: 8rpx; }
.textarea { width: 100%; height: 300rpx; font-size: 28rpx; color: var(--text-primary); background: var(--bg-tertiary); border-radius: 8rpx; padding: 16rpx; box-sizing: border-box; }
.counter { text-align: right; font-size: 24rpx; color: var(--text-muted); display: block; margin-top: 8rpx; }
.btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #1d1d1f; color: #fff; font-size: 32rpx; border-radius: 12rpx; border: none; margin-top: 30rpx; }
</style>
