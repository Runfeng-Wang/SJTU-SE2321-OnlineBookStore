import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Descriptions, Avatar, Card, Button, message } from 'antd';
import { UserOutlined } from '@ant-design/icons';

function Profile({ user, onLogout }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    onLogout();
    message.success('已退出登录');
    navigate('/', { replace: true });
  };

  return (
    <main className="main-content profile-content">
      <Card title="个人信息中心">
        <div className="profile-card-intro">
          <Avatar size={64} icon={<UserOutlined />} />
          <h2 className="profile-user-title">{user?.name || '未知用户'}</h2>
        </div>
        <Descriptions bordered column={1}>
          <Descriptions.Item label="用户 ID">{user?.id}</Descriptions.Item>
          <Descriptions.Item label="当前账号">{user?.name}</Descriptions.Item>
          <Descriptions.Item label="邮箱">{user?.email || '暂无'}</Descriptions.Item>
          <Descriptions.Item label="角色">{user?.role === 'ADMIN' ? '管理员' : '顾客'}</Descriptions.Item>
          <Descriptions.Item label="状态">{user?.enabled ? '正常' : '已禁用'}</Descriptions.Item>
        </Descriptions>
        <Button type="primary" danger className="profile-save-btn" block onClick={handleLogout}>
          退出登录
        </Button>
      </Card>
    </main>
  );
}

export default Profile;
