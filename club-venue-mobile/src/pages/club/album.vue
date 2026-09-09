<template>
  <view class="page">
    <view v-if="list.length === 0" class="empty">暂无相册图片</view>
    <view class="grid">
      <view v-for="(item, index) in list" :key="index" class="grid-item" @click="preview(index)">
        <image :src="item.url || item.filePath" mode="aspectFill" class="img" />
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request.js'

const list = ref([])

function preview(index) {
  const urls = list.value.map(item => item.url || item.filePath)
  uni.previewImage({ current: urls[index], urls })
}

onMounted(async () => {
  const pages = getCurrentPages()
  const clubId = pages[pages.length - 1].options?.clubId
  if (!clubId) return
  try {
    const res = await get('/api/club/album/' + clubId)
    list.value = res.data || []
  } catch (e) {}
})
</script>

<style scoped>
.page { padding: 16rpx; background: var(--bg-primary); min-height: 100vh; }
.grid { display: flex; flex-wrap: wrap; }
.grid-item { width: calc(33.33% - 8rpx); margin: 4rpx; border-radius: 8rpx; overflow: hidden; background: var(--bg-tertiary); }
.img { width: 100%; height: 230rpx; display: block; }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 120rpx 0; }
</style>
