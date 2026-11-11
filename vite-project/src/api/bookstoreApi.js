const API_BASE = '/api/v1';

function authHeaders(token) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  return headers;
}

async function parseResponse(res) {
  if (res.status === 204) {
    return null;
  }
  const data = await res.json().catch(() => ({}));
  if (!res.ok) {
    const message = data.message || `请求失败 (${res.status})`;
    throw new Error(message);
  }
  return data;
}

function buildQuery(params = {}) {
  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      search.set(key, value);
    }
  });
  const query = search.toString();
  return query ? `?${query}` : '';
}

export async function loginUser({ username, password }) {
  const res = await fetch(`${API_BASE}/users/login`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ username, password }),
  });
  return parseResponse(res);
}

export async function registerUser({ username, password, confirmPassword, email }) {
  const res = await fetch(`${API_BASE}/users/register`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ username, password, confirmPassword, email }),
  });
  return parseResponse(res);
}

export async function fetchUsers(token) {
  const res = await fetch(`${API_BASE}/users`, {
    headers: authHeaders(token),
  });
  return parseResponse(res);
}

export async function updateUserStatus(token, userId, enabled) {
  const res = await fetch(`${API_BASE}/users/${userId}/status`, {
    method: 'PATCH',
    headers: authHeaders(token),
    body: JSON.stringify({ enabled }),
  });
  return parseResponse(res);
}

export async function fetchBooks(params = {}) {
  const res = await fetch(`${API_BASE}/books${buildQuery(params)}`);
  return parseResponse(res);
}

export async function fetchBookById(id) {
  const res = await fetch(`${API_BASE}/book/${id}`);
  return parseResponse(res);
}

export async function createBook(token, book) {
  const res = await fetch(`${API_BASE}/admin/books`, {
    method: 'POST',
    headers: authHeaders(token),
    body: JSON.stringify(book),
  });
  return parseResponse(res);
}

export async function updateBook(token, id, book) {
  const res = await fetch(`${API_BASE}/admin/books/${id}`, {
    method: 'PUT',
    headers: authHeaders(token),
    body: JSON.stringify(book),
  });
  return parseResponse(res);
}

export async function deleteBook(token, id) {
  const res = await fetch(`${API_BASE}/admin/books/${id}`, {
    method: 'DELETE',
    headers: authHeaders(token),
  });
  return parseResponse(res);
}

export async function fetchCart(token) {
  const res = await fetch(`${API_BASE}/cart`, {
    headers: authHeaders(token),
  });
  return parseResponse(res);
}

export async function addBookToCart(token, bookId) {
  const res = await fetch(`${API_BASE}/cart`, {
    method: 'POST',
    headers: authHeaders(token),
    body: JSON.stringify({ bookId }),
  });
  return parseResponse(res);
}

export async function removeCartItem(token, cartItemId) {
  const res = await fetch(`${API_BASE}/cart/${cartItemId}`, {
    method: 'DELETE',
    headers: authHeaders(token),
  });
  return parseResponse(res);
}

export async function fetchOrders(token, params = {}) {
  const res = await fetch(`${API_BASE}/orders${buildQuery(params)}`, {
    headers: authHeaders(token),
  });
  return parseResponse(res);
}

export async function fetchAdminOrders(token, params = {}) {
  const res = await fetch(`${API_BASE}/orders/admin${buildQuery(params)}`, {
    headers: authHeaders(token),
  });
  return parseResponse(res);
}

export async function fetchBookSalesStats(token, params = {}) {
  const res = await fetch(`${API_BASE}/orders/stats/books${buildQuery(params)}`, {
    headers: authHeaders(token),
  });
  return parseResponse(res);
}

export async function fetchUserConsumptionStats(token, params = {}) {
  const res = await fetch(`${API_BASE}/orders/stats/users${buildQuery(params)}`, {
    headers: authHeaders(token),
  });
  return parseResponse(res);
}

export async function fetchMyStats(token, params = {}) {
  const res = await fetch(`${API_BASE}/orders/stats/me${buildQuery(params)}`, {
    headers: authHeaders(token),
  });
  return parseResponse(res);
}

export async function createOrder(token, cartItemIds) {
  const res = await fetch(`${API_BASE}/orders`, {
    method: 'POST',
    headers: authHeaders(token),
    body: JSON.stringify({ cartItemIds }),
  });
  return parseResponse(res);
}

export function normalizeBook(book) {
  return {
    ...book,
    price: Number(book.price),
  };
}

export function normalizeCartItem(item) {
  const price = Number(item.price);
  const quantity = item.quantity ?? 1;
  return {
    cartId: item.cartId,
    id: item.bookId,
    bookId: item.bookId,
    title: item.title,
    author: item.author,
    price,
    image: item.image,
    quantity,
    lineTotal: price * quantity,
  };
}
