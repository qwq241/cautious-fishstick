<template>
  <el-row :gutter="20">
    <el-col :span="8"><el-card><div ref="typeChart" style="height:300px"></div></el-card></el-col>
    <el-col :span="8"><el-card><div ref="priorityChart" style="height:300px"></div></el-card></el-col>
    <el-col :span="8"><el-card><div ref="statusChart" style="height:300px"></div></el-card></el-col>
  </el-row>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getStatistics } from '../api/ticket'
import * as echarts from 'echarts'

const typeChart = ref(null)
const priorityChart = ref(null)
const statusChart = ref(null)
let typeIns = null, priorityIns = null, statusIns = null

onMounted(async () => {
  try {
    const d = (await getStatistics()).data

    const pieOption = (data, title) => ({
      title: { text: title, left: 'center' },
      tooltip: { trigger: 'item' },
      series: [{ type: 'pie', radius: '60%', data }]
    })

    typeIns = echarts.init(typeChart.value)
    typeIns.setOption(pieOption([
      { value: (d.ticketsByType || {}).BUG || 0, name: 'Bug' },
      { value: (d.ticketsByType || {}).FEATURE || 0, name: '功能需求' },
      { value: (d.ticketsByType || {}).SUPPORT || 0, name: '技术支持' },
      { value: (d.ticketsByType || {}).MAINTENANCE || 0, name: '维护' },
      { value: (d.ticketsByType || {}).OTHER || 0, name: '其他' }
    ], '工单类型分布'))

    priorityIns = echarts.init(priorityChart.value)
    priorityIns.setOption(pieOption([
      { value: (d.ticketsByPriority || {}).LOW || 0, name: '低' },
      { value: (d.ticketsByPriority || {}).MEDIUM || 0, name: '中' },
      { value: (d.ticketsByPriority || {}).HIGH || 0, name: '高' },
      { value: (d.ticketsByPriority || {}).URGENT || 0, name: '紧急' }
    ], '优先级分布'))

    statusIns = echarts.init(statusChart.value)
    statusIns.setOption(pieOption([
      { value: d.pendingTickets || 0, name: '待处理' },
      { value: d.inProgressTickets || 0, name: '处理中' },
      { value: d.pendingApprovalTickets || 0, name: '待审批' },
      { value: d.resolvedTickets || 0, name: '已解决' },
      { value: d.closedTickets || 0, name: '已关闭' }
    ], '状态分布'))
  } catch (e) { /* */ }
})

onUnmounted(() => {
  typeIns?.dispose()
  priorityIns?.dispose()
  statusIns?.dispose()
})
</script>