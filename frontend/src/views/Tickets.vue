<template>
  <div>
    <el-card>
      <el-form :model="query" inline>
        <el-form-item label="标题"><el-input v-model="query.keyword" placeholder="搜索标题" clearable /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable>
            <el-option label="待处理" value="PENDING" /><el-option label="已分配" value="ASSIGNED" />
            <el-option label="处理中" value="IN_PROGRESS" /><el-option label="待审批" value="PENDING_APPROVAL" />
            <el-option label="已解决" value="RESOLVED" />
            <el-option label="已关闭" value="CLOSED" /><el-option label="已重开" value="REOPENED" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="query.priority" placeholder="全部" clearable>
            <el-option label="低" value="LOW" /><el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" /><el-option label="紧急" value="URGENT" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部" clearable>
            <el-option label="Bug" value="BUG" /><el-option label="功能需求" value="FEATURE" />
            <el-option label="技术支持" value="SUPPORT" /><el-option label="维护" value="MAINTENANCE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="search">查询</el-button></el-form-item>
        <el-form-item><el-button type="success" @click="handleAutoAssign">自动分配</el-button></el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:16px">
      <el-table :data="tickets" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="180">
          <template #default="{ row }"><el-link type="primary" @click="$router.push(`/tickets/${row.id}`)">{{ row.title }}</el-link></template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="90" />
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="{ row }"><el-tag :type="priorityType(row.priority)" size="small">{{ row.priority }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }"><el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="creatorName" label="创建人" width="100" />
        <el-table-column prop="assigneeName" label="处理人" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="$router.push(`/tickets/${row.id}`)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="query.page" v-model:page-size="query.size" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @change="search" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTickets, autoAssign } from '../api/ticket'
import { ElMessage } from 'element-plus'

const tickets = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ keyword: '', status: '', priority: '', type: '', page: 1, size: 10 })

onMounted(() => search())

async function search() {
  loading.value = true
  try {
    const params = {}
    Object.keys(query).forEach(k => { if (query[k]) params[k] = query[k] })
    const res = await getTickets(params)
    tickets.value = res.data.list
    total.value = res.data.total
  } catch (e) { /* */ }
  finally { loading.value = false }
}

async function handleAutoAssign() {
  try {
    await autoAssign()
    ElMessage.success('自动分配完成')
    search()
  } catch (e) { /* */ }
}

function statusType(s) {
  const map = { PENDING: 'warning', ASSIGNED: '', IN_PROGRESS: 'primary', PENDING_APPROVAL: 'warning', RESOLVED: 'success', CLOSED: 'info', REOPENED: 'danger' }
  return map[s] || ''
}
function statusLabel(s) {
  const map = { PENDING: '待处理', ASSIGNED: '已分配', IN_PROGRESS: '处理中', PENDING_APPROVAL: '待审批', RESOLVED: '已解决', CLOSED: '已关闭', REOPENED: '已重开' }
  return map[s] || s
}
function priorityType(p) {
  const map = { LOW: 'success', MEDIUM: 'info', HIGH: 'warning', URGENT: 'danger' }
  return map[p] || ''
}
</script>