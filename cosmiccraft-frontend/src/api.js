import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

export const register = (data) => axios.post(`${API_URL}/users/register`, data);
export const login = (data) => axios.post(`${API_URL}/users/login`, data);

export const getProducts = (params) => axios.get(`${API_URL}/products`, { params });
export const getProduct = (id) => axios.get(`${API_URL}/products/${id}`);

export const getCart = (userId) => axios.get(`${API_URL}/cart/${userId}`);
export const addToCart = (userId, productId, quantity) =>
    axios.post(`${API_URL}/cart/${userId}/items?productId=${productId}&quantity=${quantity}`);
export const removeFromCart = (userId, productId) =>
    axios.delete(`${API_URL}/cart/${userId}/items/${productId}`);

export const createOrder = (userId, userEmail, items, total) =>
    axios.post(`${API_URL}/orders/create?userId=${userId}&userEmail=${userEmail}&total=${total}`, items);

export const getOrders = (userId) => axios.get(`${API_URL}/orders/user/${userId}`);
export const getWishlist = (userId) => axios.get(`${API_URL}/wishlist/${userId}`);
export const addToWishlist = (userId, productId) => axios.post(`${API_URL}/wishlist/${userId}/products?productId=${productId}`);
export const removeFromWishlist = (userId, productId) => axios.delete(`${API_URL}/wishlist/${userId}/products/${productId}`);

export const getCategories = () => axios.get(`${API_URL}/categories`);
export const createCategory = (name, description) => axios.post(`${API_URL}/categories`, null, { params: { name, description } });
export const deleteCategory = (id) => axios.delete(`${API_URL}/categories/${id}`);

export const createProduct = (data) => axios.post(`${API_URL}/products`, data);
export const deleteProduct = (id) => axios.delete(`${API_URL}/products/${id}`);

export const getAllOrders = () => axios.get(`${API_URL}/orders/all`);
export const updateOrderStatus = (id, status) => axios.put(`${API_URL}/orders/${id}/status?status=${status}`);

export const getAllUsers = () => axios.get(`${API_URL}/users/all`);
