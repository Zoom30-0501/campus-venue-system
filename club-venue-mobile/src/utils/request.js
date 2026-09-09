// Vite dev proxy forwards /api/* → http://localhost:8083/api/*
// Pages call get('/api/xxx') → final URL is /api/xxx (relative, uses proxy)
const BASE_URL = ''

let token = ''
let userInfo = {}

/** 设置 token（登录时调用） */
export function setToken(t) {
  token = t
  uni.setStorageSync('token', t)
}

/** 获取当前 token */
export function getToken() {
  if (!token) {
    token = uni.getStorageSync('token') || ''
  }
  return token
}

/** 清除 token（退出登录时调用） */
export function clearToken() {
  token = ''
  userInfo = {}
  uni.removeStorageSync('token')
}

/** 保存用户信息（登录时调用） */
export function setUserInfo(info) {
  userInfo = info || {}
  uni.setStorageSync('userInfo', JSON.stringify(userInfo))
}

/** 获取用户信息 */
export function getUserInfo() {
  if (!userInfo || !userInfo.userId) {
    try {
      const raw = uni.getStorageSync('userInfo')
      userInfo = typeof raw === 'string' ? JSON.parse(raw) : (raw || {})
    } catch (e) {}
  }
  return userInfo || {}
}

/** 更新用户信息中的某个字段 */
export function updateUserInfo(patch) {
  const info = getUserInfo()
  Object.assign(info, patch)
  userInfo = info
  uni.setStorageSync('userInfo', JSON.stringify(info))
}

function request(url, method = 'GET', data = {}, silent = false) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': getToken() ? 'Bearer ' + getToken() : ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else if (res.statusCode === 403) {
          // Forbidden — maybe logged out, resolve null silently
          if (!silent) {
            uni.showToast({ title: '请先登录', icon: 'none', duration: 2000 })
          }
          resolve(null)
        } else {
          const msg = res.data?.message || '请求失败'
          if (!silent) {
            uni.showToast({ title: msg, icon: 'none', duration: 2000 })
          }
          reject(new Error(msg))
        }
      },
      fail: (err) => {
        if (!silent) {
          uni.showToast({ title: '网络异常', icon: 'none', duration: 2000 })
        }
        reject(err)
      }
    })
  })
}

export function get(url, params = {}, silent = false) {
  const query = Object.keys(params)
    .filter(k => params[k] !== undefined && params[k] !== null)
    .map(k => encodeURIComponent(k) + '=' + encodeURIComponent(params[k]))
    .join('&')
  return request(url + (query ? '?' + query : ''), 'GET', {}, silent)
}

export function post(url, data = {}, silent = false) {
  return request(url, 'POST', data, silent)
}

export function put(url, data = {}, silent = false) {
  return request(url, 'PUT', data, silent)
}

export function del(url, silent = false) {
  return request(url, 'DELETE', {}, silent)
}
