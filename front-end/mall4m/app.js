//app.js
var http = require("utils/http.js");
var http = require("./utils/http.js");
let config = require("./utils/config");

App({
  onLaunch: function () {
    console.log('mall4j.v230313')
    // http.getToken();
    // wx.getSetting({
    //   success(res) {
    //     if (!res.authSetting['scope.userInfo']) {
    //       wx.navigateTo({
    //         url: '/pages/login/login',
    //       })
    //     }
    //   }
    // })

    wx.onThemeChange((result) => {
      this.setData({
        theme: result.theme
      })
    });
    http.getToken();
    wx.getSetting({
      success(res) {
        if(config.debug){
          console.log('wx.getSetting', res);
        }
        // if (!res.authSetting['scope.userInfo']) {
        //   wx.navigateTo({
        //     url: '/pages/login/login',
        //   })
        // }
      }
    })
  },
  globalData: {
    // 定义全局请求队列
    requestQueue: [],
    // 是否正在进行登陆
    isLanding: true,
    // 购物车商品数量
    totalCartCount: 0
  }
})