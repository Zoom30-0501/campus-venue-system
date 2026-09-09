<template><view></view></template>
<script setup>
import { onMounted } from 'vue'
onMounted(() => { uni.switchTab({ url: '/pages/index/index' }) })
</script>
