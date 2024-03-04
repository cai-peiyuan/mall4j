//app.js
var http = require("./utils/http.js");
let config = require("./utils/config");

App({
  onLaunch: function () {
    const that = this;
    // 检测新版本
    const updateManager = wx.getUpdateManager()
    updateManager.onUpdateReady(function () {
      wx.showModal({
        title: '更新提示',
        content: '新版本已经准备好，是否重启应用？',
        success(res) {
          if (res.confirm) {
            // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
            updateManager.applyUpdate()
          }
        }
      })
    })

    /**
     * 初次加载判断网络情况
     * 无网络状态下根据实际情况进行调整
     */
    wx.getNetworkType({
      success(res) {
        const networkType = res.networkType
        if (networkType === 'none') {
          that.globalData.isConnected = false
          wx.showToast({
            title: '当前无网络',
            icon: 'loading',
            duration: 2000
          })
        }
      }
    });

    /**
     * 监听网络状态变化
     * 可根据业务需求进行调整
     */
    wx.onNetworkStatusChange(function (res) {
      if (!res.isConnected) {
        that.globalData.isConnected = false
        wx.showToast({
          title: '网络已断开',
          icon: 'loading',
          duration: 2000
        })
      } else {
        that.globalData.isConnected = true
        wx.hideToast()
      }
    })


    // ---------------检测navbar高度
    let menuButtonObject = wx.getMenuButtonBoundingClientRect();
    console.log("小程序胶囊信息", menuButtonObject)
    wx.getSystemInfo({
      success: res => {
        let statusBarHeight = res.statusBarHeight,
          navTop = menuButtonObject.top, //胶囊按钮与顶部的距离
          navHeight = statusBarHeight + menuButtonObject.height + (menuButtonObject.top - statusBarHeight) * 2; //导航高度
        this.globalData.navHeight = navHeight;
        this.globalData.navTop = navTop;
        this.globalData.windowHeight = res.windowHeight;
        this.globalData.menuButtonObject = menuButtonObject;
        console.log("navHeight", navHeight);
      },
      fail(err) {
        console.log(err);
      }
    })

    wx.onThemeChange((result) => {
      this.setData({
        theme: result.theme
      })
    });

    wx.getSetting({
      success(res) {
        console.log('wx.getSetting', res);
      }
    })
  },

  onShow(e) {
    // 保存邀请人
    if (e && e.query && e.query.inviter_id) {
      wx.setStorageSync('referrer', e.query.inviter_id)
      if (e.shareTicket) {
        wx.getShareInfo({
          shareTicket: e.shareTicket,
          success: res => {
            wx.login({
              success(loginRes) {
                if (loginRes.code) {
                  WXAPI.shareGroupGetScore(
                    loginRes.code,
                    e.query.inviter_id,
                    res.encryptedData,
                    res.iv
                  ).then(_res => {
                    console.log(_res)
                  }).catch(err => {
                    console.error(err)
                  })
                } else {
                  console.error('登录失败！' + loginRes.errMsg)
                }
              }
            })
          }
        })
      }
    }

    http.getToken();

  },

  initNickAvatarUrlPOP(_this) {
    setTimeout(() => {
      let app = getApp();
      console.log(app.globalData)
      console.log(app.globalData.apiUserInfoMap)
      if (app.globalData.apiUserInfoMap && (!app.globalData.apiUserInfoMap.nickName || !app.globalData.apiUserInfoMap.pic)) {
        _this.setData({
          nickPopShow: true,
          popnick: app.globalData.apiUserInfoMap.nickName ? app.globalData.apiUserInfoMap.nickName : '',
          popavatarUrl: app.globalData.apiUserInfoMap.pic ? app.globalData.apiUserInfoMap.pic : '',
        })
      }
    }, 2000) // 3秒后弹出
  },

  globalData: {
    theme: '',
    // 定义全局请求队列
    requestQueue: [],
    // 是否正在进行登陆
    isLanding: true,
    // 购物车商品数量
    totalCartCount: 0,
    imageUrl: config.imageDomain,
    isConnected: true,
    sdkAppID: config.sdkAppID,
    apiUserInfoMap: undefined, // 当前登陆用户信息: base/ext/idcard/saleDistributionTeam
  }
})