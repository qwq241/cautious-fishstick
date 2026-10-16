import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../views/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
      { path: 'tickets', name: 'Tickets', component: () => import('../views/Tickets.vue') },
      { path: 'tickets/create', name: 'TicketCreate', component: () => import('../views/TicketCreate.vue') },
      { path: 'tickets/:id', name: 'TicketDetail', component: () => import('../views/TicketDetail.vue') },
      { path: 'users', name: 'Users', component: () => import('../views/Users.vue') },
      { path: 'statistics', name: 'Statistics', component: () => import('../views/Statistics.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router