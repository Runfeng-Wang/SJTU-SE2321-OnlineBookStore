import React, { useCallback, useEffect, useState } from 'react';
import { Button, message, Space, Table, Tag } from 'antd';
import { fetchUsers, updateUserStatus } from '../api/bookstoreApi';

function AdminUsers({ user }) {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadUsers = useCallback(async () => {
    setLoading(true);
    try {
      const data = await fetchUsers(user.token);
      setUsers(data);
    } catch (err) {
      message.error(err.message || '加载用户失败');
    } finally {
      setLoading(false);
    }
  }, [user.token]);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  const handleToggle = async (record) => {
    try {
      await updateUserStatus(user.token, record.id, !record.enabled);
      message.success(record.enabled ? '已禁用用户' : '已解禁用户');
      loadUsers();
    } catch (err) {
      message.error(err.message || '更新用户状态失败');
    }
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 80 },
    { title: '用户名', dataIndex: 'username' },
    { title: '邮箱', dataIndex: 'email' },
    {
      title: '角色',
      dataIndex: 'role',
      render: (role) => <Tag color={role === 'ADMIN' ? 'blue' : 'default'}>{role === 'ADMIN' ? '管理员' : '顾客'}</Tag>,
    },
    {
      title: '状态',
      dataIndex: 'enabled',
      render: (enabled) => <Tag color={enabled ? 'green' : 'red'}>{enabled ? '正常' : '已禁用'}</Tag>,
    },
    {
      title: '操作',
      render: (_, record) => (
        <Space>
          <Button
            danger={record.enabled}
            disabled={record.id === user.id}
            onClick={() => handleToggle(record)}
          >
            {record.enabled ? '禁用' : '解禁'}
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <main className="main-content">
      <header>
        <h1>用户管理</h1>
        <Button onClick={loadUsers}>刷新</Button>
      </header>
      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={users}
        pagination={{ pageSize: 8 }}
      />
    </main>
  );
}

export default AdminUsers;
