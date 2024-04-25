export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  index: false,
  selection: true,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  align: 'center',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  column: [{
    label: '记录ID', prop: 'refundId', search: true
  }, {
    label: '订单ID', prop: 'orderId', search: true
  }, {
    label: '订单流水号', prop: 'orderNumber', sortable: 'custom'
  }, {
    label: '订单总金额', prop: 'orderAmount', sortable: 'custom'
  }, {
    label: '订单项ID 全部退款是0', prop: 'orderItemId', sortable: 'custom'
  }, {
    label: '订单支付单号（内部）', prop: 'orderPayNo', sortable: 'custom'
  }, {
    label: '订单支付交易号（支付平台）', prop: 'bizPayNo', sortable: 'custom'
  }, {
    label: '退款编号', prop: 'refundSn', sortable: 'custom'
  }, {
    label: '订单支付流水号', prop: 'flowTradeNo', sortable: 'custom'
  }, {
    label: '第三方退款单号(微信退款单号)', prop: 'outRefundNo', sortable: 'custom'
  }, {
    label: '买家ID', prop: 'userId', sortable: 'custom'
  }, {
    label: '退货数量', prop: 'goodsNum', sortable: 'custom'
  }, {
    label: '退款金额', prop: 'refundAmount', sortable: 'custom'
  }, {
    label: '申请类型', prop: 'applyType', type: 'select', slot: true, search: true, dicData: [{
      label: '仅退款', value: 1
    }, {
      label: '退款退货', value: 2
    }]
  }, {
    label: '处理状态', prop: 'refundSts', type: 'select', slot: true, search: true, dicData: [{
      label: '待审核', value: 1
    }, {
      label: '同意', value: 2
    }, {
      label: '不同意', value: 2
    }]
  }, {
    label: '处理退款状态', prop: 'returnMoneySts', type: 'select', slot: true, search: true, dicData: [{
      label: '退款处理中', value: 0
    }, {
      label: '退款成功', value: 1
    }, {
      label: '退款失败', value: -1
    }]
  }, {
    label: '支付方式', prop: 'payType', type: 'select', slot: true, search: true, dicData: [{
      label: '微信支付', value: 1
    }, {
      label: '支付宝', value: 2
    }]
  }, {
    label: '申请时间', prop: 'applyTime', sortable: 'custom'
  }, {
    label: '卖家处理时间', prop: 'handelTime', sortable: 'custom'
  }, {
    label: '退款时间', prop: 'refundTime', sortable: 'custom'
  }, {
    label: '文件凭证', prop: 'photoFiles', sortable: 'custom'
  }, {
    label: '申请原因', prop: 'refundReason', sortable: 'custom'
  }, {
    label: '卖家备注', prop: 'sellerMsg', sortable: 'custom'
  }, {
    label: '物流公司名称', prop: 'expressName', sortable: 'custom'
  }, {
    label: '物流单号', prop: 'expressNo', sortable: 'custom'
  }, {
    label: '发货时间', prop: 'shipTime', sortable: 'custom'
  }, {
    label: '收货时间', prop: 'receiveTime', sortable: 'custom'
  }, {
    label: '收货备注', prop: 'receiveMessage', sortable: 'custom'
  }, {
    label: '拒绝原因', prop: 'rejectMessage', sortable: 'custom'
  },]
}
