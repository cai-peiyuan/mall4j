<template>
  <el-row style="justify-content:flex-start">
    <el-form :inline="true" :model="queryForm" class="demo-form-inline">
      <el-form-item label="选择时间">
        <el-date-picker
          v-model="value2"
          type="datetimerange"
          :shortcuts="shortcuts"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          @change="onCalendarChange"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getAnalysisData">统计</el-button>
      </el-form-item>
    </el-form>
  </el-row>
  <el-row>
    <el-descriptions
      class="margin-top"
      :title="'会员储值统计  '"
      :column="3"
      :size="'large'"
      border
    >
      <template #extra>
      </template>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            用户储值总余额
          </div>
        </template>
        {{ balanceData.balanceSum }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            订单消费总数量
          </div>
        </template>
        {{
          (balanceData.usedBalanceDetailByDate && balanceData.usedBalanceDetailByDate.use_balance_order_cnt) ? balanceData.usedBalanceDetailByDate.use_balance_order_cnt : 0
        }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            订单消费总数额
          </div>
        </template>
        {{
          (balanceData.usedBalanceDetailByDate && balanceData.usedBalanceDetailByDate.use_balance_sum) ? balanceData.usedBalanceDetailByDate.use_balance_sum : 0.00
        }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            储值余额用户数量
          </div>
        </template>
        <el-tag size="small">
          {{ balanceData.hasBalance ? balanceData.hasBalance.balance_user_cnt : 0 }}
        </el-tag>
      </el-descriptions-item>

      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            储值余额总数
          </div>
        </template>
        <el-tag size="small">
          {{ balanceData.hasBalance ? balanceData.hasBalance.sum_balance : 0 }}
        </el-tag>
      </el-descriptions-item>

    </el-descriptions>
  </el-row>

  <el-row>
    <el-descriptions
      class="margin-top"
      :title="'订单统计  '"
      :column="3"
      :size="'large'"
      border
    >
      <template #extra>
      </template>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            已支付订单数量
          </div>
        </template>
        {{ orderData.orderCountPayed ? orderData.orderCountPayed.total_order : 0 }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            已支付订单总金额
          </div>
        </template>
        {{ orderData.orderCountPayed ? orderData.orderCountPayed.total_order_actual : 0 }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            总销售商品数量
          </div>
        </template>
        {{ orderData.orderCountPayed ? orderData.orderCountPayed.total_product_nums : 0 }}
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            订单打印次数
          </div>
        </template>
        {{ orderData.orderCountPayed ? orderData.orderCountPayed.total_print_times : 0 }}
      </el-descriptions-item>
    </el-descriptions>
  </el-row>
  <el-row>
    <el-descriptions
      class="margin-top"
      :title="'会员统计  '"
      :column="3"
      :size="'large'"
      border
    >
      <template #extra>
      </template>

      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            新注册会员
          </div>
        </template>
        <el-tag size="small">{{ userData.userRegCountTotal ? userData.userRegCountTotal : 0 }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item>
        <template #label>
          <div class="cell-item">
            活跃会员
          </div>
        </template>
        <el-tag size="small">{{ userData.userActCountTotal ? userData.userActCountTotal : 0 }}</el-tag>
      </el-descriptions-item>

    </el-descriptions>
  </el-row>
  <el-row :gutter="20">
    <el-col :span="6">
      <img
        src="~@/assets/img/qrcode.jpeg"
        alt=""
        style="width: 200px"
      >
    </el-col>
  </el-row>

</template>

<style lang="scss" scoped>
.el-row {
  margin-bottom: 20px;
}

.el-row:last-child {
  margin-bottom: 0;
}

.el-col {
  border-radius: 4px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
</style>
<script setup>

import PanelGroup from "./components/panel-group.vue";
import {ElMessage} from "element-plus";
import moment from 'moment';

const verifyRef = ref(null)
const dataFormRef = ref(null)
const analysisData = ref({})
const balanceData = ref({})
const orderData = ref({})
const userData = ref({})
const queryForm = reactive({
  startTime: '',
  endTime: '',
})
const value2 = ref('')

const shortcuts = [
  {
    text: '最近一天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 1)
      return [start, end]
    },
  },
  {
    text: '最近三天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 3)
      return [start, end]
    },
  },
  {
    text: '最近一周',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setDate(start.getDate() - 7)
      return [start, end]
    },
  },
  {
    text: '最近一个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setMonth(start.getMonth() - 1)
      return [start, end]
    },
  },
  {
    text: '最近三个月',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setMonth(start.getMonth() - 3)
      return [start, end]
    },
  },
]
const router = useRouter()
/**
 *
 */
const getAnalysisData = () => {
  // 请求打印订单接口
  http({
    url: http.adornUrl(`/home/analysisData`),
    method: 'get',
    params: http.adornParams(queryForm)
  }).then(({data}) => {
    console.log('首页统计数据', data)
    balanceData.value = data.balance
    orderData.value = data.order
    userData.value = data.user
    queryForm.startTime = data.startTime
    queryForm.endTime = data.endTime
    analysisData.value = data
  })
}


const onCalendarChange = (value) => {
  console.log("选择的日期发生变化", value)
  if (value) {
    console.log(value[0])
    console.log(value[1])
    console.log(moment(value[0]).format('YYYY-MM-DD HH:mm:ss'));
    console.log(moment(value[1]).format('YYYY-MM-DD HH:mm:ss'));
    queryForm.startTime = moment(value[0]).format('YYYY-MM-DD HH:mm:ss')
    queryForm.endTime = moment(value[1]).format('YYYY-MM-DD HH:mm:ss')
  }
}
getAnalysisData();

</script>
