import type { AxiosProgressEvent, GenericAbortSignal } from 'axios'
import { post, get, del, put } from '@/utils/request'

// 获取配置信息
export function fetchChatConfig<T>() {
  return get<T>({
    url: '/app/api/config',
  })
}

// 登录
export function fetchVerify<T>(data: object) {
  return get<T>({
    url: '/app/api/oauth/token',
    data,
  })
}

// 获取用户信息
export function fetchSession<T>() {
  return get<T>({
    url: '/app/user',
  })
}

// 获取用户可用模型信息
export function fetchModel<T>() {
  return get<T>({
    url: '/app/user/model',
  })
}

// 修改个人信息
export function updateUser<T>(data: object) {
  return put<T>({
    url: '/app/user',
    data: data
  })
}

// 开启上下文
export function updateContext<T>(data: object) {
  return put<T>({
    url: '/app/user/context',
    data: data
  })
}

// 修改密码
export function updatePassword<T>(data: object) {
  return put<T>({
    url: '/app/user/password/update',
    data: data
  })
}

// 获取会话列表
export function listChat<T>() {
  return get<T>({
    url: '/app/chat',
  })
}

// 获取会话内容
export function listChatMessage<T>(data: object) {
  return get<T>({
    url: '/app/chat/message',
    data
  })
}

// 删除会话列表
export function removeChat<T>(chatNumber: string) {
  return del<T>({
    url: '/app/chat/' + chatNumber,
  })
}

// 获取助手分类
export function listAssistantType<T>() {
  return get<T>({
    url: '/app/api/assistant/type',
  })
}

// 根据分类获取助手
export function listAssistantByType<T>(data: {current: number,size: number,typeId: number}) {
  return get<T>({
    url: '/app/api/assistant',
    data
  })
}

// 随机获取助手
export function listAssistantRandom<T>() {
  return get<T>({
    url: '/app/api/assistant/random',
  })
}

// 创建对话
export function fetchChatAPI<T = any>(data: Chat.ChatRequest) {
  return post<T>({
    url: '/app/chat',
    data: data
  })
}

// 发送内容
export function fetchChatMessageAPI<T = any>(data: Chat.MessageRequest) {
  return post<T>({
    url: '/app/chat/message',
    data: data
  })
}

// 根据消息id获取当前内容
export function fetchChatMessageById<T = any>(messageId: string) {
  return get<T>({
    url: '/app/chat/message/' + messageId,
  })
}

// 根据消息id获取当前内容
export function deleteChatMessageById<T = any>(messageId: string) {
  return del<T>({
    url: '/app/chat/message/' + messageId,
  })
}

// 流式响应聊天
export function fetchChatAPIProcess<T = any>(
  params: {
    conversationId: string
    signal?: GenericAbortSignal
    onDownloadProgress?: (progressEvent: AxiosProgressEvent) => void
  },
) {

  let data: Record<string, any> = {
    conversationId: params.conversationId
  }

  return get<T>({
    url: '/app/chat/stream',
    data,
    signal: params.signal,
    onDownloadProgress: params.onDownloadProgress,
  })
}
