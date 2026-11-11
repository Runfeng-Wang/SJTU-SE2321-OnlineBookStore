import React from 'react';
import { Layout, Menu } from 'antd';
import {
  BarChartOutlined,
  BookOutlined,
  HomeOutlined,
  ShoppingCartOutlined,
  HistoryOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';

const { Sider } = Layout;

function Sidebar({ user }) {
  const navigate = useNavigate();
  const location = useLocation();
  const isAdmin = user?.role === 'ADMIN';

  const menuItems = [
    { key: '/', icon: <HomeOutlined />, label: '首页' },
    { key: '/cart', icon: <ShoppingCartOutlined />, label: '我的购物车' },
    { key: '/orders', icon: <HistoryOutlined />, label: '我的订单' },
    { key: '/my-stats', icon: <BarChartOutlined />, label: '我的统计' },
    ...(isAdmin ? [
      { type: 'divider' },
      { key: '/admin/users', icon: <TeamOutlined />, label: '用户管理' },
      { key: '/admin/books', icon: <BookOutlined />, label: '书籍管理' },
      { key: '/admin/orders', icon: <HistoryOutlined />, label: '订单管理' },
      { key: '/admin/stats', icon: <BarChartOutlined />, label: '统计报表' },
    ] : []),
    { type: 'divider' },
    { key: '/profile', icon: <UserOutlined />, label: '个人信息' },
  ];

  return (
    <Sider breakpoint="lg" collapsedWidth="0" theme="light" className="app-sidebar">
      <div className="app-sidebar-brand">
        RF书城
      </div>
      <Menu
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        onClick={({ key }) => navigate(key)}
      />
    </Sider>
  );
}

export default Sidebar;
