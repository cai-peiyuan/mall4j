<template>
  <div class="mod-order-order">
    <el-form
      :inline="true"
      :model="dataForm"
      @keyup.enter="getDataList(page)"
    >
      <el-form-item label="会员编号:">
        <el-input
          v-model="dataForm.userId"
          placeholder="会员编号"
          clearable
        />
      </el-form-item>

      <el-form-item label="订单编号:">
        <el-input
          v-model="dataForm.orderNumber"
          placeholder="订单编号"
          clearable
        />
      </el-form-item>

      <el-form-item label="下单时间:">
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="至"
          value-format="YYYY-MM-DD HH:mm:ss"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item label="订单状态:">
        <el-select
          v-model="dataForm.status"
          clearable
          placeholder="请选择订单状态"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          plain
          icon="el-icon-user"
          @click="selectUser()"
        >
          选择用户
        </el-button>
        <el-button
          type="primary"
          icon="el-icon-search"
          @click="getDataList()"
        >
          查询
        </el-button>
        <el-button
          v-if="isAuth('order:order:waitingConsignmentExcel')"
          type="primary"
          @click="showConsignmentInfo()"
        >
          导出待发货订单
        </el-button>
        <el-button
          v-if="isAuth('order:order:soldExcel')"
          type="primary"
          @click="getSoldExcel()"
        >
          导出销售记录
        </el-button>
        <el-button @click="clearDatas()">
          清空
        </el-button>
      </el-form-item>
    </el-form>
    <div class="main">
      <div class="content">
        <div class="tit">
          <el-row style="width:100%">
            <!--            <el-col :span="2">
                          <el-checkbox v-model="checkedAll"></el-checkbox>
                          <span class="item product">选择</span>
                        </el-col>-->
            <el-col :span="6">
              <span class="item product">商品</span>
            </el-col>
            <el-col :span="3">
              <span class="item">成交单价/购买数量</span>
            </el-col>
            <el-col :span="2">
              <span class="item">实付金额</span>
            </el-col>
            <el-col :span="2">
              <span class="item">支付</span>
            </el-col>
            <el-col :span="2">
              <span class="item">订单状态</span>
            </el-col>
            <el-col :span="2">
              <span class="item">下单用户</span>
            </el-col>
            <el-col :span="2">
              <span class="item">物流</span>
            </el-col>
            <el-col :span="2">
              <span class="item">操作</span>
            </el-col>
            <el-col :span="2">
              <span class="item">备注</span>
            </el-col>
          </el-row>
        </div>
        <div
          v-for="order in dataList"
          :key="order.orderId"
          class="prod"
        >
          <div class="prod-tit">
            <span>订单编号：{{ order.orderNumber }}</span>
            <span>下单时间：{{ order.createTime }}</span>
          </div>
          <div class="prod-cont">
            <el-row style="width:100%">

              <!--              <el-col :span="1">
                              <div class="item">
                                <el-checkbox v-model="checkedMap[order.orderNumber]"> =</el-checkbox>
                              </div>
                            </el-col>-->
              <el-col :span="9">
                <div class="prod-item">
                  <div
                    v-for="orderItem in order.orderItems"
                    :key="orderItem.orderItemId"
                    class="items name"
                  >
                    <div class="prod-image">
                      <img
                        alt=""
                        :src="resourcesUrl + orderItem.pic"
                        style="height:60px;width: 60px;"
                      >
                    </div>

                    <div class="prod-name">
                      <span>{{ orderItem.prodName }}</span>
                      <span class="prod-info">{{ orderItem.skuName }}</span>
                    </div>

                    <div class="prod-price">
                      <span>￥{{ orderItem.price }}</span>
                      <span>×{{ orderItem.prodCount }}</span>
                    </div>

                  </div>
                </div>
              </el-col>

              <!-- 支付金额 -->
              <el-col
                :span="2"
                style="height: 100%;"
              >
                <div class="item">
                  <div>
                    <span class="totalprice">￥{{ order.actualTotal }}</span>
                    <span v-if="order.freightAmount">（含运费：￥{{ order.freightAmount }}）</span>
                    <span>共{{ order.productNums }}件</span>
                  </div>

                  <div>
                    <span v-if="order.payType === 0">余额支付</span>
                    <span v-else-if="order.payType === 1">微信支付</span>
                    <span v-else-if="order.payType === 2">支付宝</span>
                    <span v-else-if="order.payType === 3">储值余额</span>
                    <span v-else-if="order.payType == null">未支付</span>
                    <span v-else>其他支付方式 {{ order.payType }}</span>


                    <span v-if="order.isPayed === 1">已支付</span>
                    <span v-if="order.refundSts === 1">退款正在处理</span>
                    <span v-if="order.refundSts === 2">退款已到账</span>
                  </div>

                </div>
              </el-col>

              <!-- 支付方式 -->
              <el-col
                :span="2"
                style="height: 100%;"
              >
                <div class="item">
                  <div>
                    <span v-if="order.payType === 0">余额支付</span>
                    <span v-else-if="order.payType === 1">微信支付</span>
                    <span v-else-if="order.payType === 2">支付宝</span>
                    <span v-else-if="order.payType === 3">储值余额</span>
                    <span v-else-if="order.payType == null">未支付</span>
                    <span v-else>其他支付方式 {{ order.payType }}</span>


                    <span v-if="order.isPayed === 1">已支付</span>
                    <span v-if="order.refundSts === 1">退款正在处理</span>
                    <span v-if="order.refundSts === 2">退款已到账</span>
                  </div>
                </div>
              </el-col>
              <!-- 订单状态 -->
              <el-col
                :span="2"
                style="height: 100%;"
              >
                <div class="item">
                  <span
                    v-if="order.status === 1"
                    type="danger"
                  >待付款</span>
                  <span
                    v-else-if="order.status === 2"
                    type="danger"
                  >待发货</span>
                  <span
                    v-else-if="order.status === 3"
                    type="danger"
                  >待收货</span>
                  <span
                    v-else-if="order.status === 4"
                    type="danger"
                  >待评价</span>
                  <span
                    v-else-if="order.status === 5"
                    type="danger"
                  >成功</span>
                  <span
                    v-else
                  >订单关闭</span>


                  <view v-if="order.status === 6" style="color: #cc0000">
                    <span
                      v-if="order.closeType === 1"
                      type="danger"
                    >(超时未支付)</span>
                    <span
                      v-else-if="order.closeType === 2"
                      type="danger"
                    >(退款关闭)</span>
                    <span
                      v-else-if="order.closeType === 4"
                      type="danger"
                    >(买家取消)</span>
                    <span
                      v-else-if="order.closeType === 15"
                      type="danger"
                    >(已通过货到付款交易)</span>
                    <span
                      v-else
                    >{{ order.closeType }}</span>
                  </view>
                </div>
              </el-col>
              <!-- 下单用户，购买人信息 -->
              <el-col
                :span="2"
                style="height: 100%;"
              >
                <div class="item">
                  <div>
                    <span>
                      <div v-if="order.userInfo.pic != null && order.userInfo.pic != ''">
                        <img
                          alt="会员头像"
                          :src="order.userInfo.pic"
                          style="height:60px;width: 60px;"
                        >
                      </div>
                    </span>
                    <span>{{ order.userInfo.nickName }}</span>
                    <span>{{ order.userInfo.realName }}</span>
                    <span>{{ order.userInfo.userMobile }}</span>
                    <span
                      v-if="order.userInfo.nickName == null"
                      style="word-break: break-word;"
                    >{{ order.userInfo.userId }}</span>
                  </div>
                </div>
              </el-col>
              <el-col
                :span="2"
                style="height: 100%;"
              >
                <div class="item" style="font-size: 10px">
                  <div>
                    <span>{{ order.userAddrOrder.receiver }}</span>
                    <span>{{ order.userAddrOrder.mobile }}</span>
                    <span>{{ order.userAddrOrder.province }}{{ order.userAddrOrder.city }}{{
                        order.userAddrOrder.area
                      }}{{ order.userAddrOrder.addr }}</span>
                    <span v-if="order.dvyId != null" style="font-size: 12px; font-weight: bold; color: #cc0000">
                      <span>已发货物流单号：</span>
                      <span>{{ order.dvyFlowId }}</span>
                    </span>
                  </div>
                </div>
              </el-col>
              <el-col
                :span="2"
                style="height: 100%;"
              >
                <div class="item">
                  <div class="operate">
                    <el-button
                      v-if="isAuth('order:order:update')"
                      type="primary"
                      link
                      @click="onAddOrUpdate(order.orderNumber)"
                    >
                      查看
                    </el-button>
                    <br>
                    <el-button
                      v-if="isAuth('order:order:delivery') && order.status == 2"
                      type="primary"
                      link
                      @click="changeOrder(order)"
                    >
                      发货
                    </el-button>
                    <br>
                    <el-button
                      v-if="isAuth('order:order:delivery') && order.status == 3"
                      type="primary"
                      link
                      @click="arriveOrder(order)"
                    >
                      订单送达
                    </el-button>
                    <br>
                    <el-button
                      v-if="isAuth('order:order:refund') && order.refundSts == 0 && order.isPayed == 1 && order.payType == 1"
                      type="primary"
                      link
                      @click="orderRefund(order)"
                    >
                      退款
                    </el-button>
                    <br>
                    <el-button
                      v-if="order.printTimes> 0"
                      type="primary"
                      link
                      @click="printOrder(order.orderNumber)"
                    >已打印次数：{{ order.printTimes }}
                    </el-button>
                    <br>
                    <el-button
                      v-if="isAuth('order:order:print') && order.printTimes == 0"
                      type="primary"
                      link
                      @click="printOrder(order.orderNumber)"
                    >
                      打印订单
                    </el-button>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          <div class="remark">
            <div class="buyer-remark">
              <span>备注:{{ order.remarks }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 空 -->
    <div
      v-if="!dataList.length"
      class="empty-tips"
    >
      暂无数据
    </div>
    <el-pagination
      :current-page="page.pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="page.pageSize"
      :total="page.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
    />
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="getDataList"
    />
    <consignment-info
      v-if="consignmentInfoVisible"
      ref="consignmentInfoRef"
      @input-callback="getWaitingConsignmentExcel"
    />
    <!-- 弹窗, 新增 / 修改 -->
    <devy-add
      v-if="devyVisible"
      ref="devyAddRef"
      @refresh-data-list="getDataList"
    />
    <!-- 退款 -->
    <refund-add
      v-if="refundVisible"
      ref="orderRefundRef"
      @refresh-data-list="getDataList"
    />
    <!-- 送达 -->
    <order-arrive
      v-if="arriveVisible"
      ref="orderArriveRef"
      @refresh-data-list="getDataList"
    />

    <!-- 会员选择弹窗-->
    <users-select
      v-if="usersSelectVisible"
      ref="usersSelectRef"
      :is-single="true"
      @refresh-select-users="selectUsersCallback"
    />
  </div>
</template>

<script setup>
import DevyAdd from './components/order-devy.vue'
import RefundAdd from './components/order-refund.vue'
import OrderArrive from './components/order-arrive.vue'
import AddOrUpdate from './components/order-info.vue'
import ConsignmentInfo from './components/consignment-info.vue'
import {isAuth} from '@/utils'
import {ElMessage} from 'element-plus'

const resourcesUrl = import.meta.env.VITE_APP_RESOURCES_URL
const dataForm = ref({})
const checkedMap = ref({})
const dateRange = ref([])
const checkedAll = ref(false)
const devyVisible = ref(false)
const refundVisible = ref(false)
const arriveVisible = ref(false)
const options = [{
  value: 1, label: '待付款'
}, {
  value: 2, label: '待发货'
}, {
  value: 3, label: '待收货'
}, {
  value: 4, label: '待评价'
}, {
  value: 5, label: '成功'
}, {
  value: 6, label: '订单关闭'
}]
const dataList = ref([])
const page = reactive({
  orderField: '',
  order: '',
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
onMounted(() => {
  getDataList(page)
})
const devyAddRef = ref(null)
const orderRefundRef = ref(null)
const orderArriveRef = ref(null)

const usersSelectVisible = ref(false)
const usersSelectRef = ref(null)
const searchParam = reactive({
  userId: '', // 用户编号
})

/**
 * 发货
 * @param order
 */
const changeOrder = (order) => {
  devyVisible.value = true
  nextTick(() => {
    console.log(devyAddRef)
    devyAddRef.value?.init(order.orderNumber, order.dvyId, order.dvyFlowId)
  })
}

/**
 * 订单送达
 * @param order
 */
const arriveOrder = (order) => {
  arriveVisible.value = true
  nextTick(() => {
    orderArriveRef.value?.init(order.orderNumber, order.dvyId, order.dvyFlowId)
  })
}

/**
 * 显示退款选项对话框
 * @param order
 */
const orderRefund = (order) => {
  refundVisible.value = true
  console.log(order, refundVisible)
  nextTick(() => {
    console.log(devyAddRef)
    orderRefundRef.value?.init(order)
  })
}


/**
 * 选择指定用户
 */
const selectUsersCallback = (users) => {
  console.log('选中的用户信息', users)
  if (users.length > 0) {
    dataForm.value.userId = users[0].userId
  } else {
    dataForm.value.userId = ''
  }
  getDataList(page);
}

/**
 * 打开选择用户
 */
const selectUser = () => {
  usersSelectVisible.value = true
  console.log(usersSelectRef)
  nextTick(() => {
    if (dataForm.value.userId && dataForm.value.userId == '') {
      usersSelectRef.value?.init([])
    } else {
      usersSelectRef.value?.init([{userId: dataForm.value.userId}])
    }
  })
}


/**
 * 获取数据列表
 */
const getDataList = (pageParam, params, done) => {
  pageParam = (pageParam === undefined ? page : pageParam)
  http({
    url: http.adornUrl('/order/order/page'), method: 'get', params: http.adornParams(Object.assign({
      current: pageParam == null ? page.currentPage : pageParam.currentPage,
      size: pageParam == null ? page.pageSize : pageParam.pageSize,
      userId: dataForm.value.userId,
      orderNumber: dataForm.value.orderNumber,
      status: dataForm.value.status,
      startTime: dateRange.value === null ? null : dateRange.value[0], // 开始时间
      endTime: dateRange.value === null ? null : dateRange.value[1] // 结束时间
    }, params), false)
  })
    .then(({data}) => {
      dataList.value = data.records
      page.total = data.total
      page.currentPage = data.current
      page.pageSize = data.size
      if (done) done()
    })
}
/**
 * 清除数据
 */
const clearDatas = () => {
  dataForm.value = {}
  dateRange.value = []
}
/**
 * 每页数
 * @param val
 */
const sizeChangeHandle = (val) => {
  page.pageSize = val
  page.currentPage = 1
  getDataList(page)
}
/**
 * 当前页
 * @param val
 */
const currentChangeHandle = (val) => {
  page.currentPage = val
  getDataList(page)
}

const addOrUpdateRef = ref(null)
const addOrUpdateVisible = ref(false)

/**
 * 新增 / 修改
 * @param val
 */
const onAddOrUpdate = (val) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(val)
  })
}
/**
 * 打印订单
 * @param orderNumber
 */
const printOrder = (orderNumber) => {
  // 请求打印订单接口
  http({
    url: http.adornUrl(`/order/order/printOrder/${orderNumber}`), method: 'get', params: http.adornParams()
  }).then(({data}) => {
    console.log(data)
    if (!data) {
      ElMessage({
        message: '打印失败，打印接口返回数据为' + data, type: 'error', duration: 1.5 * 1000
      })
      return
    }
    if (data.error === 0) {
      ElMessage({
        message: '打印成功' || data.error_description, type: 'success', duration: 1.5 * 1000
      })
    } else {
      ElMessage({
        message: '打印失败' || data.error_description, type: 'error', duration: 1.5 * 1000
      })
    }
  })
}

const consignmentInfoRef = ref(null)
const consignmentInfoVisible = ref(false)
const showConsignmentInfo = () => {
  consignmentInfoVisible.value = true
  nextTick(() => {
    consignmentInfoRef.value?.init()
  })
}
const getWaitingConsignmentExcel = (consignmentInfo) => {
  http({
    url: http.adornUrl('/order/order/waitingConsignmentExcel'), method: 'get', params: http.adornParams({
      consignmentName: consignmentInfo.consignmentName,
      consignmentMobile: consignmentInfo.consignmentMobile,
      consignmentAddr: consignmentInfo.consignmentAddr,
      startTime: dateRange.value === null ? null : dateRange.value[0], // 开始时间
      endTime: dateRange.value === null ? null : dateRange.value[1] // 结束时间
    }), responseType: 'blob' // 解决文件下载乱码问题
  })
    .then(({data}) => {
      const blob = new Blob([data], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8'})
      const fileName = '待发货信息整理.xls'
      const elink = document.createElement('a')
      if ('download' in elink) { // 非IE下载
        elink.download = fileName
        elink.style.display = 'none'
        elink.href = URL.createObjectURL(blob)
        document.body.appendChild(elink)
        elink.click()
        URL.revokeObjectURL(elink.href) // 释放URL 对象
        document.body.removeChild(elink)
      } else { // IE10+下载
        navigator.msSaveBlob(blob, fileName)
      }
    })
}

const getSoldExcel = () => {
  http({
    url: http.adornUrl('/order/order/soldExcel'), method: 'get', params: http.adornParams({
      startTime: dateRange.value === null ? null : dateRange.value[0], // 开始时间
      endTime: dateRange.value === null ? null : dateRange.value[1] // 结束时间
    }), responseType: 'blob' // 解决文件下载乱码问题
  })
    .then(({data}) => {
      const blob = new Blob([data], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8'})
      const fileName = '销售信息整理.xls'
      const elink = document.createElement('a')
      if ('download' in elink) { // 非IE下载
        elink.download = fileName
        elink.style.display = 'none'
        elink.href = URL.createObjectURL(blob)
        document.body.appendChild(elink)
        elink.click()
        URL.revokeObjectURL(elink.href) // 释放URL 对象
        document.body.removeChild(elink)
      } else { // IE10+下载
        navigator.msSaveBlob(blob, fileName)
      }
    })
}
</script>

<style lang="scss" scoped>
.mod-order-order {
  .tit {
    display: flex;
    height: 45px;
    align-items: center;
  }

  .tit {
    .item {
      padding: 0 10px;
      width: 10%;
      text-align: center;
    }

    .product {
      width: 25%;
    }
  }

  .prod-tit {
    padding: 10px;
    background: #f8f8f9;
    border-left: 1px solid #dddee1;
    border-top: 1px solid #dddee1;
    border-right: 1px solid #dddee1;

    span {
      margin-right: 15px;
    }
  }

  .prod-cont {
    display: flex;
    border-top: 1px solid #dddee1;
    border-bottom: 1px solid #dddee1;
    border-left: 1px solid #dddee1;
    color: #495060;

    .item {
      display: flex;
      display: -webkit-flex;
      align-items: center;
      justify-content: center;
      padding: 10px;
      border-right: 1px solid #dddee1;
      text-align: center;
      height: 100%;
      flex-wrap: wrap;
      align-content: center;
      justify-content: center;
      flex-direction: column;

      span {
        display: block;
      }
    }

    .prod-item {
      display: flex;
      flex-direction: column;
      border-right: 1px solid #dddee1;
    }

    .items.name {
      display: flex;
      position: relative;
      padding: 20px;
      border-bottom: 1px solid #dddee1;

      &:last-child {
        border-bottom: none;
      }
    }
  }

  .prod-name {
    width: 55%;
    text-align: left;

    .prod-info {
      display: block;
      color: #80848f;
      margin-top: 30px;
    }
  }

  .prod-price {
    position: absolute;
    right: 40px;
    text-align: right;

    span {
      display: block;
      margin-bottom: 10px;
    }
  }

  .prod-image {
    margin-right: 20px;
    width: 60px;
    height: 60px;

    img {
      width: 60px;
      height: 60px;
    }
  }

  .item {
    span {
      display: block;
      margin-bottom: 10px;
    }

    .operate {
      color: #2d8cf0;
    }

    .totalprice {
      color: #c00;
    }
  }

  .prod {
    .remark {
      width: 100%;
      height: 50px;
      line-height: 50px;
      background-color: #e8f7f6;
      border-left: 1px solid #dddee1;
      border-right: 1px solid #dddee1;
      border-bottom: 1px solid #dddee1;
      margin-bottom: 20px;
    }
  }

  .buyer-remark {
    padding: 0 20px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .empty-tips {
    display: block;
    width: 100%;
    text-align: center;
    margin: 50px 0;
    color: #999;
  }
}

.content .el-col {
  text-align: center;
}
</style>
