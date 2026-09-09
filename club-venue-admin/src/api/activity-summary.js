import http from './index'

export default {
  list() { return http.get('/activity-summary/list') },
  getByActivity(activityId) { return http.get('/activity-summary/byActivity/' + activityId) },
  create(data) { return http.post('/activity-summary', data) },
  update(id, data) { return http.put('/activity-summary/' + id, data) },
  delete(id) { return http.delete('/activity-summary/' + id) }
}
