// pages/prod/prod.js
const app = getApp()
var http = require('../../../../utils/http.js');
var config = require('../../../../utils/config.js');
var util = require('../../../../utils/util.js');
var myBehavior = require('../../../../utils/my-behavior.js')

Page({
    behaviors: [myBehavior],
    /**
     * 页面的初始数据
     */
    data: {
        showComment: false, //是否显示评论
        shopId: 1,
        picDomain: config.picDomain,
        indicatorDots: true, //显示轮播点
        indicatorColor: '#f2f2f2',
        indicatorActiveColor: '#eba524',
        autoplay: true, //自动开启轮播
        interval: 9000, //轮播间隔时间
        duration: 2000, //动画持续时长
        prodNum: 1,
        totalCartNum: 0,
        pic: "",
        imgs: '',
        prodName: '',
        price: 0,
        oriPrice: 0,
        totalStocks: 0, //总库存
        totalSales: 0, //总销量
        content: '',
        prodId: 0,
        fromUserId: null,
        brief: '',
        skuId: 0,
        popupShow: false,
        // 是否获取过用户领取过的优惠券id
        loadCouponIds: false,
        skuShow: false,
        commentShow: false,
        couponList: [],
        skuList: [],
        transport: {}, //商品运费设置
        skuGroup: {},
        findSku: true,
        defaultSku: undefined,
        selectedProp: [],
        selectedPropObj: {},
        propKeys: [],
        allProperties: [],
        prodCommData: {},
        prodCommPage: {
            current: 0,
            pages: 0,
            records: []
        },
        littleCommPage: [],
        evaluate: -1, //评价类型
        isCollection: false, //是否收藏了商品
        left: "11.5%",
        toView: "part1",
        observerBasicInfo: null,
        observerDetailInfo: null,
        observerCommentsInfo: null,
        imageUrl: app.globalData.imageUrl,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        console.log('商品详情页传递参数', options);
        this.setData({
            prodId: options.prodid,
            fromUserId: options.fromUserId || null
        });
        this.observerBasicInfo = wx.createIntersectionObserver(this);
        this.observerDetailInfo = wx.createIntersectionObserver(this);
        this.observerCommentsInfo = wx.createIntersectionObserver(this);
        // 11.5 45 78.5

        let that = this;

        //监听商品详情
        // this.observerDetailInfo.relativeTo(".scroll-view").observe(".detailInfo", (res) => {
        //     console.log(res);
        //     if (res.intersectionRatio > 0) {
        //         console.log("进入详情");
        //         this.setData({
        //             left: "45%"
        //         });
        //     } else {
        //         console.log("离开详情");
        //         that.setData({
        //             left: "11.5%"
        //         })
        //     }
        // });
        //监听商品基本信息
        this.observerBasicInfo.relativeTo(".scroll-view").observe(".basicInfo", (res) => {
            if (res.intersectionRatio > 0) {
                console.log("进入轮播");
                that.setData({
                    left: "11.5%"
                })
            } else {
                console.log("离开轮播");
                that.setData({
                    left: "45%"
                })
            }
        });
        //监听商品评价
        this.observerCommentsInfo.relativeTo(".scroll-view").observe(".commentsInfo", (res) => {
            console.log(res);
            if (res.intersectionRatio > 0) {
                console.log("进入评论");
                this.setData({
                    left: "78.5%"
                });
            } else {
                console.log("离开评论");
                this.setData({
                    left: "45%"
                });
            }
        });

        // 加载商品信息
        this.getProdInfo();
        if(this.data.showComment){
            // 加载评论数据
            this.getProdCommData();
            // 加载评论项
            this.getLittleProdComm();
        }
        // 查看用户是否关注
        this.getCollection();
    },

    /**
     * 获取是否关注信息
     */
    getCollection() {
        wx.showLoading();
        var params = {
            url: "/p/user/collection/isCollection",
            method: "GET",
            data: {
                prodId: this.data.prodId
            },
            callBack: (res) => {
                this.setData({
                    isCollection: res
                })
                wx.hideLoading();
            }
        };
        http.request(params);
    },

    /**
     * 添加或者取消收藏商品 
     */
    addOrCannelCollection() {
        wx.showLoading();

        var params = {
            url: "/p/user/collection/addOrCancel",
            method: "POST",
            data: this.data.prodId,
            callBack: (res) => {
                this.setData({
                    isCollection: !this.data.isCollection
                })
                wx.hideLoading();
            }
        };
        http.request(params);
    },

    toTarget(val) {
        if (val.target.dataset.val == 1) {
            this.setData({
                left: "11.5%",
                toView: "part1"
            });
        } else if (val.target.dataset.val == 2) {
            this.setData({
                left: "45%",
                toView: "part2"
            })
        } else if (val.target.dataset.val == 3) {
            this.setData({
                left: "78.5%",
                toView: "part3"
            })
        }
        console.log("left", this.data.left);
    },
    /**
     * 获取商品信息
     */
    getProdInfo() {
        wx.showLoading();
        var params = {
            url: "/prod/prodInfo",
            method: "GET",
            data: {
                prodId: this.data.prodId,
                // userType: 0
            },
            callBack: (res) => {
                //console.log(res);
                // 商品图片有多个 使用逗号分隔
                var imgStrs = res.imgs;
                var imgs = imgStrs.split(",");
                // 详情是html格式
                var content = util.formatHtml(res.content);
                this.setData({
                    imgs: imgs,
                    content: content,
                    price: res.price,
                    oriPrice: res.oriPrice,
                    totalStocks: res.totalStocks, //各个sku加一起的总库存
                    totalSales: res.totalSales, //已售数据
                    prodName: res.prodName,
                    prodId: res.prodId,
                    brief: res.brief,
                    // skuId: res.skuId
                    skuList: res.skuList,
                    pic: res.pic,
                    transport: res.transport
                });
                // 获取优惠券
                //this.getCouponList();
                // 组装sku
                this.groupSkuProp();

                wx.hideLoading();
            }
        };
        http.request(params);
    },

    /**
     * 获取商品评论信息
     */
    getProdCommData() {
        http.request({
            url: "/prodComm/prodCommData",
            method: "GET",
            data: {
                prodId: this.data.prodId,
            },
            callBack: (res) => {
                this.setData({
                    prodCommData: res
                })
            }
        })
    },
    // 获取部分评论
    getLittleProdComm() {
        if (this.data.prodCommPage.records.length) {
            return;
        }
        this.getProdCommPage();
    },
    getMoreCommPage(e) {
        this.getProdCommPage();
    },
    /**
     * app分享功能
     * @param {*} option 
     */
    onShareAppMessage: function (option) {
        console.log('onShareAppMessage参数', option)
        console.log('分享商品编号', this.data.prodId);
        let userInfo = this.getLoginedUserInfo();
        console.log('分享用户信息', userInfo);
        let path = '/pages/prods/pages/prod/prod?prodid=' + this.data.prodId + '&fromUserId=' + userInfo.userId;
        console.log('分享商品页面path', path);
        return {
            title: this.data.prodName + this.data.price,
            path: path
        }
    },


    // 获取商品信息
    /**
    getProdInfo() {
      wx.showLoading();
      var params = {
        url: "/prod/prodInfo",
        method: "GET",
        data: {
          prodId: this.data.prodId,
          // userType: 0
        },
        callBack: (res) => {
          //console.log(res);
          var imgStrs = res.imgs;
          var imgs = imgStrs.split(",");
          var content = util.formatHtml(res.content);
          this.setData({
            imgs: imgs,
            content: content,
            price: res.price,
            prodName: res.prodName,
            prodId: res.prodId,
            brief: res.brief,
            // skuId: res.skuId
            skuList: res.skuList,
            pic: res.pic,
            transport: res.transport
          });
          // 获取优惠券
          //this.getCouponList();
          // 组装sku
          this.groupSkuProp();

          wx.hideLoading();
        }
      };
      http.request(params);
    },
     */

    // 获取分页获取评论
    getProdCommPage(e) {
        if (e) {
            if (e.currentTarget.dataset.evaluate === this.data.evaluate) {
                return;
            }
            this.setData({
                prodCommPage: {
                    current: 0,
                    pages: 0,
                    records: []
                },
                evaluate: e.currentTarget.dataset.evaluate
            })
        }
        http.request({
            url: "/prodComm/prodCommPageByProd",
            method: "GET",
            data: {
                prodId: this.data.prodId,
                size: 10,
                current: this.data.prodCommPage.current + 1,
                evaluate: this.data.evaluate
            },
            callBack: (res) => {
                res.records.forEach(item => {
                    if (item.pics) {
                        item.pics = item.pics.split(',')
                    }
                })
                let records = this.data.prodCommPage.records
                records = records.concat(res.records)
                this.setData({
                    prodCommPage: {
                        current: res.current,
                        pages: res.pages,
                        records: records
                    }
                })
                // 如果商品详情中没有评论的数据，截取两条到商品详情页商品详情
                if (!this.data.littleCommPage.length) {
                    this.setData({
                        littleCommPage: records.slice(0, 2)
                    })
                }
            }
        })
    },

    /**
     * 获取优惠券
     */
    getCouponList() {
        http.request({
            url: "/coupon/listByProdId",
            method: "GET",
            data: {
                prodId: this.data.prodId,
                shopId: this.data.shopId,
            },
            callBack: (res) => {
                this.setData({
                    couponList: res
                })
            }
        })
    },

    /**
     * 根据skuList进行数据组装
     */
    groupSkuProp: function () {
        var skuList = this.data.skuList;

        //当后台返回只有一个SKU时，且SKU属性值为空时，即该商品没有规格选项，该SKU直接作为默认选中SKU
        if (skuList.length == 1 && skuList[0].properties == "") {
            this.setData({
                defaultSku: skuList[0]
            });
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

    //判断数组是否包含某对象
    array_contain: function (array, obj) {
        for (var i = 0; i < array.length; i++) {
            if (array[i] == obj) //如果要求数据类型也一致，这里可使用恒等号===
                return true;
        }
        return false;
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
        if (this.observerDetailInfo) {
            this.observerDetailInfo.disconnect();
        }
        if (this.observerBasicInfo) {
            this.observerBasicInfo.disconnect();
        }
        if (this.observerCommentsInfo) {
            this.observerCommentsInfo.disconnect();
        }

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {
        // if(this._observer){
        //   this._observer.disconnect();
        // }

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
     * 跳转到首页
     */
    toHomePage: function () {
        wx.switchTab({
            url: '/pages/index/index',
        })
    },

    /**
     * 跳转到购物车
     */
    toCartPage: function () {
        wx.switchTab({
            url: '/pages/basket/basket',
        })
    },

    /**
     * 加入购物车
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
        var params = {
            url: "/p/shopCart/changeItem",
            method: "POST",
            data: {
                basketId: 0,
                count: this.data.prodNum,
                prodId: this.data.prodId,
                shopId: this.data.shopId,
                skuId: this.data.defaultSku.skuId
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

    showPopup: function () {
        if (this.data.loadCouponIds) {
            this.setData({
                popupShow: true
            });
            return;
        }
        http.request({
            url: "/p/myCoupon/listCouponIds",
            method: "GET",
            data: {},
            callBack: (couponIds) => {
                var couponList = this.data.couponList;
                console.log(couponList)

                couponList.forEach(coupon => {
                    if (couponIds && couponIds.length) {
                        // 领取该优惠券数量
                        var couponLimit = 0;
                        couponIds.forEach(couponId => {
                            if (couponId == coupon.couponId) {
                                couponLimit++;
                            }
                        });
                        // 小于用户领取优惠券上限，可以领取优惠券
                        if (couponLimit < coupon.limitNum) {
                            coupon.canReceive = true;
                        } else {
                            coupon.canReceive = false;
                        }
                    } else {
                        coupon.canReceive = true;
                    }
                });
                this.setData({
                    couponList: couponList,
                    popupShow: true,
                    loadCouponIds: true
                })
            }
        })
    },

    /**
     * 显示加入购物车
     */
    showSku: function () {
        if (this.data.defaultSku.stocks < 1) {
            wx.showToast({
                title: "商品库存不足",
                icon: "none"
            });
            return;
        }

        this.setData({
            skuShow: true
        });
    },

    showComment: function () {
        this.setData({
            commentShow: true
        });
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
     * 在页面使用客服消息
      需要将 button 组件 open-type 的值设置为 contact，当用户点击后就会进入客服会话，如果用户在会话中点击了小程序消息，则会返回到小程序，开发者可以通过 bindcontact 事件回调获取到用户所点消息的页面路径 path 和对应的参数 query，开发者需根据路径自行跳转。此外，开发者可以通过设置 session-from 将会话来源透传到客服。
     * @param {*} e 
     */
    handleContact: function (e) {
        console.log(e.detail.path)
        console.log(e.detail.query)
    },

    toConcatPage: function (e) {
        console.log(e)
    }
})