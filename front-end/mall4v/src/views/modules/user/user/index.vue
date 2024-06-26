<template>
  <div class="mod-user">
    <avue-crud
      ref="crudRef"
      :page="page"
      :data="dataList"
      :option="tableOption"
      @search-change="onSearch"
      @selection-change="selectionChange"
      @sort-change="sortChange"
      @on-load="getDataList"
    >

      <!-- 渲染到 pic头像列中的内容 -->
      <template #pic="scope">
        <span
          v-if="scope.row.pic"
          class="avue-crud__img"
        >
          <el-image :src="scope.row.pic" :preview-src-list="picSrcList"></el-image>
        </span>
        <span v-else>-</span>
      </template>

      <!-- 渲染到 status状态列中的内容 -->
      <template #status="scope">
        <el-tag
          v-if="scope.row.status === 0"
          type="danger"
        >
          禁用
        </el-tag>
        <el-tag v-else>
          正常
        </el-tag>
      </template>

      <!-- 渲染到 isStaff列中的内容 -->
      <template #isStaff="scope">
        <el-tag
          v-if="scope.row.isStaff === 1"
          type="danger"
        >
          内部
        </el-tag>
        <el-tag v-else>
          会员
        </el-tag>
      </template>

      <template #userRegtime="scope">
        <el-tag>
          {{ dayjs(scope.row.userRegtime).fromNow() }}
        </el-tag>
      </template>

      <template #userLasttime="scope">
        <el-tag>
          {{ dayjs(scope.row.userLasttime).fromNow() }}
        </el-tag>
      </template>

      <!-- 渲染到 menu菜单列中的内容 -->
      <template #menu="scope">
        <el-button
          v-if="isAuth('admin:user:update')"
          type="primary"
          icon="el-icon-edit"
          plain
          size="small"
          @click.stop="onAddOrUpdate(scope.row.userId)"
        >
          编辑
        </el-button>
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="onSearch"
    />
  </div>
</template>

<script setup>
import dayjs from 'dayjs'
import {isAuth} from '@/utils'
import {tableOption} from '@/crud/user/user.js'
import AddOrUpdate from './add-or-update.vue'

const dataList = ref([])
const dataListLoading = ref(false)
const dataListSelections = ref([])
const addOrUpdateVisible = ref(false)
const page = reactive({
  orderField: '',
  order: '',
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const picSrcList = ref([])
/**
 * 获取数据列表
 */
const getDataList = (pageParam, params, done) => {
  dataListLoading.value = true
  http({
    url: http.adornUrl('/admin/user/page'),
    method: 'get',
    params: http.adornParams(
      Object.assign(
        {
          current: pageParam == null ? page.currentPage : pageParam.currentPage,
          size: pageParam == null ? page.pageSize : pageParam.pageSize,
          orderField: pageParam == null ? page.orderField : pageParam.orderField,
          order: pageParam == null ? page.order : pageParam.order
        },
        params
      )
    )
  })
    .then(({data}) => {
      dataList.value = data.records
      page.total = data.total
      page.currentPage = data.current
      page.pageSize = data.size
      dataListLoading.value = false
      picSrcList.value = []
      data.records.forEach((row, idx) => {
        // picSrcList.value[idx] = row.pic
      })
      if (done) done()
    })
}

const addOrUpdateRef = ref(null)
/**
 * 新增 / 修改
 * @param id
 */
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}

/**
 * 条件查询
 */
const onSearch = (params, done) => {
  getDataList(page, params, done)
}

/**
 * 多选变化
 */
const selectionChange = (val) => {
  dataListSelections.value = val
}
/**
 * 排序变化
 */
const sortChange = ({column, prop, order}) => {
  page.orderField = prop;
  page.order = order;
  getDataList(page, {}, null)
}
</script>

<style lang="scss" scoped>

</style>
