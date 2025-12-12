import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

function Header() {
    const navigate = useNavigate();
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    const isLoggedIn = !!user.id;
    const isAdmin = user.role === 'ROLE_ADMIN';

    const handleLogout = () => {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <div style={{ padding: '10px', background: '#eee', marginBottom: '20px' }}>
            <Link to="/">Товары</Link> |
            {isLoggedIn && (
                <>
                    <Link to="/cart">Корзина</Link> |
                    <Link to="/wishlist">Избранное</Link> |
                    <Link to="/orders">Заказы</Link> |
                    {isAdmin && <Link to="/admin">Админка</Link>} |
                </>
            )}
            {!isLoggedIn && (
                <>
                    <Link to="/login">Вход</Link> |
                    <Link to="/register">Регистрация</Link>
                </>
            )}
            {isLoggedIn && (
                <>
                    <span style={{ marginLeft: '10px' }}>{user.email}</span>
                    <button onClick={handleLogout} style={{ marginLeft: '10px' }}>
                        Выйти
                    </button>
                </>
            )}
        </div>
    );
}

export default Header;
