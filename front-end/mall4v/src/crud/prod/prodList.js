export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: true,
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
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '产品名字',
    prop: 'prodName',
    sortable: 'custom',
    search: true
  }, {
    label: '商品原价',
    prop: 'oriPrice',
    sortable: 'custom'
  }, {
    label: '商品现价',
    prop: 'price',
    sortable: 'custom'
  }, {
    label: '商品库存',
    prop: 'totalStocks',
    sortable: 'custom'
  }, {
    label: '商品销量',
    prop: 'totalSales',
    sortable: 'custom'
  }, {
    label: '产品图片',
    prop: 'pic',
    type: 'upload',
    width: 150,
    listType: 'picture-img'
  }, {
    width: 150,
    sortable: 'custom',
    label: '状态',
    prop: 'status',
    search: true,
    slot: true,
    type: 'select',
    dicData: [
      {
        label: '未上架',
        value: 0
      }, {
        label: '上架',
        value: 1
      }
    ]
  }]
}
