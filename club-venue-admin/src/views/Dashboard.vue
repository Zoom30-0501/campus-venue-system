<template>
  <div>
    <!-- Stats Cards -->
    <div class="stat-grid">
      <div
        v-for="(card, idx) in cards"
        :key="card.key"
        v-reveal="idx * 0.08"
        class="stat-card"
        :style="{ '--accent': card.accent }"
      >
        <div class="stat-icon-wrap">
          <i :class="card.icon" class="stat-icon"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value">
            <span>{{ formatNumber(animated[card.key]) }}</span>
            <span v-if="card.unit" class="stat-unit">{{ card.unit }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Row -->
    <div class="chart-row">
      <div v-reveal:0.1 class="chart-card">
        <div class="chart-title">近 30 天活动创建趋势</div>
        <div v-if="activityDates.length === 0" class="empty-chart">
          <i class="el-icon-date empty-icon"></i>
          <span>暂无活动数据，请先录入活动</span>
        </div>
        <canvas v-else ref="activityChart" height="200"></canvas>
      </div>

      <div v-reveal:0.18 class="chart-card">
        <div class="chart-title">社团类别分布</div>
        <div v-if="clubCategories.length === 0" class="empty-chart">
          <i class="el-icon-s-flag empty-icon"></i>
          <span>暂无社团数据，请先创建社团</span>
        </div>
        <canvas v-else ref="clubChart" height="200"></canvas>
      </div>
    </div>

    <div class="chart-row" style="margin-top:20px">
      <div v-reveal:0.26 class="chart-card">
        <div class="chart-title">场地活动密度</div>
        <div v-if="venueUsage.length === 0" class="empty-chart">
          <i class="el-icon-office-building empty-icon"></i>
          <span>暂无场地使用数据</span>
        </div>
        <canvas v-else ref="venueUsageChart" height="200"></canvas>
      </div>
      <div v-reveal:0.34 class="chart-card">
        <div class="chart-title">活动签到排行 TOP 5</div>
        <div v-if="checkinStats.length === 0" class="empty-chart">
          <i class="el-icon-s-check empty-icon"></i>
          <span>暂无签到数据</span>
        </div>
        <canvas v-else ref="checkinChart" height="200"></canvas>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/api'

export default {
  name: 'Dashboard',
  data() {
    return {
      cards: [
        { key: 'venue', label: '场地总数', value: 0, unit: '个', icon: 'el-icon-office-building', accent: '#1d1d1f' },
        { key: 'club', label: '社团总数', value: 0, unit: '个', icon: 'el-icon-s-flag', accent: '#1d1d1f' },
        { key: 'activity', label: '活动总数', value: 0, unit: '场', icon: 'el-icon-date', accent: '#1d1d1f' },
        { key: 'facility', label: '设施总量', value: 0, unit: '项', icon: 'el-icon-s-tools', accent: '#1d1d1f' },
        { key: 'checkin', label: '签到次数', value: 0, unit: '次', icon: 'el-icon-s-check', accent: '#1d1d1f' }
      ],
      animated: { venue: 0, club: 0, activity: 0, facility: 0, checkin: 0 },
      activityDates: [],
      clubCategories: [],
      venueUsage: [],
      checkinStats: []
    }
  },
  async mounted() {
    await this.fetchData()
    this.$nextTick(() => {
      this.animateAll()
      this.drawCharts()
    })
  },
  activated() {
    this.refreshData()
  },
  methods: {
    formatNumber(n) {
      if (n >= 1000) return (n / 1000).toFixed(1) + 'k'
      return n
    },

    async fetchData() {
      try {
        const [venueRes, clubRes, activityRes, facilityRes] = await Promise.all([
          request.get('/venue/list'),
          request.get('/club/list'),
          request.get('/activity/list'),
          request.get('/facility/list')
        ])
        const venues = (venueRes.data || []).filter(i => i.deleted !== 1)
        const clubs = (clubRes.data || []).filter(i => i.deleted !== 1)
        const activities = (activityRes.data || []).filter(i => i.deleted !== 1)
        const facilities = (facilityRes.data || []).filter(i => i.deleted !== 1)

        this.cards[0].value = venues.length
        this.cards[1].value = clubs.length
        this.cards[2].value = activities.length
        this.cards[3].value = facilities.length

        const today = new Date()
        today.setHours(0, 0, 0, 0)
        const dateMap = {}
        for (let i = 29; i >= 0; i--) {
          const d = new Date(today)
          d.setDate(d.getDate() - i)
          const key = d.toISOString().slice(0, 10)
          dateMap[key] = 0
        }
        activities.forEach(a => {
          if (a.createTime) {
            const dt = a.createTime.slice(0, 10)
            if (dateMap[dt] !== undefined) dateMap[dt]++
          }
        })
        this.activityDates = Object.entries(dateMap).map(([date, count]) => ({ date, count }))

        const catMap = {}
        clubs.forEach(c => {
          const cat = c.category || '未分类'
          catMap[cat] = (catMap[cat] || 0) + 1
        })
        this.clubCategories = Object.entries(catMap)
          .map(([name, count]) => ({ name, count }))
          .sort((a, b) => b.count - a.count)

        const venueMap = {}
        activities.forEach(a => {
          if (a.venueId) {
            const v = venues.find(v => v.venueId === a.venueId)
            const name = v ? v.venueName : '场地#' + a.venueId
            venueMap[name] = (venueMap[name] || 0) + 1
          }
        })
        this.venueUsage = Object.entries(venueMap)
          .map(([name, count]) => ({ name, count }))
          .sort((a, b) => b.count - a.count)

        const checkInPromises = activities.slice(0, 20).map(a =>
          request.get('/checkin/count/' + a.activityId).then(r => ({
            id: a.activityId,
            name: a.activityName,
            count: r.data || 0
          })).catch(() => ({
            id: a.activityId,
            name: a.activityName,
            count: 0
          }))
        )
        const checkInResults = await Promise.all(checkInPromises)
        let totalCheckins = 0
        checkInResults.forEach(r => { totalCheckins += r.count })
        this.cards[4].value = totalCheckins
        this.checkinStats = checkInResults
          .filter(r => r.count > 0)
          .sort((a, b) => b.count - a.count)
          .slice(0, 5)
      } catch (e) {
        console.error('fetch stats error', e)
      }
    },

    animateAll() {
      this.cards.forEach(card => {
        this.countUp(card.key, card.value)
      })
    },

    countUp(key, target, duration = 1200) {
      const start = this.animated[key]
      const diff = target - start
      if (diff === 0) return
      const startTime = performance.now()
      const step = (now) => {
        const elapsed = now - startTime
        const progress = Math.min(elapsed / duration, 1)
        const eased = progress === 1 ? 1 : 1 - Math.pow(2, -10 * progress)
        this.animated[key] = Math.round(start + diff * eased)
        if (progress < 1) requestAnimationFrame(step)
      }
      requestAnimationFrame(step)
    },

    async refreshData() {
      await this.fetchData()
      this.$nextTick(() => {
        this.animateAll()
        this.drawCharts()
      })
    },

    drawCharts() {
      if (this.activityDates.length > 0) this.drawActivityChart()
      if (this.clubCategories.length > 0) this.drawClubChart()
      if (this.venueUsage.length > 0) this.drawVenueUsageChart()
      if (this.checkinStats.length > 0) this.drawCheckInChart()
    },

    drawActivityChart() {
      const canvas = this.$refs.activityChart
      if (!canvas) return
      const ctx = canvas.getContext('2d')
      const dpr = window.devicePixelRatio || 1
      const rect = canvas.parentElement.getBoundingClientRect()
      canvas.width = rect.width * dpr
      canvas.height = 200 * dpr
      canvas.style.width = rect.width + 'px'
      canvas.style.height = '200px'
      ctx.scale(dpr, dpr)

      const w = rect.width
      const h = 200
      const pad = { top: 20, right: 30, bottom: 30, left: 40 }
      const pw = w - pad.left - pad.right
      const ph = h - pad.top - pad.bottom

      const data = this.activityDates.map(d => d.count)
      const labels = this.activityDates.map(d => d.date.slice(5))
      const maxVal = Math.max(...data, 1)
      const xStep = pw / (data.length - 1)

      ctx.strokeStyle = 'rgba(0,0,0,0.04)'
      ctx.lineWidth = 1
      for (let i = 0; i <= 4; i++) {
        const y = pad.top + (ph / 4) * i
        ctx.beginPath(); ctx.moveTo(pad.left, y); ctx.lineTo(w - pad.right, y); ctx.stroke()
      }

      const grad = ctx.createLinearGradient(0, pad.top, 0, pad.top + ph)
      grad.addColorStop(0, 'rgba(0,0,0,0.08)')
      grad.addColorStop(1, 'rgba(0,0,0,0.01)')

      ctx.beginPath()
      data.forEach((v, i) => {
        const x = pad.left + i * xStep
        const y = pad.top + ph - (v / maxVal) * ph
        if (i === 0) ctx.moveTo(x, y); else ctx.lineTo(x, y)
      })
      const lastX = pad.left + (data.length - 1) * xStep
      ctx.lineTo(lastX, pad.top + ph)
      ctx.lineTo(pad.left, pad.top + ph)
      ctx.closePath()
      ctx.fillStyle = grad
      ctx.fill()

      ctx.beginPath()
      data.forEach((v, i) => {
        const x = pad.left + i * xStep
        const y = pad.top + ph - (v / maxVal) * ph
        if (i === 0) ctx.moveTo(x, y); else ctx.lineTo(x, y)
      })
      ctx.strokeStyle = '#1d1d1f'
      ctx.lineWidth = 2.5
      ctx.lineJoin = 'round'
      ctx.stroke()

      if (data.length > 0) {
        const endX = pad.left + (data.length - 1) * xStep
        const endY = pad.top + ph - (data[data.length - 1] / maxVal) * ph
        ctx.beginPath(); ctx.arc(endX, endY, 4, 0, Math.PI * 2)
        ctx.fillStyle = '#1d1d1f'; ctx.fill()
        ctx.beginPath(); ctx.arc(endX, endY, 8, 0, Math.PI * 2)
        ctx.fillStyle = 'rgba(0,0,0,0.08)'; ctx.fill()
      }

      ctx.fillStyle = '#86868b'
      ctx.font = '10px -apple-system, BlinkMacSystemFont, sans-serif'
      ctx.textAlign = 'center'
      const step = Math.max(1, Math.floor(labels.length / 5))
      for (let i = 0; i < labels.length; i += step) {
        ctx.fillText(labels[i], pad.left + i * xStep, h - 6)
      }
    },

    drawClubChart() {
      const canvas = this.$refs.clubChart
      if (!canvas) return
      const ctx = canvas.getContext('2d')
      const dpr = window.devicePixelRatio || 1
      const rect = canvas.parentElement.getBoundingClientRect()
      canvas.width = rect.width * dpr
      canvas.height = 200 * dpr
      canvas.style.width = rect.width + 'px'
      canvas.style.height = '200px'
      ctx.scale(dpr, dpr)

      const w = rect.width
      const h = 200
      const pad = { top: 10, right: 50, bottom: 10, left: 80 }
      const pw = w - pad.left - pad.right
      const maxCount = Math.max(...this.clubCategories.map(c => c.count), 1)
      const barH = Math.min(28, Math.floor(170 / this.clubCategories.length) - 4)
      const gap = 6
      const colors = ['#1d1d1f', '#424245', '#6e6e73', '#86868b', '#aeaeb2', '#d1d1d6']

      ctx.textBaseline = 'middle'
      ctx.font = '12px -apple-system, PingFang SC, sans-serif'

      this.clubCategories.forEach((cat, i) => {
        const y = pad.top + i * (barH + gap)
        const barW = (cat.count / maxCount) * pw

        const grad = ctx.createLinearGradient(pad.left, 0, pad.left + barW, 0)
        const c = colors[i % colors.length]
        grad.addColorStop(0, c)
        grad.addColorStop(1, c + '88')
        ctx.fillStyle = grad

        ctx.beginPath()
        const r = 6
        ctx.moveTo(pad.left + r, y)
        ctx.lineTo(pad.left + barW - r, y)
        ctx.arcTo(pad.left + barW, y, pad.left + barW, y + r, r)
        ctx.arcTo(pad.left + barW, y + barH, pad.left + barW - r, y + barH, r)
        ctx.lineTo(pad.left + r, y + barH)
        ctx.arcTo(pad.left, y + barH, pad.left, y + barH - r, r)
        ctx.arcTo(pad.left, y, pad.left + r, y, r)
        ctx.closePath()
        ctx.fill()

        ctx.fillStyle = '#424245'
        ctx.textAlign = 'right'
        ctx.fillText(cat.name, pad.left - 8, y + barH / 2)

        ctx.fillStyle = '#86868b'
        ctx.textAlign = 'left'
        ctx.fillText(`${cat.count} 个`, pad.left + barW + 8, y + barH / 2)
      })
    },

    drawVenueUsageChart() {
      const canvas = this.$refs.venueUsageChart
      if (!canvas) return
      const ctx = canvas.getContext('2d')
      const dpr = window.devicePixelRatio || 1
      const rect = canvas.parentElement.getBoundingClientRect()
      canvas.width = rect.width * dpr
      canvas.height = 200 * dpr
      canvas.style.width = rect.width + 'px'
      canvas.style.height = '200px'
      ctx.scale(dpr, dpr)

      const w = rect.width
      const h = 200
      const pad = { top: 20, right: 30, bottom: 30, left: 30 }
      const pw = w - pad.left - pad.right
      const ph = h - pad.top - pad.bottom

      const data = this.venueUsage.slice(0, 8)
      const maxCount = Math.max(...data.map(d => d.count), 1)
      const barW = Math.min(40, (pw / data.length) - 12)
      const colors = ['#1d1d1f', '#424245', '#6e6e73', '#86868b', '#aeaeb2', '#d1d1d6', '#3a3a3c', '#545458']

      ctx.textAlign = 'center'
      ctx.font = '10px -apple-system, sans-serif'

      data.forEach((item, i) => {
        const x = pad.left + i * (pw / data.length) + ((pw / data.length) - barW) / 2
        const barH = (item.count / maxCount) * (ph - 20)
        const y = pad.top + ph - barH

        const grad = ctx.createLinearGradient(0, y, 0, pad.top + ph)
        const c = colors[i % colors.length]
        grad.addColorStop(0, c)
        grad.addColorStop(1, c + '33')
        ctx.fillStyle = grad

        ctx.beginPath()
        const r = 4
        ctx.moveTo(x + r, y)
        ctx.lineTo(x + barW - r, y)
        ctx.arcTo(x + barW, y, x + barW, y + r, r)
        ctx.arcTo(x + barW, y + barH, x + barW - r, y + barH, r)
        ctx.lineTo(x + r, y + barH)
        ctx.arcTo(x, y + barH, x, y + barH - r, r)
        ctx.arcTo(x, y, x + r, y, r)
        ctx.closePath()
        ctx.fill()

        ctx.fillStyle = '#86868b'
        const label = item.name.length > 4 ? item.name.slice(0, 4) + '..' : item.name
        ctx.fillText(label, x + barW / 2, h - 8)
      })
    },

    drawCheckInChart() {
      const canvas = this.$refs.checkinChart
      if (!canvas) return
      const ctx = canvas.getContext('2d')
      const dpr = window.devicePixelRatio || 1
      const rect = canvas.parentElement.getBoundingClientRect()
      canvas.width = rect.width * dpr
      canvas.height = 200 * dpr
      canvas.style.width = rect.width + 'px'
      canvas.style.height = '200px'
      ctx.scale(dpr, dpr)

      const w = rect.width
      const h = 200
      const pad = { top: 10, right: 50, bottom: 10, left: 80 }
      const pw = w - pad.left - pad.right
      const maxCount = Math.max(...this.checkinStats.map(c => c.count), 1)
      const barH = 28
      const gap = 10
      const colors = ['#1d1d1f', '#424245', '#6e6e73', '#86868b', '#aeaeb2']

      ctx.textBaseline = 'middle'
      ctx.font = '12px -apple-system, PingFang SC, sans-serif'

      this.checkinStats.forEach((item, i) => {
        const y = pad.top + i * (barH + gap)
        const barW = (item.count / maxCount) * pw

        const grad = ctx.createLinearGradient(pad.left, 0, pad.left + barW, 0)
        const c = colors[i]
        grad.addColorStop(0, c)
        grad.addColorStop(1, c + '66')
        ctx.fillStyle = grad

        ctx.beginPath()
        const r = 6
        ctx.moveTo(pad.left + r, y)
        ctx.lineTo(pad.left + barW - r, y)
        ctx.arcTo(pad.left + barW, y, pad.left + barW, y + r, r)
        ctx.arcTo(pad.left + barW, y + barH, pad.left + barW - r, y + barH, r)
        ctx.lineTo(pad.left + r, y + barH)
        ctx.arcTo(pad.left, y + barH, pad.left, y + barH - r, r)
        ctx.arcTo(pad.left, y, pad.left + r, y, r)
        ctx.closePath()
        ctx.fill()

        ctx.fillStyle = '#424245'
        ctx.textAlign = 'right'
        ctx.fillText(item.name.length > 6 ? item.name.slice(0, 6) + '..' : item.name, pad.left - 8, y + barH / 2)

        ctx.fillStyle = '#86868b'
        ctx.textAlign = 'left'
        ctx.fillText(`${item.count} 次`, pad.left + barW + 8, y + barH / 2)
      })
    }
  }
}
</script>

<style scoped>
.stat-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 20px;
  padding: 28px 24px;
  display: flex;
  align-items: center;
  gap: 18px;
  position: relative;
  overflow: hidden;
  transition: transform 0.35s cubic-bezier(0.25, 0.1, 0.25, 1), box-shadow 0.35s cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.04);
}

.stat-card:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
}

.stat-icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.stat-card:hover .stat-icon-wrap {
  transform: scale(1.1);
}

.stat-icon {
  font-size: 22px;
  color: var(--accent);
}

.stat-info { flex: 1; min-width: 0; }

.stat-label {
  font-size: 13px;
  color: #86868b;
  margin-bottom: 6px;
  font-weight: 400;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #1d1d1f;
  font-variant-numeric: tabular-nums;
  letter-spacing: -1px;
  line-height: 1;
}

.stat-unit {
  font-size: 14px;
  font-weight: 400;
  color: #86868b;
  margin-left: 4px;
}

/* Charts */
.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.35s cubic-bezier(0.25, 0.1, 0.25, 1), transform 0.35s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.chart-card:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06);
  transform: translateY(-2px);
}

.chart-title {
  font-size: 17px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 18px;
  letter-spacing: -0.2px;
}

.empty-chart {
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #86868b;
  gap: 12px;
  font-size: 13px;
}

.empty-icon {
  font-size: 36px;
  opacity: 0.25;
}

@media (max-width: 1200px) {
  .stat-grid { grid-template-columns: repeat(3, 1fr); }
  .chart-row { grid-template-columns: 1fr; }
}
@media (max-width: 768px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
