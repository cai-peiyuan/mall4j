export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true, // selection: true,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 120,
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
    label: '注册手机号', prop: 'userMobile', sortable: 'custom', search: true
  }, {
    label: '积分', prop: 'score', sortable: 'custom'
  }, {
    label: '用户昵称', prop: 'nickName', sortable: 'custom', search: true
  }, {
    label: '用户头像', prop: 'pic', type: 'upload', imgWidth: 150, listType: 'picture-img', slot: true
  }, {
    label: '注册时间', prop: 'userRegtime', sortable: 'custom'
  }, {
    label: '最后使用小程序时间', prop: 'userLasttime', sortable: 'custom'
  }, {
    label: '状态', prop: 'status', search: true, type: 'select', slot: true, dicData: [{
      label: '禁用', value: 0
    }, {
      label: '正常', value: 1
    }], sortable: 'custom'
  }, {
    label: '用户身份', prop: 'isStaff', search: true, type: 'select', slot: true, dicData: [{
      label: '会员', value: 0
    }, {
      label: '内部', value: 1
    }], sortable: 'custom'
  }, {
    label: '性别', prop: 'sex', search: true, type: 'select', slot: true, dicData: [{
      label: '男', value: 'M'
    }, {
      label: '女', value: 'F'
    }]
  }]
}
