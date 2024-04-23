export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: true,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 180,
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
    label: '用户编号', prop: 'userId', search: true
  }, {
    label: '应用编号', prop: 'clientId'
  }, {
    label: '应用密钥', prop: 'clientSecret'
  }, {
    label: '打印机编号', prop: 'machineCode'
  }, {
    label: '打印机签名', prop: 'msign'
  }, {
    label: '设备状态', prop: 'machineState'
  }, {
    label: '打印机型号', prop: 'version'
  }, {
    label: '打印宽度', prop: 'printWidth'
  }, {
    label: '硬件版本', prop: 'version'
  }, {
    label: '软件版本', prop: 'software'
  }, {
    label: '备注', prop: 'remark'
  }]
}
