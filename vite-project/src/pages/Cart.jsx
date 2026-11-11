import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { message } from 'antd';
import { useCartSelection } from '../hooks/useCartSelection';
import { calculateTotal } from '../utils/orderUtils';

function Cart({ cart, checkout, removeFromCart }) {
  const navigate = useNavigate();
  const { selectedIds, toggleSelect, clearSelection } = useCartSelection();
  const [loading, setLoading] = useState(false);

  const selectedBooks = cart.filter((item) => selectedIds.includes(item.cartId));
  const totalPrice = calculateTotal(selectedBooks);

  const handleCheckout = async () => {
    if (selectedIds.length === 0) {
      message.warning('请先勾选书籍');
      return;
    }
    setLoading(true);
    try {
      await checkout(selectedIds);
      clearSelection();
      message.success('下单成功');
      navigate('/orders');
    } catch (err) {
      message.error(err.message || '下单失败');
    } finally {
      setLoading(false);
    }
  };

  const handleRemove = async (cartId) => {
    try {
      await removeFromCart([cartId]);
      message.success('已删除');
    } catch (err) {
      message.error(err.message || '删除失败');
    }
  };

  return (
    <main className="main-content">
      <header><h1>我的购物车 ({cart.length})</h1></header>
      <section className="cart-list">
        {cart.length === 0 ? (
          <p>购物车空空如也...</p>
        ) : (
          cart.map((item) => (
            <div className="cart-item" key={item.cartId}>
              <input
                type="checkbox"
                onChange={() => toggleSelect(item.cartId)}
                checked={selectedIds.includes(item.cartId)}
              />
              <img src={item.image} alt="封面" />
              <div className="item-info">
                <h3>{item.title}</h3>
                <p className="price">
                  ¥{item.price.toFixed(2)} × {item.quantity} = ¥{item.lineTotal.toFixed(2)}
                </p>
              </div>
              <button type="button" className="btn btn-danger" onClick={() => handleRemove(item.cartId)}>
                删除
              </button>
            </div>
          ))
        )}
      </section>
      {cart.length > 0 && (
        <footer className="cart-footer">
          <span>
            总计: <strong className="cart-total-em">¥{totalPrice.toFixed(2)}</strong>
          </span>
          <button
            type="button"
            className="btn btn-primary btn-checkout"
            onClick={handleCheckout}
            disabled={loading}
          >
            {loading ? '结算中...' : '结算所选'}
          </button>
        </footer>
      )}
    </main>
  );
}

export default Cart;
