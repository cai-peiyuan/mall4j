export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true, // selection: true,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 200,
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
    label: '订单编号', prop: 'orderNumber', search: false, sortable: false
  }, {
    label: '用户信息', prop: 'user', search: false, sortable: false
  }, {
    label: '商品名称', prop: 'prodName', search: false, sortable: false
  }, {
    label: '备注', prop: 'remarks', search: false, sortable: false
  }, {
    label: '是否支付', prop: 'isPayed', search: false, type: 'select', slot: true, dicData: [{
      label: '未支付', value: 0
    }, {
      label: '已支付', value: 1
    }], sortable: false
  }, {
    label: '支付方式', prop: 'payType', search: false, type: 'select', slot: true, dicData: [{
      label: '手动支付', value: 0
    }, {
      label: '微信支付', value: 1
    }, {
      label: '支付宝', value: 2
    }], sortable: false
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
    }], sortable: false
  }, {
    label: '下单时间', prop: 'createTime', sortable: false
  }, {
    label: '支付时间', prop: 'payTime',
  }, {
    label: '实际支付金额', prop: 'actualTotal',
  }, {
    label: '储值金额', prop: 'total',
  }, {
    label: '发货状态', prop: 'shipTowx', width: 150
  }]
}
