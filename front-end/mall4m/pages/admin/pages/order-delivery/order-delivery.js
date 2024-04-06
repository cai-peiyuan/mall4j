// pages/order-detail/order-detail.js

var http = require('../../../../utils/http.js');
Page({

    /**
     * 页面的初始数据
     */
    data: {
        orderItemDtos: [],
        remarks: "",
        actualTotal: 0,
        userAddrDto: null,
        orderNumber: "",
        dvyNumber: "",
        createTime: "",
        status: 0,
        productTotalAmount: '',
        transfee: '',
        reduceAmount: '',
        shopId: '',
        prodid: '',
        order: {},
        showOrderDeliverDialog: false,
        selectedDelivery: {},
        showSelectDelivery: false,
        showSelectDeliveryUser: false,
        deliveryList: [],
        deliveryUserList: [],
    },


    showSelectDeliveryFunc(e) {
        this.setData({
            showSelectDelivery: !this.data.showSelectDelivery
        })
    },


    selectDeliveryFunc(e) {
        console.log('delivery', e.target.dataset.value);
        this.setData({
            selectedDelivery: e.target.dataset.value
        });
        this.showSelectDeliveryFunc();
    },



    showSelectDeliveryUserFunc(e) {
        this.setData({
            showSelectDeliveryUser: !this.data.showSelectDeliveryUser
        })
    },


    selectDeliveryUserFunc(e) {
        console.log('delivery', e.target.dataset.value);
        this.setData({
            selectedDeliveryUser: e.target.dataset.value
        });
        this.showSelectDeliveryUserFunc();
    },
    //跳转商品详情页
    toProdPage: function (e) {
        var prodid = e.currentTarget.dataset.prodid;
        wx.navigateTo({
            url: '/pages/prods/pages/prod/prod?prodid=' + prodid,
        })
    },

    /**
     * 加入购物车
     */
    addToCart: function (event) {
        let index = event.currentTarget.dataset.index
        // if (!this.orderItemDtos) {
        //   console.log(1213)
        //   return;
        // }
        var ths = this;
        wx.showLoading({
            mask: true
        });
        var params = {
            url: "/p/shopCart/changeItem",
            method: "POST",
            data: {
                basketId: 0,
                count: this.data.orderItemDtos[index].prodCount,
                prodId: this.data.orderItemDtos[index].prodId,
                shopId: this.data.shopId,
                skuId: this.data.orderItemDtos[index].skuId
            },
            callBack: function (res) {
                //console.log(res);
                wx.hideLoading();
                wx.showToast({
                    title: "加入购物车成功",
                    icon: "none"
                })
                wx.switchTab({
                    url: '/pages/basket/basket',
                })
            }
        };
        http.request(params);
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        this.loadOrderDetail(options.orderNum);
    },

    /**
     * 加载订单数据
     */
    loadOrderDetail: function (orderNum) {
        var ths = this;
        wx.showLoading();
        //加载订单详情
        var params = {
            url: "/p/myOrder/orderDetail/" + orderNum,
            method: "GET",
            data: {

            },
            callBack: function (data) {
                let orderShop = data.orderShop;
                let order = data.order;
                let deliveryList = data.deliveryList || [{
                        "dvyId": 13,
                        "dvyName": "商家配送",
                        "companyHomeUrl": "http://www.bhgk.cc",
                        "recTime": "2024-01-24 18:09:22",
                        "modifyTime": "2024-01-24 18:09:24",
                        "queryUrl": "https://xdyx.bhgk.cc/delivery/info/{dvyFlowId}"
                    },
                    {
                        "dvyId": 14,
                        "dvyName": "顺丰快递公司",
                        "companyHomeUrl": "http://www.sf-express.com",
                        "recTime": "2015-08-20 11:58:03",
                        "modifyTime": "2017-03-22 17:12:27",
                        "queryUrl": "http://www.kuaidi100.com/query?type=shunfeng&postid={dvyFlowId}&id=11"
                    }
                ];
                let deliveryUserList = data.deliveryUserList || [{
                        "id": 1,
                        "shopId": 1,
                        "userName": "冯公子",
                        "userPhone": "13833501400",
                        "seq": 1,
                        "status": 1
                    },
                    {
                        "id": 2,
                        "shopId": 1,
                        "userName": "石璐璐",
                        "userPhone": "18712746603",
                        "seq": 0,
                        "status": 1
                    },
                    {
                        "id": 3,
                        "shopId": 1,
                        "userName": "崔美静",
                        "userPhone": "13102523363",
                        "seq": 2,
                        "status": 1
                    }
                ];
                ths.setData({
                    orderNumber: orderNum,
                    actualTotal: orderShop.actualTotal,
                    userAddrDto: orderShop.userAddrDto,
                    remarks: orderShop.remarks,
                    orderItemDtos: orderShop.orderItemDtos,
                    createTime: orderShop.createTime,
                    status: orderShop.status,
                    productTotalAmount: orderShop.orderItemDtos[0].productTotalAmount,
                    transfee: orderShop.transfee,
                    reduceAmount: orderShop.reduceAmount,
                    actualTotal: orderShop.actualTotal,
                    shopId: orderShop.shopId,
                    order: order,
                    deliveryUserList: deliveryUserList,
                    deliveryList: deliveryList
                });
                wx.hideLoading();
            }
        };
        http.request(params);

    },

    /**
     * 生成运单号码
     */
    generateExpressNumber() {
        var ths = this;
        wx.showLoading();
        //加载订单详情
        var params = {
            url: "/p/myOrder/generateExpressNumber",
            method: "GET",
            data: {

            },
            callBack: function (data) {
                ths.setData({
                    dvyNumber: data
                })
                wx.hideLoading();
            }
        };
        http.request(params);
    },
    /**
     * 
     * @param {扫描二维码读取物流单号} e 
     */
    scanDvyNumberByQRCode(e) {
        // 允许从相机和相册扫码
        /**
         * {result: "1751226703902019584", rawData: "MTc1MTIyNjcwMzkwMjAxOTU4NA==", codeVersion: 1, errMsg: "scanCode:ok", scanType: "QR_CODE", …}
         */
        let _this = this;
        wx.scanCode({
            success(res) {
                if (res.errMsg == 'scanCode:ok' && res.scanType == "QR_CODE") {
                    const dvyNumber = res.result;
                    _this.setData({
                        dvyNumber: dvyNumber
                    });
                }
            }
        })
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


    // 一键复制事件
    copyBtn: function (e) {
        var ths = this;
        wx.setClipboardData({
            //准备复制的数据
            data: ths.data.orderNumber,
            success: function (res) {
                wx.showToast({
                    title: '复制成功',
                });
            }
        })
    },

    /**
     * 订单退款
     * 1、选择退款原因
     * 2、提交退款
     * 3、刷新页面数据
     */
    orderRefund: function (e) {
        console.log(e)
    },

    /**
     * 订单发货
     * -》 未发货订单
     * 1、选择物流厂家
     * 2、填入物流单号（可同时选择配送员）
     * 3、确定提交
     * 4、刷新页面数据
     * -》已发货订单
     * 1、选择配送员
     * 2、确定提交
     * 3、刷新页面数据
     */
    orderDelivery: function (e) {
        console.log(e)
        this.setData({
            showOrderDeliverDialog: true,
        })
    },

    onDvyNumberInput: function (e) {
        this.setData({
          dvyNumber: e.detail.value
        });
      },
    /**
     * 发送订单发货数据
     * @param {*} e 
     */
    orderDeliverySubmit: function (e) {
        console.log(e)
        var ths = this;
        wx.showLoading();
        //加载订单详情
        var params = {
            url: "/p/myOrder/delivery",
            method: "PUT",
            data: {
                orderNumber: ths.data.orderNumber,
                dvyId: ths.data.selectedDelivery.dvyId,
                dvyFlowId: ths.data.dvyNumber,
                deliveryUserId: ths.data.selectedDeliveryUser.id
            },
            callBack: function (data) {
                wx.showToast({
                    title: '订单发货成功',
                });

                ths.setData({
                    showSelectDeliveryUser: false,
                })

                ths.loadOrderDetail(ths.data.orderNumber);
                wx.hideLoading();
            }
        };
        http.request(params);
    },

    /**
     * 关闭发货对话框
     * @param {*} e 
     */
    closeOrderDeliverDialog: function (e) {
        console.log(e)
        this.setData({
            showOrderDeliverDialog: false,
        })
    },

    /**
     * 订单送达
     * -》已发货订单显示此按钮
     * 1、点击订单送达，弹出对话框，可输入送达备注（如放超市、送货上门、本人签收、代签收等文本），也可不输入备注默认为：已签收
     * 2、确定提交
     * 3、刷新页面数据
     */
    orderArrived: function (e) {
        console.log(e)
    }
})