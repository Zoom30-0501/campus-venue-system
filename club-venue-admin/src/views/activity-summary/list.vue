<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar">
      <h3 class="page-title">活动总结管理</h3>
      <div style="display:flex;gap:8px">
        <el-input v-model="filterActivityId" placeholder="输入活动ID筛选" size="small" style="width:180px" clearable @clear="loadData" />
        <el-button type="primary" size="small" icon="el-icon-search" @click="loadData">查询</el-button>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="openAdd">新增总结</el-button>
      </div>
    </div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="activityName" label="活动名称" min-width="140" />
      <el-table-column prop="participantCount" label="参与人数" width="100" align="center" />
      <el-table-column prop="content" label="总结内容" min-width="220" show-overflow-tooltip />
      <el-table-column prop="feedback" label="反馈建议" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template slot-scope="{row}">
          <el-button type="text" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button type="text" size="small" style="color:#F56C6C" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="isEdit?'编辑活动总结':'新增活动总结'" :visible.sync="dialogVisible" width="520px" @closed="resetForm">
      <el-form :model="form" label-width="100px" size="small">
        <el-form-item label="活动">
          <el-select v-model="form.activityId" filterable placeholder="请选择活动" style="width:100%" :disabled="isEdit">
            <el-option v-for="a in activityOptions" :key="a.activityId" :label="a.activityName" :value="a.activityId" />
          </el-select>
        </el-form-item>
        <el-form-item label="参与人数">
          <el-input v-model="form.participantCount" type="number" />
        </el-form-item>
        <el-form-item label="总结内容">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请填写活动总结内容" />
        </el-form-item>
        <el-form-item label="反馈建议">
          <el-input v-model="form.feedback" type="textarea" :rows="3" placeholder="活动反馈或改进建议" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button size="small" @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" size="small" @click="submit" :loading="submitting">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import http from '@/api'

export default {
  name: 'ActivitySummaryList',
  data() {
    return {
      list: [], loading: false, submitting: false, filterActivityId: '',
      dialogVisible: false, isEdit: false,
      form: { id: null, activityId: '', participantCount: 0, content: '', feedback: '' },
      activityOptions: []
    }
  },
  created() { this.loadData(); this.loadActivities() },
  activated() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const id = this.filterActivityId
        var r
        if (id) {
          r = await http.get('/activity-summary/byActivity/' + id)
          this.list = r.data ? [r.data] : []
        } else {
          r = await http.get('/activity-summary/list')
          this.list = r.data || []
        }
      } catch(e) { this.list = [] }
      finally { this.loading = false }
    },
    async loadActivities() {
      try { var r = await http.get('/activity/list'); this.activityOptions = r.data || [] } catch(e) { this.activityOptions = [] }
    },
    openAdd() { this.isEdit = false; this.resetForm(); this.dialogVisible = true },
    openEdit(row) {
      this.isEdit = true
      this.form = { id: row.id, activityId: row.activityId, participantCount: row.participantCount, content: row.content || '', feedback: row.feedback || '' }
      this.dialogVisible = true
    },
    resetForm() { this.form = { id: null, activityId: '', participantCount: 0, content: '', feedback: '' } },
    async submit() {
      if (!this.form.activityId) { this.$message.warning('请选择活动'); return }
      this.submitting = true
      try {
        if (this.isEdit) {
          await http.put('/activity-summary/' + this.form.id, { activityId: this.form.activityId, participantCount: this.form.participantCount, content: this.form.content, feedback: this.form.feedback })
          this.$message.success('活动总结已更新')
        } else {
          await http.post('/activity-summary', { activityId: this.form.activityId, participantCount: this.form.participantCount, content: this.form.content, feedback: this.form.feedback })
          this.$message.success('活动总结已创建')
        }
        this.dialogVisible = false
        this.loadData()
      } catch(e) { this.$message.error(e.message || '操作失败') }
      finally { this.submitting = false }
    },
    del(row) {
      var self = this
      this.$confirm('确定删除该活动总结？', '提示', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
        .then(async function() {
          try {
            await http.delete('/activity-summary/' + row.id)
            self.$message.success('活动总结已删除')
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
