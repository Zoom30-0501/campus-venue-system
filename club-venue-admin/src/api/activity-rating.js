import http from './index'

export default {
  listByActivity(activityId) { return http.get('/activity-rating/byActivity/' + activityId) },
  list() { return http.get('/activity-rating/list') },
  delete(id) { return http.delete('/activity-rating/' + id) }
}
