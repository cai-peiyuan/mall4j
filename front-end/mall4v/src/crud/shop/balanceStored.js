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
    label: '储值卡序列号', prop: 'storedSn', search: true, sortable: 'custom'
  }, {
    label: '创建时间', prop: 'createTime', search: true, sortable: 'custom'
  }, {
    label: '储值金额', prop: 'storedValue', sortable: 'custom'
  }, {
    label: '状态', prop: 'status', type: 'select', slot: true, search: true, dicData: [{
      label: '未销售', value: 0
    }, {
      label: '已售未用', value: 1
    }, {
      label: '已使用', value: 2
    }], sortable: 'custom'
  }, {
    label: '售价', prop: 'sellValue', sortable: 'custom'
  }, {
    label: '销售时间', prop: 'sellTime', sortable: 'custom'
  }, {
    label: '备注', prop: 'remark', sortable: 'custom'
  }, {
    label: '备注', prop: 'remark', sortable: 'custom'
  }, {
    label: '储值时间', prop: 'storedTime', sortable: 'custom'
  }, {
    label: '储值手机号', prop: 'storedUserMobile', sortable: 'custom'
  },]
}
