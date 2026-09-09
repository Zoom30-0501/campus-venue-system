<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar">
      <h3 class="page-title">角色管理</h3>
      <el-button type="primary" size="small" icon="el-icon-plus" @click="openAdd">新增角色</el-button>
    </div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="roleId" label="ID" width="80" />
      <el-table-column prop="roleName" label="角色名" width="140" />
      <el-table-column prop="roleKey" label="角色标识" width="140" />
      <el-table-column label="权限" min-width="260">
        <template slot-scope="{row}">
          <el-tag v-for="p in parsePermissions(row.permissions)" :key="p" size="mini" style="margin:2px 4px 2px 0">{{ p }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template slot-scope="{row}">
          <el-button type="text" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button type="text" size="small" style="color:#F56C6C" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="isEdit?'编辑角色':'新增角色'" :visible.sync="dialogVisible" width="500px" @closed="resetForm">
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="角色名"><el-input v-model="form.roleName" /></el-form-item>
        <el-form-item label="角色标识"><el-input v-model="form.roleKey" placeholder="如 admin / manager / user" /></el-form-item>
        <el-form-item label="权限列表">
          <el-checkbox-group v-model="form.permissions">
            <el-checkbox v-for="p in allPermissions" :key="p.value" :label="p.value" style="margin-right:12px;margin-bottom:4px">{{ p.label }}</el-checkbox>
          </el-checkbox-group>
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
  name: 'RoleList',
  data() {
    return {
      list: [], loading: false, submitting: false, dialogVisible: false, isEdit: false,
      form: { roleId: null, roleName: '', roleKey: '', permissions: [] },
      allPermissions: [
        { value: 'dashboard:view', label: '看板查看' },
        { value: 'club:manage', label: '社团管理' },
        { value: 'club:member', label: '社团成员' },
        { value: 'club:album', label: '社团相册' },
        { value: 'club:approve', label: '入团审核' },
        { value: 'venue:manage', label: '场地管理' },
        { value: 'activity:manage', label: '活动管理' },
        { value: 'activity:checkin', label: '活动签到' },
        { value: 'activity:summary', label: '活动总结' },
        { value: 'activity:rating', label: '活动评分' },
        { value: 'facility:manage', label: '设施管理' },
        { value: 'suggestion:manage', label: '建议管理' },
        { value: 'user:manage', label: '用户管理' },
        { value: 'role:manage', label: '角色管理' }
      ]
    }
  },
  created() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try { var r = await http.get('/role/list'); this.list = r.data || [] } catch(e) { this.list = [] }
      finally { this.loading = false }
    },
    openAdd() { this.isEdit = false; this.resetForm(); this.dialogVisible = true },
    openEdit(row) {
      this.isEdit = true
      this.form = {
        roleId: row.roleId,
        roleName: row.roleName,
        roleKey: row.roleKey,
        permissions: this.parsePermissions(row.permissions)
      }
      this.dialogVisible = true
    },
    resetForm() { this.form = { roleId: null, roleName: '', roleKey: '', permissions: [] } },
    parsePermissions(val) {
      if (!val) return []
      if (Array.isArray(val)) return val
      if (typeof val === 'string') return val.split(',').map(s => s.trim()).filter(Boolean)
      return []
    },
    async submit() {
      this.submitting = true
      const payload = { ...this.form }
      payload.permissions = payload.permissions.join(',')
      try {
        if (this.isEdit) { await http.put('/role', payload); this.$message.success('角色已更新') }
        else { await http.post('/role', payload); this.$message.success('角色已创建') }
        this.dialogVisible = false; this.loadData()
      } catch(e) { this.$message.error(e.message || '操作失败') }
      finally { this.submitting = false }
    },
    del(row) {
      var self = this
      this.$confirm('确定删除角色「' + row.roleName + '」？', '提示', { type: 'warning' })
        .then(async function() {
          try { await http.delete('/role/' + row.roleId); self.$message.success('已删除'); self.loadData() }
          catch(e) { self.$message.error(e.message || '删除失败') }
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
