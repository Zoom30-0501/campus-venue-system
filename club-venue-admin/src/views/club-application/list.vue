<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar">
      <h3 class="page-title">入团申请审批</h3>
      <div style="display:flex;gap:8px">
        <el-select v-model="filterStatus" placeholder="申请状态" size="small" style="width:130px" clearable @change="loadData">
          <el-option label="全部" value="" />
          <el-option label="待审核" value="待审核" />
          <el-option label="已通过" value="已通过" />
          <el-option label="已驳回" value="已驳回" />
        </el-select>
        <el-button type="primary" size="small" icon="el-icon-refresh" @click="loadData">刷新</el-button>
      </div>
    </div>
    <el-table :data="filteredList" style="width:100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="userName" label="申请人" width="100" />
      <el-table-column prop="clubName" label="目标社团" width="140" />
      <el-table-column prop="reason" label="申请理由" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template slot-scope="{row}">
          <el-tag size="small" :type="row.status==='待审核'?'warning':row.status==='已通过'?'success':'danger'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="160" />
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template slot-scope="{row}">
          <template v-if="row.status==='待审核'">
            <el-button type="text" size="small" style="color:#67C23A" @click="approve(row,'已通过')">通过</el-button>
            <el-button type="text" size="small" style="color:#F56C6C" @click="approve(row,'已驳回')">驳回</el-button>
          </template>
          <el-button type="text" size="small" style="color:#909399" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import http from '@/api'

export default {
  name: 'ClubApplicationList',
  data() {
    return { list: [], loading: false, filterStatus: '' }
  },
  computed: {
    filteredList() {
      if (!this.filterStatus) return this.list
      return this.list.filter(item => item.status === this.filterStatus)
    }
  },
  created() { this.loadData() },
  activated() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try { var r = await http.get('/club-application/list'); this.list = r.data || [] } catch(e) { this.list = [] }
      finally { this.loading = false }
    },
    async approve(row, status) {
      try {
        await http.put('/club-application/approve/' + row.id + '?status=' + encodeURIComponent(status))
        this.$message.success(status === '已通过' ? '申请已通过' : '申请已驳回')
        this.loadData()
      } catch(e) { this.$message.error(e.message || '操作失败') }
    },
    del(row) {
      var self = this
      this.$confirm('确定删除该申请记录？', '提示', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
        .then(async function() {
          try {
            await http.delete('/club-application/' + row.id)
            self.$message.success('申请记录已删除')
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
