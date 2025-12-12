import React, { useEffect, useState } from 'react';
import {
    getProducts, createProduct, deleteProduct,
    getCategories, createCategory, deleteCategory,
    getAllOrders, updateOrderStatus,
    getAllUsers
} from '../api';

function AdminPanel() {
    // -- товары --
    const [products, setProducts] = useState([]);
    const [newProduct, setNewProduct] = useState({ title: '', description: '', price: '', categoryId: '' });
    // -- категории --
    const [categories, setCategories] = useState([]);
    const [newCategory, setNewCategory] = useState({ name: '', description: '' });
    // -- заказы --
    const [orders, setOrders] = useState([]);
    // -- юзеры --
    const [users, setUsers] = useState([]);

    useEffect(() => {
        getProducts().then(res => setProducts(res.data || []));
        getCategories().then(res => setCategories(res.data || []));
        getAllOrders().then(res => setOrders(res.data || []));
        getAllUsers().then(res => setUsers(res.data || []));
    }, []);

    // --- ТОВАРЫ ---
    const handleDeleteProduct = async (id) => {
        await deleteProduct(id);
        setProducts(products.filter(p => p.id !== id));
    };
    const handleCreateProduct = async (e) => {
        e.preventDefault();
        let { title, description, price, categoryId } = newProduct;
        price = +price;
        await createProduct({ title, description, price, categoryId });
        const res = await getProducts();
        setProducts(res.data || []);
        setNewProduct({ title: '', description: '', price: '', categoryId: '' });
    };

    // --- КАТЕГОРИИ ---
    const handleDeleteCategory = async (id) => {
        await deleteCategory(id);
        setCategories(categories.filter(c => c.id !== id));
    };
    const handleCreateCategory = async (e) => {
        e.preventDefault();
        await createCategory(newCategory.name, newCategory.description);
        const res = await getCategories();
        setCategories(res.data || []);
        setNewCategory({ name: '', description: '' });
    };

    // --- ЗАКАЗЫ ---
    const handleUpdateOrderStatus = async (id, status) => {
        await updateOrderStatus(id, status);
        setOrders(orders.map(o => o.id === id ? {...o, status} : o));
    };

    return (
        <div style={{ padding: '20px' }}>
            <h1>Админка</h1>

            {/* Все пользователи */}
            <section>
                <h2>Пользователи</h2>
                {users.map(u => (
                    <div key={u.id}>
                        {u.name} — {u.email} — {u.role}
                    </div>
                ))}
            </section>
            {/* Все заказы */}
            <section>
                <h2>Заказы</h2>
                {orders.map(o => (
                    <div key={o.id} style={{ border: '1px solid #ccc', marginBottom: 10, padding: 10 }}>
                        Заказ #{o.id}, Пользователь: {o.userId}, Статус: {o.status}, Сумма: {o.total}
                        <div>
                            <button onClick={() => handleUpdateOrderStatus(o.id, 'SHIPPED')}>Выполнен</button>
                            <button onClick={() => handleUpdateOrderStatus(o.id, 'CANCELLED')}>Отменить</button>
                        </div>
                        <div>Товары: {o.items.map(i => <span key={i.productId}>{i.productId} x {i.quantity}; </span>)}</div>
                    </div>
                ))}
            </section>
            {/* Все категории + форма добавления */}
            <section>
                <h2>Категории</h2>
                <form onSubmit={handleCreateCategory}>
                    <input
                        placeholder="Имя категории"
                        value={newCategory.name}
                        onChange={e => setNewCategory({ ...newCategory, name: e.target.value })}
                        required />
                    <input
                        placeholder="Описание"
                        value={newCategory.description}
                        onChange={e => setNewCategory({ ...newCategory, description: e.target.value })}
                        required />
                    <button type="submit">Добавить</button>
                </form>
                {categories.map(c => (
                    <div key={c.id}>
                        {c.name}: {c.description}
                        <button onClick={() => handleDeleteCategory(c.id)}>Удалить</button>
                    </div>
                ))}
            </section>
            {/* Все товары + форма добавления */}
            <section>
                <h2>Товары</h2>
                <form onSubmit={handleCreateProduct}>
                    <input
                        placeholder="Название"
                        value={newProduct.title}
                        onChange={e => setNewProduct({ ...newProduct, title: e.target.value })}
                        required />
                    <input
                        placeholder="Описание"
                        value={newProduct.description}
                        onChange={e => setNewProduct({ ...newProduct, description: e.target.value })}
                        required />
                    <input
                        placeholder="Цена"
                        type="number"
                        value={newProduct.price}
                        onChange={e => setNewProduct({ ...newProduct, price: e.target.value })}
                        required />
                    <select
                        value={newProduct.categoryId}
                        onChange={e => setNewProduct({ ...newProduct, categoryId: e.target.value })}
                        required>
                        <option value="">Категория</option>
                        {categories.map(c => (
                            <option key={c.id} value={c.id}>{c.name}</option>
                        ))}
                    </select>
                    <button type="submit">Добавить товар</button>
                </form>
                {products.map(p => (
                    <div key={p.id}>
                        {p.title}: {p.price}₽ — Категория: {p.categoryName}
                        <button onClick={() => handleDeleteProduct(p.id)}>Удалить</button>
                    </div>
                ))}
            </section>
        </div>
    );
}

export default AdminPanel;
