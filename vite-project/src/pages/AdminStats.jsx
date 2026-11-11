import React, { useCallback, useEffect, useState } from 'react';
import { Button, DatePicker, Form, message, Statistic, Table } from 'antd';
import { fetchBookSalesStats, fetchUserConsumptionStats } from '../api/bookstoreApi';

const { RangePicker } = DatePicker;

function formatDate(date) {
  return date ? date.format('YYYY-MM-DD') : undefined;
}

function AdminStats({ user }) {
  const [bookStats, setBookStats] = useState([]);
  const [userStats, setUserStats] = useState([]);
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  const loadStats = useCallback(async (values = {}) => {
    setLoading(true);
    try {
      const [start, end] = values.range || [];
      const params = {
        startDate: formatDate(start),
        endDate: formatDate(end),
      };
      const [books, users] = await Promise.all([
        fetchBookSalesStats(user.token, params),
        fetchUserConsumptionStats(user.token, params),
      ]);
      setBookStats(books);
      setUserStats(users);
    } catch (err) {
      message.error(err.message || '加载统计失败');
    } finally {
      setLoading(false);
    }
  }, [user.token]);

  useEffect(() => {
    loadStats();
  }, [loadStats]);

  const totalRevenue = userStats.reduce((sum, item) => sum + Number(item.totalAmount), 0);
  const totalQuantity = bookStats.reduce((sum, item) => sum + Number(item.quantity), 0);

  const bookColumns = [
    { title: '排名', render: (_, __, index) => index + 1, width: 80 },
    { title: '书名', dataIndex: 'title' },
    { title: '作者', dataIndex: 'author' },
    { title: 'ISBN', dataIndex: 'isbn' },
    { title: '销量', dataIndex: 'quantity', width: 100 },
    {
      title: '销售额',
      dataIndex: 'totalAmount',
      width: 130,
      render: (value) => `¥${Number(value).toFixed(2)}`,
    },
  ];

  const userColumns = [
    { title: '排名', render: (_, __, index) => index + 1, width: 80 },
    { title: '用户', dataIndex: 'username' },
    { title: '邮箱', dataIndex: 'email' },
    { title: '购书总本数', dataIndex: 'totalQuantity', width: 130 },
    {
      title: '累计消费',
      dataIndex: 'totalAmount',
      width: 130,
      render: (value) => `¥${Number(value).toFixed(2)}`,
    },
  ];

  return (
    <main className="main-content">
      <header><h1>统计报表</h1></header>
      <Form form={form} layout="inline" className="filter-bar" onFinish={loadStats}>
        <Form.Item name="range">
          <RangePicker />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">统计</Button>
        </Form.Item>
        <Form.Item>
          <Button onClick={() => {
            form.resetFields();
            loadStats({});
          }}>全部时间</Button>
        </Form.Item>
      </Form>
      <section className="stats-summary">
        <Statistic title="销售总本数" value={totalQuantity} />
        <Statistic title="销售总额" value={totalRevenue} precision={2} prefix="¥" />
      </section>
      <section className="admin-section">
        <h2>热销榜</h2>
        <Table rowKey="bookId" loading={loading} columns={bookColumns} dataSource={bookStats} pagination={false} />
      </section>
      <section className="admin-section">
        <h2>消费榜</h2>
        <Table rowKey="userId" loading={loading} columns={userColumns} dataSource={userStats} pagination={false} />
      </section>
    </main>
  );
}

export default AdminStats;
