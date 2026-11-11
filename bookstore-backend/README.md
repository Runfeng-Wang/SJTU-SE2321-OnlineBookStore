# Bookstore Backend

Spring Boot + MySQL + JPA，提供书城项目的用户、图书、购物车、订单和统计接口。

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.x

## 数据库准备

1. 启动 MySQL，创建数据库（也可直接执行 `schema.sql`）：

```sql
CREATE DATABASE IF NOT EXISTS bookstore DEFAULT CHARACTER SET utf8mb4;
```

2. **配置 MySQL 密码**（必做，否则会 `Access denied`）：

```bash
cd src/main/resources
  cp application-local.properties.example application-local.properties
```

编辑 `application-local.properties`：

- **无密码**（终端里 `mysql -u root` 不用 `-p` 就能进）：保持  
  `spring.datasource.password=`  
  （等号后面什么都不写）
- **有密码**：写成  
  `spring.datasource.password=你的密码`

若用户名不是 `root`，同时修改 `application.properties` 里的 `spring.datasource.username`。

3. 首次启动时 JPA 会自动建表；`DataInitializer` 会插入默认管理员，并在图书表为空时插入 3 本样例书。

默认管理员账号：

```text
用户名：admin
密码：admin123
```

也可手动执行：

```bash
mysql -u root -p < src/main/resources/schema.sql
```

## 启动

```bash
cd bookstore-backend
MAVEN_OPTS=-Dfile.encoding=UTF-8 mvn spring-boot:run
```

项目路径含中文时建议保留 `MAVEN_OPTS=-Dfile.encoding=UTF-8`，避免 Maven 增量编译状态文件出现编码问题。

若从 Maven Central 下载失败（`Connection reset`），项目已配置阿里云镜像（`.mvn/settings.xml`），在 `bookstore-backend` 目录下直接执行上述命令即可。

也可手动指定镜像：

```bash
mvn -s .mvn/settings.xml spring-boot:run
```

服务默认运行在 `http://localhost:8080`。

## 常见启动失败

| 日志关键词 | 原因 | 处理 |
|-----------|------|------|
| `Access denied for user 'root'` | MySQL 密码不对 | 按上文配置 `application-local.properties` |
| `Communications link failure` | MySQL 未启动 | 先启动 MySQL 服务 |
| `Connection reset`（Maven 阶段） | 无法访问 Maven Central | 在 `bookstore-backend` 目录执行 `mvn`（已配阿里云镜像） |

## 主要 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/users/register` | 用户注册，校验用户名、重复密码、邮箱 |
| POST | `/api/v1/users/login` | 用户登录，禁用用户返回“您的账号已经被禁用” |
| GET | `/api/v1/users` | 管理员查看用户列表 |
| PATCH | `/api/v1/users/{id}/status` | 管理员禁用/解禁用户 |
| GET | `/api/v1/books?title=关键词` | 获取/搜索书籍 |
| GET | `/api/v1/book/{id}` | 获取单本书详情，如 `/api/v1/book/1` |
| POST | `/api/v1/admin/books` | 管理员添加图书 |
| PUT | `/api/v1/admin/books/{id}` | 管理员修改图书 |
| DELETE | `/api/v1/admin/books/{id}` | 管理员删除图书 |
| GET | `/api/v1/cart` | 查看购物车 |
| POST | `/api/v1/cart` | 加入购物车 |
| POST | `/api/v1/orders` | 下单，扣减库存并清空所选购物车项 |
| GET | `/api/v1/orders` | 顾客查看自己的订单，支持时间范围和书名筛选 |
| GET | `/api/v1/orders/admin` | 管理员查看全部订单，支持时间范围和书名筛选 |
| GET | `/api/v1/orders/stats/books` | 管理员查看热销榜 |
| GET | `/api/v1/orders/stats/users` | 管理员查看消费榜 |
| GET | `/api/v1/orders/stats/me` | 顾客查看自己的购书统计 |

## 工程结构

```
src/main/java/com/sjtu/bookstore/
├── BookstoreApplication.java
├── config/          Cors、初始数据
├── controller/    REST 接口
├── dto/             请求/响应对象
├── entity/          User、Book、CartItem、CustomerOrder、OrderItem 实体
├── repository/      JpaRepository
└── service/         业务逻辑
```
