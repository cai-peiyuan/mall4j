// components/production/production.js

Component({
    behaviors: [],

    /**
     * 组件的属性列表
     */
    properties: {
        item: Object,
        sts: Number
    },
    /**
     * 组件的初始数据
     */
    data: {
        imageUrl: ''
    },

    /**
     * 组件的方法列表
     */
    methods: {
        toProdPage: function (e) {
            var prodid = e.currentTarget.dataset.prodid;
            wx.navigateTo({
                url: '/pages/prods/pages/prod/prod?prodid=' + prodid,
            })
        },
        // 组件的方法
        useGlobalConfig: function () {
            // 获取全局配置参数
            var app = getApp();
            console.log('imageUrl', app.globalData.imageUrl); // 输出全局配置参数
            this.setData({
                imageUrl: app.globalData.imageUrl
            })
        }
    },
    attached: function () {
        // 组件实例化后触发，此时可以访问全局配置参数
        this.useGlobalConfig();
    }
})