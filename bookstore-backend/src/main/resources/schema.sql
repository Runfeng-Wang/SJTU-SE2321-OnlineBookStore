CREATE DATABASE IF NOT EXISTS bookstore DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookstore;

DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(128) DEFAULT NULL,
    role VARCHAR(16) DEFAULT 'CUSTOMER',
    enabled TINYINT(1) DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE books (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(128) NOT NULL,
    author VARCHAR(128) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(64) DEFAULT NULL,
    image VARCHAR(255) DEFAULT NULL,
    isbn VARCHAR(32) DEFAULT NULL,
    stock INT NOT NULL DEFAULT 0,
    publisher VARCHAR(128) DEFAULT NULL,
    description TEXT,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cart_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_book (user_id, book_id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_book FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PAID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    image VARCHAR(255) DEFAULT NULL,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO users (username, password, email, role, enabled) VALUES
('admin', 'admin123', 'admin@bookstore.local', 'ADMIN', 1);

INSERT INTO books (title, author, price, category, image, isbn, stock, publisher, description) VALUES
('Java核心技术', '凯·S.霍斯特曼', 95.20, '编程', '/9668da63666960defb0bf63339ca1116.jpg',
 '9787111547426', 40, '机械工业出版社',
 '本书是Java领域的经典之作，深入探讨了Java的高级特性，包括流、XML处理、网络、数据库编程等内容。适合有经验的开发者深入学习 Java SE。'),
('三体', '刘慈欣', 45.00, '科幻', '/santi.jpg',
 '9787536692930', 60, '重庆出版社',
 '《三体》是刘慈欣创作的系列长篇科幻小说。作品讲述了地球人类文明和三体文明的信息交流、生死搏杀及两个文明在宇宙中的兴衰历程。'),
('活着', '余华', 28.00, '文学', '/huozhe.jpg',
 '9787506365437', 80, '作家出版社',
 '《活着》讲述了主人公福贵在时代变迁与苦难命运中，顽强生存、直面人生的故事，展现了生命的韧性与平凡生活的力量。');
