<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar">
      <h3 class="page-title">社团相册管理</h3>
      <div style="display:flex;gap:8px">
        <el-select v-model="filterClubId" filterable clearable placeholder="选择社团" size="small" style="width:200px" @change="loadData">
          <el-option v-for="c in clubOptions" :key="c.clubId" :label="c.clubName" :value="c.clubId" />
        </el-select>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="openAdd">新增照片</el-button>
      </div>
    </div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="clubName" label="社团名称" width="140" />
      <el-table-column label="图片预览" width="120" align="center">
        <template slot-scope="{row}">
          <el-image :src="row.imageUrl" fit="cover" style="width:80px;height:60px;border-radius:4px" :preview-src-list="[row.imageUrl]" />
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
      <el-table-column prop="uploaderName" label="上传人" width="100" />
      <el-table-column prop="createTime" label="上传时间" width="160" />
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template slot-scope="{row}">
          <el-button type="text" size="small" style="color:#F56C6C" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="新增相册照片" :visible.sync="dialogVisible" width="480px" @closed="resetForm">
      <el-form :model="form" label-width="80px" size="small">
        <el-form-item label="社团">
          <el-select v-model="form.clubId" filterable placeholder="请选择社团" style="width:100%">
            <el-option v-for="c in clubOptions" :key="c.clubId" :label="c.clubName" :value="c.clubId" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" placeholder="请输入图片URL地址" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" placeholder="照片描述（可选）" />
        </el-form-item>
      </el-form>
      <div v-if="form.imageUrl" style="text-align:center;margin-bottom:12px">
        <el-image :src="form.imageUrl" fit="contain" style="max-width:100%;max-height:200px" />
      </div>
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
  name: 'ClubAlbumList',
  data() {
    return {
      list: [], loading: false, submitting: false, filterClubId: '', clubOptions: [],
      dialogVisible: false, form: { clubId: '', imageUrl: '', description: '' }
    }
  },
  created() { this.loadData(); this.loadClubs() },
  activated() { this.loadData() },
  methods: {
    async loadData() {
      this.loading = true
      try {
        var r
        if (this.filterClubId) {
          r = await http.get('/club/album/' + this.filterClubId)
        } else {
          r = await http.get('/club/album/list')
        }
        this.list = r.data || []
      } catch(e) { this.list = [] }
      finally { this.loading = false }
    },
    async loadClubs() {
      try { var r = await http.get('/club/list'); this.clubOptions = r.data || [] } catch(e) { this.clubOptions = [] }
    },
    openAdd() { this.resetForm(); this.dialogVisible = true },
    resetForm() { this.form = { clubId: '', imageUrl: '', description: '' } },
    async submit() {
      if (!this.form.clubId) { this.$message.warning('请选择社团'); return }
      if (!this.form.imageUrl) { this.$message.warning('请输入图片URL'); return }
      this.submitting = true
      try {
        await http.post('/club/album', { clubId: this.form.clubId, imageUrl: this.form.imageUrl, description: this.form.description })
        this.$message.success('照片已添加')
        this.dialogVisible = false
        this.loadData()
      } catch(e) { this.$message.error(e.message || '添加失败') }
      finally { this.submitting = false }
    },
    del(row) {
      var self = this
      this.$confirm('确定删除该照片？', '提示', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
        .then(async function() {
          try {
            await http.delete('/club/album/' + row.id)
            self.$message.success('照片已删除')
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
