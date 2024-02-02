const types = ['default', 'primary', 'warn']
const pageObject = {
    data: {
        orderNumber: '',
        defaultSize: 'default',
        primarySize: 'default',
        warnSize: 'default',
        disabled: false,
        plain: false,
        loading: false,
        showOrderDialog: false, //是否显示订单管理弹框
        showReasonSel: false, //是否显示更多原因
        reasonArr: ["原因1", "原因2", "原因2", "原因2", "原因2", "原因2", "原因2", "原因2", "原因3", "原因4"],
        selectedReason: -1,
    },

    hideDialog() {
        this.setData({
            showOrderDialog: false
        })
    },

    taggleShowOrderDialog() {
        this.setData({
            showOrderDialog: !this.data.showOrderDialog
        });
    },
    showReasonSel() {
        this.setData({
            showReasonSel: !this.data.showReasonSel
        });
    },
    selectReason(e) {
        this.setData({
            selectedReason: e.target.dataset.value
        });
        this.showReasonSel();
    },
    onShareAppMessage() {
        return {
            title: 'button',
            path: 'page/component/pages/button/button'
        }
    },

    setDisabled() {
        this.setData({
            disabled: !this.data.disabled
        })
    },

    setPlain() {
        this.setData({
            plain: !this.data.plain
        })
    },

    setLoading() {
        this.setData({
            loading: !this.data.loading
        })
    },

    handleContact(e) {
        console.log(e.detail)
    },

    handleGetPhoneNumber(e) {
        console.log(e.detail)
    },

    handleGetUserInfo(e) {
        console.log(e.detail)
    },

    handleOpenSetting(e) {
        console.log(e.detail.authSetting)
    },

    handleGetUserInfo(e) {
        console.log(e.detail.userInfo)
    },

    /**
     * 打开某个订单页
     * @param {*} orderNumber 
     */
    viewOrder(orderNumber) {
        console.log(orderNumber)
        // 订单发货
        wx.navigateTo({
            url: '/pages/admin/pages/order-delivery/order-delivery?orderNum=1753239988402786304'
        })
    },
    /**
     * 
     * @param {扫描二维码打开订单页} e 
     */
    viewOrderByQRCode(e) {
        // 允许从相机和相册扫码
        /**
         * {result: "1751226703902019584", rawData: "MTc1MTIyNjcwMzkwMjAxOTU4NA==", codeVersion: 1, errMsg: "scanCode:ok", scanType: "QR_CODE", …}
         */
        wx.scanCode({
            success(res) {
                if (res.errMsg == 'scanCode:ok' && res.scanType == "QR_CODE") {
                    const orderNumber = res.result;
                    //打开订单管理页面 订单发货
                    wx.navigateTo({
                        url: '/pages/admin/pages/order-delivery/order-delivery?orderNum=' + orderNumber
                    })
                }
            }
        })
    }
}

for (let i = 0; i < types.length; ++i) {
    (function (type) {
        pageObject[type] = function () {
            const key = type + 'Size'
            const changedData = {}
            changedData[key] =
                this.data[key] === 'default' ? 'mini' : 'default'
            this.setData(changedData)
        }
    }(types[i]))
}

Page(pageObject)