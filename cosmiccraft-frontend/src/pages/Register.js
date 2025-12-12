import React, { useState } from 'react';
import { register } from '../api';
import { useNavigate } from 'react-router-dom';

function Register() {
    const [form, setForm] = useState({ name: '', email: '', phoneNumber: '', password: '' });
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await register(form);
            alert('Регистрация успешна!');
            navigate('/login');
        } catch (err) {
            alert('Ошибка: ' + err.response?.data);
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <h1>Регистрация</h1>
            <form onSubmit={handleSubmit}>
                <input placeholder="Имя" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required /><br />
                <input placeholder="Email" type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required /><br />
                <input placeholder="Телефон" value={form.phoneNumber} onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })} /><br />
                <input placeholder="Пароль" type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required /><br />
                <button type="submit">Зарегистрироваться</button>
            </form>
        </div>
    );
}

export default Register;
