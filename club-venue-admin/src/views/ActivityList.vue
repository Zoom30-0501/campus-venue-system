<template>
  <div class="page-wrap" v-loading="loading">
    <div class="page-toolbar"><h3 class="page-title">活动管理</h3><el-button type="primary" size="small" icon="el-icon-plus" @click="openAdd">新增活动</el-button></div>
    <el-table :data="list" style="width:100%">
      <el-table-column prop="activityId" label="ID" width="200" />
      <el-table-column prop="activityName" label="活动名称" min-width="140" />
      <el-table-column prop="reason" label="申请原因" min-width="160" show-overflow-tooltip />
      <el-table-column prop="startTime" label="开始时间" width="160" />
      <el-table-column prop="endTime" label="结束时间" width="160" />
      <el-table-column prop="status" label="状态" width="90" align="center"><template slot-scope="{row}"><el-tag size="small" :type="statusMap[row.status]||''">{{ row.status }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="360" align="center" fixed="right">
        <template slot-scope="{row}">
          <el-button type="text" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button type="text" size="small" @click="openCheckIn(row)">签到</el-button>
          <el-button type="text" size="small" @click="openSummary(row)">总结</el-button>
          <el-button type="text" size="small" @click="openRating(row)">评分</el-button>
          <template v-if="row.status==='待审核'">
            <el-button type="text" size="small" style="color:#67C23A" @click="approve(row.activityId,'已通过')">通过</el-button>
            <el-button type="text" size="small" style="color:#F56C6C" @click="approve(row.activityId,'已驳回')">驳回</el-button>
          </template>
          <template v-if="row.status==='一级审核通过'">
            <el-button type="text" size="small" style="color:#67C23A" @click="secondApprove(row.activityId,'已通过')">二级通过</el-button>
            <el-button type="text" size="small" style="color:#F56C6C" @click="secondApprove(row.activityId,'已驳回')">二级驳回</el-button>
          </template>
          <el-button type="text" size="small" style="color:#F56C6C" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :title="isEdit?'编辑活动':'新增活动'" :visible.sync="dialogVisible" width="480px" @closed="resetForm">
      <el-form :model="form" label-width="90px" size="small">
        <el-form-item label="活动名称"><el-input v-model="form.activityName" /></el-form-item>
        <el-form-item label="申请原因"><el-input v-model="form.reason" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="社团ID"><el-input v-model="form.clubId" /></el-form-item>
        <el-form-item label="场地ID"><el-input v-model="form.venueId" /></el-form-item>
        <el-form-item label="审批人ID"><el-input v-model="form.approverId" placeholder="指定审批人的用户ID" /></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="form.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="form.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:100%" /></el-form-item>
      </el-form>
      <span slot="footer"><el-button size="small" @click="dialogVisible=false">取消</el-button><el-button type="primary" size="small" @click="submit" :loading="submitting">保存</el-button></span>
    </el-dialog>

    <el-dialog :title="checkInActivityName + ' - 签到管理'" :visible.sync="checkInVisible" width="500px">
      <div style="margin-bottom:12px;display:flex;gap:8px">
        <el-input v-model="checkInName" placeholder="签到人姓名" size="small" style="width:160px" />
        <el-button type="primary" size="small" @click="doCheckIn">签到</el-button>
      </div>
      <el-table :data="checkInList" stripe size="small" style="width:100%">
        <el-table-column prop="userName" label="姓名" width="120" />
        <el-table-column prop="checkInTime" label="签到时间" min-width="160" />
        <el-table-column label="验证" width="80" align="center">
          <template slot-scope="{row}">
            <el-tag size="mini" :type="row.verified?'success':'info'">{{ row.verified?'已核验':'未核验' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template slot-scope="{row}">
            <el-button v-if="!row.verified" type="text" size="small" @click="verifyCheckIn(row.id)">核验</el-button>
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer"><el-button size="small" @click="checkInVisible=false">关闭</el-button></span>
    </el-dialog>

    <!-- 活动总结 -->
    <el-dialog :title="summaryActivityName + ' - 活动总结'" :visible.sync="summaryVisible" width="500px">
      <el-form :model="summaryForm" label-width="100px" size="small">
        <el-form-item label="活动总结">
          <el-input v-model="summaryForm.content" type="textarea" :rows="6" placeholder="请填写活动总结/回顾" />
        </el-form-item>
        <el-form-item label="参与人数">
          <el-input v-model="summaryForm.participantCount" type="number" />
        </el-form-item>
        <el-form-item label="反馈意见">
          <el-input v-model="summaryForm.feedback" type="textarea" :rows="3" placeholder="活动反馈或改进意见" />
        </el-form-item>
      </el-form>
      <span slot="footer"><el-button size="small" @click="summaryVisible=false">取消</el-button><el-button type="primary" size="small" @click="saveSummary">保存</el-button></span>
    </el-dialog>

    <!-- 活动评分 -->
    <el-dialog :title="ratingActivityName + ' - 活动评分'" :visible.sync="ratingVisible" width="500px">
      <div style="margin-bottom:12px;display:flex;gap:8px">
        <el-input v-model="ratingScore" placeholder="评分(1-5)" type="number" size="small" style="width:100px" />
        <el-input v-model="ratingComment" placeholder="评语" size="small" style="width:200px" />
        <el-button type="primary" size="small" @click="doRating">提交评分</el-button>
      </div>
      <el-table :data="ratingList" stripe size="small" style="width:100%">
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="score" label="评分" width="80">
          <template slot-scope="{row}">{{ '★'.repeat(row.score) }}{{ '☆'.repeat(5-row.score) }}</template>
        </el-table-column>
        <el-table-column prop="comment" label="评语" min-width="150" />
        <el-table-column prop="createTime" label="时间" width="160" />
      </el-table>
      <span slot="footer"><el-button size="small" @click="ratingVisible=false">关闭</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
import http from '@/api'

export default {
  name: 'ActivityList',
  data() { return { list: [], loading: false, submitting: false, dialogVisible: false, isEdit: false, form: { activityId: null, activityName: '', reason: '', clubId: '', venueId: '', approverId: '', startTime: '', endTime: '' }, statusMap: { '待审核':'warning','已通过':'success','已驳回':'danger','进行中':'','已结束':'info','一级审核通过':'warning' }, checkInVisible: false, checkInActivityId: null, checkInActivityName: '', checkInName: '', checkInList: [], summaryVisible: false, summaryActivityId: null, summaryActivityName: '', summaryForm: { content: '', participantCount: 0, feedback: '' }, ratingVisible: false, ratingActivityId: null, ratingActivityName: '', ratingScore: 5, ratingComment: '', ratingList: [] } },
  created() { this.loadData() },
  activated() { this.loadData() },
  methods: {
    async loadData() { this.loading = true; try { var r = await http.get('/activity/list', { _: Date.now() }); this.list = r.data || [] } catch(e) { this.list = [] } finally { this.loading = false } },
    openAdd() { this.isEdit = false; this.resetForm(); this.dialogVisible = true },
    openEdit(row) { this.isEdit = true; this.form = { ...row }; this.dialogVisible = true },
    resetForm() { this.form = { activityId: null, activityName: '', reason: '', clubId: '', venueId: '', approverId: '', startTime: '', endTime: '' } },
    async submit() {
      this.submitting = true
      try {
        if (this.isEdit) { await http.put('/activity', this.form); this.$message.success('活动信息已更新') }
        else { await http.post('/activity', this.form); this.$message.success('活动创建成功') }
        this.dialogVisible = false; this.loadData()
      } catch(e) { this.$message.error(e.message || '操作失败') } finally { this.submitting = false }
    },
    async approve(id, status) {
      try { await http.put('/activity/approve/' + id + '?status=' + encodeURIComponent(status)); this.$message.success(status === '已通过' ? '活动已通过' : '活动已驳回'); this.loadData() }
      catch(e) { this.$message.error(e.message || '审批失败') }
    },
    async secondApprove(id, status) {
      try { await http.put('/activity/second-approve/' + id + '?status=' + encodeURIComponent(status)); this.$message.success(status === '已通过' ? '二级审核已通过' : '二级审核已驳回'); this.loadData() }
      catch(e) { this.$message.error(e.message || '二级审批失败') }
    },
    async openCheckIn(row) {
      this.checkInActivityId = row.activityId
      this.checkInActivityName = row.activityName
      this.checkInVisible = true
      await this.loadCheckIns()
    },
    async loadCheckIns() {
      try { var r = await http.get('/checkin/activity/' + this.checkInActivityId); this.checkInList = r.data || [] } catch(e) { this.checkInList = [] }
    },
    async doCheckIn() {
      if (!this.checkInName) { this.$message.warning('请输入签到人姓名'); return }
      try {
        await http.post('/checkin', { activityId: this.checkInActivityId, userId: 0, userName: this.checkInName })
        this.$message.success('签到成功')
        this.checkInName = ''
        this.loadCheckIns()
      } catch(e) { this.$message.error(e.message || '签到失败') }
    },
    async verifyCheckIn(id) {
      try { await http.put('/checkin/verify/' + id); this.$message.success('已核验'); this.loadCheckIns() }
      catch(e) { this.$message.error(e.message || '核验失败') }
    },
    async openSummary(row) {
      this.summaryActivityId = row.activityId
      this.summaryActivityName = row.activityName
      this.summaryVisible = true
      try {
        var r = await http.get('/activity-summary/byActivity/' + row.activityId)
        if (r.data && r.data.id) {
          this.summaryForm = { content: r.data.content || '', participantCount: r.data.participantCount || 0, feedback: r.data.feedback || '' }
          this.summaryForm.id = r.data.id
        } else {
          this.summaryForm = { content: '', participantCount: 0, feedback: '' }
        }
      } catch(e) { this.summaryForm = { content: '', participantCount: 0, feedback: '' } }
    },
    async saveSummary() {
      try {
        if (this.summaryForm.id) {
          await http.put('/activity-summary/' + this.summaryForm.id, {
            content: this.summaryForm.content,
            participantCount: this.summaryForm.participantCount,
            feedback: this.summaryForm.feedback
          })
        } else {
          await http.post('/activity-summary', {
            activityId: this.summaryActivityId,
            content: this.summaryForm.content,
            participantCount: this.summaryForm.participantCount,
            feedback: this.summaryForm.feedback
          })
        }
        this.$message.success('活动总结已保存')
        this.summaryVisible = false
      } catch(e) { this.$message.error(e.message || '保存失败') }
    },
    async openRating(row) {
      this.ratingActivityId = row.activityId
      this.ratingActivityName = row.activityName
      this.ratingVisible = true
      this.ratingScore = 5
      this.ratingComment = ''
      try { var r = await http.get('/activity-rating/list/' + row.activityId); this.ratingList = r.data || [] } catch(e) { this.ratingList = [] }
    },
    async doRating() {
      const score = parseInt(this.ratingScore)
      if (score < 1 || score > 5) { this.$message.warning('评分需在1-5之间'); return }
      const user = this.$store.state.userInfo || {}
      try {
        await http.post('/activity-rating', {
          activityId: this.ratingActivityId,
          userId: user.userId,
          userName: user.nickname,
          score: score,
          comment: this.ratingComment
        })
        this.$message.success('评分已提交')
        var r = await http.get('/activity-rating/list/' + this.ratingActivityId)
        this.ratingList = r.data || []
      } catch(e) { this.$message.error(e.message || '评分失败') }
    },
    del(row) {
      var self = this, id = row.activityId, name = row.activityName
      this.$confirm('确定删除活动「' + name + '」？', '提示', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' })
        .then(async function() {
          try {
            await http.delete('/activity/' + id)
            self.$message.success('活动「' + name + '」已删除')
            var r = await http.get('/activity/list', { _: Date.now() })
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
