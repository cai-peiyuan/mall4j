<template>
  <div class="mod-user">
    <avue-crud ref="crudRef" :page="page" :data="dataList" :option="tableOption" @search-change="onSearch"
      @selection-change="selectionChange" @on-load="getDataList">
      <template #isPayed="scope">
        <el-tag
          v-if="scope.row.isPayed === 0"
          type="danger"
        >
          未支付
        </el-tag>
        <el-tag v-else>
          已支付
        </el-tag>
      </template>
      <template #shipTowx="scope">
        <el-tag
          v-if="scope.row.isPayed === 0"
          type="danger"
        >
          未发货
        </el-tag>
        <el-tag v-else>
          已发货
        </el-tag>
      </template>
      <template #menu="scope">
        <el-popconfirm v-if="isAuth('order:balance:delivery') && scope.row.isPayed == 1 && scope.row.shipTowx == 0"
          confirm-button-text="确定" cancel-button-text="取消" :icon="InfoFilled" icon-color="#626AEF"
          title="确定发送虚拟发货信息到腾讯公众平台" @confirm="confirmEvent(scope.row.orderNumber)" @cancel="cancelEvent">
          <template #reference>
            <el-button type="primary" icon="el-icon-edit" @click.stop="orderDelivery(scope.row.orderNumber)">
              手动发货
            </el-button>
          </template>
        </el-popconfirm>
      </template>
    </avue-crud>

    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdateRef" @refresh-data-list="getDataList" />
  </div>
</template>

<script setup>
  import {
    isAuth
  } from '@/utils'
  import {
    tableOption
  } from '@/crud/balance/balanceOrder.js'
  import AddOrUpdate from './add-or-update.vue'

  const dataList = ref([])
  const dataListLoading = ref(false)
  const dataListSelections = ref([])
  const addOrUpdateVisible = ref(false)
  const page = reactive({
    total: 0, // 总页数
    currentPage: 1, // 当前页数
    pageSize: 10 // 每页显示多少条
  })
  const picSrcList = ref([])

  const confirmEvent = (orderNumber) => {
    console.log('confirm!', orderNumber)
    http({
        url: http.adornUrl('/admin/balance/order/sendDeliveryInfo/' + orderNumber),
        method: 'get',
        params: http.adornParams()
      })
      .then(({
        data
      }) => {
        console.log('虚拟发货数据返回', data)
        onSearch()
      })
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
        url: http.adornUrl('/admin/balance/order/page'),
        method: 'get',
        params: http.adornParams(
          Object.assign({
              current: pageParam == null ? page.currentPage : pageParam.currentPage,
              size: pageParam == null ? page.pageSize : pageParam.pageSize
            },
            params
          )
        )
      })
      .then(({
        data
      }) => {
        dataList.value = data.records
        page.total = data.total
        dataListLoading.value = false
        picSrcList.value = []
        data.records.forEach((row, idx) => {
          // picSrcList.value[idx] = row.pic
        })
        if (done) done()
      })
  }

  /**
   * 手动发货给腾讯平台
   * @param id
   */
  const orderDelivery = (id) => {

  }

  /**
   * 条件查询
   */
  const onSearch = (params, done) => {
    getDataList(page, params, done)
  }

  /**
   * 多选变化
   */
  const selectionChange = (val) => {
    dataListSelections.value = val
  }
</script>

<style lang="scss" scoped>

</style>
