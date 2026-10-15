import request from '../utils/request'

// 认证
export const adminLogin = (data) => request.post('/admin/auth/login', data)
export const getMenus = () => request.get('/admin/auth/menus')

// 商品
export const getProductPage = (params) => request.get('/admin/product/page', { params })
export const addProduct = (data) => request.post('/admin/product', data)
export const updateProduct = (data) => request.put('/admin/product', data)
export const deleteProduct = (id) => request.delete(`/admin/product/${id}`)
export const onoffProduct = (id, status) => request.put(`/admin/product/${id}/status/${status}`)
export const updateStock = (id, stock) => request.put(`/admin/product/${id}/stock`, null, { params: { stock } })

// 分类
export const getCategoryList = () => request.get('/admin/category/list')
export const addCategory = (data) => request.post('/admin/category', data)
export const updateCategory = (data) => request.put('/admin/category', data)
export const deleteCategory = (id) => request.delete(`/admin/category/${id}`)

// 订单
export const getOrderPage = (params) => request.get('/admin/order/page', { params })
export const shipOrder = (id) => request.put(`/admin/order/${id}/ship`)

// 用户
export const getUserPage = (params) => request.get('/admin/user/page', { params })
export const setUserStatus = (id, status) => request.put(`/admin/user/${id}/status/${status}`)

// 上传
export const uploadImage = (file) => {
  const form = new FormData()
  form.append('file', file)
  return request.post('/common/upload', form, { headers: { 'Content-Type': 'multipart/form-data' } })
}