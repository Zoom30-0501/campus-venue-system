<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar">
      <h3 class="page-title">用户管理</h3>
      <div style="display:flex;gap:12px">
        <el-input v-model="keyword" placeholder="搜索用户名或昵称" size="small" clearable style="width:220px" @clear="loadData" @keyup.enter.native="loadData"><el-button slot="append" icon="el-icon-search" @click="loadData" /></el-input>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="openAdd">新增用户</el-button>
      </div>
    </div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="userId" label="ID" width="200" />
      <el-table-column label="头像" width="70" align="center"><template slot-scope="{row}"><el-avatar :size="32" :src="row.avatar">{{ (row.nickname||'?')[0] }}</el-avatar></template></el-table-column>
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="clubName" label="所属社团" min-width="120" />
      <el-table-column label="角色" width="120" align="center"><template slot-scope="{row}"><el-tag size="small" :type="row.role==='admin'?'danger':row.role==='club_leader'?'warning':'info'">{{ row.role==='admin'?'管理员':row.role==='club_leader'?'社团负责人':'普通用户' }}</el-tag></template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="150" align="center" fixed="right">
        <template slot-scope="{row}"><el-button type="text" size="small" @click="openEdit(row)">编辑</el-button><el-button type="text" size="small" style="color:#F56C6C" @click="del(row)">删除</el-button></template>
      </el-table-column>
    </el-table>
    <el-dialog :title="isEdit?'编辑用户':'新增用户'" :visible.sync="dialogVisible" width="460px" @closed="resetForm">
      <el-form :model="form" label-width="90px" size="small">
        <el-form-item label="角色"><el-select v-model="form.role" style="width:100%"><el-option label="普通用户" value="user" /><el-option label="社团负责人" value="club_leader" /><el-option label="管理员" value="admin" /></el-select></el-form-item>
        <el-form-item label="头像"><el-avatar :size="64" :src="avatarPreview" class="avatar-upload" @click.native="$refs.fileInput.click()"><span>{{ (form.nickname||'?')[0] }}</span></el-avatar><input ref="fileInput" type="file" accept="image/*" hidden @change="onAvatarChange"></el-form-item>
        <el-form-item label="用户名"><el-input v-model="form.username" :disabled="isEdit" /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item v-if="!isEdit" label="密码"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-form-item label="所属社团"><el-select v-model="form.clubId" style="width:100%" clearable filterable placeholder="选择社团"><el-option v-for="c in clubList" :key="c.clubId" :label="c.clubName" :value="c.clubId" /></el-select></el-form-item>
      </el-form>
      <span slot="footer"><el-button size="small" @click="dialogVisible=false">取消</el-button><el-button type="primary" size="small" @click="submit" :loading="submitting">保存</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import http from '@/api'

export default {
  name: 'UserList',
  data() { return { list: [], clubList: [], keyword: '', loading: false, submitting: false, dialogVisible: false, isEdit: false, avatarPreview: '', form: { userId:null, username:'', nickname:'', password:'', clubId:null, avatar:'', role:'user' } } },
  created() { this.loadData(); this.loadClubs() },
  activated() { this.loadData(); this.loadClubs() },
  methods: {
    async loadData() { this.loading = true; try { var r = await http.get('/user/list', { keyword: this.keyword || undefined }); this.list = r.data || [] } catch(e) { this.list = [] } finally { this.loading = false } },
    async loadClubs() { try { var r = await http.get('/club/list', { _: Date.now() }); this.clubList = r.data || [] } catch(e) {} },
    openAdd() { this.isEdit = false; this.resetForm(); this.dialogVisible = true },
    openEdit(row) { this.isEdit = true; this.form = { ...row, password:'' }; this.avatarPreview = row.avatar || ''; this.dialogVisible = true },
    resetForm() { this.form = { userId:null, username:'', nickname:'', password:'', clubId:null, avatar:'', role:'user' }; this.avatarPreview = '' },
    onAvatarChange(e) { var file=e.target.files[0];if(!file)return;var self=this;var r=new FileReader();r.onload=function(ev){self.avatarPreview=ev.target.result};r.readAsDataURL(file);var fd=new FormData();fd.append('file',file);http.post('/file/upload',fd).then(function(r){self.form.avatar=r.data}).catch(function(){});e.target.value='' },
    async submit() {
      this.submitting = true
      try {
        if (this.isEdit) { var clubId=this.form.clubId,club=this.clubList.find(function(c){return c.clubId===clubId});await http.put('/user',{userId:this.form.userId,nickname:this.form.nickname,avatar:this.form.avatar,clubId:clubId||null,clubName:club?club.clubName:null,role:this.form.role});this.$message.success('用户信息已更新') }
        else { await http.post('/user/register',{username:this.form.username,nickname:this.form.nickname,password:this.form.password,avatar:this.form.avatar,clubId:this.form.clubId||null});this.$message.success('用户创建成功') }
        this.dialogVisible = false; this.loadData()
      } catch(e) { this.$message.error(e.message || '操作失败') } finally { this.submitting = false }
    },
    del(row) {
      var self = this, id = row.userId, name = row.nickname || row.username
      this.$confirm('确定删除用户「' + name + '」？', '提示', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
        .then(async function() {
          try {
            await http.delete('/user/' + id)
            self.$message.success('用户「' + name + '」已删除')
            var r = await http.get('/user/list', { _: Date.now(), keyword: self.keyword || undefined })
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
.avatar-upload { cursor: pointer; border: 2px dashed rgba(0,0,0,0.08); border-radius: 16px; transition: border-color 0.3s; }
.avatar-upload:hover { border-color: #1d1d1f; }
</style>
