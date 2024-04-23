<template>
  <el-dialog
    v-model="visible"
    title="选择用户"
    :modal="false"
    width="800"
    :close-on-click-modal="false"
  >
    <el-form :model="dataForm" :inline="true">
      <el-form-item label="手机号">
        <el-input v-model="dataForm.userMobile"></el-input>
      </el-form-item>
      <el-form-item label="会员名称">
        <el-input v-model="dataForm.nickName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" plain size="small" @click="getDataList">查询</el-button>
        <el-button type="info" plain size="small" @click="reload">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table
      ref="userTableRef"
      v-loading="dataListLoading"
      :data="dataList"
      border
      style="width: 100%;"
      @selection-change="selectChangeHandle"
    >
      <el-table-column
        v-if="isSingle"
        width="50"
        header-align="center"
        align="center"
      >
        <template #default="scope">
          <div>
            <el-radio
              v-model="singleSelectUserId"
              :label="scope.row.userId"
              @change="getSelectUserRow(scope.row)"
            >
              &nbsp;
            </el-radio>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!isSingle"
        type="selection"
        header-align="center"
        align="center"
        width="50"
      />
      <el-table-column
        prop="userId"
        header-align="center"
        align="center"
        label="用户编号"
      />

      <el-table-column
        prop="userMobile"
        header-align="center"
        align="center"
        label="会员手机号"
      />
      <el-table-column
        prop="nickName"
        header-align="center"
        align="center"
        label="用户昵称"
      />
      <el-table-column
        align="center"
        width="140"
        label="用户头像"
      >
        <template #default="scope">
          <img
            alt=""
            :src="scope.row.pic"
            width="25"
            height="25"
          >
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="pageIndex"
      :page-sizes="[5, 10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
    />
    <template #footer>
      <span>
        <el-button @click="visible = false">取消</el-button>
        <el-button
          type="primary"
          @click="submitUsers()"
        >确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import {ElMessage} from 'element-plus'

const emit = defineEmits(['refreshSelectUsers'])
// eslint-disable-next-line no-unused-vars
const props = defineProps({
  isSingle: {
    default: false,
    type: Boolean
  }
})
const visible = ref(false)
const dataForm = reactive({
  userMobile: '',
  nickName: '',
})
const singleSelectUserId = ref(0)
const selectUsers = ref([])
const dataList = ref([])
const pageIndex = ref(1)
const pageSize = ref(5)
const totalPage = ref(0)
const dataListLoading = ref(false)
const dataListSelections = ref([])

onMounted(() => {
  getDataList()
})

/**
 * 获取数据列表
 */
const init = (selectUserParam) => {
  console.log(selectUserParam)
  selectUsers.value = selectUserParam
  visible.value = true
  dataListLoading.value = true
  if (selectUsers.value) {
    selectUsers.value?.forEach(row => {
      dataListSelections.value.push(row)
    })
  }
  getDataList()
}
defineExpose({init})

const userTableRef = ref(null)

const reload = () => {
  dataForm.userMobile = '';
  dataForm.nickName = '';
  getDataList();
}

const getDataList = () => {
  http({
    url: http.adornUrl('/admin/user/page'),
    method: 'get',
    params: http.adornParams(
      Object.assign(
        {
          current: pageIndex.value,
          size: pageSize.value
        },
        {
          userMobile: dataForm.userMobile,
          nickName: dataForm.nickName,
        }
      )
    )
  })
    .then(({data}) => {
      dataList.value = data.records
      totalPage.value = data.total
      dataListLoading.value = false
      if (selectUsers.value) {
        nextTick(() => {
          selectUsers.value?.forEach(row => {
            const index = dataList.value?.findIndex((user) => user.userId === row.userId)
            if (index != -1) {
              userTableRef.value?.toggleRowSelection(dataList.value[index])
            }
          })
        })
      }
    })
}
/**
 * 每页数
 * @param val
 */
const sizeChangeHandle = (val) => {
  pageSize.value = val
  pageIndex.value = 1
  getDataList()
}
/**
 * 当前页
 * @param val
 */
const currentChangeHandle = (val) => {
  pageIndex.value = val
  getDataList()
}
/**
 * 单选商品事件
 * @param row
 */
const getSelectUserRow = (row) => {
  dataListSelections.value = [row]
}
/**
 * 多选点击事件
 * @param selection
 */
const selectChangeHandle = (selection) => {
  dataList.value?.forEach((tableItem) => {
    const selectedUserIndex = selection.findIndex((selectedUser) => {
      if (!selectedUser) {
        return false
      }
      return selectedUser.userId === tableItem.userId
    })
    const dataSelectedUserIndex = dataListSelections.value?.findIndex((dataSelectedUser) => dataSelectedUser.userId === tableItem.userId)
    if (selectedUserIndex > -1 && dataSelectedUserIndex === -1) {
      dataListSelections.value.push(tableItem)
    } else if (selectedUserIndex === -1 && dataSelectedUserIndex > -1) {
      dataListSelections.value.splice(dataSelectedUserIndex, 1)
    }
  })
}
/**
 * 确定事件
 */
const submitUsers = () => {
  if (!dataListSelections.value.length) {
    ElMessage({
      message: '请选择数据',
      type: 'error',
      duration: 1000,
      onClose: () => {
      }
    })
    return
  }
  const users = []
  dataListSelections.value.forEach(item => {
    const userIndex = users.findIndex((user) => user.userId === item.userId)
    if (userIndex === -1) {
      users.push({userId: item.userId, nickName: item.nickName, pic: item.pic})
    }
  })
  emit('refreshSelectUsers', users)
  dataListSelections.value = []
  visible.value = false
}
</script>
