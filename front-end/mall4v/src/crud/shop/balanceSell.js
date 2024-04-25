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
    label: '店铺编号', prop: 'shopId', search: true, sortable: 'custom'
  }, {
    label: '储值到账金额', prop: 'storedValue', search: true, sortable: 'custom'
  }, {
    label: '售价', prop: 'sellValue', search: true, sortable: 'custom'
  }, {
    label: '已售次数', prop: 'sellCnt', sortable: 'custom'
  }, {
    label: '启用状态', prop: 'status', type: 'select', slot: true, search: true, dicData: [{
      label: '未上架', value: 0
    }, {
      label: '可售卖', value: 1
    }, {
      label: '已售罄', value: 1
    }], sortable: 'custom'
  }]
}
