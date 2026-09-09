<template>
  <view class="page">
    <view class="form-card">
      <view class="form-item"><text class="label">活动名称</text><input v-model="form.activityName" class="input" placeholder="请输入活动名称" /></view>
      <view class="form-item"><text class="label">申请理由</text><textarea v-model="form.reason" class="textarea" placeholder="请说明申请理由..." /></view>
      <view class="form-item"><text class="label">选择场地</text><picker mode="selector" :range="venueNames" @change="onVenueChange"><view class="picker-val">{{ selectedVenueName || '请选择场地' }}</view></picker></view>
      <view class="form-item"><text class="label">选择社团</text><picker mode="selector" :range="clubNames" @change="onClubChange"><view class="picker-val">{{ selectedClubName || '请选择社团' }}</view></picker></view>
      <view class="form-item"><text class="label">开始时间</text><picker mode="multiSelector" :range="dateTimeRange" :value="startIndexes" @change="onStartChange"><view class="picker-val">{{ form.startTime || '请选择开始时间' }}</view></picker></view>
      <view class="form-item"><text class="label">结束时间</text><picker mode="multiSelector" :range="dateTimeRange" :value="endIndexes" @change="onEndChange"><view class="picker-val">{{ form.endTime || '请选择结束时间' }}</view></picker></view>
      <button class="submit-btn" @click="handleSubmit" :disabled="submitting">{{ submitting ? '提交中...' : '提交申请' }}</button>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { get, post, getUserInfo } from '@/utils/request.js'
const venues = ref([]); const clubs = ref([]); const submitting = ref(false)
const venueNames = ref([]); const clubNames = ref([])
const selectedVenueName = ref(''); const selectedClubName = ref('')
const form = reactive({ activityName: '', reason: '', venueId: null, clubId: null, startTime: '', endTime: '' })
const now = new Date()
const years=[], months=[], days=[], hours=[], minutes=[]
for (let y=now.getFullYear(); y<=now.getFullYear()+1; y++) years.push(y)
for (let m=1; m<=12; m++) months.push(m<10?'0'+m:''+m)
for (let d=1; d<=31; d++) days.push(d<10?'0'+d:''+d)
for (let h=0; h<24; h++) hours.push(h<10?'0'+h:''+h)
for (let m=0; m<60; m+=5) minutes.push(m<10?'0'+m:''+m)
const dateTimeRange = ref([years, months, days, hours, minutes])
const startIndexes = ref([0, now.getMonth(), now.getDate()-1, now.getHours(), 0])
const endIndexes = ref([0, now.getMonth(), now.getDate()-1, now.getHours()+1, 0])
onMounted(async () => {
  try {
    const [vRes, cRes] = await Promise.all([get('/api/venue/list'), get('/api/club/list')])
    venues.value = vRes.data || []; clubs.value = cRes.data || []
    venueNames.value = venues.value.map(v => v.venueName); clubNames.value = clubs.value.map(c => c.clubName)
    const pages = getCurrentPages(); const query = pages[pages.length-1].options||{}
    if (query.venueId) { const idx = venues.value.findIndex(v => String(v.venueId)===String(query.venueId)); if (idx>=0) { form.venueId=venues.value[idx].venueId; selectedVenueName.value=venues.value[idx].venueName } }
  } catch (e) {}
})
function onVenueChange(e) { const idx=e.detail.value; form.venueId=venues.value[idx].venueId; selectedVenueName.value=venues.value[idx].venueName }
function onClubChange(e) { const idx=e.detail.value; form.clubId=clubs.value[idx].clubId; selectedClubName.value=clubs.value[idx].clubName }
function fmt(arr) { return years[arr[0]]+'-'+months[arr[1]]+'-'+days[arr[2]]+' '+hours[arr[3]]+':'+minutes[arr[4]]+':00' }
function onStartChange(e) { startIndexes.value=e.detail.value; form.startTime=fmt(e.detail.value) }
function onEndChange(e) { endIndexes.value=e.detail.value; form.endTime=fmt(e.detail.value) }
async function handleSubmit() {
  if (!form.activityName.trim()) return uni.showToast({ title:'请输入活动名称', icon:'none' })
  if (!form.venueId) return uni.showToast({ title:'请选择场地', icon:'none' })
  if (!form.startTime||!form.endTime) return uni.showToast({ title:'请选择活动时间', icon:'none' })
  if (form.startTime>=form.endTime) return uni.showToast({ title:'结束时间必须晚于开始时间', icon:'none' })
  submitting.value = true
  try {
    const u = getUserInfo()
    const res = await post('/api/activity', { activityName:form.activityName, reason:form.reason, venueId:form.venueId, clubId:form.clubId||null, applicantId:u.userId, startTime:form.startTime, endTime:form.endTime })
    if (res.code===200) { uni.showToast({ title:'申请已提交，等待审核', icon:'success' }); setTimeout(()=>uni.navigateBack(), 1500) }
    else { uni.showToast({ title:res.message||'提交失败', icon:'none' }) }
  } catch (e) { uni.showToast({ title:'提交失败，请重试', icon:'none' }) }
  finally { submitting.value = false }
}
</script>

<style scoped>
.page { padding: 20rpx; min-height: 100vh; background: var(--bg-primary); }
.form-card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: 16rpx; padding: 32rpx 28rpx; box-shadow: var(--shadow-sm); }
.form-item { padding: 24rpx 0; border-bottom: 1rpx solid var(--border-color); }
.label { font-size: 28rpx; color: var(--text-secondary); display: block; margin-bottom: 12rpx; }
.input { font-size: 28rpx; color: var(--text-primary); background: var(--bg-tertiary); border-radius: 8rpx; padding: 16rpx 20rpx; height: auto; }
.textarea { font-size: 28rpx; color: var(--text-primary); background: var(--bg-tertiary); border-radius: 8rpx; padding: 16rpx 20rpx; width: 100%; height: 160rpx; box-sizing: border-box; }
.picker-val { font-size: 28rpx; color: var(--accent); background: rgba(0,0,0,0.04); border-radius: 8rpx; padding: 16rpx 20rpx; }
.submit-btn { width: 100%; height: 96rpx; line-height: 96rpx; background: #1d1d1f; color: #fff; font-size: 34rpx; font-weight: bold; border-radius: 16rpx; border: none; margin-top: 40rpx; letter-spacing: 2rpx; }
.submit-btn[disabled] { opacity: 0.5; }
</style>
