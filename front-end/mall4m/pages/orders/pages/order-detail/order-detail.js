// pages/order-detail/order-detail.js

var http = require('../../../../utils/http.js');
var myBehavior = require('../../../../utils/my-behavior.js')

Page({
  behaviors: [myBehavior],
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
    order: {},
    userBalance: {},
    popupShowPayType: false,
    payType: 1
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
   * 关闭支付方式
   */
  closePopupPayType: function () {
    this.setData({
      popupShowPayType: false
    });
  },

  /**
   * 提交订单
   */
  toPay: function () {
    if (this.data.payType == 0 && this.data.userBalance.balance < this.data.actualTotal) {
      wx.showToast({
        title: '账户余额不足',
        icon: "none"
      });
      return;
    }
    if (this.data.payType == 1) {
      // 发起微信支付，传入平台订单编号
      this.calWeixinPay(this.data.orderNumber);
    } else if (this.data.payType == 0) {
      // 发起普通，传入平台订单编号
      this.callNormalPay(this.data.orderNumber);
    }
  },



  /**
   * 唤起微信支付
   * 通过后端接口，换取支付参数
   */
  calWeixinPay: function (orderNumbers) {
    let _this = this;
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
        wx.hideLoading();
        wx.requestPayment({
          timeStamp: res.timeStamp,
          nonceStr: res.nonceStr,
          package: res.packageStr,
          signType: "RSA",
          paySign: res.sign,
          success: e => {
            console.log("wx.requestPayment支付成功", e);
            wx.showToast({
              title: "微信支付成功",
              icon: "none"
            })
            _this.closePopupPayType();
            _this.loadOrderDetail(_this.data.orderNumber)
          },
          fail: err => {
            console.log("wx.requestPayment支付失败", err);
            wx.showToast({
              title: "微信支付失败" + err.errMsg,
              icon: "none"
            })
            _this.loadOrderDetail(_this.data.orderNumber)
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
          wx.showToast({
            title: "余额支付成功",
            icon: "none"
          })
          _this.closePopupPayType();
          _this.loadOrderDetail(_this.data.orderNumber)
        } else {
          console.log("余额支付失败", res);
          wx.showToast({
            title: "余额支付失败" + res,
            icon: "none"
          })
          _this.loadOrderDetail(_this.data.orderNumber)
        }
      }
    };
    http.request(params);
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
  //跳转商品详情页
  toProdPage: function (e) {
    var prodid = e.currentTarget.dataset.prodid;
    wx.navigateTo({
      url: '/pages/prods/pages/prod/prod?prodid=' + prodid,
    })
  },



  /**
   * 申请退款
   */
  onRefund: function (e) {
    console.log('申请退款', e)
    var ordernum = e.currentTarget.dataset.ordernum;
    var ths = this;

    wx.showModal({
        title: '',
        content: '已下单支付的订单请联系客服人员申请退款操作',
        confirmColor: "#eba524",
        success(res) {

        }
    });
    return ;
    
    wx.showModal({
      title: '',
      content: '尚未发货的订单支持无理由退款，是否申请退款？',
      confirmColor: "#eba524",
      success(res) {
        if (res.confirm) {
          wx.showLoading({
            mask: true
          });
          var params = {
            url: "/p/myOrder/refundApply/" + e.currentTarget.dataset.ordernum,
            method: "PUT",
            data: {},
            callBack: function (res) {
              ths.loadOrderDetail(ths.data.orderNumber)
              wx.hideLoading();
            }
          };
          http.request(params);
        } else if (res.cancel) {}
      }
    })
  },

  /**
   * 加入购物车
   */
  addToCart: function (event) {
    let index = event.currentTarget.dataset.index
    console.log('add to cart  item index -> ', index);
    // if (!this.data.orderItemDtos) {
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
        // wx.switchTab({
        //   url: '/pages/basket/basket',
        // })
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
   * 取消订单
   */
  onCancelOrder: function (e) {
    var ordernum = e.currentTarget.dataset.ordernum;
    var ths = this;
    wx.showModal({
      title: '',
      content: '要取消此订单？',
      confirmColor: "#3e62ad",
      cancelColor: "#3e62ad",
      cancelText: '否',
      confirmText: '是',
      success(res) {
        if (res.confirm) {
          wx.showLoading({
            mask: true
          });
          var params = {
            url: "/p/myOrder/cancel/" + ordernum,
            method: "PUT",
            data: {},
            callBack: function (res) {
              //console.log(res);
              ths.loadOrderDetail(ordernum);
              wx.hideLoading();
            }
          };
          http.request(params);
        } else if (res.cancel) {
          //console.log('用户点击取消')
        }
      }
    })
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
      data: {},
      callBack: function (data) {
        let orderShop = data.orderShop;
        let order = data.order;
        let userBalance = data.userBalance;
        ths.setData({
          orderNumber: orderNum,
          actualTotal: orderShop.actualTotal,
          userAddrDto: orderShop.userAddrDto,
          remarks: orderShop.remarks,
          orderItemDtos: orderShop.orderItemDtos,
          createTime: orderShop.createTime,
          status: orderShop.status,
          productTotalAmount: orderShop.actualTotal,
          transfee: orderShop.transfee,
          reduceAmount: orderShop.reduceAmount,
          actualTotal: orderShop.actualTotal,
          shopId: orderShop.shopId,
          order: order,
          userBalance: userBalance || {
            balance: 0,
            carNumber: ''
          }
        });
        wx.hideLoading();
      }
    };
    http.request(params);

  },

  /**
   * 删除订单
   * @param {*} e 
   */
  deleteOrder: function (e) {
    var ordernum = e.currentTarget.dataset.ordernum;
    var ths = this
    wx.showModal({
      title: '',
      content: '确定要删除此订单吗？',
      confirmColor: "#eba524",
      success(res) {
        if (res.confirm) {
          wx.showLoading();
          var params = {
            url: "/p/myOrder/" + ordernum,
            method: "DELETE",
            data: {},
            callBack: function (res) {
              wx.hideLoading();
              wx.navigateBack({
                delta: 1
              })
            }
          }
          http.request(params);
        } else if (res.cancel) {
          console.log('用户点击取消')
        }
      }
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
})