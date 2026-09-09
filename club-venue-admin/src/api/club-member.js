import http from './index'

export default {
  list() { return http.get('/club-member/list') },
  listByClub(clubId) { return http.get('/club-member/list/' + clubId) },
  create(data) { return http.post('/club-member', data) },
  update(id, data) { return http.put('/club-member/' + id, data) },
  delete(id) { return http.delete('/club-member/' + id) }
}
