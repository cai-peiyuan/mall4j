// pages/submit-order/submit-order.js
const app = getApp();
var http = require("../../utils/http.js");
var myBehavior = require('../../utils/my-behavior.js')

Page({
  behaviors: [myBehavior],

  /**
   * 页面的初始数据
   */
  data: {
    popupShowPayType: false,
    popupShow: false,
    couponSts: 1,
    couponList: [],
    // 订单入口 0购物车 1立即购买
    orderEntry: "0",
    userAddr: null,
    orderItems: [],
    coupon: {
      totalLength: 0,
      canUseCoupons: [],
      noCanUseCoupons: []
    },
    actualTotal: 0,
    total: 0,
    totalCount: 0,
    transfee: 0,
    reduceAmount: 0,
    remark: "",
    couponIds: [],
    payType: 1,
    userBalance: {
      balance: 0,
      cardNumber: '',
    }, //用户余额信息
    imageUrl: app.globalData.imageUrl,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      orderEntry: options.orderEntry,
    });
    let userInfo = this.getLoginedUserInfo();
    let userMobile = this.data.userMobile;
    console.log('userMobile', userMobile)
    if (userMobile == null) {
      //{errMsg: "navigateTo:fail can not navigateTo a tabbar page"}errMsg: "navigateTo:fail can not navigateTo a tabbar page"__proto__: Object
      wx.switchTab({
        url: '/pages/user/user?sts=1',
      })
    }
  },

  //加载订单数据
  loadOrderData: function () {
    var addrId = 0;
    if (this.data.userAddr != null) {
      addrId = this.data.userAddr.addrId;
    }
    wx.showLoading({
      mask: true
    });
    var params = {
      url: "/p/order/confirm",
      method: "POST",
      data: {
        addrId: addrId,
        orderItem: this.data.orderEntry === "1" ? JSON.parse(wx.getStorageSync("orderItem")) : undefined,
        basketIds: this.data.orderEntry === "0" ? JSON.parse(wx.getStorageSync("basketIds")) : undefined,
        couponIds: this.data.couponIds,
        userChangeCoupon: 1
      },
      callBack: res => {
        wx.hideLoading();
        let orderItems = [];
        res.shopCartOrders[0].shopCartItemDiscounts.forEach(itemDiscount => {
          orderItems = orderItems.concat(itemDiscount.shopCartItems)
        })
        if (res.shopCartOrders[0].coupons) {
          let canUseCoupons = []
          let unCanUseCoupons = []
          res.shopCartOrders[0].coupons.forEach(coupon => {
            if (coupon.canUse) {
              canUseCoupons.push(coupon)
            } else {
              unCanUseCoupons.push(coupon)
            }
          })
          this.setData({
            coupons: {
              totalLength: res.shopCartOrders[0].coupons.length,
              canUseCoupons: canUseCoupons,
              unCanUseCoupons: unCanUseCoupons
            }
          })
        }
        this.setData({
          orderItems: orderItems,
          userBalance: res.userBalance || {
            balance: 0,
            cardNumber: ''
          },
          actualTotal: res.actualTotal,
          total: res.total,
          totalCount: res.totalCount,
          userAddr: res.userAddr,
          transfee: res.shopCartOrders[0].transfee,
          shopReduce: res.shopCartOrders[0].shopReduce,
        });
      },
      errCallBack: res => {
        wx.hideLoading();
        this.chooseCouponErrHandle(res)
      }
    };
    http.request(params);

  },

  /**
   * 优惠券选择出错处理方法
   */
  chooseCouponErrHandle(res) {
    // 优惠券不能共用处理方法
    if (res.statusCode == 601) {
      wx.showToast({
        title: res.data,
        icon: "none",
        duration: 3000,
        success: res => {
          this.setData({
            couponIds: []
          })
        }
      })
      setTimeout(() => {
        this.loadOrderData();
      }, 2500)
    }
  },

  onRemarksInput: function (e) {
    this.setData({
      remarks: e.detail.value
    });
  },

  /**
   * 提交订单
   */
  toPay: function () {
    // 检查地址选择情况
    if (!this.data.userAddr) {
      wx.showToast({
        title: '请选择地址',
        icon: "none"
      });
      return;
    }
    if (this.data.payType == 0 && this.data.userBalance.balance < this.data.actualTotal) {
      wx.showToast({
        title: '账户余额不足',
        icon: "none"
      });
      return;
    }
    // 提交订单
    this.submitOrder();
  },


  /**
   * 提交订单，平台内部订单编号
   * 通过内部订单编号发起微信支付请求
   */
  submitOrder: function () {
    wx.showLoading({
      mask: true
    });
    var params = {
      url: "/p/order/submit",
      method: "POST",
      data: {
        orderShopParam: [{
          remarks: this.data.remark,
          shopId: 1
        }]
      },
      callBack: res => {
        wx.hideLoading();
        if (this.data.payType == 1) {
          // 发起微信支付，传入平台订单编号
          this.calWeixinPay(res.orderNumbers);
        } else if (this.data.payType == 0) {
          // 发起普通，传入平台订单编号
          this.callNormalPay(res.orderNumbers);
        }
      }
    };
    http.request(params);
  },

  /**
   * 唤起微信支付
   * 通过后端接口，换取支付参数
   */
  calWeixinPay: function (orderNumbers) {
    wx.showLoading({
      mask: true
    });
    var params = {
      url: "/p/order/weChatPay",
      method: "POST",
      data: {
        payType: this.data.payType,
        orderNumbers: orderNumbers
      },
      callBack: function (res) {
        let _this = this;
        wx.hideLoading();
        wx.requestPayment({
          timeStamp: res.timeStamp,
          nonceStr: res.nonceStr,
          package: res.packageStr,
          signType: "RSA",
          paySign: res.sign,
          success: e => {
            console.log("wx.requestPayment支付成功", e);
            //_this.closePopupPayType();
            wx.navigateTo({
              url: '/pages/pay-result/pay-result?sts=1&orderNumbers=' + orderNumbers + "&orderType=" + this.data.orderType,
            })
          },
          fail: err => {
            console.log("wx.requestPayment支付失败", err);
            //_this.closePopupPayType();
            wx.navigateTo({
              url: '/pages/pay-result/pay-result?sts=0&orderNumbers=' + orderNumbers + "&orderType=" + this.data.orderType,
            })
          }
        })
      }
    };
    http.request(params);
  },

  /**
   * 使用余额支付订单
   */
  callNormalPay: function (orderNumbers) {
    let _this = this;
    wx.showLoading({
      mask: true
    });
    var params = {
      url: "/p/order/normalPay",
      method: "POST",
      data: {
        payType: this.data.payType,
        orderNumbers: orderNumbers
      },
      callBack: function (res) {
        console.log("余额支付结果", res);
        wx.hideLoading();
        if (res.success) {
          console.log("余额支付成功", res);
          _this.closePopupPayType();
          wx.reLaunch({
            url: '/pages/pay-result/pay-result?sts=1&orderNumbers=' + orderNumbers + "&orderType=" + this.data.orderType,
          })
        } else {
          console.log("余额支付失败", res);
          _this.closePopupPayType();
          wx.reLaunch({
            url: '/pages/pay-result/pay-result?sts=0&orderNumbers=' + orderNumbers + "&orderType=" + this.data.orderType,
          })
        }
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
    var pages = getCurrentPages();
    var currPage = pages[pages.length - 1];
    if (currPage.data.selAddress == "yes") {
      this.setData({ //将携带的参数赋值
        userAddr: currPage.data.item
      });
    }
    //获取订单数据
    this.loadOrderData();
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

  changeCouponSts: function (e) {
    this.setData({
      couponSts: e.currentTarget.dataset.sts
    });
  },

  /**
   * 切换支付方式
   * @param {*} e
   */
  changePayType: function (e) {
    this.setData({
      payType: e.currentTarget.dataset.sts
    });
  },

  /**
   * 显示优惠券
   */
  showCouponPopup: function () {
    this.setData({
      popupShow: true
    });
  },

  /**
   * 显示支付方式
   */
  showPayTypePopup: function () {
    this.setData({
      popupShowPayType: true
    });
  },


  /**
   * 选择支付方式
   */
  choosePayType: function () {
    this.setData({
      popupShowPayType: true
    });
  },

  /**
   * 关闭支付方式
   */
  closePopupPayType: function () {
    this.setData({
      popupShowPayType: false
    });
  },

  /**
   * 关闭优惠券
   */
  closePopup: function () {
    this.setData({
      popupShow: false
    });
  },

  /**
   * 去地址页面
   */
  toAddrListPage: function () {
    wx.navigateTo({
      url: '/pages/address/pages/delivery-address/delivery-address?order=0',
    })
  },
  /**
   * 确定选择好的优惠券
   */
  choosedCoupon: function () {
    this.loadOrderData();
    this.setData({
      popupShow: false
    });
  },

  /**
   * 优惠券子组件发过来
   */
  checkCoupon: function (e) {
    var ths = this;
    let index = ths.data.couponIds.indexOf(e.detail.couponId);
    if (index === -1) {
      ths.data.couponIds.push(e.detail.couponId)
    } else {
      ths.data.couponIds.splice(index, 1)
    }
  }
})