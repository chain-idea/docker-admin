import {defineConfig} from 'umi';


export default defineConfig({
  history: {type: 'hash'},
  nodeModulesTransform: {
    type: 'none',
  },
  locale: {antd: true},
  define: {
    "process.env.api": "/"
  },

  routes: [
    {
      path: '/admin',
      component: '@/layouts/admin',
      routes: [
        {path: '/admin', component: '@/pages/index'},
        {path: '/admin/project', component: '@/pages/admin/project'},
        {path: '/admin/project/:id', component: '@/pages/admin/project/view'},
        {path: '/admin/image', component: '@/pages/admin/image'},
        {path: '/admin/image/star', component: '@/pages/admin/image/star'},

        {path: '/admin/host', component: '@/pages/admin/host'},
        {path: '/admin/host/:id', component: '@/pages/admin/host/view'},
        {path: '/admin/app', component: '@/pages/admin/app'},
        {path: '/admin/app/deploy', component: '@/pages/admin/app/deploy'},
        {path: '/admin/app/:id', component: '@/pages/admin/app/view'},
        {path: '/admin/registry', component: '@/pages/admin/registry'},
        {path: '/admin/dockerHub', component: '@/pages/admin/dockerHub'},
        {path: '/admin/runner', component: '@/pages/admin/runner'},
        {path: '/admin/group', component: '@/pages/admin/group'},
        {path: '/admin/container/:hostId/:containerId', component: '@/pages/admin/container/view'},
        // 修改密码
        {path: '*', component: '@/pages/404'},
      ],
    },
    {
      path: '/front',
      component: '@/layouts/user',
      routes: [
        {path: '*', component: '@/pages/404'},
      ],
    },

    {
      path: '/login',
      component: '@/layouts/user',
      routes: [
        {path: '/login', component: '@/pages/login'},
      ],
    },

    {
      path: '/',
      component: '@/layouts/admin',
      routes: [
        {path: '/', redirect: '/login'},
      ],
    },


  ],
});


