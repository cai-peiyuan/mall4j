<template>
  <el-dialog
    v-model="visible"
    :modal="false"
    title="订单退款"
    :close-on-click-modal="false"
  >
    <el-form
      ref="refundDataFormRef"
      :model="refundDataForm"
      :rules="dataRule"
      label-width="80px"
      @keyup.enter="onSubmit()"
    >
      <el-form-item label="退款类型" prop="applyType">
        <el-select
            v-model="refundDataForm.applyType"
            placeholder="请选择"
        >
          <el-option
              label="仅退款"
              value="1"
          />
          <el-option
              label="退款退货"
              value="2"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="退款原因" prop="refundReason">
        <el-select
            v-model="refundDataForm.refundReason"
            placeholder="请选择"
        >
          <el-option
              v-for="item in refundDataForm.refundOptions"
              :key="item.refundName"
              :label="item.refundName"
              :value="item.refundName"
          />
        </el-select>
      </el-form-item>

      <el-form-item
          label="退款备注"
          prop="refundMsg"
      >
        <el-input
            v-model="refundDataForm.refundMsg"
            controls-position="right"
            :min="0"
            label="退款备注"
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

const dataRule = {

}

const visible = ref(false)

const refundDataForm = reactive({
  applyType: '1',
  refundAmount: 0,
  refundOptions: [],
  orderNumber: 0,
  sellerMsg: '',
  refundReason: '',
  refundMsg: ''
})

/**
 * 初始化数据方法
 * @param orderNumber
 */
const init = (order) => {
  visible.value = true
  refundDataForm.orderNumber = order.orderNumber || ''
  //加载退款原因字典信息
  http({
    url: http.adornUrl('/shop/refundOption/list'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    console.log(data)
    refundDataForm.refundOptions = data
  })
}

defineExpose({ init })

const refundDataFormRef = ref(null)
/**
 * 表单提交
 */
const onSubmit = () => {
  refundDataFormRef.value?.validate((valid) => {
    if (valid) {
      http({
        url: http.adornUrl('/order/order/refund'),
        method: 'put',
        data: http.adornData(refundDataForm)
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
