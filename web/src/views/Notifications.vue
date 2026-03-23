<template>
  <div class="notifications-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">消息中心</h1>
        <p class="page-subtitle">查看系统通知和匹配结果消息</p>
      </div>
      <el-button @click="handleMarkAllRead" :disabled="unreadCount === 0">
        <el-icon><Check /></el-icon>
        全部标为已读
      </el-button>
    </div>

    <!-- 未读数提示 -->
    <div class="unread-banner" v-if="unreadCount > 0">
      <el-icon><Bell /></el-icon>
      您有 <strong>{{ unreadCount }}</strong> 条未读消息
    </div>

    <!-- 通知列表 -->
    <div class="notification-list" v-loading="loading">
      <div v-if="!loading && notifications.length === 0" class="empty-state">
        <el-icon size="64"><Bell /></el-icon>
        <h3>暂无消息</h3>
        <p>您还没有收到任何通知</p>
      </div>

      <div 
        v-for="item in notifications" 
        :key="item.id"
        class="notification-item"
        :class="{ unread: !item.isRead }"
        @click="handleRead(item)"
      >
        <div class="notification-icon" :class="getTypeClass(item.type)">
          <el-icon size="20">
            <component :is="getTypeIcon(item.type)" />
          </el-icon>
        </div>
        <div class="notification-body">
          <div class="notification-header">
            <span class="notification-title">{{ item.title }}</span>
            <span class="notification-time">{{ formatTime(item.createTime) }}</span>
          </div>
          <p class="notification-content">{{ item.content }}</p>
        </div>
        <div class="notification-actions">
          <el-badge :is-dot="!item.isRead" />
          <el-button 
            type="danger" 
            plain 
            size="small" 
            text
            @click.stop="handleDelete(item.id)"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchNotifications"
        @current-change="fetchNotifications"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { notificationApi } from '@/api'
import { Bell, Check, Delete, InfoFilled, Message, WarnTriangleFilled } from '@element-plus/icons-vue'

const loading = ref(false)
const notifications = ref([])
const total = ref(0)
const unreadCount = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
let pollTimer = null

const fetchNotifications = async () => {
  loading.value = true
  try {
    const res = await notificationApi.list({ pageNum: pageNum.value, pageSize: pageSize.value })
    const page = res.data.data
    notifications.value = page.records || []
    total.value = page.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchUnreadCount = async () => {
  try {
    const res = await notificationApi.unreadCount()
    unreadCount.value = res.data.data || 0
  } catch (e) {
    console.error(e)
  }
}

const handleRead = async (item) => {
  if (!item.isRead) {
    await notificationApi.markRead(item.id)
    item.isRead = true
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }
}

const handleMarkAllRead = async () => {
  try {
    await ElMessageBox.confirm('确定将所有消息标为已读吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await notificationApi.markAllRead()
    ElMessage.success('已全部标记为已读')
    fetchNotifications()
    unreadCount.value = 0
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await notificationApi.delete(id)
    ElMessage.success('删除成功')
    fetchNotifications()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const getTypeIcon = (type) => {
  const map = { SYSTEM: InfoFilled, MATCH_RESULT: Message, JOB_UPDATE: WarnTriangleFilled }
  return map[type] || InfoFilled
}

const getTypeClass = (type) => {
  const map = { SYSTEM: 'type-system', MATCH_RESULT: 'type-match', JOB_UPDATE: 'type-job' }
  return map[type] || 'type-system'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return date.toLocaleDateString()
}

onMounted(() => {
  fetchNotifications()
  fetchUnreadCount()
  // 每30秒轮询未读数
  pollTimer = setInterval(fetchUnreadCount, 30000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<style scoped>
.notifications-page {
  padding: 32px;
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
}

.page-subtitle {
  color: var(--text-muted, #6b7280);
  margin: 0;
  font-size: 14px;
}

.unread-banner {
  background: linear-gradient(135deg, rgba(0, 240, 255, 0.1), rgba(191, 90, 242, 0.1));
  border: 1px solid var(--border-accent);
  border-radius: 10px;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
  font-size: 14px;
  color: var(--text-primary);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-subtle, #e5e7eb);
  border-radius: 16px;
}

.empty-state h3 {
  margin: 16px 0 8px;
  font-size: 20px;
}

.empty-state p {
  color: var(--text-muted, #6b7280);
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  background: var(--bg-card, #fff);
  border: 1px solid var(--border-subtle, #e5e7eb);
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.notification-item:hover {
  border-color: var(--border-accent, #f59e0b);
}

.notification-item.unread {
  background: linear-gradient(135deg, rgba(0, 240, 255, 0.08), rgba(191, 90, 242, 0.08));
  border-color: var(--accent-cyan);
}

.notification-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.type-system { background: rgba(0, 240, 255, 0.15); color: var(--accent-cyan); }
.type-match { background: rgba(191, 90, 242, 0.15); color: var(--accent-purple); }
.type-job { background: rgba(255, 55, 95, 0.15); color: var(--accent-pink); }

.notification-body {
  flex: 1;
  min-width: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.notification-title {
  font-weight: 600;
  font-size: 15px;
  color: var(--text-primary, #1f2937);
}

.notification-time {
  font-size: 12px;
  color: var(--text-muted, #9ca3af);
  flex-shrink: 0;
}

.notification-content {
  font-size: 14px;
  color: var(--text-secondary, #4b5563);
  margin: 0;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notification-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.pagination-wrap {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}
</style>
