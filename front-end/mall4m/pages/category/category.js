// pages/category/category.js

var http = require("../../utils/http.js");
var config = require("../../utils/config.js");
const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    selIndex: 0,
    selChildIndex:null,
    categoryList:[],
    productList: [],
    categoryImg: '',
    prodid:'',
    imageUrl:app.globalData.imageUrl,
    totalCartNum: 0,//购物车总数量
    prodNum: 1,//购买产品数量
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var ths = this;
    //加载分类列表
    var params = {
      url: "/category/categoryInfo",
      method: "GET",
      data: {
        parentId: ''
      },
      callBack: function (res) {
        // console.log(res);
        ths.setData({
          categoryImg: res[0].pic,
          categoryList: res,
        });
        ths.getProdList(res[0].categoryId)
      }
    };
    http.request(params);
  },
  addToCart: function (item) {
    if (item.defaultSku.stocks < 1) {
        wx.showToast({
            title: "商品库存不足",
            icon: "none"
        });
        return;
    }
    if (!item.findSku) {
        return;
    }
    var ths = this;
    wx.showLoading({
        mask: true
    });
    var params = {
        url: "/p/shopCart/changeItem",
        method: "POST",
        data: {
            basketId: 0,
            count: item.prodNum,
            prodId: item.prodId,
            shopId: item.shopId,
            skuId: item.defaultSku.skuId
        },
        callBack: function (res) {
            //console.log(res);
            ths.setData({
                totalCartNum: ths.data.totalCartNum + ths.data.prodNum
            });
            wx.hideLoading();
            wx.showToast({
                title: "加入购物车成功",
                icon: "none",
                complete: () => {
                    ths.setData({
                        skuShow: false
                    });
                }
            })
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
    this.setData({
        totalCartNum: app.globalData.totalCartCount
    });
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
   * 分类点击事件
   */
  onMenuTab: function (e) {
    console.log(e);
    var id = e.currentTarget.dataset.id;//父菜单ID
    var index = e.currentTarget.dataset.index;//父菜单index
    var childIndex=e.currentTarget.dataset.childindex??null;//子菜单Index
    var childId=e.currentTarget.dataset.childid;//子菜单ID
    // this.getProdList(id);
    if(!this.data.categoryList[index].childNode){
        this.data.categoryList[index].childNode=[{id:'111',name:'蔬菜1蔬菜1'},{id:'222',name:'蔬菜2'},{id:'333',name:'蔬菜3'}];
    }
    this.getProdList(this.data.categoryList[index].categoryId);
    this.setData({
      categoryImg: this.data.categoryList[index].pic,
      selIndex: index,
      selChildIndex:childIndex,
      categoryList:this.data.categoryList
    });
    console.log(this.data.selChildIndex);
  },

  // 跳转搜索页
  toSearchPage: function () {
    wx.navigateTo({
      url: '/pages/search/pages/search-page/search-page',
    })
  },
  getProdList(categoryId) {
    //加载分类列表
    var params = {
      url: "/prod/pageProd",
      method: "GET",
      data: {
        categoryId: categoryId
      },
      callBack: (res) => {
        // console.log(res);
        this.setData({
          productList: res.records,
        });
      }
    };
    http.request(params);
  },

  //跳转商品详情页
  toProdPage: function (e) {
    var prodid = e.currentTarget.dataset.prodid;
    wx.navigateTo({
      url: '/pages/prods/pages/prod/prod?prodid=' + prodid,
    })
  },
})