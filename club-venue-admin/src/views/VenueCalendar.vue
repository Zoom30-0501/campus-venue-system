<template>
  <div class="page-wrap">
    <div class="page-toolbar">
      <h3 class="page-title">场地排期</h3>
      <div class="toolbar-right">
        <el-select v-model="selectedVenue" placeholder="选择场地" size="small" style="width:200px" @change="loadSchedule">
          <el-option v-for="v in venues" :key="v.venueId" :label="v.venueName" :value="v.venueId" />
        </el-select>
        <el-date-picker v-model="currentWeek" type="week" format="yyyy 第 WW 周" size="small" style="margin-left:12px" @change="loadSchedule" />
      </div>
    </div>

    <div class="calendar-grid" v-loading="loading">
      <div class="day-header" v-for="day in weekDays" :key="day.date">
        <span class="day-name">{{ day.label }}</span>
        <span class="day-date" :class="{ today: day.isToday }">{{ day.dateStr }}</span>
      </div>
      <div class="day-column" v-for="day in weekDays" :key="'col-' + day.date">
        <div class="slot-block" v-for="s in getSlotsByDay(day.date)" :key="s.id"
          :class="{ active: s.status === '进行中', locked: s.status === '已锁定' }">
          <div class="slot-title">{{ s.title }}</div>
          <div class="slot-time">{{ formatTime(s.startTime) }} - {{ formatTime(s.endTime) }}</div>
        </div>
        <div class="empty-day" v-if="getSlotsByDay(day.date).length === 0">空闲</div>
      </div>
    </div>
  </div>
</template>

<script>
import http from '@/api'

export default {
  name: 'VenueCalendar',
  data() {
    return {
      venues: [],
      selectedVenue: null,
      currentWeek: new Date(),
      schedules: [],
      loading: false
    }
  },
  computed: {
    weekDays() {
      const days = []
      const monday = this.getMonday(this.currentWeek)
      const today = new Date()
      const todayStr = today.getFullYear() + '-' +
        String(today.getMonth() + 1).padStart(2, '0') + '-' +
        String(today.getDate()).padStart(2, '0')
      const names = ['一', '二', '三', '四', '五', '六', '日']
      for (let i = 0; i < 7; i++) {
        const d = new Date(monday)
        d.setDate(d.getDate() + i)
        const dateStr = d.getFullYear() + '-' +
          String(d.getMonth() + 1).padStart(2, '0') + '-' +
          String(d.getDate()).padStart(2, '0')
        days.push({
          date: dateStr,
          label: '周' + names[i],
          dateStr: (d.getMonth() + 1) + '/' + d.getDate(),
          isToday: dateStr === todayStr
        })
      }
      return days
    }
  },
  created() { this.loadVenues() },
  activated() { if (this.selectedVenue) this.loadSchedule() },
  methods: {
    getMonday(d) {
      const date = new Date(d)
      const day = date.getDay()
      const diff = date.getDate() - day + (day === 0 ? -6 : 1)
      return new Date(date.setDate(diff))
    },
    async loadVenues() {
      try {
        const r = await http.get('/venue/list')
        this.venues = r.data || []
        if (this.venues.length > 0) {
          this.selectedVenue = this.venues[0].venueId
          this.loadSchedule()
        }
      } catch (e) { /* ignore */ }
    },
    async loadSchedule() {
      if (!this.selectedVenue) return
      this.loading = true
      try {
        const startDate = this.weekDays[0].date
        const r = await http.get('/venue/' + this.selectedVenue + '/schedule', { date: startDate })
        this.schedules = r.data || []
        // load remaining 6 days
        for (let i = 1; i < 7; i++) {
          const r2 = await http.get('/venue/' + this.selectedVenue + '/schedule', { date: this.weekDays[i].date })
          if (r2.data) this.schedules.push(...r2.data)
        }
      } catch (e) {
        this.schedules = []
      } finally {
        this.loading = false
      }
    },
    getSlotsByDay(date) {
      return this.schedules.filter(s => s.startTime && s.startTime.startsWith(date))
    },
    formatTime(t) {
      if (!t) return ''
      return t.substring(11, 16)
    }
  }
}
</script>

<style scoped>
.page-wrap { padding: 28px; }
.page-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.page-title { margin: 0; font-size: 22px; font-weight: 600; color: #1d1d1f; letter-spacing: -0.3px; }
.toolbar-right { display: flex; align-items: center; }
.calendar-grid { display: flex; gap: 0; border: 1px solid rgba(0,0,0,0.06); border-radius: 14px; overflow: hidden; min-height: 300px; }
.day-header { flex: 1; text-align: center; padding: 14px 4px; background: rgba(0,0,0,0.02); border-right: 1px solid rgba(0,0,0,0.04); display: flex; flex-direction: column; align-items: center; }
.day-header:last-child { border-right: none; }
.day-name { font-size: 12px; color: #86868b; margin-bottom: 4px; }
.day-date { font-size: 13px; font-weight: 600; color: #1d1d1f; }
.day-date.today { color: #1d1d1f; background: rgba(0,0,0,0.06); padding: 2px 8px; border-radius: 10px; }
.day-column { flex: 1; padding: 8px 6px; border-right: 1px solid rgba(0,0,0,0.04); min-height: 200px; }
.day-column:last-child { border-right: none; }
.slot-block { padding: 8px; margin-bottom: 6px; border-radius: 8px; background: rgba(0,0,0,0.04); border-left: 3px solid #1d1d1f; font-size: 12px; }
.slot-block.locked { background: rgba(0,0,0,0.03); border-left-color: #86868b; }
.slot-block.active { background: rgba(0,0,0,0.06); border-left-color: #424245; }
.slot-title { font-weight: 600; color: #1d1d1f; margin-bottom: 2px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.slot-time { color: #86868b; }
.empty-day { text-align: center; color: #86868b; font-size: 12px; padding: 20px 0; }
</style>
