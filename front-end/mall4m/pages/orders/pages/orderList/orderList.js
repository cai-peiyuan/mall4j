const app = getApp();
var http = require('../../../../utils/http.js');
var config = require('../../../../utils/config.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    loading: false,
    list: [],
    current: 1,
    pages: 0,
    sts: 0,
    popupShowPayType: false,
    imageUrl: app.globalData.imageUrl,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    console.log('订单列表', options);
    if (options.sts) {
      this.setData({
        sts: options.sts
      });
      this.loadOrderData(options.sts, 1);
    } else {
      this.loadOrderData(0, 1);
    }
  },

  /**
   * 加载订单数据
   */
  loadOrderData: function (sts, current) {
    var ths = this;
    if (ths.data.loading == true) {
      return;
    }
    ths.setData({
      loading: true
    });

    wx.showLoading();
    //加载订单列表
    var params = {
      url: "/p/myOrder/myOrder",
      method: "GET",
      data: {
        current: current,
        size: 10,
        status: sts,
      },
      callBack: function (res) {
        //console.log(res);
        var list = [];
        if (res.current == 1) {
          list = res.records;
        } else {
          list = ths.data.list;
          Array.prototype.push.apply(list, res.records);
        }
        ths.setData({
          list: list,
          pages: res.pages,
          current: res.current,
          loading: false
        });
        wx.hideLoading();
      }
    };
    http.request(params);
  },

  /**
   * 状态点击事件
   */
  onStsTap: function (e) {
    var sts = e.currentTarget.dataset.sts;
    this.setData({
      sts: sts
    });
    this.loadOrderData(sts, 1);
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
    //用于在订单详情页返回到此页面时刷新数据使用
    this.loadOrderData(this.data.sts, 1);
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
    this.loadOrderData(this.data.sts, 1);
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    if (this.data.current < this.data.pages) {
      this.loadOrderData(this.data.sts, this.data.current + 1);
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  },


  /**
   * 查看物流
   */
  toDeliveryPage: function (e) {
    wx.navigateTo({
      url: '/pages/express-delivery/express-delivery?orderNum=' + e.currentTarget.dataset.ordernum
    })
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
              ths.loadOrderData(ths.data.sts, 1);
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
              ths.loadOrderData(ths.data.sts, 1);
              wx.hideLoading();
            }
          };
          http.request(params);
        } else if (res.cancel) {
        }
      }
    })

    // wx.navigateTo({
    //     url: '/pages/orders/pages/order-refund/order-refund?orderNum=' + ordernum,
    // })
  },


  /**
   * 微信付款
   */
  onPayAgain: function (e) {
    this.showPayTypePopup();
    this.toOrderDetailPage(e);
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
   * 微信付款
   */
  calWeixinPay: function (orderNumbers) {
    wx.showLoading({
      mask: true
    });
    var params = {
      url: "/p/order/weChatPay",
      method: "POST",
      data: {
        payType: 1,
        orderNumbers: orderNumbers
      },
      callBack: res => {
        wx.hideLoading();
        wx.requestPayment({
          timeStamp: res.timeStamp,
          nonceStr: res.nonceStr,
          package: res.packageStr,
          signType: "RSA",
          paySign: res.sign,
          success: function (e) {
            console.log("wx.requestPayment支付成功", e);
            wx.navigateTo({
              url: '/pages/pay-result/pay-result?sts=1&orderNumbers=' + orderNumbers,
            })
          },
          fail: function (err) {
            console.log("wx.requestPayment支付失败", err);
            //这里模拟跳转到支付结果页面
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
   * 查看订单详情
   */
  toOrderDetailPage: function (e) {
    wx.navigateTo({
      url: '/pages/orders/pages/order-detail/order-detail?orderNum=' + e.currentTarget.dataset.ordernum,
    })
  },

  /**
   * 确认收货
   */
  onConfirmReceive: function (e) {
    var ths = this;
    wx.showModal({
      title: '到货确认',
      content: '确定已收到货？',
      confirmColor: "#f38d08",
      success(res) {
        if (res.confirm) {
          wx.showLoading({
            mask: true
          });
          var params = {
            url: "/p/myOrder/receipt/" + e.currentTarget.dataset.ordernum,
            method: "PUT",
            data: {},
            callBack: function (res) {
              //console.log(res);
              ths.loadOrderData(ths.data.sts, 1);
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
  //删除已完成||已取消的订单
  delOrderList: function (e) {
    var ths = this
    wx.showModal({
      title: '',
      content: '确定要删除此订单吗？',
      confirmColor: "#eba524",
      success(res) {
        if (res.confirm) {
          var ordernum = e.currentTarget.dataset.ordernum;
          wx.showLoading();
          var params = {
            url: "/p/myOrder/" + ordernum,
            method: "DELETE",
            data: {},
            callBack: function (res) {
              ths.loadOrderData(ths.data.sts, 1);
              wx.hideLoading();
            }
          }
          http.request(params);
        } else if (res.cancel) {
          console.log('用户点击取消')
        }
      }
    })
  }
})