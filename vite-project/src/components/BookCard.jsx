import React from 'react';
import { Link } from 'react-router-dom';

function BookCard({ book }) {
  return (
    <article className="book-card">
      <figure>
        <Link to={`/detail/${book.id}`}>
          <img src={book.image} alt={book.title} />
        </Link>
        <figcaption>
          <h3>{book.title}</h3>
          <p>作者：{book.author}</p>
          <p className="book-meta">ISBN：{book.isbn || '暂无'}</p>
          <p className="book-meta">库存：{book.stock ?? 0}</p>
          <p className="price">¥{book.price.toFixed(2)}</p>
        </figcaption>
      </figure>
    </article>
  );
}

export default BookCard;
