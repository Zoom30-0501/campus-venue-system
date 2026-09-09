import request from '@/api'

export default {
  getList(params) {
    return request.get('/activity-registration/list/' + params.activityId)
  },
  listByUser(userId) {
    return request.get('/activity-registration/my/' + userId)
  },
  approve(id, status) {
    return request.put('/activity-registration/approve/' + id, null, { params: { status } })
  },
  delete(id) {
    return request.delete('/activity-registration/' + id)
  }
}
