import React, { useEffect, useState } from 'react';
import { getProducts, addToCart } from '../api';

function Products() {
    const [products, setProducts] = useState([]);
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    useEffect(() => {
        getProducts().then(res => setProducts(res.data));
    }, []);

    const handleAddToCart = async (productId) => {
        if (!user.id) return alert('Войдите в систему!');
        try {
            await addToCart(user.id, productId, 1);
            alert('Товар добавлен в корзину!');
        } catch (err) {
            alert('Ошибка');
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <h1>Товары</h1>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px' }}>
                {products.map(p => (
                    <div key={p.id} style={{ border: '1px solid #ccc', padding: '10px' }}>
                        <h3>{p.title}</h3>
                        <p>{p.description}</p>
                        <p>Цена: {p.price}₽</p>
                        <button onClick={() => handleAddToCart(p.id)}>В корзину</button>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default Products;
