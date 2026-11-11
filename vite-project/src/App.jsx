import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAppLogic } from './hooks/useAppLogic';
import MainLayout from './components/MainLayout';
import Home from './pages/Home';
import Detail from './pages/Detail';
import Login from './pages/Login';
import Register from './pages/Register';
import Cart from './pages/Cart';
import Orders from './pages/Orders';
import Profile from './pages/Profile';
import AdminBooks from './pages/AdminBooks';
import AdminOrders from './pages/AdminOrders';
import AdminStats from './pages/AdminStats';
import AdminUsers from './pages/AdminUsers';
import MyStats from './pages/MyStats';
import './style.css';

const isLoggedIn = (user) => user?.id != null;

function RootGate({ user, onLogin }) {
  if (!isLoggedIn(user)) {
    return <Login onLogin={onLogin} />;
  }
  return <MainLayout user={user} />;
}

function AdminRoute({ user, children }) {
  if (user?.role !== 'ADMIN') {
    return <Navigate to="/" replace />;
  }
  return children;
}

function App() {
  const {
    user,
    cart,
    orders,
    login,
    logout,
    addToCart,
    removeFromCart,
    checkout,
  } = useAppLogic();

  return (
    <Router>
      <Routes>
        <Route
          path="/register"
          element={isLoggedIn(user) ? <Navigate to="/" replace /> : <Register />}
        />
        <Route path="/" element={<RootGate user={user} onLogin={login} />}>
          <Route index element={<Home />} />
          <Route
            path="cart"
            element={
              <Cart cart={cart} checkout={checkout} removeFromCart={removeFromCart} />
            }
          />
          <Route path="orders" element={<Orders orders={orders} user={user} />} />
          <Route path="my-stats" element={<MyStats user={user} />} />
          <Route
            path="detail/:id"
            element={
              <Detail user={user} addToCart={addToCart} checkout={checkout} />
            }
          />
          <Route path="profile" element={<Profile user={user} onLogout={logout} />} />
          <Route
            path="admin/users"
            element={<AdminRoute user={user}><AdminUsers user={user} /></AdminRoute>}
          />
          <Route
            path="admin/books"
            element={<AdminRoute user={user}><AdminBooks user={user} /></AdminRoute>}
          />
          <Route
            path="admin/orders"
            element={<AdminRoute user={user}><AdminOrders user={user} /></AdminRoute>}
          />
          <Route
            path="admin/stats"
            element={<AdminRoute user={user}><AdminStats user={user} /></AdminRoute>}
          />
        </Route>
        <Route path="/login" element={<Navigate to="/" replace />} />
        <Route path="/home" element={<Navigate to="/" replace />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
