// pages/user/user.js

var http = require("../../utils/http.js");
var util = require("../../utils/util.js");
var myBehavior = require('../../utils/my-behavior.js')
var config = require("../../utils/config.js");
Page({
  behaviors: [myBehavior],
  /**
   * 页面的初始数据
   */
  data: {
    userBalance: {},
    orderAmount: '',
    sts: '',
    collectionCount: 0,
    openSource: true,
    newNickName: '',
    phonenum: '',
    closeEye: false,
    wxUserInfo: {},
    userBalance: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let userInfo = this.getLoginedUserInfo();
    let userMobile = this.data.userMobile;
    this.setData({
      phonenum: userMobile
    });
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
    //加载订单数字
    var ths = this;
    // var status = ths.data.status
    wx.showLoading();
    //从接口加载用户信息
    ths.loadUserInfo();
    // var params = {
    //   url: "/p/myOrder/orderCount",
    //   method: "GET",
    //   data: {},
    //   callBack: function (res) {
    //     wx.hideLoading();
    //     ths.setData({
    //       orderAmount: res
    //     });
    //   }
    // };
    // http.request(params);
    //this.showCollectionCount();
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
   * 选择用户头像
   */
  onChooseAvatar(e) {
    const {
      avatarUrl
    } = e.detail;
    console.log(e, e.detail, avatarUrl);
    this.updateAvatarUrl(avatarUrl);
    this.setData({
      avatarUrl: avatarUrl,
    })
  },

  /**
   * 从小程序接口获取用户详细信息 
   * 加载个人页面的信息
   */
  loadUserInfo() {
    let ths = this;
    var params = {
      url: "/p/user/wxAppUserInfo",
      method: "GET",
      data: {},
      callBack: function (res) {
        wx.hideLoading();
        ths.setData({
          wxUserInfo: res.userInfo,
          userBalance: res.userBalance,
          orderAmount: res.orderCountData,
          collectionCount: res.collectionCount,
        });
        ths.setData({
          avatarUrl: res.pic,
          nickName: res.nickName,
          userMobile: res.userMobile
        });
      }
    };
    http.request(params);
  },
  /**
   * 打开隐藏显示手机号
   */
  changeEye() {
    this.setData({
      closeEye: !this.data.closeEye
    })
    console.log(this.data.closeEye)
  },

  /**
   * 更新用户输入的用户名
   * @param {} e 
   */
  setInputValue: function (e) {
    console.log("写入：", e.detail.value);
    let newNickName = e.detail.value;
    if (newNickName != '' && this.data.nickName != newNickName) {
      console.log("更新用户名称", newNickName);
      //通过接口更新用户名称
      this.updateNickName(newNickName);
    }
  },
  /**
   * 更新用户头像
   * @param {} avatarUrl
   */
  updateAvatarUrl(avatarUrl) {
    let _this = this;
    wx.uploadFile({
      url: `${config.domain}/p/user/uploadAvatarImg`,
      filePath: avatarUrl,
      name: 'file',
      header: {
        'Authorization': wx.getStorageSync('token')
      },
      success(res) {
        console.log('图片上传成功回调', res.data)
        _this.setData({
          avatarUrl: res.data.data
        });
        let userInfoInToken = wx.getStorageSync('userInfoInToken');
        userInfoInToken.pic = res.data;
        wx.setStorageSync('userInfoInToken', userInfoInToken);
      },
      fail(err) {
        console.log('图片上传失败回调', err)
      }
    })
  },
  /**
   * 更新用户名称
   */
  updateNickName(newNickName) {
    let _this = this;
    let avatarUrl = this.data.avatarUrl;
    http.request({
      url: "/p/user/setUserInfo",
      method: "PUT",
      data: {
        avatarUrl: avatarUrl,
        nickName: newNickName
      },
      callBack: function (res) {
        _this.setData({
          nickName: newNickName
        });
        let userInfoInToken = wx.getStorageSync('userInfoInToken');
        userInfoInToken.nickName = newNickName;
        wx.setStorageSync('userInfoInToken', userInfoInToken);
      }
    });
  },

  toDistCenter: function () {
    wx.showToast({
      icon: "none",
      title: '该功能未开源'
    })
  },

  /**
   * 跳转领券中心页面
   */
  toCouponCenter: function () {
    // wx.showToast({
    //   icon: "none",
    //   title: '即将上线'
    // })
    wx.navigateTo({
      url: '/pages/user-info/pages/ticketCenter/ticketCenter',
    })
  },

  /**
   * 跳转到我的优惠券页面
   */
  toMyCouponPage: function () {
    wx.navigateTo({
      url: '/pages/user-info/pages/myTicket/myTicket',
    })
  },

  /**
   * 跳转到我的收获地址页面
   */
  toAddressList: function () {
    wx.navigateTo({
      url: '/pages/address/pages/delivery-address/delivery-address',
    })
  },

  /**
   * 关于我们
   */
  toAboutUsPage: function () {
    wx.navigateTo({
      url: '/pages/news/pages/news-detail/news-detail?id=1',
    })
  },

  // 跳转绑定手机号
  toBindingPhone: function () {
    return;
    // wx.navigateTo({
    //   url: '/pages/user-info/pages/binding-phone/binding-phone',
    // })
  },

  /**
   * 加密手机号
   * @param {} theMobile 
   */
  desensitiveMobile: function (theMobile) {
    console.log('加密手机号', theMobile);
    return theMobile;
  },
  /**
   * 退出登录
   */
  logout: function () {
    // 请求退出登陆接口
    http.request({
      url: '/logOut',
      method: 'post',
      callBack: res => {
        util.removeTabBadge()
        wx.removeStorageSync('loginResult');
        wx.removeStorageSync('token');
        // this.$Router.pushTab('/pages/index/index')
        wx.showToast({
          title: "退出成功",
          icon: "none"
        })
        this.setData({
          orderAmount: ''
        });
        setTimeout(() => {
          wx.switchTab({
            url: "/pages/index/index"
          })
        }, 1000)
      }
    })
  },

  /**
   * 同步用户输入的昵称
   * @param {} e 
   */
  syncInputValue(e) {
    console.log("输入：", e.detail.value);
    let newNickName = e.detail.value;
    this.setData({
      newNickName: newNickName
    });
  },

  /**
   * 跳转订单列表页面
   * @param {} e 
   */
  toOrderListPage: function (e) {
    var sts = e.currentTarget.dataset.sts;
    wx.navigateTo({
      url: '/pages/orders/pages/orderList/orderList?sts=' + sts,
    })
  },

  /**
   * 跳转余额页面
   * @param {} e 
   */
  toUserBalancePage: function (e) {
    console.log(1234);
    wx.navigateTo({
      url: '/pages/balance/pages/user-balance/user-balance',
    })
  },
  /**
   * 查询所有的收藏量
   */
  showCollectionCount: function () {
    var ths = this;
    wx.showLoading();
    var params = {
      url: "/p/user/collection/count",
      method: "GET",
      data: {},
      callBack: function (res) {
        wx.hideLoading();
        ths.setData({
          collectionCount: res
        });
      }
    };
    http.request(params);
  },

  /**
   * 我的收藏跳转
   */
  myCollectionHandle: function () {
    var url = '/pages/prods/pages/prod-classify/prod-classify?sts=5';
    var id = 0;
    var title = "商品收藏";
    if (id) {
      url += "&tagid=" + id + "&title=" + title;
    }
    wx.navigateTo({
      url: url
    })
  },

  /**
   * 获取用户的手机号
   * @param {} e 
   */
  getPhoneNumber: function (e) {
    var that = this;
    console.log('获取用户手机号', e)
    if (e.detail.errMsg == 'getPhoneNumber:ok') {
      /**
       * detail: code: "0b3f7cf7302e9245ed5a4998a69c804ba1b9de31baef938821bacb106d2f9a77"
        encryptedData: "ME3pnlirrd2z4+yHMeaPC3hO1JH2HBkahOO+dSOJmpywsTTqmDLZuPrHTPJzqa/nH1375CoiWPT3wC+V6mLxa07/HzIAwMH/eudhboRoeMvFb5mfHkJVAqxXEWRHOwjQeETs2aHMEEsARCrjzNMNXBDGj1p9qrgoVtFUaAg+rPFy985aa1uDjqaNjj+xHR/A7fQqp5zUIvFRM+nJqYp77w=="
        errMsg: "getPhoneNumber:ok"
        iv: "PS5v8/xdITknqTnlbPmIQQ=="
       */
      var params = {
        url: "/p/user/getWxPhoneNumber",
        method: "POST",
        data: e.detail,
        callBack: function (res) {
          console.log('获取小程序用户手机号', res);
          http.getToken();
          //that.loadUserInfo();
          setTimeout(() => {
            wx.navigateTo({
              url: '/pages/user/user?sts=1',
            })
          }, 1000)
        }
      };
      http.request(params);
    }
  }
})