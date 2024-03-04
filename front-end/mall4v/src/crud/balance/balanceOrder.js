export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true, // selection: true,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 350,
  align: 'center',
  refreshBtn: true,
  searchSize: 'small',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  props: {
    label: 'label', value: 'value'
  },
  column: [{
    label: '用户信息', prop: 'user', search: false, sortable: false
  }, {
    label: '序号', prop: 'orderId', search: false, sortable: true
  }, {
    label: '用户编号', prop: 'userId', search: true, sortable: true
  }, {
    label: '订单编号', prop: 'orderNumber', search: false, sortable: true
  }, {
    label: '商品名称', prop: 'prodName', search: false, sortable: true
  }, {
    label: '备注', prop: 'remarks', search: false, sortable: true
  }, {
    label: '是否支付', prop: 'isPayed', search: false, type: 'select', slot: true, dicData: [{
      label: '未支付', value: 0
    }, {
      label: '已支付', value: 1
    }], sortable: true
  }, {
    label: '支付方式', prop: 'payType', search: false, type: 'select', slot: true, dicData: [{
      label: '手动支付', value: 0
    }, {
      label: '微信支付', value: 1
    }, {
      label: '支付宝', value: 2
    }], sortable: true
  }, {
    label: '订单状态', prop: 'status', search: false, type: 'select', slot: true, dicData: [{
      label: '预支付订单', value: 0
    }, {
      label: '待付款', value: 1
    }, {
      label: '待发货', value: 2
    }, {
      label: '待收货', value: 3
    }, {
      label: '待评价', value: 4
    }, {
      label: '成功', value: 5
    }, {
      label: '失败', value: 6
    }], sortable: true
  }, {
    label: '下单时间', prop: 'createTime', imgWidth: 150, sortable: true
  }, {
    label: '支付时间', prop: 'payTime', imgWidth: 150
  }, {
    label: '实际支付金额', prop: 'actualTotal', imgWidth: 150
  }, {
    label: '储值金额', prop: 'total', imgWidth: 150
  }, {
    label: '优惠金额', prop: 'reduceAmount', imgWidth: 150
  }, {
    label: '发货状态', prop: 'shipTowx', imgWidth: 150
  }]
}
