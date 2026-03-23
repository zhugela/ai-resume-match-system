<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="16">
        <!-- 基本信息 -->
        <el-card>
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button type="primary" size="small" @click="editMode = !editMode">
                {{ editMode ? '取消' : '编辑' }}
              </el-button>
            </div>
          </template>
          
          <el-form :model="userForm" label-width="80px" :disabled="!editMode">
            <el-form-item label="用户名">
              <el-input v-model="userForm.username" disabled />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userForm.email" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userForm.phone" />
            </el-form-item>
            <el-form-item label="角色">
              <el-tag>{{ userForm.role === 'HR' ? '招聘方' : '求职者' }}</el-tag>
            </el-form-item>
            <el-form-item v-if="editMode">
              <el-button type="primary" @click="handleSave">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 修改密码 -->
        <el-card class="mt-20">
          <template #header>
            <span>修改密码</span>
          </template>
          
          <el-form :model="passwordForm" label-width="80px">
            <el-form-item label="原密码">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="8">
        <!-- 头像 -->
        <el-card>
          <div class="avatar-section">
            <el-avatar :size="100" :src="avatarUrl">
              <el-icon :size="50"><User /></el-icon>
            </el-avatar>
            <el-upload
              class="avatar-upload"
              :show-file-list="false"
              :auto-upload="false"
            >
              <el-button size="small">更换头像</el-button>
            </el-upload>
          </div>
        </el-card>

        <!-- 账号信息 -->
        <el-card class="mt-20">
          <template #header>
            <span>账号信息</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户ID">{{ userStore.userInfo.userId }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ formatDate(userInfo.createdAt) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 安全提示 -->
        <el-card class="mt-20">
          <template #header>
            <span>安全提示</span>
          </template>
          <div class="security-tips">
            <p>🔒 定期修改密码保护账号安全</p>
            <p>🔐 不要将账号密码透露给他人</p>
            <p>📧 建议绑定邮箱便于找回密码</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const userStore = useUserStore()

const editMode = ref(false)
const userInfo = ref({})

const userForm = reactive({
  username: '',
  email: '',
  phone: '',
  role: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const avatarUrl = computed(() => {
  return userStore.userInfo.avatarUrl || ''
})

onMounted(() => {
  // 从store获取用户信息
  const info = userStore.userInfo
  userForm.username = info.username || ''
  userForm.email = info.email || ''
  userForm.phone = info.phone || ''
  userForm.role = info.role || 'USER'
})

const handleSave = () => {
  // TODO: 调用更新接口
  ElMessage.success('保存成功')
  editMode.value = false
}

const handleChangePassword = () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请填写完整')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次密码输入不一致')
    return
  }
  if (passwordForm.newPassword.length < 6) {
    ElMessage.warning('密码长度不能少于6位')
    return
  }
  // TODO: 调用修改密码接口
  ElMessage.success('密码修改成功')
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD') : '-'
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-section {
  text-align: center;
  padding: 20px 0;
}

.avatar-upload {
  margin-top: 15px;
}

.security-tips p {
  margin: 10px 0;
  color: #606266;
  font-size: 14px;
}

.mt-20 {
  margin-top: 20px;
}
</style>
