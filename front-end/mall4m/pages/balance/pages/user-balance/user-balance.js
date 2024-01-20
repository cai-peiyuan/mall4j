// pages/user-balance/user-balance.js
// pages/user/user.js

var http = require("../../../../utils/http.js");
var util = require("../../../../utils/util.js");
var myBehavior = require('../../../../utils/my-behavior.js')
var config = require("../../../../utils/config.js");
Page({

    behaviors: [myBehavior],
    /**
     * 页面的初始数据
     */
    data: {
        activeTab: 0,
        cardIndex: 0,
        cardMoney: 100,
        userBalance: {
            "userId": "o7hh869v8oVzVJ8UEUPorTqeMI8",
            "balance": 0.00,
            "updateTime": "",
            "credits": 0,
            "cardNumber": ""
       },
        userBalanceSells: [
            {
                 "id": 1,
                 "storedValue": 100,
                 "sellValue": 95,
                 "status": null
            },
            {
                 "id": 2,
                 "storedValue": 200,
                 "sellValue": 190,
                 "status": null
            },
            {
                 "id": 3,
                 "storedValue": 300,
                 "sellValue": 285,
                 "status": null
            },
            {
                 "id": 4,
                 "storedValue": 400,
                 "sellValue": 370,
                 "status": null
            },
            {
                 "id": 5,
                 "storedValue": 500,
                 "sellValue": 460,
                 "status": null
            },
            {
                 "id": 6,
                 "storedValue": 1000,
                 "sellValue": 900,
                 "status": null
            },
            {
                 "id": 7,
                 "storedValue": 1,
                 "sellValue": 1,
                 "status": null
            }
       ],
    },

    /**
     * 余额明细页面
     * @param {} e 
     */
    toUserBalanceDetailPage: function (e) {
        wx.navigateTo({
            url: '/pages/balance/pages/user-balance-detail/user-balance-detail',
        })
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        //获取账户余额喝售卖储值卡
        this.getUserBalanceAndSell();
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    },

    //修改tab标签 在线储值 or 储值券
    changeActiveTab(e) {
        // console.log(index);
        let index = e.currentTarget.dataset['index'];
        this.setData({
            activeTab: index
        })
    },

    //修改充值面额
    changeCard(e) {
        let index = e.currentTarget.dataset['index'];
        let money = e.currentTarget.dataset["money"];
        if (index != this.cardIndex) {
            this.setData({
                cardIndex: index
            });
            this.setData({
                cardMoney: money
            });
        }
    },

    /**
     * 加载账户余额
     */
    getUserBalanceAndSell() {
        //加载订单数字
        var ths = this;
        // var status = ths.data.status
        wx.showLoading();
        var params = {
            url: "/p/balance/userBalanceAndSell",
            method: "GET",
            data: {},
            callBack: function (res) {
                wx.hideLoading();
                ths.setData({
                    userBalance: res.userBalance,
                    userBalanceSells: res.userBalanceSells
                })
            }
        };
        http.request(params);
    },

    /**
     * 储值卡充值
     */
    chargeByCode() {
        wx.showToast({
            title: '券码不正确',
            icon: "none"
        })
    }
})