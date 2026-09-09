<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar">
      <h3 class="page-title">活动评价管理</h3>
      <div style="display:flex;gap:8px">
        <el-input v-model="filterActivityId" placeholder="输入活动ID筛选" size="small" style="width:180px" clearable @clear="loadData" />
        <el-button type="primary" size="small" icon="el-icon-search" @click="loadData">查询</el-button>
      </div>
    </div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="activityName" label="活动名称" min-width="140" />
      <el-table-column label="评分" width="130" align="center">
        <template slot-scope="{row}">
          <span style="color:#f7ba2a;font-size:16px">{{ '★'.repeat(row.score || 0) }}{{ '☆'.repeat(5 - (row.score || 0)) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="comment" label="评价内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="userName" label="评价人" width="100" />
      <el-table-column prop="createTime" label="评价时间" width="160" />
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template slot-scope="{row}">
          <el-button type="text" size="small" style="color:#F56C6C" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import http from '@/api'

export default {
  name: 'ActivityRatingList',
  data() {
    return { list: [], loading: false, filterActivityId: '' }
  },
  created() { this.loadData() },
  activated() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const id = this.filterActivityId
        var r
        if (id) {
          r = await http.get('/activity-rating/byActivity/' + id)
        } else {
          r = await http.get('/activity-rating/list')
        }
        this.list = r.data || []
      } catch(e) { this.list = [] }
      finally { this.loading = false }
    },
    del(row) {
      var self = this
      this.$confirm('确定删除该评价？', '提示', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
        .then(async function() {
          try {
            await http.delete('/activity-rating/' + row.id)
            self.$message.success('评价已删除')
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
