<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar">
      <h3 class="page-title">用户建议</h3>
    </div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="userName" label="提交人" width="100" />
      <el-table-column prop="type" label="类型" width="100" />
      <el-table-column prop="content" label="建议内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template slot-scope="{row}">
          <el-tag size="small" :type="row.status==='待处理'?'warning':row.status==='已采纳'?'success':'info'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="160" />
      <el-table-column label="操作" width="150" align="center">
        <template slot-scope="{row}">
          <el-button v-if="row.status==='待处理'" type="text" size="small" style="color:#67C23A" @click="handle(row.id,'已采纳')">采纳</el-button>
          <el-button v-if="row.status==='待处理'" type="text" size="small" style="color:#909399" @click="handle(row.id,'已关闭')">关闭</el-button>
          <el-button type="text" size="small" style="color:#F56C6C" @click="delSug(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import http from '@/api'

export default {
  name: 'SuggestionList',
  data() { return { list: [], loading: false } },
  created() { this.loadData() },
  activated() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try { var r = await http.get('/suggestion/list', { _: Date.now() }); this.list = r.data || [] } catch(e) { this.list = [] }
      finally { this.loading = false }
    },
    async handle(id, status) {
      try { await http.put('/suggestion/' + id + '?status=' + encodeURIComponent(status)); this.$message.success(status === '已采纳' ? '已采纳' : '已关闭'); this.loadData() }
      catch(e) { this.$message.error(e.message || '操作失败') }
    },
    async delSug(row) {
      try { await http.delete('/suggestion/' + row.id); this.$message.success('已删除'); this.loadData() }
      catch(e) { this.$message.error(e.message || '删除失败') }
    }
  }
}
</script>

<style scoped>
.page-wrap { padding: 28px; }
.page-toolbar { margin-bottom: 24px; }
.page-title { margin: 0; font-size: 22px; font-weight: 600; color: #1d1d1f; letter-spacing: -0.3px; }
</style>
