<template>
  <div class="page">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>活动报名管理</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="filter-bar">
      <el-select v-model="selectedActivityId" placeholder="选择活动" filterable clearable @change="fetchData" style="width:260px">
        <el-option v-for="a in activities" :key="a.activityId" :label="a.activityName" :value="a.activityId" />
      </el-select>
    </div>

    <el-table :data="list" v-loading="loading" style="margin-top:16px">
      <el-table-column prop="id" label="报名ID" width="80" />
      <el-table-column prop="activityId" label="活动ID" width="80" />
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="{row}">
          <el-tag :type="statusTag(row.status)" size="small">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="报名时间" width="180" />
      <el-table-column label="操作">
        <template slot-scope="{row}">
          <el-button v-if="row.status === '待审核'" type="success" size="mini" @click="handleApprove(row, '已通过')">通过</el-button>
          <el-button v-if="row.status === '待审核'" type="danger" size="mini" @click="handleApprove(row, '已驳回')">驳回</el-button>
          <el-button type="text" size="mini" icon="el-icon-delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import registrationApi from '@/api/activity-registration'
import request from '@/api'

export default {
  name: 'ActivityRegistrationList',
  data() {
    return {
      list: [],
      activities: [],
      selectedActivityId: null,
      loading: false
    }
  },
  methods: {
    statusTag(status) {
      const map = { '待审核': 'warning', '已通过': 'success', '已驳回': 'danger' }
      return map[status] || 'info'
    },
    async fetchActivities() {
      const res = await request.get('/activity/list')
      this.activities = res.data || []
    },
    async fetchData() {
      if (!this.selectedActivityId) { this.list = []; return }
      this.loading = true
      try {
        const res = await registrationApi.getList({ activityId: this.selectedActivityId })
        this.list = res.data || []
      } catch (e) { this.list = [] }
      finally { this.loading = false }
    },
    async handleApprove(row, status) {
      try {
        await registrationApi.approve(row.id, status)
        this.$message.success('操作成功')
        this.fetchData()
      } catch (e) { this.$message.error('操作失败') }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该报名记录？', '提示', { type: 'warning' })
        await registrationApi.delete(row.id)
        this.$message.success('已删除')
        this.fetchData()
      } catch (e) {}
    }
  },
  mounted() {
    this.fetchActivities()
  }
}
</script>

<style scoped>
.page { padding: 28px; }
.filter-bar { display: flex; align-items: center; gap: 12px; margin-top: 12px; }
</style>
