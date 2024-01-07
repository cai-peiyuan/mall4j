var config = require("config.js");

module.exports = Behavior({
  data: {
    sharedText: 'This is a piece of data shared between pages.',
    userInfo: null,
    avatarUrl: null,
    nickName: null,
    userMobile: null,
    theme: wx.getSystemInfoSync().theme,
  },
  methods: {
    getLoginedUserInfo: function () {
      this.data.sharedText === 'This is a piece of data shared between pages.';
      let userInfoInToken = wx.getStorageSync('userInfoInToken');
      console.log('userInfoInToken -> ', userInfoInToken);
      if (userInfoInToken) {
        this.setData({
          userMobile: userInfoInToken.userMobile,
          nickName: userInfoInToken.nickName,
          avatarUrl: userInfoInToken.pic
        });
      }
      return userInfoInToken;
    }
  }
})