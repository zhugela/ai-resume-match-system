import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    children: [
      {
        path: '',
        redirect: '/home'
      },
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'resume',
        name: 'Resume',
        component: () => import('@/views/Resume.vue')
      },
      {
        path: 'resume/:id',
        name: 'ResumeDetail',
        component: () => import('@/views/ResumeDetail.vue')
      },
      {
        path: 'job',
        name: 'Job',
        component: () => import('@/views/Job.vue')
      },
      {
        path: 'job/:id',
        name: 'JobDetail',
        component: () => import('@/views/JobDetail.vue')
      },
      {
        path: 'match',
        name: 'Match',
        component: () => import('@/views/Match.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue')
      },
      // === v2.0 新增页面 ===
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('@/views/Notifications.vue')
      },
      {
        path: 'questions',
        name: 'Questions',
        component: () => import('@/views/Questions.vue')
      },
      {
        path: 'salary',
        name: 'Salary',
        component: () => import('@/views/Salary.vue')
      },
      {
        path: 'templates',
        name: 'Templates',
        component: () => import('@/views/Templates.vue')
      },
      {
        path: 'progress',
        name: 'Progress',
        component: () => import('@/views/Progress.vue')
      },
      {
        path: 'admin',
        name: 'AdminDashboard',
        component: () => import('@/views/AdminDashboard.vue')
      },
      {
        path: 'compare-resumes',
        name: 'CompareResumes',
        component: () => import('@/views/CompareResumes.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login' || to.path === '/register') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
