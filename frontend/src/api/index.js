import request from '../utils/request'

// ============ 认证 ============
export const sendSmsCode = (phone) => request.post('/member/auth/sms-code', null, { params: { phone } })
export const register = (data) => request.post('/member/auth/register', data)
export const login = (data) => request.post('/member/auth/login', data)
export const loginBySms = (data) => request.post('/member/auth/login/sms', data)

// ============ 分类 ============
export const getCategories = () => request.get('/category/list')

// ============ 商品 ============
export const getProducts = (params) => request.get('/product/list', { params })
export const getProductDetail = (id) => request.get(`/product/${id}`)

// ============ 会员 ============
export const getProfile = () => request.get('/member/profile')
export const updateProfile = (data) => request.put('/member/profile', data)
export const getAddressList = () => request.get('/member/address')
export const addAddress = (data) => request.post('/member/address', data)
export const updateAddress = (data) => request.put('/member/address', data)
export const deleteAddress = (id) => request.delete(`/member/address/${id}`)
export const setDefaultAddress = (id) => request.put(`/member/address/${id}/default`)

// ============ 购物车 ============
export const getCart = () => request.get('/member/cart')
export const addCart = (data) => request.post('/member/cart', data)
export const updateCart = (data) => request.put('/member/cart', data)
export const deleteCart = (productId) => request.delete(`/member/cart/${productId}`)
export const clearCart = () => request.delete('/member/cart')

// ============ 订单 ============
export const createOrder = (data) => request.post('/member/order', data)
export const getOrders = (params) => request.get('/member/order/page', { params })
export const getOrderDetail = (id) => request.get(`/member/order/${id}`)
export const cancelOrder = (id) => request.put(`/member/order/${id}/cancel`)
export const payOrder = (id) => request.put(`/member/order/${id}/pay`)