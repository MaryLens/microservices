import React from 'react';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Register from './pages/Register';
import Login from './pages/Login';
import Products from './pages/Products';
import Cart from './pages/Cart';
import Orders from './pages/Orders';
import AdminPanel from './pages/AdminPanel';
import Wishlist from "./pages/Wishlist";
import Header from './Header';


function App() {
    return (
        <Router>
            <Header/>
            <Routes>
                <Route path="/register" element={<Register/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/" element={<Products/>}/>
                <Route path="/cart" element={<Cart/>}/>
                <Route path="/orders" element={<Orders/>}/>
                <Route path="/admin" element={<AdminPanel/>}/>
                <Route path="/wishlist" element={<Wishlist/>}/>
            </Routes>
        </Router>
    );
}

export default App;
