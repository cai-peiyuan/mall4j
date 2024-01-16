// pages/user-balance/user-balance.js
Page({

    /**
     * 页面的初始数据
     */
    data: {
        activeTab:0,
        cardIndex:0,
        cardMoney:100,
    },

    /**
     * 余额明细页面
     * @param {} e 
     */
    toUserBalanceDetailPage: function(e){
        wx.navigateTo({
            url: '/pages/balance/pages/user-balance-detail/user-balance-detail',
          })
    },
    /**
     * 生命周期函数--监听页面加载
     */
    onLoad(options) {

    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    },
    //修改tab标签 在线储值 or 储值券
    changeActiveTab(e){
        // console.log(index);
        let index=e.currentTarget.dataset['index'];
        this.setData({activeTab:index})
    },
    //修改充值面额
    changeCard(e){
        let index=e.currentTarget.dataset['index'];
        let money=e.currentTarget.dataset["money"];
        if(index!=cardIndex){
            this.setData({cardIndex:index});
            this.setData({cardMoney:money});
        }
       
    }
})