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
        selNodeId: '', //左侧所选菜单ID
        selChildIndex: null,
        categoryList: [],
        productList: [],
        categoryImg: '',
        prodid: '',
        imageUrl: app.globalData.imageUrl,
        totalCartNum: 0, //购物车总数量
        prodNum: 1, //购买产品数量
        current: 1,
        size: 10,
        pages: 0,
        scrollTop: 0,
        skuShow: false,
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
                    selNodeId: res[0].categoryId
                });
                ths.getSubCategory(res[0].categoryId, null, 0);
                ths.getProdList(res[0].categoryId);
            }
        };
        http.request(params);
    },

    /**
     * 显示加入购物车
     */
    showSku: function (e) {

        var prodid = e.currentTarget.dataset.prodid;
        console.log('待加入购物车的商品id', prodid);

        // if (this.data.defaultSku.stocks < 1) {
        //     wx.showToast({
        //         title: "商品库存不足",
        //         icon: "none"
        //     });
        //     return;
        // }

        this.setData({
            skuShow: true
        });
    },

    /**
     * 将商品加入购物车
     * @param {} item 
     */
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
    loadMore() {
        console.log(11111111111111);
        if (this.data.current < this.data.pages) {
            this.setData({
                current: this.data.current + 1
            })
            this.getProdList(this.data.selNodeId)
        }
    },
    /**
     * 分类点击事件
     */
    onMenuTab: function (e) {
        console.log(e);
        var id = e.currentTarget.dataset.id; //当前菜单ID
        if (this.data.selNodeId == id) {
            return;
        } else {

        }
        var index = e.currentTarget.dataset.index; //父菜单index
        var childIndex = e.currentTarget.dataset.childindex ?? null; //子菜单Index
        var parentid = e.currentTarget.dataset.parentid; //上级分类ID
        if (!parentid) {
            //获取二级分类
            this.getSubCategory(id, childIndex, index);
        }
        this.setData({
            current: 1,
            scrollTop: 0,
            selNodeId: id,
            categoryImg: this.data.categoryList[index].pic,
            selIndex: index,
            selChildIndex: childIndex,
            categoryList: this.data.categoryList
        });
        //获取点击的分类下的商品列表
        this.getProdList(id);
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
        var ths = this;
        var params = {
            url: "/prod/pageProd",
            method: "GET",
            data: {
                categoryId: categoryId,
                current: ths.data.current,
                size: ths.data.size
            },
            callBack: (res) => {
                // console.log(res);
                let list = []
                if (res.current == 1) {
                    list = res.records
                } else {
                    //拼接到上一页的数据中 合并显示
                    list = ths.data.productList.concat(res.records)
                }
                this.setData({
                    productList: list,
                    pages: res.pages
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