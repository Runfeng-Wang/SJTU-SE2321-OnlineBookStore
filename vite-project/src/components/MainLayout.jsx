import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';

function MainLayout({ user }) {
  return (
    <div className="page-with-sidebar">
      <Sidebar user={user} />
      <Outlet />
    </div>
  );
}

export default MainLayout;
