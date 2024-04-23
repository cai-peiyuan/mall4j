<template>
  <iframe style="width: 100%;height: 100%;overflow: hidden" :src="url"></iframe>
</template>

<script setup>
import {isAuth} from '@/utils'
import {ElMessage, ElMessageBox} from 'element-plus'
import cookie from 'vue-cookies'

const url = ref('https://www.baidu.coim')
const serviceUrl = import.meta.env.VITE_APP_BASE_API;
url.value = serviceUrl + "/jmreport/list?token=" + cookie.get('Authorization');
</script>
