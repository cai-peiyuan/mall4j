// pages/binding-phone/binding-phone.js

var http = require("../../../../utils/http.js");
var config = require("../../../../utils/config.js");
var myBehavior = require('../../../../utils/my-behavior.js')

Page({
  behaviors: [myBehavior],
  /**
   * 页面的初始数据
   */
  data: {
    phonenum:'',
    code:'',
    sendTelCodeBtnText : '发送验证码',
    sendCodeStatus : 0,
    sendLeftTime : 60,
    sending : 0,
    sendLeftTimeIntervalFunc : null,
    getting : 0,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let userMobile = this.data.userMobile;
    this.setData({
      phonenum: userMobile
    })
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

  getCodeNumber:function(){
      let _this = this;
    if (!this.data.phonenum) {
      wx.showToast({
        title: '请输入手机号',
        icon: "none"
      })
      return;
    }
    var params = {
      url: "/p/sms/send",
      method: "POST",
      data: {
        // phonenum: this.data.phonenum,
        // code: this.data.code
        mobile: this.data.phonenum
      },
      callBack: (res) => {
        wx.showToast({
            title: '验证码已发送',
            icon: "none"
          })
        _this.sendCodeStatus = 1;
        _this.sendLeftTimeIntervalFunc = setInterval(function(){
            _this.sendLeftTime = _this.sendLeftTime - 1;
            _this.sendTelCodeBtnText = "("+_this.sendLeftTime+")秒后重发";
            if(_this.sendLeftTime == 0 && _this.sendLeftTimeIntervalFunc ){
                clearInterval(_this.sendLeftTimeIntervalFunc);
                _this.sendTelCodeBtnText = '发送验证码';
                _this.sending = 0;
                //news.sendCodeStatus = 0;
            }
        },1000);

      }
    };
    http.request(params);
  },
  onPhoneInput:function(e){
    this.setData({
      phonenum: e.detail.value
    });
  },
  onCodeInput: function (e) {
    this.setData({
      code: e.detail.value
    });
  },
  /**
   * 绑定
   */
  bindMobile() {
      let _this = this;
    var params = {
      // url: '/user/registerOrBindUser',
      url: '/p/user/bindUserPhoneNum',
      method: 'PUT',
      data: {
        appType: 1, // 微信小程序
        mobile: this.data.phonenum,
        validCode: this.data.code,
        validateType: 1, // 验证类型:1验证码验证 ,
        registerOrBind: 2 // 验证类型 1注册 2绑定
      },
      callBack: res => {
        /*uni.navigateTo({
          url: '/pages/index/index'
        });*/
        if (res == null || res == '') {
        _this.setData({
            userMobile: _this.data.phonenum
        });
          wx.showModal({
            title: '提示',
            content: '绑定成功',
            showCancel: false,
            success(res) {
              if (res.confirm) {
                console.log('用户点击确定')
                wx.navigateBack({
                  delta: 1
                })
              } else if (res.cancel) {
                console.log('用户点击取消')
              }
            }
          });
        } else {
          wx.showModal({
            title: '提示',
            content: res,
            showCancel: false,
            success(res) {
              if (res.confirm) {
                console.log('用户点击确定')
              } else if (res.cancel) {
                console.log('用户点击取消')
              }
            }
          });
        }
      },
    }
    http.request(params)
  }
})
