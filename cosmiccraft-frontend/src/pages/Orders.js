import React, { useEffect, useState } from 'react';
import { getOrders } from '../api';

function Orders() {
    const [orders, setOrders] = useState([]);
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    useEffect(() => {
        if (user.id) getOrders(user.id).then(res => setOrders(res.data));
    }, [user.id]);

    return (
        <div style={{ padding: '20px' }}>
            <h1>Мои заказы</h1>
            {orders.map(order => (
                <div key={order.id} style={{ border: '1px solid #ccc', marginBottom: 10, padding: 10 }}>
                    <p>Статус: {order.status}</p>
                    <p>Дата: {order.createdDate}</p>
                    <p>Сумма: {order.total}₽</p>
                    <div>Товары: {order.items.map(i => <span key={i.productId}>{i.productId} x {i.quantity}; </span>)}</div>
                </div>
            ))}
        </div>
    );
}
export default Orders;
