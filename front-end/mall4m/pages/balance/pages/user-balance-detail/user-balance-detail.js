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
        tabIndex: 0,
        userBalanceDetail: [],
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {
        this.getUserBalanceDetail();
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
        console.log(111);
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    },
    //修改tab标签 近一个月 or 全部
    changeTab(e) {
        // console.log(index);
        let index = e.currentTarget.dataset['index'];
        if (index !== this.tabIndex) {
            this.setData({
                tabIndex: index
            })
        }
    },

    /**
     * 获取用户余额明细
     */
    getUserBalanceDetail() {
        var ths = this;
        wx.showLoading();
        var params = {
            url: "/p/balance/userBalanceDetail",
            method: "GET",
            data: {},
            callBack: function (res) {
                wx.hideLoading();
                ths.setData({
                    userBalanceDetail: res.userBalanceDetail
                })
            }
        };
        http.request(params);
    }
})