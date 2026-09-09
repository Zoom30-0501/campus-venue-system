import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueReveal from './directives/scroll-reveal'

Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.directive('reveal', VueReveal)

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
