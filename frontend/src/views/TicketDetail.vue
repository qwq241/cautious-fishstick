<template>
  <div v-loading="loading">
    <el-card v-if="ticket">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>{{ ticket.title }}</span>
          <div>
            <el-button v-if="ticket.status === 'PENDING' || ticket.status === 'REOPENED'" type="primary" @click="openAssign">分配</el-button>
            <el-button v-if="ticket.status === 'ASSIGNED'" type="primary" @click="doStartProcess">开始处理</el-button>
            <el-button v-if="ticket.status === 'IN_PROGRESS'" type="primary" @click="doSubmitApproval">提交审批</el-button>
            <el-button v-if="ticket.status === 'IN_PROGRESS'" type="success" @click="doResolve">标记已解决</el-button>
            <el-button v-if="ticket.status === 'PENDING_APPROVAL' && (auth.userRole === 'ADMIN' || auth.userRole === 'MANAGER')" type="success" @click="openApproveDialog('approve')">通过</el-button>
            <el-button v-if="ticket.status === 'PENDING_APPROVAL' && (auth.userRole === 'ADMIN' || auth.userRole === 'MANAGER')" type="danger" @click="openApproveDialog('reject')">驳回</el-button>
            <el-button v-if="ticket.status === 'RESOLVED'" type="info" @click="doClose">关闭</el-button>
            <el-button v-if="ticket.status === 'CLOSED'" type="warning" @click="doReopen">重新打开</el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="状态"><el-tag :type="statusType(ticket.status)">{{ statusLabel(ticket.status) }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="优先级"><el-tag :type="priorityType(ticket.priority)">{{ ticket.priority }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="类型">{{ ticket.type }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ ticket.category }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ ticket.creatorName }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ ticket.assigneeName || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ ticket.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ ticket.updatedAt }}</el-descriptions-item>
        <el-descriptions-item label="自定义字段" :span="2">{{ ticket.customFields }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ ticket.description }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>审批记录</el-divider>
      <div v-if="!ticket.approvals || ticket.approvals.length === 0" style="color:#909399">暂无审批记录</div>
      <div v-for="a in ticket.approvals" :key="a.id" class="comment">
        <strong>审批</strong>
        <el-tag :type="a.status === 'APPROVED' ? 'success' : a.status === 'REJECTED' ? 'danger' : 'warning'" size="small" style="margin-left:8px">{{ a.status === 'APPROVED' ? '已通过' : a.status === 'REJECTED' ? '已驳回' : '待审批' }}</el-tag>
        <span style="color:#909399;margin-left:8px">{{ a.createdAt }}</span>
        <div v-if="a.comment">{{ a.comment }}</div>
      </div>

      <el-divider>评论</el-divider>
      <div v-for="c in ticket.comments" :key="c.id" class="comment">
        <strong>{{ c.realName || c.username }}</strong>
        <span style="color:#909399;margin-left:8px">{{ c.createdAt }}</span>
        <div>{{ c.content }}</div>
      </div>
      <el-input v-model="newComment" placeholder="添加评论..." style="margin-top:12px" type="textarea" :rows="2" />
      <el-button type="primary" style="margin-top:8px" @click="addComment">发送</el-button>

      <el-divider>附件</el-divider>
      <div v-for="a in ticket.attachments" :key="a.id" class="attachment">
        <el-icon><Link /></el-icon> {{ a.fileName }} ({{ formatSize(a.fileSize) }})
      </div>
      <el-upload :action="uploadUrl" :headers="uploadHeaders" :on-success="onUploadSuccess" style="margin-top:8px">
        <el-button type="primary">上传附件</el-button>
      </el-upload>
    </el-card>

    <el-dialog v-model="showAssign" title="分配工单" width="400px">
      <el-select v-model="assigneeId" placeholder="选择处理人" filterable style="width:100%">
        <el-option v-for="u in engineers" :key="u.id" :label="u.realName || u.username" :value="u.id" />
      </el-select>
      <template #footer>
        <el-button @click="showAssign = false">取消</el-button>
        <el-button type="primary" @click="doAssign">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showApproval" title="审批意见" width="400px">
      <el-input v-model="approvalComment" placeholder="请输入审批意见（选填）" type="textarea" :rows="3" />
      <template #footer>
        <el-button @click="showApproval = false">取消</el-button>
        <el-button type="primary" @click="doApproval">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { getTicket, addComment as addCommentApi, assignTicket as assignTicketApi, startProcess as startProcessApi, resolveTicket as resolveTicketApi, closeTicket as closeTicketApi, reopenTicket as reopenTicketApi, submitForApproval as submitApprovalApi, approveTicket as approveTicketApi, rejectTicket as rejectTicketApi } from '../api/ticket'
import { getEngineers } from '../api/user'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const auth = useAuthStore()
const ticket = ref(null)
const loading = ref(false)
const showAssign = ref(false)
const assigneeId = ref(null)
const engineers = ref([])
const newComment = ref('')
const showApproval = ref(false)
const approvalComment = ref('')
const approvalAction = ref('')

const uploadUrl = computed(() => `/api/tickets/${route.params.id}/attachments`)
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${auth.token}` }))

onMounted(() => loadTicket())

async function loadTicket() {
  loading.value = true
  try {
    const res = await getTicket(route.params.id)
    ticket.value = res.data
  } finally { loading.value = false }
}

async function addComment() {
  if (!newComment.value.trim()) return
  await addCommentApi(route.params.id, { content: newComment.value })
  newComment.value = ''
  ElMessage.success('评论成功')
  loadTicket()
}

async function doAssign() {
  await assignTicketApi(route.params.id, { assigneeId: assigneeId.value })
  ElMessage.success('分配成功')
  showAssign.value = false
  loadTicket()
}

async function doStartProcess() { await startProcessApi(route.params.id); ElMessage.success('已开始处理'); loadTicket() }
async function doResolve() { await resolveTicketApi(route.params.id); ElMessage.success('已标记已解决'); loadTicket() }
async function doClose() { await closeTicketApi(route.params.id); ElMessage.success('已关闭'); loadTicket() }
async function doReopen() { await reopenTicketApi(route.params.id); ElMessage.success('已重新打开'); loadTicket() }

async function openAssign() {
  const res = await getEngineers()
  engineers.value = res.data
  showAssign.value = true
}

function onUploadSuccess() { ElMessage.success('上传成功'); loadTicket() }

async function doSubmitApproval() {
  await submitApprovalApi(route.params.id)
  ElMessage.success('已提交审批')
  loadTicket()
}

function openApproveDialog(action) {
  approvalAction.value = action
  approvalComment.value = ''
  showApproval.value = true
}

async function doApproval() {
  const data = { comment: approvalComment.value }
  if (approvalAction.value === 'approve') {
    await approveTicketApi(route.params.id, data)
    ElMessage.success('审批通过')
  } else {
    await rejectTicketApi(route.params.id, data)
    ElMessage.success('已驳回')
  }
  showApproval.value = false
  loadTicket()
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
function formatSize(bytes) { return bytes ? (bytes / 1024).toFixed(1) + ' KB' : '' }
</script>

<style scoped>
.comment { padding: 8px 0; border-bottom: 1px solid #eee; }
.attachment { padding: 4px 0; }
</style>