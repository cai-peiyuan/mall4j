<template>
  <div class="mod-hotSearcch">
    <avue-crud
      ref="crudRef"
      :page="page"
      :data="dataList"
      :table-loading="dataListLoading"
      :option="tableOption"
      @search-change="onSearch"
      @on-load="getDataList"
      @refresh-change="refreshChange"
      @selection-change="selectionChange"
@sort-change="sortChange"
    >
      <template #menu-left>
        <el-button
          v-if="isAuth('order:refund:save')"
          type="primary"
          icon="el-icon-plus"
          @click="onAddOrUpdate()"
        >
          新增
        </el-button>
        <el-button
          type="danger"
          :disabled="dataListSelections.length <= 0"
          @click.stop="onDeconste"
        >
          批量删除
        </el-button>
      </template>

      <template #status="scope">
        <el-tag
          v-if="scope.row.status === 0"
          type="danger"
        >
          未启用
        </el-tag>
        <el-tag v-else>
          启用
        </el-tag>
      </template>

      <template #menu="scope">

        <el-popconfirm v-if="isAuth('order:refund:accept') && scope.row.refundSts == 1"
                       confirm-button-text="确定" cancel-button-text="取消" :icon="InfoFilled" icon-color="#626AEF"
                       title="确定执行退款操作？" @confirm="acceptRefund(scope.row.refundId)" @cancel="cancelEvent">
          <template #reference>
            <el-button icon="el-icon-edit" type="primary"
                       link >
              退款
            </el-button>
          </template>
        </el-popconfirm>

        <el-button
          v-if="isAuth('order:refund:reject') && scope.row.refundSts == 1"
          type="danger"
          link
          icon="el-icon-deconste"
          @click.stop="rejectRefund(scope.row, scope.index)"
        >
          拒绝
        </el-button>
      </template>
    </avue-crud>

    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="getDataList"
    />
  </div>
</template>

<script setup>
import { isAuth } from '@/utils'
import { ElMessage, ElMessageBox } from 'element-plus'
import { tableOption } from '@/crud/order/orderRefund.js'
import AddOrUpdate from './add-or-update.vue'
const dataList = ref([])
const page = reactive({
  orderField: '',
  order: '',
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const dataListLoading = ref(false)

/**
 * 审批同意退款
 * @param id
 */
const acceptRefund = (refundId) => {
  console.log('审批同意退款', refundId)
  // 后台接口验证按钮权限  order:refund:accept
  http({
    url: http.adornUrl('/order/refund/accept/'+refundId),
    method: 'get',
    data: {

    }
  }).then(() => {
      ElMessage({
        message: '操作成功',
        type: 'success',
        duration: 1500,
        onClose: () => {
          getDataList()
        }
      })
    })
}
/**
 * 审批拒绝退款
 * @param id
 */
const rejectRefund = (id, index) => {
  console.log('审批拒绝退款', id)
}

const cancelEvent = () => {
  console.log('cancel!')
}

/**
 * 获取数据列表
 */
const getDataList = (pageParam, params, done) => {
  dataListLoading.value = true
  http({
    url: http.adornUrl('/order/refund/page'),
    method: 'get',
    params: http.adornParams(Object.assign({
      current: pageParam ? pageParam.currentPage : 1,
      size: pageParam ? pageParam.pageSize : 20,
      orderField: pageParam == null ? page.orderField : pageParam.orderField,
      order: pageParam == null ? page.order : pageParam.order
    }, params))
  })
    .then(({ data }) => {
      page.total = data.total
      page.pageSize = data.size
      page.currentPage = data.current
      dataList.value = data.records
      dataListLoading.value = false
      if (done) done()
    })
}
const dataListSelections = ref([])
/**
 * 多选回调
 * @param list
 */
const selectionChange = (list) => {
  dataListSelections.value = list
}

const addOrUpdateVisible = ref(false)
const addOrUpdateRef = ref(null)
/**
 * 新增 / 修改
 * @param id
 */
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}

/**
 * 点击查询
 */
const onSearch = (params, done) => {
  getDataList(page, params, done)
}

/**
 * 删除
 */
const onDeconste = (row) => {
  const ids = row.refundId ? [row.refundId] : dataListSelections.value?.map(item => {
    return item.refundId
  })
  ElMessageBox.confirm(`确定进行[${row.id ? '删除' : '批量删除'}]操作?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      http({
        url: http.adornUrl('/order/refund'),
        method: 'delete',
        data: http.adornData(ids, false)
      })
        .then(() => {
          ElMessage({
            message: '操作成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              getDataList()
            }
          })
        })
    }).catch(() => { })
}
const refreshChange = () => {
  getDataList(page)
}

</script>
