-- ============================================================
-- 粤尝粤鲜 鲜果商城 数据库初始化脚本
-- 数据库: MySQL 8.0
-- 执行方式: mysql -uroot -p123456 < ycyx_shop.sql
-- 说明: 账号(admin/123456, 13800138000/123456)由后端首次启动时自动初始化
--       (密码使用 BCrypt 加密，无法在 SQL 中静态写死)
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ycyx_shop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ycyx_shop`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. 前台会员表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `phone`       VARCHAR(20)  NOT NULL COMMENT '手机号',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname`    VARCHAR(50)  DEFAULT '' COMMENT '昵称',
    `avatar`      VARCHAR(255) DEFAULT '' COMMENT '头像',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='前台会员表';

-- ------------------------------------------------------------
-- 2. 后台管理员表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '登录账号',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname`    VARCHAR(50)  DEFAULT '' COMMENT '昵称',
    `avatar`      VARCHAR(255) DEFAULT '' COMMENT '头像',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台管理员表';

-- ------------------------------------------------------------
-- 3. 角色表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    `role_key`    VARCHAR(50)  NOT NULL COMMENT '角色标识',
    `description` VARCHAR(200) DEFAULT '' COMMENT '描述',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ------------------------------------------------------------
-- 4. 菜单权限表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id`   BIGINT       DEFAULT 0 COMMENT '父菜单ID, 0为根',
    `menu_name`   VARCHAR(50)  NOT NULL COMMENT '菜单名称',
    `menu_type`   CHAR(1)      DEFAULT 'C' COMMENT '类型: M目录 C菜单 F按钮',
    `path`        VARCHAR(200) DEFAULT '' COMMENT '路由路径',
    `component`   VARCHAR(200) DEFAULT '' COMMENT '前端组件路径',
    `perms`       VARCHAR(100) DEFAULT '' COMMENT '权限标识',
    `icon`        VARCHAR(100) DEFAULT '' COMMENT '图标',
    `order_num`   INT          DEFAULT 0 COMMENT '排序号',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ------------------------------------------------------------
-- 5. 管理员-角色关联表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id`      BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '管理员ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色关联表';

-- ------------------------------------------------------------
-- 6. 角色-菜单关联表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu` (
    `id`      BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ------------------------------------------------------------
-- 7. 收货地址表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id`        BIGINT       NOT NULL COMMENT '用户ID',
    `receiver_name`  VARCHAR(50)  NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20)  NOT NULL COMMENT '收货人手机号',
    `province`       VARCHAR(50)  NOT NULL COMMENT '省',
    `city`           VARCHAR(50)  NOT NULL COMMENT '市',
    `district`       VARCHAR(50)  NOT NULL COMMENT '区',
    `detail_address` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `is_default`     TINYINT      DEFAULT 0 COMMENT '是否默认: 1是 0否',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT      DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ------------------------------------------------------------
-- 8. 商品分类表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`        VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon`        VARCHAR(50) DEFAULT '' COMMENT '图标/emoji',
    `sort`        INT         DEFAULT 0 COMMENT '排序号',
    `status`      TINYINT     DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT     DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ------------------------------------------------------------
-- 9. 商品表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `category_id`    BIGINT        NOT NULL COMMENT '分类ID',
    `name`           VARCHAR(100)  NOT NULL COMMENT '商品名称',
    `origin_place`   VARCHAR(20)   NOT NULL COMMENT '产地省份: 广东/海南',
    `origin_detail`  VARCHAR(100)  DEFAULT '' COMMENT '产地详细: 广东·湛江',
    `spec`           VARCHAR(50)   DEFAULT '' COMMENT '规格: 如5斤装',
    `price`          DECIMAL(10,2) NOT NULL COMMENT '单价',
    `stock`          INT           DEFAULT 0 COMMENT '库存',
    `sales`          INT           DEFAULT 0 COMMENT '销量',
    `main_image`     VARCHAR(255)  DEFAULT '' COMMENT '商品主图URL',
    `description`    VARCHAR(500)  DEFAULT '' COMMENT '产地/口感描述',
    `is_seasonal`    TINYINT       DEFAULT 0 COMMENT '是否时令: 1是 0否',
    `is_hot`         TINYINT       DEFAULT 0 COMMENT '是否热点(需缓存): 1是 0否',
    `status`         TINYINT       DEFAULT 1 COMMENT '上架状态: 1上架 0下架',
    `create_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        TINYINT       DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ------------------------------------------------------------
-- 10. 订单表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`         VARCHAR(32)   NOT NULL COMMENT '订单号',
    `user_id`          BIGINT        NOT NULL COMMENT '用户ID',
    `total_amount`     DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `status`           TINYINT       DEFAULT 0 COMMENT '状态: 0待支付 1已支付 2已发货 3已取消',
    `receiver_name`    VARCHAR(50)   DEFAULT '' COMMENT '收货人姓名快照',
    `receiver_phone`   VARCHAR(20)   DEFAULT '' COMMENT '收货人手机号快照',
    `receiver_address` VARCHAR(300)  DEFAULT '' COMMENT '收货地址快照',
    `pay_time`         DATETIME      DEFAULT NULL COMMENT '支付时间',
    `ship_time`        DATETIME      DEFAULT NULL COMMENT '发货时间',
    `cancel_time`      DATETIME      DEFAULT NULL COMMENT '取消时间',
    `create_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          TINYINT       DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ------------------------------------------------------------
-- 11. 订单明细表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id`      BIGINT        NOT NULL COMMENT '订单ID',
    `product_id`    BIGINT        NOT NULL COMMENT '商品ID',
    `product_name`  VARCHAR(100)  DEFAULT '' COMMENT '商品名称快照',
    `product_image` VARCHAR(255)  DEFAULT '' COMMENT '商品图片快照',
    `spec`          VARCHAR(50)   DEFAULT '' COMMENT '规格快照',
    `price`         DECIMAL(10,2) NOT NULL COMMENT '下单单价',
    `quantity`      INT           NOT NULL COMMENT '数量',
    `total_price`   DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ------------------------------------------------------------
-- 12. 操作日志表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `operate_log`;
CREATE TABLE `operate_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `operator`    VARCHAR(50)  DEFAULT '' COMMENT '操作人',
    `operation`   VARCHAR(100) DEFAULT '' COMMENT '操作描述',
    `method`      VARCHAR(200) DEFAULT '' COMMENT '方法',
    `params`      TEXT         COMMENT '请求参数',
    `ip`          VARCHAR(50)  DEFAULT '' COMMENT '请求IP',
    `cost_time`   BIGINT       DEFAULT 0 COMMENT '耗时(ms)',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ------------------------------------------------------------
-- 种子数据：角色
-- ------------------------------------------------------------
INSERT INTO `role` (`id`, `role_name`, `role_key`, `description`) VALUES
(1, '超级管理员', 'super_admin', '拥有全部权限'),
(2, '普通管理员', 'admin', '不具备系统管理权限');

-- ------------------------------------------------------------
-- 种子数据：菜单（目录 / 菜单 / 按钮权限）
-- ------------------------------------------------------------
INSERT INTO `menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `order_num`) VALUES
(1,   0,   '商品管理', 'M', '/product', '',        '',            'Goods',   1),
(101, 1,   '商品列表', 'C', 'product',  'product/list', 'product:list', '', 1),
(102, 1,   '分类管理', 'C', 'category', 'category/list', 'category:list', '', 2),
(2,   0,   '订单管理', 'M', '/order',   '',        '',            'List',    2),
(201, 2,   '订单列表', 'C', 'order',    'order/list', 'order:list', '', 1),
(3,   0,   '用户管理', 'M', '/user',    '',        '',            'User',    3),
(301, 3,   '用户列表', 'C', 'user',     'user/list', 'user:list', '', 1),
(4,   0,   '系统管理', 'M', '/system',  '',        '',            'Setting', 4),
(401, 4,   '权限配置', 'C', 'perm',     'system/perm', 'system:perm', '', 1),
(1001, 101, '商品新增', 'F', '', '', 'product:add',   '', 1),
(1002, 101, '商品编辑', 'F', '', '', 'product:edit',  '', 2),
(1003, 101, '商品删除', 'F', '', '', 'product:delete','', 3),
(1004, 101, '商品上下架', 'F', '', '', 'product:onoff', '', 4),
(1005, 102, '分类新增', 'F', '', '', 'category:add',  '', 1),
(1006, 102, '分类编辑', 'F', '', '', 'category:edit', '', 2),
(1007, 102, '分类删除', 'F', '', '', 'category:delete', '', 3),
(1008, 201, '订单发货', 'F', '', '', 'order:ship',    '', 1),
(1009, 301, '用户状态', 'F', '', '', 'user:status',   '', 1);

-- ------------------------------------------------------------
-- 种子数据：角色-菜单关联
--   超级管理员(1)拥有全部菜单
-- ------------------------------------------------------------
INSERT INTO `role_menu` (`role_id`, `menu_id`) SELECT 1, `id` FROM `menu`;

-- 普通管理员(2): 排除系统管理目录(4)及其子菜单(401)
INSERT INTO `role_menu` (`role_id`, `menu_id`) SELECT 2, `id` FROM `menu` WHERE `id` NOT IN (4, 401);

-- ------------------------------------------------------------
-- 种子数据：商品分类
-- ------------------------------------------------------------
INSERT INTO `category` (`id`, `name`, `icon`, `sort`) VALUES
(1, '凤梨菠萝', '🍍', 1),
(2, '荔枝龙眼', '🍒', 2),
(3, '芒果类',   '🥭', 3),
(4, '柑橘类',   '🍊', 4),
(5, '浆果类',   '🫐', 5),
(6, '热带杂果', '🥥', 6);

-- ------------------------------------------------------------
-- 种子数据：商品
-- ------------------------------------------------------------
INSERT INTO `product` (`category_id`, `name`, `origin_place`, `origin_detail`, `spec`, `price`, `stock`, `sales`, `description`, `is_seasonal`, `is_hot`, `status`) VALUES
(1, '湛江金钻凤梨', '广东', '广东·湛江', '5斤装', 39.90, 500, 1280, '广东湛江金钻凤梨，不用泡盐水，削皮即食，果肉细腻无涩味。', 1, 1, 1),
(1, '海南三亚金钻凤梨', '海南', '海南·三亚', '9斤装', 69.90, 320, 866, '海南三亚金钻凤梨，日照充足，果肉甜度高，削皮即食。', 1, 1, 1),
(2, '妃子笑荔枝', '海南', '海南·三亚', '5斤装', 59.90, 200, 2033, '海南三亚妃子笑荔枝，果大核小，果肉爽脆清甜，江南一骑红尘妃子笑。', 1, 1, 1),
(2, '增城桂味荔枝', '广东', '广东·广州', '3斤装', 79.90, 150, 512, '广东增城桂味荔枝，桂花清香，核小肉厚，荔枝中的爱马仕。', 1, 0, 1),
(3, '海南贵妃芒果', '海南', '海南·三亚', '5斤装', 49.90, 400, 921, '海南贵妃芒，果肉嫩滑，香气浓郁，甜过初恋。', 1, 0, 1),
(3, '湛江台农芒果', '广东', '广东·湛江', '9斤装', 79.90, 260, 301, '广东湛江台农芒果，个头饱满，果肉金黄，香甜多汁。', 0, 0, 1),
(4, '廉江红橙', '广东', '广东·湛江', '10斤装', 59.90, 350, 1588, '广东廉江红橙，果肉橙红，酸甜适中，汁多化渣。', 1, 0, 1),
(4, '琼中绿橙', '海南', '海南·琼中', '5斤装', 39.90, 280, 422, '海南琼中绿橙，皮薄汁多，甜中带香，海南特产。', 0, 0, 1),
(5, '从化蓝莓', '广东', '广东·广州', '2盒装', 69.90, 180, 766, '广东从化蓝莓，果粉厚实，花青素满满，新鲜采摘。', 1, 0, 1),
(6, '文昌椰子', '海南', '海南·文昌', '6个装', 55.90, 300, 655, '海南文昌椰子，椰汁清甜，椰肉软嫩，树上熟才发。', 0, 0, 1);

SET FOREIGN_KEY_CHECKS = 1;