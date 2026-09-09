import http from './index'

export default {
  list() { return http.get('/checkin/list') },
  listByActivity(activityId) { return http.get('/checkin/activity/' + activityId) },
  listByUser(userId) { return http.get('/checkin/user/' + userId) },
  verify(id) { return http.put('/checkin/verify/' + id) },
  delete(id) { return http.delete('/checkin/' + id) }
}
