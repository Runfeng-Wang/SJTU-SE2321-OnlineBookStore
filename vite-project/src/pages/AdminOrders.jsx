import React, { useCallback, useEffect, useState } from 'react';
import { Button, DatePicker, Form, Input, message, Table, Tag } from 'antd';
import { fetchAdminOrders } from '../api/bookstoreApi';

const { RangePicker } = DatePicker;

function formatDate(date) {
  return date ? date.format('YYYY-MM-DD') : undefined;
}

function AdminOrders({ user }) {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  const loadOrders = useCallback(async (values = {}) => {
    setLoading(true);
    try {
      const [start, end] = values.range || [];
      const data = await fetchAdminOrders(user.token, {
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
  }, [user.token]);

  useEffect(() => {
    loadOrders();
  }, [loadOrders]);

  const columns = [
    { title: '订单号', dataIndex: 'orderId', width: 100 },
    { title: '用户', dataIndex: 'username', width: 140 },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      width: 190,
      render: (value) => value?.replace('T', ' ').slice(0, 19),
    },
    {
      title: '书籍',
      dataIndex: 'items',
      render: (items) => items.map((item) => `${item.title} x${item.quantity}`).join('，'),
    },
    {
      title: '金额',
      dataIndex: 'totalAmount',
      width: 120,
      render: (value) => `¥${Number(value).toFixed(2)}`,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (value) => <Tag color="green">{value === 'PAID' ? '已支付' : value}</Tag>,
    },
  ];

  return (
    <main className="main-content">
      <header><h1>订单管理</h1></header>
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
      <Table
        rowKey="orderId"
        loading={loading}
        columns={columns}
        dataSource={orders}
        pagination={{ pageSize: 8 }}
      />
    </main>
  );
}

export default AdminOrders;
