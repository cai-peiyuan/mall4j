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
        prodId: 0,
        shopId: 1,
        current: 1,
        size: 10,
        pages: 0,
        scrollTop: 0,
        price: 0,
        currentSelectedProduct: {},
        skuShow: false,
        findSku: true,
        skuList: [],
        skuGroup: {},
        defaultSku: undefined,
        selectedProp: [],
        selectedPropObj: {},
        propKeys: [],
        allProperties: [],
        pic: "",
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
        let product = this.data.productList.filter(item => {
            // console.log(item)
            return item.prodId == prodid
        });
        console.log('待加入购物车的商品', product);
        // if (this.data.defaultSku.stocks < 1) {
        //     wx.showToast({
        //         title: "商品库存不足",
        //         icon: "none"
        //     });
        //     return;
        // }

        this.setData({
            price: 0,
            currentSelectedProduct: {},
            skuShow: false,
            findSku: true,
            skuList: [],
            skuGroup: {},
            defaultSku: undefined,
            selectedProp: [],
            selectedPropObj: {},
            propKeys: [],
            allProperties: [],
            pic: "",
        });

        if (product.length > 0) {
            this.setData({
                prodId: prodid,
                currentSelectedProduct: product[0],
                price: product[0].price,
                skuList: product[0].skuList,
                skuShow: true,
                prodNum: 1,
                pic: product[0].pic,
            });
            // 组装sku
            console.log('组装sku', this.data.currentSelectedProduct, this.data.skuList, this.data.skuShow);
            this.groupSkuProp();
        } else {
            this.setData({
                prodId: 0,
                currentSelectedProduct: undefined,
                price: 0,
                skuList: [],
                skuShow: false,
                prodNum: 1,
                pic: "",
            });
            wx.showToast({
                title: "商品库存不足",
                icon: "none"
            });
        }

    },


    /**
     * 根据skuList进行数据组装
     */
    groupSkuProp: function () {
        var skuList = this.data.skuList;
        console.log('skuList', skuList);
        //当后台返回只有一个SKU时，且SKU属性值为空时，即该商品没有规格选项，该SKU直接作为默认选中SKU
        if (skuList.length == 1 && skuList[0].properties == "") {
            this.setData({
                defaultSku: skuList[0]
            });
            console.log('skuList.length == 1 defaultSku', this.data.defaultSku);
            return;
        }

        var skuGroup = {}; //所有的规格名(包含规格名下的规格值集合）对象，如 {"颜色"：["金色","银色"],"内存"：["64G","256G"]}
        var allProperties = []; //所有SKU的属性值集合，如 ["颜色:金色;内存:64GB","颜色:银色;内存:64GB"]
        var propKeys = []; //所有的规格名集合，如 ["颜色","内存"]

        for (var i = 0; i < skuList.length; i++) {

            //找到和商品价格一样的那个SKU，作为默认选中的SKU
            var defaultSku = this.data.defaultSku;
            var isDefault = false;
            if (!defaultSku && skuList[i].price == this.data.price) {
                defaultSku = skuList[i];
                isDefault = true;
                this.setData({
                    defaultSku: defaultSku
                });
                console.log('skuList.defaultSku', this.data.defaultSku);
            }

            var properties = skuList[i].properties; //如：版本:公开版;颜色:金色;内存:64GB
            allProperties.push(properties);
            var propList = properties.split(";"); // 如：["版本:公开版","颜色:金色","内存:64GB"]

            var selectedPropObj = this.data.selectedPropObj;
            for (var j = 0; j < propList.length; j++) {

                var propval = propList[j].split(":"); //如 ["版本","公开版"]
                var props = skuGroup[propval[0]]; //先取出 规格名 对应的规格值数组

                //如果当前是默认选中的sku，把对应的属性值 组装到selectedProp
                if (isDefault) {
                    propKeys.push(propval[0]);
                    selectedPropObj[propval[0]] = propval[1];
                }

                if (props == undefined) {
                    props = []; //假设还没有版本，新建个新的空数组
                    props.push(propval[1]); //把 "公开版" 放进空数组
                } else {
                    if (!this.array_contain(props, propval[1])) { //如果数组里面没有"公开版"
                        props.push(propval[1]); //把 "公开版" 放进数组
                    }
                }
                skuGroup[propval[0]] = props; //最后把数据 放回版本对应的值
            }
            this.setData({
                selectedPropObj: selectedPropObj,
                propKeys: propKeys
            });
        }
        this.parseSelectedObjToVals();
        this.setData({
            skuGroup: skuGroup,
            allProperties: allProperties
        });
    },

    //将已选的 {key:val,key2:val2}转换成 [val,val2]
    parseSelectedObjToVals: function () {
        var selectedPropObj = this.data.selectedPropObj;
        var selectedProperties = "";
        var selectedProp = [];
        for (var key in selectedPropObj) {
            selectedProp.push(selectedPropObj[key]);
            selectedProperties += key + ":" + selectedPropObj[key] + ";";
        }
        selectedProperties = selectedProperties.substring(0, selectedProperties.length - 1);
        this.setData({
            selectedProp: selectedProp
        });

        var findSku = false;
        for (var i = 0; i < this.data.skuList.length; i++) {
            if (this.data.skuList[i].properties == selectedProperties) {
                findSku = true;
                this.setData({
                    defaultSku: this.data.skuList[i],
                });
                break;
            }
        }
        this.setData({
            findSku: findSku
        });
    },

    //判断数组是否包含某对象
    array_contain: function (array, obj) {
        for (var i = 0; i < array.length; i++) {
            if (array[i] == obj) //如果要求数据类型也一致，这里可使用恒等号===
                return true;
        }
        return false;
    },
    /**
     * 关闭弹窗
     */
    closePopup: function () {
        this.setData({
            popupShow: false,
            skuShow: false,
            commentShow: false
        });
    },

    /**
     * 减数量
     */
    onCountMinus: function () {
        var prodNum = this.data.prodNum;
        if (prodNum > 1) {
            this.setData({
                prodNum: prodNum - 1
            });
        }
    },


    /**
     * 加数量
     */
    onCountPlus: function () {
        var prodNum = this.data.prodNum;
        if (prodNum < 1000) {
            this.setData({
                prodNum: prodNum + 1
            });
        }
    },


    //点击选择规格
    toChooseItem: function (e) {
        var val = e.currentTarget.dataset.val;
        var key = e.currentTarget.dataset.key;
        console.log(val, key)
        var selectedPropObj = this.data.selectedPropObj;
        selectedPropObj[key] = val;
        console.log(selectedPropObj)
        this.setData({
            selectedPropObj: selectedPropObj
        });
        this.parseSelectedObjToVals();
    },

    /**
     * 立即购买
     */
    buyNow: function () {
        if (this.data.defaultSku.stocks < 1) {
            wx.showToast({
                title: "商品库存不足",
                icon: "none"
            });
            return;
        }

        if (!this.data.findSku) {
            return;
        }
        wx.setStorageSync("orderItem", JSON.stringify({
            prodId: this.data.prodId,
            skuId: this.data.defaultSku.skuId,
            prodCount: this.data.prodNum,
            shopId: this.data.shopId,
            fromUserId: this.data.fromUserId,
        }));
        wx.navigateTo({
            url: '/pages/submit-order/submit-order?orderEntry=1',
        })

    },

    /**
     * 将商品加入购物车
     * @param {} event 
     */
    addToCart: function (event) {
        if (this.data.defaultSku.stocks < 1) {
            wx.showToast({
                title: "商品库存不足",
                icon: "none"
            });
            return;
        }
        if (!this.data.findSku) {
            return;
        }
        var ths = this;
        wx.showLoading({
            mask: true
        });

        //{"basketId":0,"count":1,"prodId":83,"shopId":1,"skuId":478}
        var params = {
            url: "/p/shopCart/changeItem",
            method: "POST",
            data: {
                basketId: 0,
                count: ths.data.prodNum,
                prodId: ths.data.prodId,
                shopId: ths.data.shopId,
                skuId: ths.data.defaultSku.skuId
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