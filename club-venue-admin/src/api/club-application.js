import http from './index'

export default {
  list() { return http.get('/club-application/list') },
  approve(id, status) { return http.put('/club-application/' + id + '?status=' + encodeURIComponent(status)) },
  delete(id) { return http.delete('/club-application/' + id) }
}
