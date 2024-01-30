<template>
  <el-dialog
    v-model="visible"
    :modal="false"
    title="订单退款"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      label-width="80px"
      @keyup.enter="onSubmit()"
    />
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

const dataRule = {

}

const visible = ref(false)
const dataForm = reactive({
  refundAmount: 0,
  refundNames: [],
  orderNumber: 0,
  refundMsg: ''
})
/**
 * 初始化数据方法
 * @param orderNumber
 */
const init = (order) => {
  visible.value = true
  dataForm.orderNumber = order.orderNumber || ''
  http({
    url: http.adornUrl('/admin/delivery/list'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    dataForm.orderNumber = data.orderNumber
  })
}
defineExpose({ init })

const dataFormRef = ref(null)
/**
 * 表单提交
 */
const onSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      http({
        url: http.adornUrl('/order/order/delivery'),
        method: 'put',
        data: http.adornData({
          orderNumber: dataForm.orderNumber
        })
      }).then(() => {
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
