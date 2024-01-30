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
  column: [
    {
      label: '退款名称',
      prop: 'refundName',
      search: true
    },
    {
      label: '顺序',
      prop: 'seq',
      sortable: true
    },
    {
      label: '启用状态',
      prop: 'status',
      type: 'select',
      slot: true,
      search: true,
      dicData: [
        {
          label: '未启用',
          value: 0
        }, {
          label: '启用',
          value: 1
        }
      ]
    }
  ]
}
