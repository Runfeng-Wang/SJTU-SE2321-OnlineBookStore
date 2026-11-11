import React, { useCallback, useEffect, useState } from 'react';
import { Button, DatePicker, Form, message, Statistic, Table } from 'antd';
import { fetchMyStats } from '../api/bookstoreApi';

const { RangePicker } = DatePicker;

function formatDate(date) {
  return date ? date.format('YYYY-MM-DD') : undefined;
}

function MyStats({ user }) {
  const [stats, setStats] = useState({ totalQuantity: 0, totalAmount: 0, books: [] });
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();

  const loadStats = useCallback(async (values = {}) => {
    setLoading(true);
    try {
      const [start, end] = values.range || [];
      const data = await fetchMyStats(user.token, {
        startDate: formatDate(start),
        endDate: formatDate(end),
      });
      setStats(data);
    } catch (err) {
      message.error(err.message || '加载统计失败');
    } finally {
      setLoading(false);
    }
  }, [user.token]);

  useEffect(() => {
    loadStats();
  }, [loadStats]);

  const columns = [
    { title: '书名', dataIndex: 'title' },
    { title: '购买数量', dataIndex: 'quantity', width: 120 },
    {
      title: '金额',
      dataIndex: 'totalAmount',
      width: 130,
      render: (value) => `¥${Number(value).toFixed(2)}`,
    },
  ];

  return (
    <main className="main-content">
      <header><h1>我的统计</h1></header>
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
        <Statistic title="购书总本数" value={stats.totalQuantity || 0} />
        <Statistic title="购书总金额" value={Number(stats.totalAmount || 0)} precision={2} prefix="¥" />
      </section>
      <Table
        rowKey="bookId"
        loading={loading}
        columns={columns}
        dataSource={stats.books || []}
        pagination={false}
      />
    </main>
  );
}

export default MyStats;
