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
          v-if="isAuth('shop:balanceSell:save')"
          type="primary"
          icon="el-icon-plus"
          @click="onAddOrUpdate()"
        >
          新增
        </el-button>
        <el-button
          type="danger"
          :disabled="dataListSelections.length <= 0"
          @click.stop="onDelete"
        >
          批量删除
        </el-button>
      </template>

      <template #status="scope">
        <el-tag
          v-if="scope.row.status === 0"
          type="warning"
        >
          未销售
        </el-tag>
        <el-tag v-if="scope.row.status === 1"
                type="success">
          可销售
        </el-tag>
        <el-tag v-if="scope.row.status === 2"
                type="danger">
          已售罄
        </el-tag>
      </template>

      <template #menu="scope">
        <el-button
          v-if="isAuth('shop:balanceSell:update')"
          type="primary"
          icon="el-icon-edit"
          @click="onAddOrUpdate(scope.row.id)"
        >
          修改
        </el-button>
        <el-button
          v-if="isAuth('shop:balanceSell:delete')"
          type="danger"
          icon="el-icon-deconste"
          @click.stop="onDelete(scope.row,scope.index)"
        >
          删除
        </el-button>
      </template>
    </avue-crud>

    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="onSearch"
    />
  </div>
</template>

<script setup>
import {isAuth} from '@/utils'
import {ElMessage, ElMessageBox} from 'element-plus'
import {tableOption} from '@/crud/shop/balanceSell.js'
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
 * 获取数据列表
 */
const getDataList = (pageParam, params, done) => {
  dataListLoading.value = true
  http({
    url: http.adornUrl('/shop/balanceSell/page'),
    method: 'get',
    params: http.adornParams(Object.assign({
      current: pageParam ? pageParam.currentPage : 1,
      size: pageParam ? pageParam.pageSize : 20,
      orderField: pageParam == null ? page.orderField : pageParam.orderField,
      order: pageParam == null ? page.order : pageParam.order
    }, params))
  })
    .then(({data}) => {
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
const onDelete = (row) => {
  const ids = row.id ? [row.id] : dataListSelections.value?.map(item => {
    return item.id
  })
  ElMessageBox.confirm(`确定进行[${row.id ? '删除' : '批量删除'}]操作?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      http({
        url: http.adornUrl('/shop/balanceSell'),
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
    }).catch(() => {
  })
}
const refreshChange = () => {
  getDataList(page)
}
/**
 * 排序变化
 */
const sortChange = ({column, prop, order}) => {
  console.log('表格排序条件变化', column, prop, order)
  page.orderField = prop;
  page.order = order;
  getDataList(page, {}, null)
}
</script>
