import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// Request interceptor: attach token
http.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
}, error => Promise.reject(error))

// Response interceptor: handle errors globally
http.interceptors.response.use(response => {
  const data = response.data
  if (data.code !== 200) {
    const err = new Error(data.message || '请求失败')
    err.data = data
    return Promise.reject(err)
  }
  return data
}, error => {
  if (error.response) {
    if (error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('role')
      window.location.href = '/login'
      return Promise.reject(new Error('登录已过期，请重新登录'))
    }
    return Promise.reject(new Error(error.response.data?.message || '服务器错误'))
  }
  return Promise.reject(error)
})

export default {
  get(url, params) {
    return http.get(url, { params })
  },
  post(url, data) {
    return http.post(url, data)
  },
  put(url, data) {
    return http.put(url, data)
  },
  delete(url) {
    return http.delete(url)
  }
}
