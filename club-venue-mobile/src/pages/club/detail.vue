<template>
  <view class="page">
    <view v-if="!club" class="empty">加载中...</view>
    <template v-else>
      <view class="header-banner">
        <view class="club-avatar">{{ club.clubName ? club.clubName[0] : '社' }}</view>
        <text class="club-name">{{ club.clubName }}</text>
        <text class="club-info">类别：{{ club.category || '未分类' }} | 负责人：{{ club.leaderName || '未知' }}</text>
        <button class="apply-btn" @click="goApply">申请加入社团</button>
      </view>
      <view class="section">
        <view class="section-title">社团活动</view>
        <view v-if="activities.length === 0" class="empty">暂无活动</view>
        <view v-for="item in activities" :key="item.activityId" class="card" @click="goActivity(item.activityId)">
          <text class="card-title">{{ item.activityName }}</text>
          <text class="card-sub">{{ item.startTime }}</text>
          <view class="tag" :class="'tag-' + item.status">{{ item.status }}</view>
        </view>
      </view>
      <view class="section">
        <view class="section-title">社团相册</view>
        <view v-if="photos.length === 0" class="empty">暂无照片</view>
        <view class="photo-grid">
          <image v-for="(p, i) in photos" :key="i" :src="p" mode="aspectFill" class="photo" />
        </view>
      </view>
      <view class="section">
        <view class="section-title">社团成员</view>
        <view v-if="members.length === 0" class="empty">暂无成员</view>
        <view v-for="m in members" :key="m.id || m.userId" class="member-item">
          <text>{{ m.nickname || m.userName || m.username || '未知' }}</text>
          <text class="role-tag">{{ m.role || '成员' }}</text>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { get, getUserInfo } from '@/utils/request.js'
const club = ref(null); const activities = ref([]); const photos = ref([]); const members = ref([])
const userInfo = ref({})

function goActivity(id) { uni.navigateTo({ url: '/pages/activity/detail?id=' + id }) }
function goApply() {
  const u = userInfo.value
  if (!u || !u.userId) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  uni.navigateTo({ url: '/pages/club/apply?clubId=' + club.value.clubId + '&clubName=' + encodeURIComponent(club.value.clubName) })
}

onMounted(async () => {
  userInfo.value = getUserInfo()
  const pages = getCurrentPages(); const id = pages[pages.length - 1].options?.id
  if (!id) return
  try {
    const [clubRes, actRes, albumRes, memberRes] = await Promise.all([
      get('/api/club/' + id), get('/api/activity/list?clubId=' + id),
      get('/api/club-album/list/' + id).catch(() => ({ data: [] })),
      get('/api/club-member/list/' + id).catch(() => ({ data: [] }))
    ])
    club.value = clubRes.data; activities.value = actRes.data || []
    photos.value = (albumRes.data || []).map(p => p.imageUrl || p.url || p).filter(Boolean)
    members.value = memberRes.data || []
  } catch (e) {}
})
</script>

<style scoped>
.page { padding-bottom: 30rpx; background: var(--bg-primary); min-height: 100vh; }
.header-banner { background: var(--header-bg); padding: 40rpx 30rpx; text-align: center; border-bottom: 1px solid var(--border-color); }
.club-avatar { width: 100rpx; height: 100rpx; line-height: 100rpx; border-radius: 20rpx; background: rgba(0,0,0,0.05); border: 1px solid rgba(0,0,0,0.08); font-size: 40rpx; color: var(--accent); margin: 0 auto 16rpx; }
.club-name { font-size: 36rpx; font-weight: bold; display: block; color: var(--text-primary); }
.club-info { font-size: 24rpx; color: var(--text-muted); margin-top: 8rpx; display: block; }
.apply-btn { margin: 24rpx auto 0; width: 300rpx; height: 68rpx; line-height: 68rpx; background: #1d1d1f; color: #fff; font-size: 26rpx; font-weight: bold; border-radius: 34rpx; border: none; letter-spacing: 2rpx; }
.section { padding: 20rpx; }
.section-title { font-size: 30rpx; font-weight: bold; color: var(--text-primary); margin-bottom: 16rpx; }
.card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 12rpx; padding: 24rpx; margin-bottom: 12rpx; box-shadow: var(--shadow-sm); }
.card-title { font-size: 28rpx; font-weight: bold; color: var(--text-primary); display: block; }
.card-sub { font-size: 24rpx; color: var(--text-secondary); display: block; margin-top: 6rpx; }
.tag { display: inline-block; font-size: 22rpx; padding: 4rpx 12rpx; border-radius: 6rpx; margin-top: 8rpx; }
.tag-已通过, .tag-进行中 { background: var(--tag-success-bg); color: var(--tag-success-text); }
.tag-待审核 { background: var(--tag-warning-bg); color: var(--tag-warning-text); }
.tag-一级审核通过 { background: var(--tag-warning-bg); color: var(--tag-warning-text); }
.tag-已驳回 { background: var(--tag-danger-bg); color: var(--tag-danger-text); }
.tag-已结束 { background: var(--tag-info-bg); color: var(--tag-info-text); }
.photo-grid { display: flex; flex-wrap: wrap; gap: 8rpx; }
.photo { width: 216rpx; height: 216rpx; border-radius: 8rpx; }
.member-item { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 8rpx; padding: 20rpx 24rpx; margin-bottom: 8rpx; display: flex; justify-content: space-between; font-size: 28rpx; color: var(--text-primary); }
.role-tag { font-size: 22rpx; color: var(--accent); background: rgba(0,0,0,0.05); padding: 4rpx 12rpx; border-radius: 4rpx; }
.empty { text-align: center; color: var(--text-muted); font-size: 26rpx; padding: 30rpx; }
</style>
