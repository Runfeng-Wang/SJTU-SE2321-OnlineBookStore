import React, { useCallback, useEffect, useState } from 'react';
import {
  Button,
  Form,
  Image,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Space,
  Table,
} from 'antd';
import { createBook, deleteBook, fetchBooks, normalizeBook, updateBook } from '../api/bookstoreApi';

const emptyBook = {
  title: '',
  author: '',
  price: 0,
  category: '',
  image: '',
  isbn: '',
  stock: 0,
  publisher: '',
  description: '',
};

function AdminBooks({ user }) {
  const [books, setBooks] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editingBook, setEditingBook] = useState(null);
  const [form] = Form.useForm();

  const loadBooks = useCallback(async (title = '') => {
    setLoading(true);
    try {
      const data = await fetchBooks({ title });
      setBooks(data.map(normalizeBook));
    } catch (err) {
      message.error(err.message || '加载书籍失败');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadBooks();
  }, [loadBooks]);

  const openCreate = () => {
    setEditingBook({ ...emptyBook });
    form.setFieldsValue(emptyBook);
  };

  const openEdit = (book) => {
    setEditingBook(book);
    form.setFieldsValue(book);
  };

  const handleSave = async () => {
    const values = await form.validateFields();
    setSaving(true);
    try {
      if (editingBook.id) {
        await updateBook(user.token, editingBook.id, values);
        message.success('书籍已更新');
      } else {
        await createBook(user.token, values);
        message.success('书籍已添加');
      }
      setEditingBook(null);
      loadBooks(keyword);
    } catch (err) {
      message.error(err.message || '保存失败');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteBook(user.token, id);
      message.success('书籍已删除');
      loadBooks(keyword);
    } catch (err) {
      message.error(err.message || '删除失败');
    }
  };

  const columns = [
    {
      title: '封面',
      dataIndex: 'image',
      width: 90,
      render: (image, record) => <Image width={48} height={64} src={image} alt={record.title} fallback="/favicon.svg" />,
    },
    { title: '书名', dataIndex: 'title' },
    { title: '作者', dataIndex: 'author' },
    { title: 'ISBN', dataIndex: 'isbn' },
    { title: '出版社', dataIndex: 'publisher' },
    { title: '库存', dataIndex: 'stock', width: 90 },
    {
      title: '定价',
      dataIndex: 'price',
      width: 100,
      render: (price) => `¥${Number(price).toFixed(2)}`,
    },
    {
      title: '操作',
      width: 170,
      render: (_, record) => (
        <Space>
          <Button onClick={() => openEdit(record)}>编辑</Button>
          <Popconfirm title="确认删除这本书？" onConfirm={() => handleDelete(record.id)}>
            <Button danger>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <main className="main-content">
      <header>
        <h1>书籍管理</h1>
        <Button type="primary" onClick={openCreate}>添加图书</Button>
      </header>
      <div className="filter-bar">
        <Input.Search
          allowClear
          placeholder="按书名搜索"
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
          onSearch={(value) => {
            setKeyword(value);
            loadBooks(value);
          }}
        />
      </div>
      <Table
        rowKey="id"
        loading={loading}
        columns={columns}
        dataSource={books}
        pagination={{ pageSize: 6 }}
      />
      <Modal
        title={editingBook?.id ? '编辑图书' : '添加图书'}
        open={!!editingBook}
        onCancel={() => setEditingBook(null)}
        onOk={handleSave}
        confirmLoading={saving}
        width={720}
      >
        <Form form={form} layout="vertical">
          <div className="two-column-form">
            <Form.Item name="title" label="书名" rules={[{ required: true, message: '请输入书名' }]}>
              <Input />
            </Form.Item>
            <Form.Item name="author" label="作者" rules={[{ required: true, message: '请输入作者' }]}>
              <Input />
            </Form.Item>
            <Form.Item name="isbn" label="ISBN 编号">
              <Input />
            </Form.Item>
            <Form.Item name="publisher" label="出版社">
              <Input />
            </Form.Item>
            <Form.Item name="price" label="定价" rules={[{ required: true, message: '请输入定价' }]}>
              <InputNumber min={0} precision={2} className="full-width" />
            </Form.Item>
            <Form.Item name="stock" label="库存" rules={[{ required: true, message: '请输入库存' }]}>
              <InputNumber min={0} precision={0} className="full-width" />
            </Form.Item>
            <Form.Item name="category" label="分类">
              <Input />
            </Form.Item>
            <Form.Item name="image" label="封面地址">
              <Input placeholder="/santi.jpg 或图片 URL" />
            </Form.Item>
          </div>
          <Form.Item name="description" label="简介">
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </main>
  );
}

export default AdminBooks;
