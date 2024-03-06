import request from '@/utils/request'

// 查询在线用户列表
export function list(query) {
  return request({
    url: '/login-log/online/list',
    method: 'get',
    params: query
  })
}

// 强退用户
export function forceLogout(ids) {
  return request({
    url: '/login-log/force/' + ids,
    method: 'delete'
  })
}
