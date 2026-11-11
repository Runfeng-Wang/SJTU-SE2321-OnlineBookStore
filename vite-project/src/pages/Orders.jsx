import React, { useCallback, useEffect, useState } from 'react';
import { Button, DatePicker, Form, Input, message, Spin } from 'antd';
import { fetchOrders } from '../api/bookstoreApi';

const { RangePicker } = DatePicker;

function formatDate(date) {
  return date ? date.format('YYYY-MM-DD') : undefined;
}

function Orders({ orders: initialOrders, user }) {
  const [orders, setOrders] = useState(initialOrders || []);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  const loadOrders = useCallback(async (values = {}) => {
    if (!user?.token) return;
    setLoading(true);
    try {
      const [start, end] = values.range || [];
      const data = await fetchOrders(user.token, {
        startDate: formatDate(start),
        endDate: formatDate(end),
        bookName: values.bookName,
      });
      setOrders(data);
    } catch (err) {
      message.error(err.message || '加载订单失败');
    } finally {
      setLoading(false);
    }
  }, [user?.token]);

  useEffect(() => {
    setOrders(initialOrders || []);
  }, [initialOrders]);

  return (
    <main className="main-content">
      <header><h1>我的订单 ({orders.length})</h1></header>
      <Form form={form} layout="inline" className="filter-bar" onFinish={loadOrders}>
        <Form.Item name="range">
          <RangePicker />
        </Form.Item>
        <Form.Item name="bookName">
          <Input allowClear placeholder="书籍名称" />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">筛选</Button>
        </Form.Item>
        <Form.Item>
          <Button onClick={() => {
            form.resetFields();
            loadOrders({});
          }}>全部</Button>
        </Form.Item>
      </Form>
      {loading && <Spin className="inline-spinner" />}
      {orders.length === 0 ? (
        <p>暂无订单记录</p>
      ) : (
        orders.map((order) => (
          <article className="order-card" key={order.orderId}>
            <div className="order-header">
              <span>订单号: {order.orderId}</span>
              <span className="order-status">{order.status === 'PAID' ? '已支付' : order.status}</span>
            </div>
            {order.items.map((item) => (
              <div className="order-body" key={item.id}>
                <img src={item.image} alt="封面" />
                <div className="order-body-main">
                  <h4>{item.title}</h4>
                  <p className="order-qty-muted">数量: {item.quantity}</p>
                </div>
                <div>¥{(Number(item.price) * item.quantity).toFixed(2)}</div>
              </div>
            ))}
            <div className="order-footer-total">
              合计: ¥{Number(order.totalAmount).toFixed(2)}
            </div>
          </article>
        ))
      )}
    </main>
  );
}

export default Orders;
