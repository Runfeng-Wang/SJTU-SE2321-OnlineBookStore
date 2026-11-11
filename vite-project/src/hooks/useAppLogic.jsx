import { useCallback, useEffect, useState } from 'react';
import {
  addBookToCart,
  createOrder,
  fetchCart,
  fetchOrders,
  loginUser as apiLogin,
  normalizeCartItem,
  removeCartItem,
} from '../api/bookstoreApi';

const USER_STORAGE_KEY = 'bookStoreUser';

const defaultUser = { id: null, name: '未定义', email: null, role: null, enabled: true, token: null };

function loadUser() {
  try {
    const raw = localStorage.getItem(USER_STORAGE_KEY);
    if (!raw) return defaultUser;
    const parsed = JSON.parse(raw);
    if (parsed?.id && parsed?.name && parsed?.token && parsed.name !== '未定义') {
      return {
        id: parsed.id,
        name: parsed.name,
        email: parsed.email ?? null,
        role: parsed.role ?? 'CUSTOMER',
        enabled: parsed.enabled ?? true,
        token: parsed.token,
      };
    }
  } catch {
    /* ignore */
  }
  return defaultUser;
}

export const useAppLogic = () => {
  const [user, setUser] = useState(loadUser);
  const [cart, setCart] = useState([]);
  const [orders, setOrders] = useState([]);

  const refreshCart = useCallback(async (token = user.token) => {
    if (!token) {
      setCart([]);
      return;
    }
    const data = await fetchCart(token);
    setCart(data.map(normalizeCartItem));
  }, [user.token]);

  const refreshOrders = useCallback(async (token = user.token, params = {}) => {
    if (!token) {
      setOrders([]);
      return;
    }
    const data = await fetchOrders(token, params);
    setOrders(data);
  }, [user.token]);

  useEffect(() => {
    if (user.token) {
      refreshCart(user.token);
      refreshOrders(user.token);
    }
  }, [user.token, refreshCart, refreshOrders]);

  const login = async (username, password) => {
    const res = await apiLogin({ username, password });
    const next = {
      id: res.id,
      name: res.username,
      email: res.email,
      role: res.role,
      enabled: res.enabled,
      token: res.token,
    };
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(next));
    setUser(next);
    await refreshCart(res.token);
    await refreshOrders(res.token);
    return next;
  };

  const logout = () => {
    localStorage.removeItem(USER_STORAGE_KEY);
    setUser(defaultUser);
    setCart([]);
    setOrders([]);
  };

  const addToCart = async (book) => {
    if (!user.token) throw new Error('请先登录');
    await addBookToCart(user.token, book.id);
    await refreshCart();
  };

  const removeFromCart = async (cartIds) => {
    if (!user.token) return;
    await Promise.all(cartIds.map((id) => removeCartItem(user.token, id)));
    await refreshCart();
  };

  const checkout = async (cartItemIds) => {
    if (!user.token) throw new Error('请先登录');
    await createOrder(user.token, cartItemIds);
    await refreshCart();
    await refreshOrders();
  };

  return {
    user,
    cart,
    orders,
    login,
    logout,
    addToCart,
    removeFromCart,
    checkout,
    refreshCart,
    refreshOrders,
  };
};
