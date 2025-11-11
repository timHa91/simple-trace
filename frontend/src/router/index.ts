import {createRouter, createWebHistory} from 'vue-router'
import TraceDetailView from '@/views/TraceDetailView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import HomeView from '@/views/HomeView.vue'
import ErrorTraceView from "@/views/ErrorTraceView.vue";
import TraceListView from "@/views/TraceListView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', component: HomeView},
    { path: '/errors', component: ErrorTraceView},
    { path: '/traces', component: TraceListView},
    { path: '/trace/:id', component: TraceDetailView },
    { path: '/:pathMatch(.*)*', name: 'NotFound', component: NotFoundView },
  ],
})

export default router
