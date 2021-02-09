import {defineConfig} from 'umi';


export default defineConfig({
  define: {
    ENV: 'dev',
    "process.env.api": "http://127.0.0.1:8080/",
    "process.env.apiPort": 8080, // 如果设置了apiPort,会使用当前页面地址，只不过更换了端口号
  },

});


