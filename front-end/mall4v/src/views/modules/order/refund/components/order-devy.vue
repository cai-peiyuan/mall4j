<template>
  <el-dialog
    v-model="visible"
    :modal="false"
    title="选择发货地址"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      label-width="80px"
      @keyup.enter="onSubmit()"
    >
      <el-form-item label="快递公司">
        <el-select
          v-model="dataForm.dvyId"
          placeholder="请选择"
        >
          <el-option
            v-for="item in dataForm.dvyNames"
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
        <el-input
          v-model="dataForm.dvyFlowId"
          controls-position="right"
          :min="0"
          label="快递单号"
        >
          <template #append v-if="dataForm.dvyId == 13">
            <el-button type="primary" @click="getDvyFlowId()">生成单号</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item
        label="快递员"
        prop="deliveryUserId"
        v-if="dataForm.dvyId == 13"
      >
        <el-select
          v-model="dataForm.deliveryUserId"
          placeholder="请选择"
        >
          <el-option
            v-for="item in dataForm.dvyUsers"
            :key="item.id"
            :label="item.userName + '      ' + item.userPhone"
            :value="item.id"
          />
        </el-select>
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
const dataForm = reactive({
  dvyId: '',
  dvyFlowId: 0,
  dvyNames: [],
  dvyUsers: [],
  orderNumber: 0,
  deliveryUserId: null
})

const init = (orderNumber, dvyId, dvyFlowId) => {
  visible.value = true
  dataForm.orderNumber = orderNumber || ''
  dataForm.dvyId = dvyId || ''
  dataForm.dvyFlowId = dvyFlowId || ''
  http({
    url: http.adornUrl('/admin/delivery/list'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    dataForm.dvyNames = data
  })

  http({
    url: http.adornUrl('/admin/delivery/getDeliveryUsers'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    dataForm.dvyUsers = data
  })
}
defineExpose({ init })

const dataFormRef = ref(null)

/**
 * 生成运单号码
 */
const getDvyFlowId = () => {
  http({
    url: http.adornUrl('/order/order/generateExpressNumber'),
    method: 'get',
    data: http.adornData({
    })
  }).then((data) => {
    dataForm.dvyFlowId = data.data;
  })
}
/**
 * 表单提交
 * 订单发货
 */
const onSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      http({
        url: http.adornUrl('/order/order/delivery'),
        method: 'put',
        data: http.adornData({
          orderNumber: dataForm.orderNumber,
          dvyId: dataForm.dvyId,
          dvyFlowId: dataForm.dvyFlowId,
          deliveryUserId: dataForm.deliveryUserId
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
