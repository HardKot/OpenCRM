import { createMemoryHistory, createRouter } from 'vue-router'

import { LoginPage } from "@pages/auth/login"

const routes = [
  { path: '/login', component: LoginPage },
  { path: '/', redirect: '/login' }
]

export const router = createRouter({
  history: createMemoryHistory(),
  routes,
});