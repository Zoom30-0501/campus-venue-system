<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar">
      <h3 class="page-title">社团管理</h3>
      <el-button type="primary" size="small" icon="el-icon-plus" @click="openAdd">新增社团</el-button>
    </div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="clubId" label="ID" width="200" />
      <el-table-column label="头像" width="70" align="center">
        <template slot-scope="{row}"><el-avatar :size="32" :src="row.avatar">{{ (row.clubName||'?')[0] }}</el-avatar></template>
      </el-table-column>
      <el-table-column prop="clubName" label="社团名称" min-width="140" />
      <el-table-column prop="category" label="类别" width="120" />
      <el-table-column prop="leaderName" label="社长" width="100" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="280" align="center" fixed="right">
        <template slot-scope="{row}">
          <el-button type="text" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button type="text" size="small" @click="openMembers(row)">成员</el-button>
          <el-button type="text" size="small" @click="openActivities(row)">活动</el-button>
          <el-button type="text" size="small" @click="openAlbum(row)">相册</el-button>
          <el-button type="text" size="small" @click="openApplications(row)">申请</el-button>
          <el-button type="text" size="small" style="color:#F56C6C" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="isEdit?'编辑社团':'新增社团'" :visible.sync="dialogVisible" width="480px" @closed="resetForm">
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="头像">
          <el-avatar :size="64" :src="avatarPreview" class="avatar-upload" @click.native="$refs.fileInput.click()">
            <span>{{ (form.clubName||'?')[0] }}</span>
          </el-avatar>
          <input ref="fileInput" type="file" accept="image/*" hidden @change="onAvatarChange">
        </el-form-item>
        <el-form-item label="社团名称"><el-input v-model="form.clubName" /></el-form-item>
        <el-form-item label="类别">
          <el-select v-model="form.category" style="width:100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="社长"><el-input v-model="form.leaderName" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item>
      </el-form>
      <span slot="footer">
        <el-button size="small" @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" size="small" @click="submit" :loading="submitting">保存</el-button>
      </span>
    </el-dialog>

    <el-dialog :title="memberClubName + ' - 成员管理'" :visible.sync="memberDialogVisible" width="560px">
      <div style="margin-bottom:12px">
        <el-form :model="memberForm" inline size="small">
          <el-form-item><el-input v-model="memberForm.userName" placeholder="成员姓名" style="width:120px" /></el-form-item>
          <el-form-item>
            <el-select v-model="memberForm.role" placeholder="角色" style="width:110px">
              <el-option label="社长" value="社长" />
              <el-option label="副社长" value="副社长" />
              <el-option label="干事" value="干事" />
              <el-option label="成员" value="成员" />
            </el-select>
          </el-form-item>
          <el-form-item><el-button type="primary" size="small" @click="addMember">添加</el-button></el-form-item>
        </el-form>
      </div>
      <el-table :data="members" stripe size="small" style="width:100%">
        <el-table-column prop="userName" label="姓名" width="120" />
        <el-table-column prop="role" label="角色" width="100" />
        <el-table-column prop="status" label="状态" width="80" />
        <el-table-column prop="createTime" label="加入时间" min-width="150" />
        <el-table-column label="操作" width="120" align="center">
          <template slot-scope="{row}">
            <el-button type="text" size="small" @click="editMember(row)">编辑</el-button>
            <el-button type="text" size="small" style="color:#F56C6C" @click="removeMember(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer"><el-button size="small" @click="memberDialogVisible=false">关闭</el-button></span>
    </el-dialog>

    <el-dialog title="编辑成员" :visible.sync="editMemberVisible" width="360px">
      <el-form :model="editMemberForm" label-width="80px" size="small">
        <el-form-item label="姓名"><el-input v-model="editMemberForm.userName" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editMemberForm.role" style="width:100%">
            <el-option label="社长" value="社长" /><el-option label="副社长" value="副社长" />
            <el-option label="干事" value="干事" /><el-option label="成员" value="成员" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button size="small" @click="editMemberVisible=false">取消</el-button><el-button type="primary" size="small" @click="saveMemberEdit">保存</el-button></span>
    </el-dialog>

    <!-- 社团活动历史 -->
    <el-dialog :title="activityClubName + ' - 活动历史'" :visible.sync="activityDialogVisible" width="700px">
      <el-table :data="clubActivities" stripe size="small">
        <el-table-column prop="activityName" label="活动名称" min-width="150" />
        <el-table-column prop="venueName" label="场地" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="status" label="状态" width="100" />
      </el-table>
      <span slot="footer"><el-button size="small" @click="activityDialogVisible=false">关闭</el-button></span>
    </el-dialog>

    <!-- 社团相册 -->
    <el-dialog :title="albumClubName + ' - 相册'" :visible.sync="albumDialogVisible" width="700px">
      <div style="margin-bottom:12px">
        <el-input v-model="albumDesc" placeholder="照片描述" size="small" style="width:200px;margin-right:8px" />
        <el-input v-model="albumUrl" placeholder="图片URL" size="small" style="width:300px;margin-right:8px" />
        <el-button type="primary" size="small" @click="uploadAlbum">添加照片</el-button>
      </div>
      <div class="album-grid">
        <div v-for="item in clubAlbums" :key="item.id" class="album-item">
          <el-image :src="item.imageUrl" fit="cover" style="width:120px;height:120px" />
          <p class="album-desc">{{ item.description }}</p>
          <el-button type="text" size="small" style="color:#F56C6C" @click="delAlbum(item.id)">删除</el-button>
        </div>
      </div>
      <span slot="footer"><el-button size="small" @click="albumDialogVisible=false">关闭</el-button></span>
    </el-dialog>

    <!-- 入社申请审核 -->
    <el-dialog :title="appClubName + ' - 入社申请'" :visible.sync="appDialogVisible" width="600px">
      <el-table :data="clubApplications" stripe size="small">
        <el-table-column prop="userName" label="申请人" width="100" />
        <el-table-column prop="reason" label="申请理由" min-width="180" />
        <el-table-column prop="status" label="状态" width="80" />
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column label="操作" width="160">
          <template slot-scope="{row}">
            <el-button v-if="row.status==='待审核'" type="text" size="small" @click="approveApp(row.id,'通过')">通过</el-button>
            <el-button v-if="row.status==='待审核'" type="text" size="small" style="color:#F56C6C" @click="approveApp(row.id,'驳回')">驳回</el-button>
            <el-button type="text" size="small" style="color:#909399" @click="delApp(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer"><el-button size="small" @click="appDialogVisible=false">关闭</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import http from '@/api'

export default {
  name: 'ClubList',
  data() {
    return {
      list: [], loading: false, submitting: false,
      dialogVisible: false, isEdit: false, avatarPreview: '',
      form: { clubId: null, clubName: '', category: '', leaderName: '', phone: '', avatar: '' },
      categories: ['学术科技','文化艺术','体育竞技','志愿公益','创新创业'],
      memberDialogVisible: false, memberClubId: null, memberClubName: '',
      members: [], memberForm: { userName: '', role: '成员' },
      editMemberVisible: false, editMemberForm: { id: null, userName: '', role: '' },
      activityDialogVisible: false, activityClubName: '', clubActivities: [],
      albumDialogVisible: false, albumClubName: '', clubAlbums: [], albumDesc: '', albumUrl: '',
      appDialogVisible: false, appClubName: '', appClubId: null, clubApplications: []
    }
  },
  created() { this.loadData() },
  activated() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await http.get('/club/list', { _: Date.now() })
        this.list = res.data || []
      } catch(e) { this.list = [] }
      finally { this.loading = false }
    },
    openAdd() { this.isEdit = false; this.resetForm(); this.dialogVisible = true },
    openEdit(row) { this.isEdit = true; this.form = { ...row }; this.avatarPreview = row.avatar || ''; this.dialogVisible = true },
    resetForm() { this.form = { clubId: null, clubName: '', category: '', leaderName: '', phone: '', avatar: '' }; this.avatarPreview = '' },
    onAvatarChange(e) {
      var file = e.target.files[0]; if (!file) return
      var self = this
      var reader = new FileReader()
      reader.onload = function(ev) { self.avatarPreview = ev.target.result }
      reader.readAsDataURL(file)
      var fd = new FormData(); fd.append('file', file)
      http.post('/file/upload', fd).then(function(res) { self.form.avatar = res.data }).catch(function(){})
      e.target.value = ''
    },
    async submit() {
      this.submitting = true
      try {
        if (this.isEdit) {
          await http.put('/club', this.form)
          this.$message.success('社团信息已更新')
        } else {
          await http.post('/club', this.form)
          this.$message.success('社团创建成功')
        }
        this.dialogVisible = false
        this.loadData()
      } catch(e) { this.$message.error(e.message || '操作失败') }
      finally { this.submitting = false }
    },
    del(row) {
      var self = this, id = row.clubId, name = row.clubName
      this.$confirm('确定删除社团「' + name + '」？', '提示', {
        confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning'
      }).then(async function() {
        try {
          await http.delete('/club/' + id)
          self.$message.success('社团「' + name + '」已删除')
          // 从服务器获取最新数据确保一致
          var r = await http.get('/club/list', { _: Date.now() })
          self.list = r.data || []
          self.$forceUpdate()
        } catch(e) {
          self.$message.error(e.message || '删除失败')
        }
      }).catch(function(){})
    },
    async openMembers(row) {
      this.memberClubId = row.clubId
      this.memberClubName = row.clubName
      this.memberDialogVisible = true
      await this.loadMembers()
    },
    async loadMembers() {
      try { var r = await http.get('/club-member/list/' + this.memberClubId); this.members = r.data || [] } catch(e) { this.members = [] }
    },
    async addMember() {
      if (!this.memberForm.userName) { this.$message.warning('请输入成员姓名'); return }
      try {
        await http.post('/club-member', {
          clubId: this.memberClubId,
          userId: 0,
          userName: this.memberForm.userName,
          role: this.memberForm.role
        })
        this.$message.success('成员已添加')
        this.memberForm.userName = ''
        this.loadMembers()
      } catch(e) { this.$message.error(e.message || '添加失败') }
    },
    editMember(row) {
      this.editMemberForm = { id: row.id, userName: row.userName, role: row.role }
      this.editMemberVisible = true
    },
    async saveMemberEdit() {
      try {
        await http.put('/club-member/' + this.editMemberForm.id, { userName: this.editMemberForm.userName, role: this.editMemberForm.role })
        this.$message.success('成员信息已更新')
        this.editMemberVisible = false
        this.loadMembers()
      } catch(e) { this.$message.error(e.message || '保存失败') }
    },
    async removeMember(row) {
      try {
        await http.delete('/club-member/' + row.id)
        this.$message.success('成员已移除')
        this.loadMembers()
      } catch(e) { this.$message.error(e.message || '移除失败') }
    },
    async openActivities(row) {
      this.activityClubName = row.clubName
      this.activityDialogVisible = true
      try { var r = await http.get('/club/' + row.clubId + '/activities'); this.clubActivities = r.data || [] } catch(e) { this.clubActivities = [] }
    },
    async openAlbum(row) {
      this.albumClubName = row.clubName
      this.albumClubId = row.clubId
      this.albumDialogVisible = true
      this.albumDesc = ''
      this.albumUrl = ''
      try { var r = await http.get('/club-album/list/' + row.clubId); this.clubAlbums = r.data || [] } catch(e) { this.clubAlbums = [] }
    },
    async uploadAlbum() {
      if (!this.albumUrl) { this.$message.warning('请输入图片URL'); return }
      const user = this.$store.state.userInfo || {}
      try {
        await http.post('/club-album', {
          clubId: this.albumClubId,
          imageUrl: this.albumUrl,
          description: this.albumDesc,
          uploaderId: user.userId,
          uploaderName: user.nickname
        })
        this.$message.success('照片已添加')
        this.albumUrl = ''
        this.albumDesc = ''
        var r = await http.get('/club-album/list/' + this.albumClubId)
        this.clubAlbums = r.data || []
      } catch(e) { this.$message.error(e.message || '添加失败') }
    },
    async delAlbum(id) {
      try {
        await http.delete('/club-album/' + id)
        this.$message.success('已删除')
        this.clubAlbums = this.clubAlbums.filter(a => a.id !== id)
      } catch(e) { this.$message.error(e.message || '删除失败') }
    },
    async openApplications(row) {
      this.appClubName = row.clubName
      this.appClubId = row.clubId
      this.appDialogVisible = true
      try { var r = await http.get('/club-application/list/' + row.clubId); this.clubApplications = r.data || [] } catch(e) { this.clubApplications = [] }
    },
    async approveApp(id, status) {
      try {
        await http.put('/club-application/approve/' + id, null, { params: { status } })
        this.$message.success('已' + status)
        var r = await http.get('/club-application/list/' + this.appClubId)
        this.clubApplications = r.data || []
      } catch(e) { this.$message.error(e.message || '操作失败') }
    },
    async delApp(id) {
      try {
        await http.delete('/club-application/' + id)
        this.$message.success('已删除')
        this.clubApplications = this.clubApplications.filter(a => a.id !== id)
      } catch(e) { this.$message.error(e.message || '删除失败') }
    }
  }
}
</script>

<style scoped>
.page-wrap { padding: 28px; }
.page-toolbar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.page-title { margin: 0; font-size: 22px; font-weight: 600; color: #1d1d1f; letter-spacing: -0.3px; }
.avatar-upload { cursor: pointer; border: 2px dashed rgba(0,0,0,0.08); border-radius: 16px; transition: border-color 0.3s }
.avatar-upload:hover { border-color: #1d1d1f }
.album-grid { display: flex; flex-wrap: wrap; gap: 20px; }
.album-item { text-align: center; }
.album-desc { margin: 6px 0; font-size: 12px; color: #86868b; }
</style>
