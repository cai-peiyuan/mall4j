// pages/user-balance-detail/user-balance-detail.js
Page({

    /**
     * 页面的初始数据
     */
    data: {
        tabIndex:0,
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
      console.log(111);
    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {

    },
    //修改tab标签 近一个月 or 全部
    changeTab(e){
        // console.log(index);
        let index=e.currentTarget.dataset['index'];
        if(index!==this.tabIndex){
            this.setData({tabIndex:index})
        }
       
    },
})