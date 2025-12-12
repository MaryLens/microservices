import React, { useState } from 'react';
import { login } from '../api';
import { useNavigate } from 'react-router-dom';

function Login() {
    const [form, setForm] = useState({ email: '', password: '' });
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const res = await login(form);
            localStorage.setItem('user', JSON.stringify(res.data.user));
            localStorage.setItem('token', res.data.token);
            alert('Вход выполнен!');
            navigate('/');
        } catch (err) {
            alert('Ошибка: ' + err.response?.data);
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <h1>Вход</h1>
            <form onSubmit={handleSubmit}>
                <input placeholder="Email" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required /><br />
                <input placeholder="Пароль" type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required /><br />
                <button type="submit">Войти</button>
            </form>
        </div>
    );
}

export default Login;
