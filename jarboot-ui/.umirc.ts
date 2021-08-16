import {defineConfig} from 'umi';

export default defineConfig({
    nodeModulesTransform: {
        type: 'none',
    },
    title: 'Jarboot',
    routes: [
        {path: '/', component: '@/pages/index'},
        {path: '/login', component: '@/pages/login/Login'},
    ],
    fastRefresh: {},
    inlineLimit: 100000,
    //devtool: 'source-map',
    locale: {
        default: 'zh-CN',
        antd: true,
        title: true,
        baseNavigator: true,
        baseSeparator: '-',
    },
    proxy: {
        '/jarboot-service': {
            'target': 'http://localhost:9899/jarboot-service/',
            'changeOrigin': true,
            'pathRewrite': {'^/jarboot-service': ''}
        },
        '/jarboot-setting': {
            'target': 'http://localhost:9899/jarboot-setting/',
            'changeOrigin': true,
            'pathRewrite': {'^/jarboot-setting': ''}
        },
        '/jarboot-upload': {
            'target': 'http://localhost:9899/jarboot-upload/',
            'changeOrigin': true,
            'pathRewrite': {'^/jarboot-upload': ''}
        },
    },
});
