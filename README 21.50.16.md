# 523031910189 在线书店（迭代二）

## 目录

| 目录 | 说明 |
|------|------|
| `vite-project/` | React 前端（Fetch API + Ant Design） |
| `bookstore-backend/` | Spring Boot 后端（JPA + MySQL） |

## 启动

1. MySQL 启动，配置 `bookstore-backend/src/main/resources/application-local.properties`
2. 后端：`cd bookstore-backend && mvn spring-boot:run`
3. 前端：`cd vite-project && npm install && npm run dev`

## API 一览

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/users/register` | 注册 |
| POST | `/api/v1/users/login` | 登录（校验数据库） |
| GET | `/api/v1/books` | 书籍列表 |
| GET | `/api/v1/book/{id}` | 书籍详情 |
| GET | `/api/v1/cart` | 购物车（Header: `X-User-Id`） |
| POST | `/api/v1/cart` | 加入购物车 |
| DELETE | `/api/v1/cart/{id}` | 删除购物车项 |
| GET | `/api/v1/orders` | 订单列表 |
| POST | `/api/v1/orders` | 下单结算 |

## 数据库脚本

提交作业请包含：`bookstore-backend/src/main/resources/schema.sql`
