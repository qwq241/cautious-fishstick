import request from './index'

export function getTickets(params) {
  return request.get('/tickets', { params })
}

export function getMyCreated(params) {
  return request.get('/tickets/my-created', { params })
}

export function getMyAssigned(params) {
  return request.get('/tickets/my-assigned', { params })
}

export function getTicket(id) {
  return request.get(`/tickets/${id}`)
}

export function createTicket(data) {
  return request.post('/tickets', data)
}

export function assignTicket(id, data) {
  return request.put(`/tickets/${id}/assign`, data)
}

export function startProcess(id) {
  return request.put(`/tickets/${id}/start`)
}

export function resolveTicket(id) {
  return request.put(`/tickets/${id}/resolve`)
}

export function closeTicket(id) {
  return request.put(`/tickets/${id}/close`)
}

export function reopenTicket(id) {
  return request.put(`/tickets/${id}/reopen`)
}

export function addComment(id, content) {
  return request.post(`/tickets/${id}/comments`, content)
}

export function uploadAttachment(id, formData) {
  return request.post(`/tickets/${id}/attachments`, formData, { headers: { 'Content-Type': 'multipart/form-data' } })
}

export function autoAssign() {
  return request.post('/tickets/auto-assign')
}

export function submitForApproval(id) {
  return request.post(`/tickets/${id}/submit-approval`)
}

export function approveTicket(id, data) {
  return request.put(`/tickets/${id}/approve`, data)
}

export function rejectTicket(id, data) {
  return request.put(`/tickets/${id}/reject`, data)
}

export function getStatistics() {
  return request.get('/statistics/overview')
}