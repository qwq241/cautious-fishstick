<template>
  <el-container class="layout">
    <el-aside width="200px">
      <div class="logo">企业工单系统</div>
      <el-menu :default-active="activeMenu" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/dashboard"><el-icon><DataBoard /></el-icon>工作台</el-menu-item>
        <el-menu-item index="/tickets"><el-icon><Tickets /></el-icon>工单列表</el-menu-item>
        <el-menu-item index="/tickets/create"><el-icon><Plus /></el-icon>创建工单</el-menu-item>
        <el-menu-item index="/statistics"><el-icon><TrendCharts /></el-icon>统计报表</el-menu-item>
        <el-menu-item v-if="auth.userRole === 'ADMIN'" index="/users"><el-icon><User /></el-icon>用户管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <span>欢迎, {{ auth.userName }}</span>
        <el-button type="text" @click="handleLogout">退出登录</el-button>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const activeMenu = computed(() => route.path)

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.el-aside { background: #304156; }
.logo { color: #fff; text-align: center; padding: 16px; font-size: 18px; font-weight: bold; }
.el-header { background: #fff; display: flex; align-items: center; justify-content: flex-end; gap: 16px; border-bottom: 1px solid #e6e6e6; }
.el-main { background: #f0f2f5; padding: 20px; }
</style>
