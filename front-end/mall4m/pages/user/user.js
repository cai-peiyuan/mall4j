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
    orderAmount: '',
    sts: '',
    collectionCount: 0,
    openSource: true,
    newNickName: '',
    closeEye:true,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    let userInfo = this.getLoginedUserInfo();
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {

    //加载订单数字
    var ths = this;
    // var status = ths.data.status
    wx.showLoading();
    var params = {
      url: "/p/myOrder/orderCount",
      method: "GET",
      data: {},
      callBack: function(res) {
        wx.hideLoading();
        ths.setData({
          orderAmount: res
        });
      }
    };
    http.request(params);
    this.showCollectionCount();
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {

  },

  /**
   * 获取用户详细信息
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
  changeEye(){
      this.setData({closeEye:!this.data.closeEye})
        console.log(this.data.closeEye)
  },
  setInputValue: function (e) {
    console.log("写入：", e.detail.value);
    let newNickName = e.detail.value;
    if (newNickName != '' && this.data.nickName != newNickName) {
      console.log("更新用户名称", newNickName);
      this.updateNickName(newNickName);
    }
  },
  /**
   * 更新用户头像
   * @param {}} avatarUrl
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

  toCouponCenter: function() {
    wx.showToast({
      icon: "none",
      title: '即将上线'
    })
  },

  toMyCouponPage: function() {
    wx.showToast({
      icon: "none",
      title: '即将上线'
    })
  },

  toAddressList: function() {
    wx.navigateTo({
      url: '/pages/address/pages/delivery-address/delivery-address',
    })
  },

  /**
   * 关于我们
   */
  toAboutUsPage: function() {
    wx.navigateTo({
        url: '/pages/news/pages/news-detail/news-detail?id=1',
      })
  },

  // 跳转绑定手机号
  toBindingPhone: function() {
    wx.navigateTo({
      url: '/pages/user-info/pages/binding-phone/binding-phone',
    })
  },

  /**
 * 退出登录
 */
  logout: function() {
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
  toOrderListPage: function(e) {
    var sts = e.currentTarget.dataset.sts;
    wx.navigateTo({
      url: '/pages/orders/pages/orderList/orderList?sts=' + sts,
    })
  },

  /**
   * 跳转余额页面
   * @param {} e 
   */
  toUserBalancePage: function(e){
      console.log(1234);
    wx.navigateTo({
        url: '/pages/balance/pages/user-balance/user-balance',
      })
  },
  /**
   * 查询所有的收藏量
   */
  showCollectionCount: function() {
    var ths = this;
    wx.showLoading();
    var params = {
      url: "/p/user/collection/count",
      method: "GET",
      data: {},
      callBack: function(res) {
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
  myCollectionHandle: function() {
    var url = '/pages/prods/pages/prod-classify/prod-classify?sts=5';
    var id = 0;
    var title = "我的收藏商品";
    if (id) {
      url += "&tagid=" + id + "&title=" + title;
    }
    wx.navigateTo({
      url: url
    })
  }


})