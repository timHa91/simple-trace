import { createRouter, createWebHistory } from 'vue-router'
import TraceDetail from '@/views/TraceDetail.vue'
import NotFound from '@/views/NotFound.vue'
import HomeView from '@/views/HomeView.vue'
import TraceList from '@/views/TraceList.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', component: HomeView},
    { path: '/traces', component: TraceList},
    { path: '/trace/:id', component: TraceDetail },
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFound },
  ],
})

export default router
