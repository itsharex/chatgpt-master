import request from '@/utils/request'

// 查询分页缩略图配置列表
export function pageUploadConfig(query) {
  return request({
    url: '/gpt/upload-config/page',
    method: 'get',
    params: query
  })
}

// 查询部分耶缩略图配置列表
export function listUploadConfig(query) {
  return request({
    url: '/gpt/upload-config/list',
    method: 'get',
    params: query
  })
}

// 查询缩略图配置详细
export function getUploadConfig(id) {
  return request({
    url: '/gpt/upload-config/' + id,
    method: 'get'
  })
}

// 新增缩略图配置
export function addUploadConfig(data) {
  return request({
    url: '/gpt/upload-config',
    method: 'post',
    data: data
  })
}

// 修改缩略图配置
export function updateUploadConfig(data) {
  return request({
    url: '/gpt/upload-config',
    method: 'put',
    data: data
  })
}

// 删除缩略图配置
export function delUploadConfig(id) {
  return request({
    url: '/gpt/upload-config/' + id,
    method: 'delete'
  })
}
