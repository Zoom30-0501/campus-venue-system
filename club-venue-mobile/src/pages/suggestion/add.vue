<template>
  <view class="page">
    <view class="form">
      <textarea v-model="content" class="textarea" placeholder="请输入你的建议..." maxlength="500" />
      <text class="counter">{{ content.length }}/500</text>
      <button class="btn" @click="handleSubmit">提交建议</button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { post, getUserInfo } from '@/utils/request.js'
const content = ref('')
async function handleSubmit() {
  if (!content.value.trim()) { uni.showToast({ title:'请输入建议内容', icon:'none' }); return }
  try {
    const u = getUserInfo()
    const res = await post('/api/suggestion', { content: content.value, userId: u.userId })
    if (res.code===200) { uni.showToast({ title:'提交成功', icon:'success' }); setTimeout(()=>uni.navigateBack(), 1500) }
    else { uni.showToast({ title:res.message||'提交失败', icon:'none' }) }
  } catch (e) { uni.showToast({ title:'提交失败', icon:'none' }) }
}
</script>

<style scoped>
.page { padding: 20rpx; background: var(--bg-primary); min-height: 100vh; }
.form { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 30rpx; box-shadow: var(--shadow-sm); }
.textarea { width: 100%; height: 300rpx; font-size: 28rpx; color: var(--text-primary); background: var(--bg-tertiary); border-radius: 8rpx; padding: 16rpx; box-sizing: border-box; }
.counter { text-align: right; font-size: 24rpx; color: var(--text-muted); display: block; margin-top: 8rpx; }
.btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #1d1d1f; color: #fff; font-size: 32rpx; border-radius: 12rpx; border: none; margin-top: 30rpx; }
</style>
