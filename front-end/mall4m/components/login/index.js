const WXAPI = require('apifm-wxapi')
const app = getApp();
var config = require("../../utils/config.js");
var http = require("../../utils/http.js");

Component({
  options: {
    // 样式隔离 https://developers.weixin.qq.com/miniprogram/dev/framework/custom-component/wxml-wxss.html#%E7%BB%84%E4%BB%B6%E6%A0%B7%E5%BC%8F%E9%9A%94%E7%A6%BB
    styleIsolation: 'apply-shared',
  },
  /**
   * 组件的对外属性，是属性名到属性设置的映射表
   */
  properties: {
    avatarUrl: String,
    name: String,
    show: Boolean,
  },

  /**
   * 组件的内部数据，和 properties 一同用于组件的模板渲染
   */
  data: {
    nick: undefined
  },
  // 组件数据字段监听器，用于监听 properties 和 data 的变化
  observers: {

  },
  /**
   * 组件生命周期声明对象，组件的生命周期：`created`、`attached`、`ready`、`moved`、`detached` 将收归到 `lifetimes` 字段内进行声明，原有声明方式仍旧有效，如同时存在两种声明方式，则 `lifetimes` 字段内声明方式优先级最高
   */
  lifetimes: {
    attached() {
      console.log('attached')
      console.log(this.data)
      this.setData({
        nick: this.data.name
      })
    },
    detached: function () {
      // 在组件实例被从页面节点树移除时执行
    },
  },
  /**
   * 组件的方法列表
   */
  methods: {
    async _editNick() {
      if (!this.data.nick) {
        wx.showToast({
          title: '请填写昵称',
          icon: 'none'
        })
        return
      }
      const postData = {
        token: wx.getStorageSync('token'),
        nick: this.data.nick,
      }
      // const res = await WXAPI.modifyUserInfo(postData)
      // if (res.code != 0) {
      //   wx.showToast({
      //     title: res.msg,
      //     icon: 'none'
      //   })
      //   return
      // }
      wx.showToast({
        title: '保存成功',
      })
      this.setData({
        show: false
      })
      http.getToken();
    },

    /**
     * 选择用户头像
     */
    async onChooseAvatar(e) {
      const {
        avatarUrl
      } = e.detail;
      console.log(e, e.detail, avatarUrl);
      this.updateAvatarUrl(avatarUrl);
    },

    async onChooseAvatar__(e) {
      let avatarUrl = e.detail.avatarUrl
      let res = await WXAPI.uploadFileV2(wx.getStorageSync('token'), avatarUrl)
      if (res.code != 0) {
        wx.showToast({
          title: res.msg,
          icon: 'none'
        })
        return
      }
      avatarUrl = res.data.url
      res = await WXAPI.modifyUserInfo({
        token: wx.getStorageSync('token'),
        avatarUrl,
      })
      if (res.code != 0) {
        wx.showToast({
          title: res.msg,
          icon: 'none'
        })
        return
      }
      this.setData({
        avatarUrl
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
        nick: newNickName
      });
    },


    /**
     * 更新用户输入的用户名
     * @param {} e
     */
    setInputValue: function (e) {
      console.log("写入：", e.detail.value);
      let newNickName = e.detail.value;
      if (newNickName != '' && this.data.nick != newNickName) {
        console.log("更新用户名称", newNickName);
        //通过接口更新用户名称
        this.updateNickName(newNickName);
      }
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
            nick: newNickName
          });
          let userInfoInToken = wx.getStorageSync('userInfoInToken');
          userInfoInToken.nickName = newNickName;
          wx.setStorageSync('userInfoInToken', userInfoInToken);
        }
      });
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
          let result = JSON.parse(res.data);
          console.log('图片上传成功回调', JSON.parse(res.data))
          avatarUrl = result.data;
          _this.setData({
            avatarUrl
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
     * 获取用户的手机号
     * @param {} e
     */
    getPhoneNumber: function (e) {
      var that = this;
      console.log('获取用户手机号', e)
      if (e.detail.errMsg == 'getPhoneNumber:ok') {
        var params = {
          url: "/p/user/getWxPhoneNumber",
          method: "POST",
          data: e.detail,
          callBack: function (res) {
            console.log('获取小程序用户手机号', res);
            http.getToken();
            setTimeout(() => {
              //{errMsg: "navigateTo:fail can not navigateTo a tabbar page"}errMsg: "navigateTo:fail can not navigateTo a tabbar page"__proto__: Object
              wx.navigateTo({
                url: '/pages/user/user?sts=1',
              })
            }, 1000)
          }
        };
        http.request(params);
      }
    },
    jump() {
      this.setData({
        show: false
      })
    }
  }
})
