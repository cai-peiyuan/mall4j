<template>
  <el-dialog
    v-model="visible"
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      label-width="130px"
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        label="用户编号"
        prop="userId"
      >
        <el-input
          v-model="dataForm.userId"
          placeholder="用户编号"
        />
      </el-form-item>
      <el-form-item
        label="应用编号"
        prop="clientId"
      >
        <el-input
          v-model="dataForm.clientId"
          placeholder="应用编号"
        />
      </el-form-item>
      <el-form-item
        label="应用密钥"
        prop="clientSecret"
      >
        <el-input
          v-model="dataForm.clientSecret"
          placeholder="应用密钥"
        />
      </el-form-item>
      <el-form-item
        label="接口调用token"
        prop="accessToken"
      >
        <el-input
          v-model="dataForm.accessToken"
          placeholder="接口调用token"
        />
      </el-form-item>
      <el-form-item
        label="打印机设备编号"
        prop="machineCode"
      >
        <el-input
          v-model="dataForm.machineCode"
          placeholder="打印机设备编号"
        />
      </el-form-item>
      <el-form-item
        label="打印机设备签名"
        prop="msign"
      >
        <el-input
          v-model="dataForm.msign"
          placeholder="打印机设备签名"
        />
      </el-form-item>
      <el-form-item
        label="备注"
        prop="remark"
      >
        <el-input
          v-model="dataForm.remark"
          placeholder="备注"
        />
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
import { Debounce } from '@/utils/debounce'
const emit = defineEmits(['refreshDataList'])
const visible = ref(false)
const dataForm = reactive({
  id: 0,
  userId: '',
  clientId: '',
  clientSecret: '',
  accessToken: '',
  machineCode: '',
  msign: '',
  remark: ''
})
const dataRule = {
  userId: [
    { required: true, message: '不能为空', trigger: 'blur' }
  ],
  clientId: [
    { required: true, message: '不能为空', trigger: 'blur' }
  ],
  clientSecret: [
    { required: true, message: '不能为空', trigger: 'blur' }
  ],
  machineCode: [
    { required: true, message: '不能为空', trigger: 'blur' }
  ],
  msign: [
    { required: true, message: '不能为空', trigger: 'blur' }
  ],
  remark: [
    { required: true, message: '不能为空', trigger: 'blur' }
  ]
}
const dataFormRef = ref(null)
const init = (id) => {
  dataForm.id = id || 0
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
    if (dataForm.id) {
      http({
        url: http.adornUrl(`/sys/printer/info/${dataForm.id}`),
        method: 'get',
        params: http.adornParams()
      }).then(({ data }) => {
        Object.assign(dataForm, data)
      })
    }
  })
}
defineExpose({ init })
/**
 * 表单提交
 */
const onSubmit = Debounce(() => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      const theData = Object.assign({}, dataForm, {
        id: dataForm.id || undefined
      })
      http({
        url: http.adornUrl('/sys/printer'),
        method: dataForm.id ? 'put' : 'post',
        data: http.adornData(theData)
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
})
</script>
