import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/views/Layout.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'club',
        name: 'ClubList',
        component: () => import('@/views/ClubList.vue'),
        meta: { title: '社团管理' }
      },
      {
        path: 'venue',
        name: 'VenueList',
        component: () => import('@/views/VenueList.vue'),
        meta: { title: '场地管理' }
      },
      {
        path: 'activity',
        name: 'ActivityList',
        component: () => import('@/views/ActivityList.vue'),
        meta: { title: '活动管理' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'user',
        name: 'UserList',
        component: () => import('@/views/UserList.vue'),
        meta: { title: '用户管理', role: 'admin' }
      },
      {
        path: 'facility',
        name: 'FacilityList',
        component: () => import('@/views/FacilityList.vue'),
        meta: { title: '设施管理' }
      },
      {
        path: 'venue-calendar',
        name: 'VenueCalendar',
        component: () => import('@/views/VenueCalendar.vue'),
        meta: { title: '场地排期' }
      },
      {
        path: 'suggestion',
        name: 'SuggestionList',
        component: () => import('@/views/SuggestionList.vue'),
        meta: { title: '用户建议' }
      },
      {
        path: 'activity-rating',
        name: 'ActivityRating',
        component: () => import('@/views/activity-rating/list.vue'),
        meta: { title: '活动评价管理' }
      },
      {
        path: 'activity-summary',
        name: 'ActivitySummary',
        component: () => import('@/views/activity-summary/list.vue'),
        meta: { title: '活动总结管理' }
      },
      {
        path: 'checkin',
        name: 'CheckinList',
        component: () => import('@/views/checkin/list.vue'),
        meta: { title: '签到管理' }
      },
      {
        path: 'club-album',
        name: 'ClubAlbum',
        component: () => import('@/views/club-album/list.vue'),
        meta: { title: '社团相册管理' }
      },
      {
        path: 'club-application',
        name: 'ClubApplication',
        component: () => import('@/views/club-application/list.vue'),
        meta: { title: '入团申请审批' }
      },
      {
        path: 'activity-registration',
        name: 'ActivityRegistration',
        component: () => import('@/views/activity-registration/list.vue'),
        meta: { title: '活动报名管理' }
      },
      {
        path: 'club-member',
        name: 'ClubMember',
        component: () => import('@/views/club-member/list.vue'),
        meta: { title: '社团成员管理' }
      },
      {
        path: 'role',
        name: 'RoleList',
        component: () => import('@/views/RoleList.vue'),
        meta: { title: '角色管理', role: 'admin' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// Global route guard: auth + role check
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  const role = user.role || localStorage.getItem('role') || 'user'

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
    // Role check
    if (to.meta.role && to.meta.role !== role) {
      next({ path: '/dashboard' })
      return
    }
    next()
  } else if (to.name === 'Login' && token) {
    next({ path: '/' })
  } else {
    next()
  }
})

export default router
