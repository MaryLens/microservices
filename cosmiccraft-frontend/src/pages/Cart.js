import React, { useEffect, useState } from 'react';
import { getCart, removeFromCart, createOrder } from '../api';

function Cart() {
    const [cart, setCart] = useState({ items: [] });
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    useEffect(() => {
        if (user.id) getCart(user.id).then(res => setCart(res.data));
    }, [user.id]);

    const handleRemove = async (productId) => {
        await removeFromCart(user.id, productId);
        setCart({ ...cart, items: cart.items.filter(i => i.productId !== productId) });
    };

    const handleCheckout = async () => {
        const total = cart.items.reduce((sum, i) => sum + (i.quantity * 15), 0); // заглушка цены
        const orderItems = cart.items.map(i => ({ productId: i.productId, quantity: i.quantity, price: 100 }));
        try {
            await createOrder(user.id, user.email, orderItems, total);
            alert('Заказ создан! Проверьте email.');
            setCart({ items: [] });
        } catch (err) {
            alert('Ошибка создания заказа');
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <h1>Корзина</h1>
            {cart.items.map(i => (
                <div key={i.productId} style={{ border: '1px solid #ccc', padding: '10px', marginBottom: '10px' }}>
                    <p>Товар ID: {i.productId}, Количество: {i.quantity}</p>
                    <button onClick={() => handleRemove(i.productId)}>Удалить</button>
                </div>
            ))}
            <button onClick={handleCheckout} disabled={cart.items.length === 0}>Оформить заказ</button>
        </div>
    );
}

export default Cart;
