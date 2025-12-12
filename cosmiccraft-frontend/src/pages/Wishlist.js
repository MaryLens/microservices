import React, { useEffect, useState } from 'react';
import { getWishlist, addToWishlist, removeFromWishlist } from '../api';

function Wishlist() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const [wishlist, setWishlist] = useState({ products: [] });

    useEffect(() => {
        if (user.id) getWishlist(user.id).then(res => setWishlist(res.data));
    }, [user.id]);

    const handleRemove = async (productId) => {
        await removeFromWishlist(user.id, productId);
        setWishlist({
            ...wishlist,
            products: wishlist.products.filter(pid => pid !== productId)
        });
    };

    return (
        <div style={{ padding: '20px' }}>
            <h1>Избранное</h1>
            {wishlist.products.map(pid => (
                <div key={pid} style={{ border: '1px solid #ccc', marginBottom: 10 }}>
                    Товар ID: {pid}
                    <button onClick={() => handleRemove(pid)}>Удалить</button>
                </div>
            ))}
        </div>
    );
}
export default Wishlist;
