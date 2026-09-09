<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar">
      <h3 class="page-title">社团成员管理</h3>
      <div style="display:flex;gap:8px">
        <el-select v-model="filterClubId" filterable clearable placeholder="选择社团" size="small" style="width:200px" @change="loadData">
          <el-option v-for="c in clubOptions" :key="c.clubId" :label="c.clubName" :value="c.clubId" />
        </el-select>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="openAdd">新增成员</el-button>
      </div>
    </div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="clubName" label="社团名称" width="140" />
      <el-table-column prop="userName" label="成员姓名" width="100" />
      <el-table-column prop="role" label="角色" width="120" align="center">
        <template slot-scope="{row}">
          <el-tag size="small" :type="row.role==='负责人'||row.role==='社长'?'':row.role==='副负责人'||row.role==='副社长'?'warning':'info'">{{ row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template slot-scope="{row}">
          <el-tag size="small" :type="row.status==='正常'?'success':'info'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="加入时间" width="160" />
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template slot-scope="{row}">
          <el-button type="text" size="small" @click="openEdit(row)">编辑角色</el-button>
          <el-button type="text" size="small" style="color:#F56C6C" @click="del(row)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="isEdit?'编辑成员角色':'新增成员'" :visible.sync="dialogVisible" width="440px" @closed="resetForm">
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="社团">
          <el-select v-model="form.clubId" filterable placeholder="请选择社团" style="width:100%" :disabled="isEdit">
            <el-option v-for="c in clubOptions" :key="c.clubId" :label="c.clubName" :value="c.clubId" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="用户">
          <el-select v-model="form.userId" filterable placeholder="请选择用户" style="width:100%">
            <el-option v-for="u in userOptions" :key="u.userId" :label="u.nickname || u.userName" :value="u.userId" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="成员姓名">
          <el-input v-model="form.userName" placeholder="成员姓名" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width:100%">
            <el-option label="负责人" value="负责人" />
            <el-option label="副负责人" value="副负责人" />
            <el-option label="成员" value="成员" />
          </el-select>
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
  name: 'ClubMemberList',
  data() {
    return {
      list: [], loading: false, submitting: false, filterClubId: '',
      clubOptions: [], userOptions: [],
      dialogVisible: false, isEdit: false,
      form: { id: null, clubId: '', userId: '', userName: '', role: '成员' }
    }
  },
  created() { this.loadData(); this.loadClubs(); this.loadUsers() },
  activated() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        var r
        if (this.filterClubId) {
          r = await http.get('/club-member/list/' + this.filterClubId)
        } else {
          r = await http.get('/club-member/list')
        }
        this.list = r.data || []
      } catch(e) { this.list = [] }
      finally { this.loading = false }
    },
    async loadClubs() {
      try { var r = await http.get('/club/list'); this.clubOptions = r.data || [] } catch(e) { this.clubOptions = [] }
    },
    async loadUsers() {
      try { var r = await http.get('/user/list'); this.userOptions = r.data || [] } catch(e) { this.userOptions = [] }
    },
    openAdd() { this.isEdit = false; this.resetForm(); this.dialogVisible = true },
    openEdit(row) {
      this.isEdit = true
      this.form = { id: row.id, clubId: row.clubId, role: row.role }
      this.dialogVisible = true
    },
    resetForm() { this.form = { id: null, clubId: '', userId: '', userName: '', role: '成员' } },
    async submit() {
      if (!this.form.clubId) { this.$message.warning('请选择社团'); return }
      if (!this.isEdit && !this.form.userName) { this.$message.warning('请输入成员姓名'); return }
      this.submitting = true
      try {
        if (this.isEdit) {
          await http.put('/club-member/' + this.form.id, { role: this.form.role })
          this.$message.success('成员角色已更新')
        } else {
          await http.post('/club-member', { clubId: this.form.clubId, userId: this.form.userId || 0, userName: this.form.userName, role: this.form.role })
          this.$message.success('成员已添加')
        }
        this.dialogVisible = false
        this.loadData()
      } catch(e) { this.$message.error(e.message || '操作失败') }
      finally { this.submitting = false }
    },
    del(row) {
      var self = this
      this.$confirm('确定移除成员「' + row.userName + '」？', '提示', { confirmButtonText: '确定移除', cancelButtonText: '取消', type: 'warning' })
        .then(async function() {
          try {
            await http.delete('/club-member/' + row.id)
            self.$message.success('成员已移除')
            self.loadData()
          } catch(e) { self.$message.error(e.message || '移除失败') }
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
