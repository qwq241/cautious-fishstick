<template>
  <el-card>
    <template #header>
      <span>用户管理</span>
      <el-button type="primary" style="float:right" @click="openCreate">新增用户</el-button>
    </template>
    <el-table :data="users" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }"><el-tag>{{ row.role }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="enabled" label="状态" width="80">
        <template #default="{ row }"><el-tag :type="row.enabled ? 'success' : 'danger'">{{ row.enabled ? '启用' : '禁用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="toggleStatus(row.id)">{{ row.enabled ? '禁用' : '启用' }}</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="showCreate" title="新增用户" width="400px">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="用户名" prop="username"><el-input v-model="form.username" /></el-form-item>
      <el-form-item label="密码" prop="password"><el-input v-model="form.password" type="password" /></el-form-item>
      <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
      <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
      <el-form-item label="角色"><el-select v-model="form.role" style="width:100%"><el-option label="管理员" value="ADMIN" /><el-option label="经理" value="MANAGER" /><el-option label="工程师" value="ENGINEER" /><el-option label="普通用户" value="USER" /></el-select></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showCreate = false">取消</el-button>
      <el-button type="primary" @click="createUser">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUsers, createUser as createUserApi, toggleUserStatus } from '../api/user'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)
const showCreate = ref(false)
const formRef = ref(null)

const defaultForm = () => ({ username: '', password: '', realName: '', email: '', role: 'USER' })
const form = ref(defaultForm())
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

onMounted(() => loadUsers())

function openCreate() {
  form.value = defaultForm()
  formRef.value?.clearValidate()
  showCreate.value = true
}

async function loadUsers() {
  loading.value = true
  try {
    const res = await getUsers({ page: 1, size: 100 })
    users.value = res.data.list
  } finally { loading.value = false }
}

async function createUser() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await createUserApi(form.value)
    ElMessage.success('创建成功')
    showCreate.value = false
    loadUsers()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || '创建失败'
    ElMessage.error(msg)
  }
}

async function toggleStatus(id) {
  try {
    await toggleUserStatus(id)
    ElMessage.success('操作成功')
    loadUsers()
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || '操作失败'
    ElMessage.error(msg)
  }
}
</script>