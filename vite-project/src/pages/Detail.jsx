import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Spin, Alert, message } from 'antd';
import { fetchBookById, fetchCart, normalizeBook } from '../api/bookstoreApi';

function Detail({ user, addToCart, checkout }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const data = await fetchBookById(id);
        if (!cancelled) {
          setBook(normalizeBook(data));
        }
      } catch (err) {
        if (!cancelled) {
          setError(err.message || '加载书籍详情失败');
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [id]);

  const handleAddToCart = async () => {
    setActionLoading(true);
    try {
      await addToCart(book);
      message.success('已加入购物车！');
    } catch (err) {
      message.error(err.message || '加入购物车失败');
    } finally {
      setActionLoading(false);
    }
  };

  const handleBuyNow = async () => {
    setActionLoading(true);
    try {
      await addToCart(book);
      const cart = await fetchCart(user.token);
      const item = cart.find((c) => c.bookId === book.id);
      if (!item) throw new Error('购物车项未找到');
      await checkout([item.cartId]);
      message.success('购买成功');
      navigate('/orders');
    } catch (err) {
      message.error(err.message || '购买失败');
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="detail-loading">
        <Spin tip="加载中..." />
      </div>
    );
  }

  if (error || !book) {
    return (
      <main className="main-content">
        <Alert type="error" message={error || '书籍不存在'} showIcon />
      </main>
    );
  }

  return (
    <main className="main-content">
      <header>
        <button type="button" className="btn" onClick={() => navigate(-1)}>← 返回</button>
      </header>
      <article className="detail-container">
        <section className="detail-image">
          <img src={book.image} alt={book.title} />
        </section>
        <section className="detail-info">
          <h2>{book.title}</h2>
          <p>作者：{book.author}</p>
          <p>ISBN：{book.isbn || '暂无'}</p>
          <p>出版社：{book.publisher || '暂无'}</p>
          <p>库存：{book.stock ?? 0}</p>
          <div className="price-tag">¥{book.price.toFixed(2)}</div>
          <div className="action-buttons">
            <button
              type="button"
              className="btn btn-add-to-cart"
              onClick={handleAddToCart}
              disabled={actionLoading || (book.stock ?? 0) <= 0}
            >
              加入购物车
            </button>
            <button
              type="button"
              className="btn btn-buy-now"
              onClick={handleBuyNow}
              disabled={actionLoading || (book.stock ?? 0) <= 0}
            >
              立即购买
            </button>
          </div>
          <div className="description">
            <h3>简介</h3>
            <p>{book.description}</p>
          </div>
        </section>
      </article>
    </main>
  );
}

export default Detail;
