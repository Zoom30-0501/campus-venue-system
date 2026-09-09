<template>
  <view class="page">
    <view class="form">
      <view class="input-group"><text class="label">昵称</text><input v-model="form.nickname" class="input" placeholder="请输入昵称" /></view>
      <view class="input-group"><text class="label">所属社团</text><input v-model="form.clubName" class="input" placeholder="社团名称" disabled /></view>
      <button class="btn" @click="handleSave">保存修改</button>
    </view>
  </view>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { put, getUserInfo } from '@/utils/request.js'
const form = reactive({ nickname: '', clubName: '' })
onMounted(() => {
  const u = getUserInfo()
  form.nickname = u.nickname || ''
  form.clubName = u.clubName || ''
})
async function handleSave() {
  try {
    const u = getUserInfo()
    const res = await put('/api/user', { userId: u.userId, nickname: form.nickname })
    if (res.code===200) { uni.showToast({ title:'保存成功', icon:'success' }) }
    else { uni.showToast({ title:res.message||'保存失败', icon:'none' }) }
  } catch (e) { uni.showToast({ title:'保存失败', icon:'none' }) }
}
</script>

<style scoped>
.page { padding: 20rpx; background: var(--bg-primary); min-height: 100vh; }
.form { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 30rpx; box-shadow: var(--shadow-sm); }
.input-group { display: flex; align-items: center; padding: 20rpx 0; border-bottom: 1rpx solid var(--border-color); }
.label { font-size: 28rpx; color: var(--text-secondary); width: 160rpx; }
.input { flex: 1; font-size: 28rpx; color: var(--text-primary); background: transparent; }
.btn { width: 100%; height: 88rpx; line-height: 88rpx; background: #1d1d1f; color: #fff; font-size: 32rpx; border-radius: 12rpx; border: none; margin-top: 40rpx; }
</style>
