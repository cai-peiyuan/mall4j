const types = ['default', 'primary', 'warn']
const pageObject = {
    data: {
        defaultSize: 'default',
        primarySize: 'default',
        warnSize: 'default',
        disabled: false,
        plain: false,
        loading: false
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
        wx.navigateTo({
            url: '/pages/admin/pages/order-detail/order-detail?orderNum=1751226703902019584'
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
                    //打开订单管理页面
                    wx.navigateTo({
                        url: '/pages/admin/pages/order-detail/order-detail?orderNum=' + orderNumber
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