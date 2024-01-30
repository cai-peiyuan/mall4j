// pages/order-detail/order-detail.js

var http = require('../../../../utils/http.js');
Page({

    /**
     * 页面的初始数据
     */
    data: {
        orderItemDtos: [],
        remarks: "",
        actualTotal: 0,
        userAddrDto: null,
        orderNumber: "",
        createTime: "",
        status: 0,
        productTotalAmount: '',
        transfee: '',
        reduceAmount: '',
        shopId: '',
        prodid: '',
        order: {}
    },

    //跳转商品详情页
    toProdPage: function (e) {
        var prodid = e.currentTarget.dataset.prodid;
        wx.navigateTo({
            url: '/pages/prods/pages/prod/prod?prodid=' + prodid,
        })
    },

    /**
     * 加入购物车
     */
    addToCart: function (event) {
        let index = event.currentTarget.dataset.index
        // if (!this.orderItemDtos) {
        //   console.log(1213)
        //   return;
        // }
        var ths = this;
        wx.showLoading({
            mask: true
        });
        var params = {
            url: "/p/shopCart/changeItem",
            method: "POST",
            data: {
                basketId: 0,
                count: this.data.orderItemDtos[index].prodCount,
                prodId: this.data.orderItemDtos[index].prodId,
                shopId: this.data.shopId,
                skuId: this.data.orderItemDtos[index].skuId
            },
            callBack: function (res) {
                //console.log(res);
                wx.hideLoading();
                wx.showToast({
                    title: "加入购物车成功",
                    icon: "none"
                })
                wx.switchTab({
                    url: '/pages/basket/basket',
                })
            }
        };
        http.request(params);
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.loadOrderDetail(options.orderNum);
    },

    /**
     * 加载订单数据
     */
    loadOrderDetail: function (orderNum) {
        var ths = this;
        wx.showLoading();
        //加载订单详情
        var params = {
            url: "/p/myOrder/orderDetail/" + orderNum,
            method: "GET",
            data: {

            },
            callBack: function (data) {
                let res = data.orderShop;
                let order = data.order;
                ths.setData({
                    orderNumber: orderNum,
                    actualTotal: res.actualTotal,
                    userAddrDto: res.userAddrDto,
                    remarks: res.remarks,
                    orderItemDtos: res.orderItemDtos,
                    createTime: res.createTime,
                    status: res.status,
                    productTotalAmount: res.orderItemDtos[0].productTotalAmount,
                    transfee: res.transfee,
                    reduceAmount: res.reduceAmount,
                    actualTotal: res.actualTotal,
                    shopId: res.shopId,
                    order: order
                });
                wx.hideLoading();
            }
        };
        http.request(params);

    },


    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    },


    // 一键复制事件
    copyBtn: function (e) {
        var ths = this;
        wx.setClipboardData({
            //准备复制的数据
            data: ths.data.orderNumber,
            success: function (res) {
                wx.showToast({
                    title: '复制成功',
                });
            }
        })
    },

    /**
     * 订单退款
     * 1、选择退款原因
     * 2、提交退款
     * 3、刷新页面数据
     */
    orderRefund: function(e){
        console.log(e)
    },

    /**
     * 订单发货
     * -》 未发货订单
     * 1、选择物流厂家
     * 2、填入物流单号（可同时选择配送员）
     * 3、确定提交
     * 4、刷新页面数据
     * -》已发货订单
     * 1、选择配送员
     * 2、确定提交
     * 3、刷新页面数据
     */
    orderDelivery: function(e){
        console.log(e)
    },

    /**
     * 订单送达
     * -》已发货订单显示此按钮
     * 1、点击订单送达，弹出对话框，可输入送达备注（如放超市、送货上门、本人签收、代签收等文本），也可不输入备注默认为：已签收
     * 2、确定提交
     * 3、刷新页面数据
     */
    orderArrived: function(e){
        console.log(e)
    }
})