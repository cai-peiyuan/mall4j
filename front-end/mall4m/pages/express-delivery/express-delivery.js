// pages/express-delivery/express-delivery.js

var http = require('../../utils/http.js');
Page({
  /**
   * 页面的初始数据
   */
  data: {
    companyName: "商家配送",
    dvyFlowId: "123123123",
    deliveryRoutes: [], //订单物流路由信息,
    deliveryOrder: {},
    dvyData: [{
      context: "派送员已取快递",
      ftime: "ftime",
      location: "location",
      time: "2024年1月27日 18点35分"
    }, {
      context: "派送员已取快递",
      ftime: "ftime",
      location: "location",
      time: "2024年1月27日 18点35分"
    }]
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var ths = this;
    wx.showLoading();
    var params = {
      url: "/delivery/check/" + options.orderNum,
      method: "GET",
      data: {
        // orderNumber: options.orderNum
      },
      callBack: function (res) {
        //console.log(res);
        ths.setData({
          companyName: res.companyName,
          dvyFlowId: res.dvyFlowId,
          dvyData: res.data,
          deliveryOrder: res.deliveryOrder,
          deliveryRoutes: res.deliveryRoutes
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
  /**
   * 给快递员打电话
   * @param {*} e 
   */
  callDeliveryUser: function (e) {
console.log(e)
    const phoneNumber = e.target.dataset.expressphone;
    console.log('电话号码', phoneNumber)
    wx.makePhoneCall({
      phoneNumber: phoneNumber
    })
  }
})