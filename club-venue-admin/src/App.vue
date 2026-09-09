<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  created() {
    // 仅浅色模式
    document.documentElement.setAttribute('data-theme', 'light')
  },
  mounted() {
    // 监听主题变化（从 Layout 触发）
    const observer = new MutationObserver(() => {
      const current = document.documentElement.getAttribute('data-theme')
      localStorage.setItem('theme', current || 'light')
    })
    observer.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] })
  }
}
</script>

<style>
@import './theme.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
html, body, #app {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  color: var(--text-primary);
  background: var(--content-bg);
  -webkit-font-smoothing: antialiased;
}
</style>
