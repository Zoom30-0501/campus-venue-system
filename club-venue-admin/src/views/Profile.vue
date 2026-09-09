<template>
  <div class="page-card">
    <div class="page-header">个人中心</div>
    <el-tabs v-model="activeTab" type="card" style="padding:0 24px">
      <el-tab-pane label="基本信息" name="info">
        <el-form :model="form" label-width="100px" style="max-width:480px;padding:24px 0">
          <el-form-item label="头像">
            <div class="avatar-section">
              <el-avatar :size="72" :src="avatarPreview" @click.native="triggerUpload" class="avatar-clickable">
                <span v-if="!avatarPreview" style="font-size:24px">{{ form.nickname ? form.nickname.charAt(0) : '?' }}</span>
              </el-avatar>
              <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="handleFileChange" />
              <span class="upload-tip">点击头像更换</span>
            </div>
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="form.username" size="small" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="form.nickname" size="small" />
          </el-form-item>
          <el-form-item label="所属社团">
            <el-input v-model="form.clubName" size="small" disabled />
          </el-form-item>
          <el-form-item label="旧密码">
            <el-input v-model="oldPassword" type="password" size="small" show-password placeholder="不修改则留空" />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="newPassword" type="password" size="small" show-password placeholder="不修改则留空" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="save">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="我的社团" name="myClubs">
        <el-table :data="myClubMembers" border>
          <el-table-column prop="clubName" label="社团名称" />
          <el-table-column prop="userName" label="姓名" />
          <el-table-column prop="status" label="状态" />
          <el-table-column prop="joinTime" label="加入时间" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="我的活动" name="myActivities">
        <el-table :data="myActivities" border>
          <el-table-column prop="activityName" label="活动名称" />
          <el-table-column prop="clubName" label="所属社团" />
          <el-table-column prop="startTime" label="开始时间" width="160" />
          <el-table-column prop="status" label="状态" width="100" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="我的签到" name="myCheckins">
        <el-table :data="myCheckins" border>
          <el-table-column prop="activityName" label="活动名称" />
          <el-table-column prop="createTime" label="签到时间" width="160" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="我的建议" name="mySuggestions">
        <el-table :data="mySuggestions" border>
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="content" label="内容" />
          <el-table-column prop="status" label="状态" width="100" />
          <el-table-column prop="createTime" label="提交时间" width="160" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import request from '@/api'

export default {
  name: 'Profile',
  data() {
    const userInfo = this.$store.state.userInfo || {}
    return {
      activeTab: 'info',
      form: {
        userId: userInfo.userId,
        username: userInfo.username,
        nickname: userInfo.nickname,
        clubName: userInfo.clubName || '',
        avatar: userInfo.avatar || ''
      },
      avatarPreview: userInfo.avatar || '',
      oldPassword: '',
      newPassword: '',
      myClubMembers: [],
      myActivities: [],
      myCheckins: [],
      mySuggestions: []
    }
  },
  watch: {
    activeTab(val) {
      if (val === 'myClubs') this.fetchMyClubs()
      else if (val === 'myActivities') this.fetchMyActivities()
      else if (val === 'myCheckins') this.fetchMyCheckins()
      else if (val === 'mySuggestions') this.fetchMySuggestions()
    }
  },
  methods: {
    async fetchMyClubs() {
      const userId = this.$store.getters.userId
      const res = await request.get(`/club-member/user/${userId}`)
      this.myClubMembers = res.data || []
    },
    async fetchMyActivities() {
      const userId = this.$store.getters.userId
      const res = await request.get('/activity/list')
      const all = res.data || []
      this.myActivities = all.filter(a => a.applicantId === userId)
    },
    async fetchMyCheckins() {
      const userId = this.$store.getters.userId
      const res = await request.get(`/checkin/user/${userId}`)
      this.myCheckins = res.data || []
    },
    async fetchMySuggestions() {
      const userId = this.$store.getters.userId
      const res = await request.get(`/suggestion/user/${userId}`)
      this.mySuggestions = res.data || []
    },
    triggerUpload() {
      this.$refs.fileInput.click()
    },
    handleFileChange(e) {
      const file = e.target.files[0]
      if (!file) return
      const reader = new FileReader()
      reader.onload = ev => { this.avatarPreview = ev.target.result }
      reader.readAsDataURL(file)
      const formData = new FormData()
      formData.append('file', file)
      request.post('/file/upload', formData).then(res => {
        if (res.code === 200) {
          this.form.avatar = res.data
          this.$message.success('头像上传成功，请点击保存')
        } else {
          this.$message.error(res.message || '上传失败')
        }
      }).catch(() => {
        this.$message.error('上传失败')
      })
      e.target.value = ''
    },
    async save() {
      await request.put('/user', this.form)
      if (this.oldPassword && this.newPassword) {
        await request.put('/user/password', {
          username: this.form.username,
          oldPassword: this.oldPassword,
          newPassword: this.newPassword
        })
      }
      this.$store.commit('SET_USER_INFO', {
        ...this.$store.state.userInfo,
        nickname: this.form.nickname,
        avatar: this.form.avatar
      })
      this.$message.success('保存成功')
    }
  }
}
</script>

<style scoped>
.avatar-section {
  display: flex;
  align-items: center;
  gap: 14px;
}

.avatar-clickable {
  cursor: pointer;
  border: 1px solid #DCDFE6;
  transition: border-color 0.2s;
}

.avatar-clickable:hover {
  border-color: #00BFFF;
}

.upload-tip {
  font-size: 12px;
  color: #C0C4CC;
}
</style>
