import http from './index'

export default {
  list() { return http.get('/club/album/list') },
  listByClub(clubId) { return http.get('/club/album/' + clubId) },
  create(data) { return http.post('/club/album', data) },
  delete(id) { return http.delete('/club/album/' + id) }
}
