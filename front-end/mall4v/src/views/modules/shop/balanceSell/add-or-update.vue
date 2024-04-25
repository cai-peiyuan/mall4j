<template>
  <div class="mod-balanceSell-add-or-update">
    <el-dialog
      v-model="visible"
      :title="!dataForm.id ? '新增' : '修改'"
      :close-on-click-modal="false"
    >
      <el-form
        ref="dataFormRef"
        :model="dataForm"
        :rules="dataRule"
        label-width="80px"
        @keyup.enter="onSubmit()"
      >
        <el-form-item
          label="储值金额"
          prop="storedValue"
        >
          <el-input-number
            v-model="dataForm.storedValue"
            :min="0"
            label="储值金额"
          />
        </el-form-item>

        <el-form-item
          label="售价"
          prop="sellValue"
        >
          <el-input-number
            v-model="dataForm.sellValue"
            :min="0"
            label="sellValue"
          />
        </el-form-item>
        <el-form-item
          label="已售"
          prop="sellCnt"
        >
          <el-input-number
            v-model="dataForm.sellCnt"
            controls-position="right"
            :min="0"
            label="已售"
          />
        </el-form-item>
        <el-form-item
          label="状态"
          prop="status"
        >
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="0">
              未上架
            </el-radio>
            <el-radio :label="1">
              可销售
            </el-radio>
            <el-radio :label="2">
              已售罄
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="visible = false">
            取消
          </el-button>
          <el-button
            type="primary"
            @click="onSubmit()"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { Debounce } from '@/utils/debounce'
const emit = defineEmits(['refreshDataList'])
const dataForm = ref({
  id: 0,
  storedValue: 0,
  sellValue: 0,
  sellCnt: 0,
  status: 0
})
const visible = ref(false)
const dataRule = {
  storedValue: [
    { required: true, message: '不能为空', trigger: 'blur' },
  ],
  sellValue: [
    { required: true, message: '不能为空', trigger: 'blur' },
  ],
  status: [
    { required: true, message: '不能为空', trigger: 'blur' },
  ]
}

const dataFormRef = ref(null)
const init = (id) => {
  dataForm.value.id = id || 0
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
    if (dataForm.value.id) {
      http({
        url: http.adornUrl('/shop/balanceSell/info/' + dataForm.value.id),
        method: 'get',
        params: http.adornParams()
      })
        .then(({ data }) => {
          dataForm.value = data
        })
    }
  })
}
defineExpose({ init })

/**
 * 表单提交
 */
const onSubmit = Debounce(() => {
  dataFormRef.value?.validate(valid => {
    if (valid) {
      const param = dataForm.value
      http({
        url: http.adornUrl('/shop/balanceSell'),
        method: param.id ? 'put' : 'post',
        data: http.adornData(param)
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
})

</script>
