<template>
  <el-dialog
    v-model="visible"
    :modal="false"
    title="订单送达"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="orderArriveDataForm"
      :rules="dataRule"
      label-width="80px"
      @keyup.enter="onSubmit()"
    >
      <el-form-item label="快递公司">
        <el-select
            disabled
          v-model="orderArriveDataForm.dvyId"
          placeholder="请选择"
        >
          <el-option
            v-for="item in orderArriveDataForm.dvyNames"
            :key="item.dvyId"
            :label="item.dvyName"
            :value="item.dvyId"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="快递单号"
        prop="dvyFlowId"
      >
        <el-input disabled
          v-model="orderArriveDataForm.dvyFlowId"
          controls-position="right"
          :min="0"
          label="快递单号"
        >
        </el-input>
      </el-form-item>
      <el-form-item
        label="快递员"
        prop="deliveryUserId"
        v-if="orderArriveDataForm.dvyId == 13"
      >
        <el-select
          v-model="orderArriveDataForm.deliveryUserId"
          placeholder="请选择"
        >
          <el-option
            v-for="item in orderArriveDataForm.dvyUsers"
            :key="item.id"
            :label="item.userName + '      ' + item.userPhone"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item
          label="备注信息"
          prop="remark"
      >
        <el-input
                  v-model="orderArriveDataForm.remark"
                  controls-position="right"
                  :min="0"
                  label="备注信息"
        >
        </el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button
          type="primary"
          @click="onSubmit()"
        >确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script setup>
import { ElMessage } from 'element-plus'
const emit = defineEmits(['refreshDataList'])

// eslint-disable-next-line no-unused-vars
const validDvyFlowId = (rule, value, callback) => {
  if (!value.trim()) {
    callback(new Error('不能为空'))
  } else {
    callback()
  }
}
const dataRule = {
  dvyFlowId: [
    { required: true, message: '不能为空', trigger: 'blur' },
    { validator: validDvyFlowId, trigger: 'blur' }
  ]
}

const visible = ref(false)
const orderArriveDataForm = reactive({
  remark: '订单已送达',
  dvyId: '',
  dvyFlowId: 0,
  dvyNames: [],
  dvyUsers: [],
  orderNumber: 0,
  deliveryUserId: null,
  deliveryInfo: {}
})

const init = (orderNumber, dvyId, dvyFlowId) => {
  visible.value = true
  orderArriveDataForm.orderNumber = orderNumber || ''
  orderArriveDataForm.dvyId = dvyId || ''
  orderArriveDataForm.dvyFlowId = dvyFlowId || ''
  http({
    url: http.adornUrl('/admin/delivery/list'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    orderArriveDataForm.dvyNames = data
  })

  http({
    url: http.adornUrl('/admin/delivery/getDeliveryUsers'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    orderArriveDataForm.dvyUsers = data
  })

  http({
    url: http.adornUrl('/admin/delivery/info/'+dvyFlowId),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    orderArriveDataForm.deliveryInfo = data
  })
}
defineExpose({ init })

const orderArriveDataFormRef = ref(null)

/**
 * 表单提交
 * 订单发货
 */
const onSubmit = () => {
  orderArriveDataFormRef.value?.validate((valid) => {
    if (valid) {
      http({
        url: http.adornUrl('/order/order/delivery'),
        method: 'put',
        data: http.adornData({
          orderNumber: orderArriveDataForm.orderNumber,
          dvyId: orderArriveDataForm.dvyId,
          dvyFlowId: orderArriveDataForm.dvyFlowId,
          deliveryUserId: orderArriveDataForm.deliveryUserId
        })
      })
        .then(() => {
          ElMessage({
            message: '操作成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              visible.value = false
              emit('refreshDataList')
            }
          })
        })
    }
  })
}

</script>
<style>
.el-dialog {
  --el-dialog-width: 25%;
}
</style>
