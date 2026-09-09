<template>
  <el-container class="app-shell">
    <!-- Sidebar — White Glass -->
    <el-aside :width="collapsed ? '68px' : '240px'" class="sidebar">
      <div class="logo">
        <div class="logo-dot"></div>
        <transition name="logo-text">
          <span v-show="!collapsed" class="logo-text">社团场地管理</span>
        </transition>
      </div>

      <el-menu
        router
        :default-active="activeMenu"
        :collapse="collapsed"
        :collapse-transition="false"
        background-color="transparent"
        text-color="#86868b"
        active-text-color="#1d1d1f"
        class="side-menu"
      >
        <el-menu-item index="/dashboard">
          <i class="el-icon-s-home"></i>
          <span>数据概览</span>
        </el-menu-item>
        <el-submenu index="business">
          <template slot="title">
            <i class="el-icon-s-operation"></i>
            <span>业务管理</span>
          </template>
          <el-menu-item index="/club"><span>社团管理</span></el-menu-item>
          <el-menu-item index="/venue"><span>场地管理</span></el-menu-item>
          <el-menu-item index="/activity"><span>活动管理</span></el-menu-item>
          <el-menu-item index="/facility"><span>设施管理</span></el-menu-item>
          <el-menu-item index="/venue-calendar"><span>场地排期</span></el-menu-item>
          <el-menu-item index="/suggestion"><span>用户建议</span></el-menu-item>
          <el-menu-item index="/activity-rating"><span>活动评价</span></el-menu-item>
          <el-menu-item index="/activity-summary"><span>活动总结</span></el-menu-item>
          <el-menu-item index="/checkin"><span>签到管理</span></el-menu-item>
          <el-menu-item index="/club-album"><span>社团相册</span></el-menu-item>
          <el-menu-item index="/club-application"><span>入团申请</span></el-menu-item>
          <el-menu-item index="/activity-registration"><span>活动报名</span></el-menu-item>
          <el-menu-item index="/club-member"><span>社团成员</span></el-menu-item>
        </el-submenu>
        <el-submenu v-if="role === 'admin'" index="system">
          <template slot="title">
            <i class="el-icon-setting"></i>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/user"><span>用户管理</span></el-menu-item>
          <el-menu-item index="/role"><span>角色管理</span></el-menu-item>
        </el-submenu>
      </el-menu>

      <div class="collapse-btn" @click="collapsed = !collapsed">
        <i :class="collapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
      </div>
    </el-aside>

    <!-- Main -->
    <el-container class="main-container">
      <!-- Header — Frosted Glass -->
      <el-header class="header-bar" height="56px">
        <div class="header-left">
          <span class="header-title">高校社团活动场地管理系统</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="user-dropdown">
              <el-avatar :size="30" :src="userAvatar" class="user-avatar">
                <span>{{ userNickname ? userNickname.charAt(0) : '?' }}</span>
              </el-avatar>
              <span class="username">{{ userNickname }}</span>
              <i class="el-icon-arrow-down arrow-icon"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <!-- Content -->
      <el-main class="main-area">
        <div class="content-wrapper">
          <transition name="page-fade" mode="out-in">
            <router-view :key="$route.fullPath" />
          </transition>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return {
      collapsed: false
    }
  },
  computed: {
    userNickname() { return this.$store.getters.userName || '用户' },
    userAvatar() { return this.$store.getters.userAvatar || '' },
    role() { return this.$store.getters.userRole || 'user' },
    activeMenu() {
      const path = this.$route.path
      if (path.startsWith('/club-album')) return '/club-album'
      if (path.startsWith('/club-application')) return '/club-application'
      if (path.startsWith('/activity-registration')) return '/activity-registration'
      if (path.startsWith('/club-member')) return '/club-member'
      if (path.startsWith('/club')) return '/club'
      if (path.startsWith('/activity-rating')) return '/activity-rating'
      if (path.startsWith('/activity-summary')) return '/activity-summary'
      if (path.startsWith('/checkin')) return '/checkin'
      if (path.startsWith('/activity')) return '/activity'
      if (path.startsWith('/venue')) return '/venue'
      if (path.startsWith('/facility')) return '/facility'
      if (path.startsWith('/user')) return '/user'
      if (path.startsWith('/profile')) return ''
      return path
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'profile') {
        this.$router.push('/profile')
      } else if (command === 'logout') {
        this.$store.dispatch('logout')
        this.$router.push('/login')
        this.$message.success('已退出登录')
      }
    }
  }
}
</script>

<style scoped>
/* === Shell === */
.app-shell { height: 100vh; }

/* === Sidebar — White Glass Morphism === */
.sidebar {
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: saturate(200%) blur(20px);
  -webkit-backdrop-filter: saturate(200%) blur(20px);
  display: flex;
  flex-direction: column;
  transition: width 0.35s cubic-bezier(0.25, 0.1, 0.25, 1);
  overflow: hidden;
  border-right: 0.5px solid rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 10;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.logo-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #1d1d1f;
  flex-shrink: 0;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);
}

.logo-text {
  color: #1d1d1f;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: -0.3px;
  white-space: nowrap;
}

.logo-text-enter-active,
.logo-text-leave-active {
  transition: opacity 0.25s, transform 0.25s;
}
.logo-text-enter,
.logo-text-leave-to {
  opacity: 0;
  transform: translateX(-8px);
}

/* === Menu === */
.side-menu {
  border: none;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  position: relative;
  z-index: 1;
  padding: 8px 0;
}

.side-menu::-webkit-scrollbar { width: 0; }

.side-menu >>> .el-menu-item,
.side-menu >>> .el-submenu__title {
  height: 44px;
  line-height: 44px;
  font-size: 14px;
  margin: 3px 12px;
  border-radius: 10px;
  transition: all 0.25s cubic-bezier(0.25, 0.1, 0.25, 1);
  position: relative;
  overflow: hidden;
  color: #86868b !important;
}

.side-menu >>> .el-menu-item:hover,
.side-menu >>> .el-submenu__title:hover {
  background: rgba(0, 0, 0, 0.04) !important;
  transform: translateX(2px);
}

.side-menu >>> .el-menu-item.is-active {
  background: rgba(0, 0, 0, 0.06) !important;
  color: #1d1d1f !important;
  font-weight: 600;
}

.side-menu >>> .el-menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  border-radius: 0 4px 4px 0;
  background: #1d1d1f;
  box-shadow: none;
}

.side-menu >>> .el-submenu .el-menu {
  background: transparent !important;
  padding: 0;
}

.side-menu >>> .el-submenu .el-menu-item {
  padding-left: 56px !important;
  font-size: 13px;
  height: 40px;
  line-height: 40px;
  margin: 2px 12px 2px 20px;
}

.side-menu >>> .el-submenu .el-menu-item:hover {
  background: rgba(0, 0, 0, 0.04) !important;
}

.side-menu >>> .el-submenu .el-menu-item.is-active {
  background: rgba(0, 0, 0, 0.06) !important;
}

.side-menu >>> .el-submenu__title i {
  color: #86868b;
}

.side-menu >>> .el-menu-item i {
  color: inherit;
}

.side-menu.el-menu--collapse >>> .el-submenu__title span,
.side-menu.el-menu--collapse >>> .el-submenu__title .el-submenu__icon-arrow {
  display: none;
}

.side-menu.el-menu--collapse >>> .el-menu-item {
  padding-left: 20px !important;
}

/* === Collapse Button === */
.collapse-btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #86868b;
  cursor: pointer;
  border-top: 0.5px solid rgba(0, 0, 0, 0.06);
  transition: color 0.25s, background 0.25s, transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.collapse-btn:hover {
  color: #1d1d1f;
  background: rgba(0, 0, 0, 0.04);
  transform: scale(1.05);
}

.collapse-btn i { font-size: 18px; }

/* === Main Container === */
.main-container { position: relative; }

/* === Header — Frosted Glass === */
.header-bar {
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: saturate(200%) blur(20px);
  -webkit-backdrop-filter: saturate(200%) blur(20px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.06);
  padding: 0 24px;
  position: relative;
  z-index: 5;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-title {
  font-size: 15px;
  font-weight: 500;
  color: #1d1d1f;
  letter-spacing: -0.2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 10px;
  transition: all 0.25s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.user-dropdown:hover {
  background: rgba(0, 0, 0, 0.04);
  transform: scale(1.02);
}

.user-avatar { flex-shrink: 0; }

.username {
  font-size: 13px;
  color: #424245;
  font-weight: 500;
}

.arrow-icon {
  font-size: 11px;
  color: #86868b;
  transition: transform 0.2s;
}

/* === Content === */
.main-area {
  background: #f5f5f7;
  padding: 24px;
  position: relative;
  overflow: auto;
}

.content-wrapper {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
}
</style>
