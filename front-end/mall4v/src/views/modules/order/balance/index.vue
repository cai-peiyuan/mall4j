<template>
  <div class="mod-user">
    <avue-crud ref="crudRef" :page="page" :data="dataList" :option="tableOption"
               :search="searchParam"
               @search-change="onSearch"
               @selection-change="selectionChange" @on-load="getDataList">
      <template #menu-left>
        <el-button
          v-if="true"
          type="primary"
          icon="el-icon-plus"
          @click="addUser()"
        >
          选择用户
        </el-button>
      </template>

      <template #user="scope">
        <el-table-column
          prop="user.userMobile"
          header-align="center"
          align="center"
          label="会员手机号"
        />
        <el-table-column
          prop="user.nickName"
          header-align="center"
          align="center"
          label="用户昵称"
        />
        <el-table-column
          align="center"
          width="60"
          label="头像"
        >
          <template #default="scope">
            <img
              alt=""
              :src="scope.row.user.pic"
              width="25"
              height="25"
            >
          </template>
        </el-table-column>
      </template>

      <template #isPayed="scope">
        <el-tag
          v-if="scope.row.isPayed === 0"
          type="danger"
        >
          未支
        </el-tag>
        <el-tag v-else>
          已支
        </el-tag>
      </template>

      <template #shipTowx="scope">
        <el-tag
          v-if="scope.row.shipTowx === 0"
          type="danger"
        >
          未推送到账
        </el-tag>
        <el-tag v-else>
          已推送到账
        </el-tag>
      </template>

      <template #menu="scope">
        <el-popconfirm v-if="isAuth('order:balance:delivery') && scope.row.isPayed == 1 && scope.row.shipTowx == 0"
                       confirm-button-text="确定" cancel-button-text="取消" :icon="InfoFilled" icon-color="#626AEF"
                       title="确定发送虚拟发货信息到腾讯公众平台" @confirm="confirmEvent(scope.row.orderNumber)"
                       @cancel="cancelEvent">
          <template #reference>
            <el-button type="primary" icon="el-icon-edit" @click.stop="orderDelivery(scope.row.orderNumber)">
              手动发货
            </el-button>
          </template>
        </el-popconfirm>

        <el-popconfirm v-if="isAuth('order:balance:refund') && scope.row.isPayed == 1"
                       confirm-button-text="确定" cancel-button-text="取消" :icon="InfoFilled" icon-color="#626AEF"
                       title="确定退款" @confirm="confirmEvent(scope.row.orderNumber)"
                       @cancel="cancelEvent">
          <template #reference>
            <el-button type="primary" icon="el-icon-edit" @click.stop="orderRefund(scope.row.orderNumber)">
              订单退款
            </el-button>
          </template>
        </el-popconfirm>
      </template>

    </avue-crud>

    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdateRef" @refresh-data-list="getDataList"/>

    <!-- 商品选择弹窗-->
    <users-select
      v-if="usersSelectVisible"
      ref="usersSelectRef"
      :is-single="true"
      @refresh-select-users="selectUsers"
    />

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

const usersSelectVisible = ref(false)
const usersSelectRef = ref(null)
const crudRef = ref(null)

const dataList = ref([])
const dataListLoading = ref(false)
const dataListSelections = ref([])
const addOrUpdateVisible = ref(false)
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})


const searchParam = reactive({
  userId: '', // 用户编号
})
const picSrcList = ref([])

/**
 * 选择指定用户
 */
const selectUsers = (users) => {
  console.log('选中的用户信息', users)
  console.log('表格对象crudRef', crudRef)
  if (users.length > 0) {
    searchParam.userId = users[0].userId
  } else {
    searchParam.userId = ''
  }
  onSearch(searchParam)
}

/**
 * 打开选择用户
 */
const addUser = () => {
  usersSelectVisible.value = true
  console.log(usersSelectRef)
  nextTick(() => {
    if (searchParam.userId == '') {
      usersSelectRef.value?.init([])
    } else {
      usersSelectRef.value?.init([searchParam.userId])
    }
  })
}

/**
 * 订单发货 手动发货给腾讯平台
 * @param orderNumber
 */
const orderDelivery = (orderNumber) => {
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
      onSearch(searchParam)
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
    url: http.adornUrl('/admin/balance/order/page'), method: 'get', params: http.adornParams(Object.assign({
      current: pageParam == null ? page.currentPage : pageParam.currentPage,
      size: pageParam == null ? page.pageSize : pageParam.pageSize
    }, params))
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
 * 订单退款
 * @param orderNumber
 */
const orderRefund = (orderNumber) => {
  console.log('confirm!', orderNumber)
  http({
    url: http.adornUrl('/admin/balance/order/orderRefund/' + orderNumber), method: 'get', params: http.adornParams()
  })
    .then(({
             data
           }) => {
      console.log('充值订单退款请求返回值', data)
      onSearch(searchParam)
    })
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
