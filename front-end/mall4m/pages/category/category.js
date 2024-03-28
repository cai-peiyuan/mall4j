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
        selChildIndex: null,
        categoryList: [],
        productList: [],
        categoryImg: '',
        prodid: '',
        imageUrl: app.globalData.imageUrl,
        totalCartNum: 0, //购物车总数量
        prodNum: 1, //购买产品数量
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
                ths.getSubCategory(res[0].categoryId, null, 0);
                ths.getProdList(res[0].categoryId);
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
        var id = e.currentTarget.dataset.id; //父菜单ID
        var index = e.currentTarget.dataset.index; //父菜单index
        var childIndex = e.currentTarget.dataset.childindex ?? null; //子菜单Index
        var parentid = e.currentTarget.dataset.parentid; //上级分类ID
        if (!parentid) {
            //获取二级分类
            this.getSubCategory(id, childIndex, index);
        }
        //获取点击的分类下的商品列表
        this.getProdList(id);
        this.setData({
            categoryImg: this.data.categoryList[index].pic,
            selIndex: index,
            selChildIndex: childIndex,
            categoryList: this.data.categoryList
        });
        console.log(this.data.selChildIndex);
    },

    // 跳转搜索页
    toSearchPage: function () {
        wx.navigateTo({
            url: '/pages/search/pages/search-page/search-page',
        })
    },

    /**
     * 获取下一级的分类数据
     * @param {} categoryId 
     */
    getSubCategory(categoryId, childIndex, index) {
        let ths = this;
        //加载分类列表
        var params = {
            url: "/category/categoryInfo",
            method: "GET",
            data: {
                parentId: categoryId
            },
            callBack: function (res) {
                console.log('getSubCategory', res);
                ths.data.categoryList[index].childNode = res
                ths.setData({
                    childIndex: childIndex,
                    categoryList: ths.data.categoryList,
                });
            }
        };
        http.request(params);
    },
    /**
     * 获取指定分类下的商品信息
     * @param {} categoryId 
     */
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