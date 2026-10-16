import request from './index'

export function getUsers(params) {
  return request.get('/users', { params })
}

export function getUser(id) {
  return request.get(`/users/${id}`)
}

export function createUser(data) {
  return request.post('/users', data)
}

export function toggleUserStatus(id) {
  return request.put(`/users/${id}/toggle-status`)
}

export function getEngineers() {
  return request.get('/users/engineers')
}