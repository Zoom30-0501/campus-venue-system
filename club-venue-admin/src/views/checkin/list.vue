<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar">
      <h3 class="page-title">签到管理</h3>
      <div style="display:flex;gap:8px">
        <el-select v-model="filterType" placeholder="筛选方式" size="small" style="width:110px" @change="handleFilterTypeChange">
          <el-option label="按活动" value="activity" />
          <el-option label="按用户" value="user" />
        </el-select>
        <el-input v-if="filterType==='activity'" v-model="filterActivityId" placeholder="活动ID" size="small" style="width:140px" clearable />
        <el-input v-if="filterType==='user'" v-model="filterUserId" placeholder="用户ID" size="small" style="width:140px" clearable />
        <el-button type="primary" size="small" icon="el-icon-search" @click="loadData">查询</el-button>
      </div>
    </div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="activityName" label="活动名称" min-width="140" />
      <el-table-column prop="userName" label="签到人" width="100" />
      <el-table-column prop="checkInTime" label="签到时间" width="160" />
      <el-table-column label="核验状态" width="120" align="center">
        <template slot-scope="{row}">
          <el-tag size="small" :type="row.verified ? 'success' : 'info'">{{ row.verified ? '已核验' : '未核验' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" align="center" fixed="right">
        <template slot-scope="{row}">
          <el-button
            type="text" size="small"
            :style="{ color: row.verified ? '#909399' : '#67C23A' }"
            @click="toggleVerify(row)"
          >{{ row.verified ? '取消核验' : '核验' }}</el-button>
          <el-button type="text" size="small" style="color:#F56C6C" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import http from '@/api'

export default {
  name: 'CheckinList',
  data() {
    return { list: [], loading: false, filterType: 'activity', filterActivityId: '', filterUserId: '' }
  },
  created() { this.loadData() },
  activated() { this.loadData() },
  methods: {
    handleFilterTypeChange() {
      this.filterActivityId = ''
      this.filterUserId = ''
    },
    async loadData() {
      this.loading = true
      try {
        var r
        if (this.filterType === 'activity' && this.filterActivityId) {
          r = await http.get('/checkin/activity/' + this.filterActivityId)
        } else if (this.filterType === 'user' && this.filterUserId) {
          r = await http.get('/checkin/user/' + this.filterUserId)
        } else {
          r = await http.get('/checkin/list')
        }
        this.list = r.data || []
      } catch(e) { this.list = [] }
      finally { this.loading = false }
    },
    async toggleVerify(row) {
      try {
        await http.put('/checkin/verify/' + row.id)
        this.$message.success(row.verified ? '已取消核验' : '已核验')
        this.loadData()
      } catch(e) { this.$message.error(e.message || '操作失败') }
    },
    del(row) {
      var self = this
      this.$confirm('确定删除该签到记录？', '提示', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
        .then(async function() {
          try {
            await http.delete('/checkin/' + row.id)
            self.$message.success('签到记录已删除')
            self.loadData()
          } catch(e) { self.$message.error(e.message || '删除失败') }
        }).catch(function(){})
    }
  }
}
</script>

<style scoped>
.page-wrap { padding: 28px; }
.page-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.page-title { margin: 0; font-size: 22px; font-weight: 600; color: #1d1d1f; letter-spacing: -0.3px; }
</style>
