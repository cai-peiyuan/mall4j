<template>
  <div class="mod-deliveryUser-add-or-update">
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
          label="姓名"
          prop="userName"
        >
          <el-input
            v-model="dataForm.userName"
            controls-position="right"
            :min="0"
            maxlength="50"
            show-word-limit
            label="标题"
          />
        </el-form-item>

        <el-form-item
          label="手机号"
          prop="userPhone"
        >
          <el-input
            v-model="dataForm.userPhone"
            controls-position="right"
            type="textarea"
            :min="0"
            maxlength="255"
            show-word-limit
            label="手机号"
          />
        </el-form-item>
        <el-form-item
          label="排序号"
          prop="seq"
        >
          <el-input-number
            v-model="dataForm.seq"
            controls-position="right"
            :min="0"
            label="排序号"
          />
        </el-form-item>
        <el-form-item
          label="状态"
          prop="status"
        >
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="0">
              禁用
            </el-radio>
            <el-radio :label="1">
              启用
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
  userName: '',
  userPhone: '',
  recDate: '',
  seq: 0,
  status: 0
})
const page = reactive({
  orderField: '',
  order: '',
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const visible = ref(false)
const dataRule = {
  userName: [
    { required: true, message: '不能为空', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在1到50个字符内', trigger: 'blur' },
    { pattern: /\s\S+|S+\s|\S/, message: '不能为空', trigger: 'blur' }
  ],
  userPhone: [
    { required: true, message: '不能为空', trigger: 'blur' },
    { min: 1, max: 255, message: '长度在1到255个字符内', trigger: 'blur' },
    { pattern: /\s\S+|S+\s|\S/, message: '不能为空', trigger: 'blur' }
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
        url: http.adornUrl('/shop/deliveryUser/info/' + dataForm.value.id),
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
        url: http.adornUrl('/shop/deliveryUser'),
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
              emit('refreshDataList', page)
            }
          })
        })
    }
  })
})

</script>
