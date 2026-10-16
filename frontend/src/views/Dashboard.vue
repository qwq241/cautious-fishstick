<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in cards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="card-content">
            <div class="card-label">{{ card.label }}</div>
            <div class="card-value" :style="{ color: card.color }">{{ card.value }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card><div ref="pieChart" style="height:350px"></div></el-card>
      </el-col>
      <el-col :span="12">
        <el-card><div ref="barChart" style="height:350px"></div></el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { getStatistics } from '../api/ticket'
import * as echarts from 'echarts'

const pieChart = ref(null)
const barChart = ref(null)
let pieInstance = null
let barInstance = null

const cards = reactive([
  { label: '待处理工单', value: 0, color: '#e6a23c' },
  { label: '处理中工单', value: 0, color: '#409eff' },
  { label: '待审批工单', value: 0, color: '#e6a23c' },
  { label: '已解决工单', value: 0, color: '#67c23a' },
  { label: '工单总数', value: 0, color: '#f56c6c' }
])

onMounted(async () => {
  try {
    const d = (await getStatistics()).data
    cards[0].value = d.pendingTickets || 0
    cards[1].value = d.inProgressTickets || 0
    cards[2].value = d.pendingApprovalTickets || 0
    cards[3].value = d.resolvedTickets || 0
    cards[4].value = d.totalTickets || 0

    pieInstance = echarts.init(pieChart.value)
    pieInstance.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '0%' },
      series: [{
        type: 'pie', radius: ['40%', '70%'],
        data: [
          { value: (d.ticketsByType || {}).BUG || 0, name: 'Bug' },
          { value: (d.ticketsByType || {}).FEATURE || 0, name: '功能需求' },
          { value: (d.ticketsByType || {}).SUPPORT || 0, name: '技术支持' },
          { value: (d.ticketsByType || {}).MAINTENANCE || 0, name: '维护' },
          { value: (d.ticketsByType || {}).OTHER || 0, name: '其他' }
        ]
      }]
    })

    barInstance = echarts.init(barChart.value)
    barInstance.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['低', '中', '高', '紧急'] },
      yAxis: { type: 'value' },
      series: [{
        type: 'bar',
        data: [
          { value: (d.ticketsByPriority || {}).LOW || 0, itemStyle: { color: '#67c23a' } },
          { value: (d.ticketsByPriority || {}).MEDIUM || 0, itemStyle: { color: '#409eff' } },
          { value: (d.ticketsByPriority || {}).HIGH || 0, itemStyle: { color: '#e6a23c' } },
          { value: (d.ticketsByPriority || {}).URGENT || 0, itemStyle: { color: '#f56c6c' } }
        ]
      }]
    })
  } catch (e) { /* empty */ }
})

onUnmounted(() => {
  pieInstance?.dispose()
  barInstance?.dispose()
})
</script>

<style scoped>
.stat-card { cursor: pointer; }
.card-content { text-align: center; padding: 10px 0; }
.card-label { font-size: 14px; color: #909399; margin-bottom: 8px; }
.card-value { font-size: 28px; font-weight: bold; }
</style>