<template>
  <el-card>
    <template #header>创建工单</template>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width:600px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="form.type" style="width:100%">
          <el-option label="Bug" value="BUG" /><el-option label="功能需求" value="FEATURE" />
          <el-option label="技术支持" value="SUPPORT" /><el-option label="维护" value="MAINTENANCE" />
          <el-option label="其他" value="OTHER" />
        </el-select>
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-select v-model="form.priority" style="width:100%">
          <el-option label="低" value="LOW" /><el-option label="中" value="MEDIUM" />
          <el-option label="高" value="HIGH" /><el-option label="紧急" value="URGENT" />
        </el-select>
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-input v-model="form.category" placeholder="如：网络、软件、硬件" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="自定义字段">
        <el-input v-model="form.customFields" type="textarea" :rows="2" placeholder="JSON格式，如: {&quot;department&quot;:&quot;技术部&quot;}" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit">提交</el-button>
        <el-button @click="$router.push('/tickets')">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createTicket } from '../api/ticket'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)

const form = reactive({ title: '', type: 'BUG', priority: 'MEDIUM', category: '', description: '', customFields: '' })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await createTicket(form)
    ElMessage.success('创建成功')
    router.push('/tickets')
  } catch (e) { /* */ }
}
</script>
