<template>
  <el-row :gutter="20">
    <el-col :span="6">
      <img
        src="~@/assets/img/qrcode.jpeg"
        alt=""
      >
    </el-col>
  </el-row>
</template>

<style lang="scss" scoped>
.el-row {
  margin-bottom: 20px;
}

.el-row:last-child {
  margin-bottom: 0;
}

.el-col {
  border-radius: 4px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
</style>
<script setup>

const verifyRef = ref(null)
const dataFormRef = ref(null)

const router = useRouter()


</script>
