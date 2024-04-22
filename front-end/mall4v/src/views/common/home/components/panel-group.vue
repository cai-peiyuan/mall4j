<template>
  <el-row :gutter="0" justify="space-between" class="prefixCls">
    <el-col :xl="6" :lg="6" :md="12" :sm="12" :xs="24">
      <el-card style="width: 480px" shadow="always"></el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import {ElMessage} from 'element-plus';
import {ElRow, ElCol, ElCard, ElSkeleton} from 'element-plus'

const loading = ref(true)

</script>

<style lang="scss" scoped>
.prefixCls {

}
</style>
