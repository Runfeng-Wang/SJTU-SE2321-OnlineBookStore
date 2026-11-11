import React, { useEffect, useState } from 'react';
import { Spin, Alert, Input } from 'antd';
import BookCard from '../components/BookCard';
import { fetchBooks, normalizeBook } from '../api/bookstoreApi';

function Home() {
  const [books, setBooks] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const data = await fetchBooks({ title: keyword });
        if (!cancelled) {
          setBooks(data.map(normalizeBook));
        }
      } catch (err) {
        if (!cancelled) {
          setError(err.message || '加载书籍失败');
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
  }, [keyword]);

  if (loading) {
    return (
      <main className="main-content">
        <div className="page-loading"><Spin size="large" tip="加载书籍中..." /></div>
      </main>
    );
  }

  if (error) {
    return (
      <main className="main-content">
        <Alert type="error" message={error} showIcon />
      </main>
    );
  }

  return (
    <main className="main-content">
      <header>
        <h1>书籍列表</h1>
        <Input.Search
          className="page-search"
          placeholder="按书名搜索"
          allowClear
          onSearch={setKeyword}
        />
      </header>
      <section className="book-grid">
        {books.map((book) => (
          <BookCard key={book.id} book={book} />
        ))}
      </section>
    </main>
  );
}

export default Home;
