export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: true,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 350,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '轮播图片',
    prop: 'imgUrl',
    type: 'upload',
    slot: true,
    listType: 'picture-img'
  }, {
    label: '顺序',
    prop: 'seq'
  }, {
    label: '描述',
    prop: 'des'
  }, {
    label: '标题',
    prop: 'title'
  }, {
    label: '链接',
    prop: 'link'
  }, {
    width: 150,
    label: '点击类型',
    prop: 'type',
    search: true,
    type: 'select',
    dicData: [
      {
        label: '无',
        value: -1
      }, {
        label: '商品',
        value: 0
      }, {
        label: '链接',
        value: 1
      }
    ]
  }, {
    width: 150,
    label: '状态',
    prop: 'status',
    search: true,
    type: 'select',
    dicData: [
      {
        label: '禁用',
        value: 0
      }, {
        label: '正常',
        value: 1
      }
    ]
  }]
}
