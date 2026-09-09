import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('user') || 'null')
  },
  getters: {
    isLoggedIn: state => !!state.token,
    userRole: state => state.userInfo?.role || 'user',
    userId: state => state.userInfo?.userId,
    userName: state => state.userInfo?.nickname || state.userInfo?.username || '',
    userAvatar: state => state.userInfo?.avatar || ''
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      localStorage.setItem('user', JSON.stringify(userInfo))
      if (userInfo?.role) {
        localStorage.setItem('role', userInfo.role)
      }
    },
    CLEAR_AUTH(state) {
      state.token = ''
      state.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('role')
    }
  },
  actions: {
    login({ commit }, { token, userInfo }) {
      commit('SET_TOKEN', token)
      commit('SET_USER_INFO', userInfo)
    },
    logout({ commit }) {
      commit('CLEAR_AUTH')
    }
  }
})
