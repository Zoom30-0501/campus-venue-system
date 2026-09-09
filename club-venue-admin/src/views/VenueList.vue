<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar"><h3 class="page-title">场地管理</h3><el-button type="primary" size="small" icon="el-icon-plus" @click="openAdd">新增场地</el-button></div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="venueId" label="ID" width="200" />
      <el-table-column prop="venueName" label="场地名称" min-width="140" />
      <el-table-column prop="venueNo" label="编号" width="120" />
      <el-table-column label="状态" width="100" align="center"><template slot-scope="{row}"><el-tag size="small" :type="row.status==='可用'?'success':'warning'">{{ row.status }}</el-tag></template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="150" align="center" fixed="right">
        <template slot-scope="{row}">
          <el-button type="text" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button type="text" size="small" style="color:#F56C6C" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :title="isEdit?'编辑场地':'新增场地'" :visible.sync="dialogVisible" width="420px" @closed="resetForm">
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="场地名称"><el-input v-model="form.venueName" /></el-form-item>
        <el-form-item label="场地编号"><el-input v-model="form.venueNo" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="可用" value="可用" /><el-option label="维护中" value="维护中" /></el-select></el-form-item>
      </el-form>
      <span slot="footer"><el-button size="small" @click="dialogVisible=false">取消</el-button><el-button type="primary" size="small" @click="submit" :loading="submitting">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import http from '@/api'

export default {
  name: 'VenueList',
  data() { return { list: [], loading: false, submitting: false, dialogVisible: false, isEdit: false, form: { venueId: null, venueName: '', venueNo: '', status: '可用' } } },
  created() { this.loadData() },
  activated() { this.loadData() },
  methods: {
    async loadData() { this.loading = true; try { var r = await http.get('/venue/list', { _: Date.now() }); this.list = r.data || [] } catch(e) { this.list = [] } finally { this.loading = false } },
    openAdd() { this.isEdit = false; this.resetForm(); this.dialogVisible = true },
    openEdit(row) { this.isEdit = true; this.form = { ...row }; this.dialogVisible = true },
    resetForm() { this.form = { venueId: null, venueName: '', venueNo: '', status: '可用' } },
    async submit() {
      this.submitting = true
      try {
        if (this.isEdit) { await http.put('/venue', this.form); this.$message.success('场地信息已更新') }
        else { await http.post('/venue', this.form); this.$message.success('场地创建成功') }
        this.dialogVisible = false; this.loadData()
      } catch(e) { this.$message.error(e.message || '操作失败') } finally { this.submitting = false }
    },
    del(row) {
      var self = this, id = row.venueId, name = row.venueName
      this.$confirm('确定删除场地「' + name + '」？', '提示', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
        .then(async function() {
          try {
            await http.delete('/venue/' + id)
            self.$message.success('场地「' + name + '」已删除')
            var r = await http.get('/venue/list', { _: Date.now() })
            self.list = r.data || []
            self.$forceUpdate()
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
