# 粤尝粤鲜 · 鲜果商城

一个前后端分离的生鲜电商项目，跑通「登录 → 浏览 → 加购 → 下单 → 订单管理」全链路。

## 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Spring Boot 2.7.18 · JDK 8 · MyBatis-Plus 3.5 · Spring Security 5.8（JWT + RBAC） · Redis · MySQL 8 |
| 前台 | Vue 3（`<script setup>`） · Vite 5 · Element Plus · Pinia · Vue Router 4 |
| 后台 | Vue 3 · Vite 5 · Element Plus · Pinia · Vue Router 4 |
| 文档 | Knife4j（接口文档） |

## 目录结构

```
dianshang/
├── backend/        # Spring Boot 后端（端口 8080，context-path /api）
├── frontend/       # 前台商城（端口 5173）
├── admin/          # 后台管理（端口 5174）
├── sql/            # 数据库初始化脚本 ycyx_shop.sql
├── start-*.bat     # 一键启动脚本
└── README.md
```

## 环境要求

- JDK 8
- Maven 3.6+
- MySQL 8（默认账号 `root / 123456`）
- Redis（Windows 便携版，默认位置 `D:\soft\Redis`）
- Node.js 18+（本项目使用 24）

## 快速启动

### 1. 初始化数据库

```bash
mysql -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS ycyx_shop DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p123456 ycyx_shop < sql/ycyx_shop.sql
```

### 2. 启动依赖与各端

按顺序双击运行，或直接运行 `start-all.bat`：

| 脚本 | 说明 |
| --- | --- |
| `start-redis.bat` | 启动 Redis（默认 `D:\soft\Redis\redis-server.exe`，路径不对请自行修改脚本） |
| `start-backend.bat` | 启动后端，首次会 Maven 下载依赖 |
| `start-frontend.bat` | 启动前台商城 |
| `start-admin.bat` | 启动后台管理 |

### 3. 访问地址

| 项目 | 地址 |
| --- | --- |
| 前台商城 | http://localhost:5173 |
| 后台管理 | http://localhost:5174 |
| 接口文档（Knife4j） | http://localhost:8080/api/doc.html |

### 4. 默认账号

| 端 | 账号 | 密码 | 说明 |
| --- | --- | --- | --- |
| 前台 | `13800138000` | `123456` | 会员用户 |
| 后台 | `admin` | `123456` | 超级管理员（super_admin） |

## 核心接口

- 前台认证：`POST /api/member/auth/login`
- 商品列表：`GET /api/product/list`
- 购物车：`GET/POST /api/member/cart`（Redis 存储）
- 收货地址：`GET/POST /api/member/address`
- 创建订单：`POST /api/member/order`（幂等 + 原子扣库存）
- 模拟支付：`PUT /api/member/order/{id}/pay`
- 后台登录：`POST /api/admin/auth/login`
- 后台发货：`PUT /api/admin/order/{id}/ship`（RBAC `order:ship` 权限）

## 联调验证

已通过端到端验证：商品浏览 → 会员登录 → 加入购物车 → 查询购物车 → 新增地址 → 创建订单（状态 `0 待支付`）→ 模拟支付（状态 `1 已支付`）→ 管理员登录 → 后台订单列表 → 发货（状态 `2 已发货`）。状态流转 0 → 1 → 2 正常，RBAC 权限校验正常。