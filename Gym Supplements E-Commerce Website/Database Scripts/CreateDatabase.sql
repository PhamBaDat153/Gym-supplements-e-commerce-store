
-- Drop Database user nếu tồn tại.
DROP USER if exists 'store_admin'@'%' ;

-- Tạo user mới với mọi đặc quyền
CREATE USER 'store_admin'@'%' IDENTIFIED BY 'admin12345';

GRANT ALL PRIVILEGES ON * . * TO 'store_admin'@'%';

-- =-- =========================================
-- TẠO DATABASE
-- =========================================
CREATE DATABASE IF NOT EXISTS `ecommerce_db`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `ecommerce_db`;

SET NAMES utf8mb4;

-- =========================================
-- TẮT RÀNG BUỘC KHÓA NGOẠI ĐỂ DROP TABLE
-- =========================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `discount_order`;
DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS `user_account_role`;
DROP TABLE IF EXISTS `product_review`;
DROP TABLE IF EXISTS `product_image`;
DROP TABLE IF EXISTS `product_category`;
DROP TABLE IF EXISTS `product_brand`;
DROP TABLE IF EXISTS `wishlist_item`;
DROP TABLE IF EXISTS `wishlist`;
DROP TABLE IF EXISTS `user_address`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `shipping_unit`;
DROP TABLE IF EXISTS `user_account`;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `discount`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `brand`;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================
-- BẢNG THƯƠNG HIỆU
-- =========================================
CREATE TABLE `brand` (
  `brand_id`   BINARY(16)    NOT NULL COMMENT 'Khóa chính của bảng thương hiệu (UUID v4 lưu dạng BINARY(16))',
  `brand_name` NVARCHAR(100)  NOT NULL COMMENT 'Tên thương hiệu (ví dụ: Nike, Adidas)',

  PRIMARY KEY (`brand_id`),
  UNIQUE KEY `uk_brand_name` (`brand_name`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin các thương hiệu sản phẩm';

-- =========================================
-- BẢNG DANH MỤC SẢN PHẨM
-- =========================================
CREATE TABLE `category` (
  `category_id`   BINARY(16)    NOT NULL COMMENT 'Khóa chính của bảng danh mục (UUID v4 lưu dạng BINARY(16))',
  `category_name` NVARCHAR(100)  NOT NULL COMMENT 'Tên danh mục sản phẩm',

  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uk_category_name` (`category_name`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin danh mục phân loại sản phẩm';

-- =========================================
-- BẢNG KHUYẾN MÃI / MÃ GIẢM GIÁ
-- =========================================
CREATE TABLE `discount` (
  `discount_id`   BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng khuyến mãi (UUID v4 lưu dạng BINARY(16))',
  `discount_code` VARCHAR(50)  NOT NULL COMMENT 'Mã giảm giá hiển thị cho người dùng',
  `discount_type` ENUM('PERCENT','FIXED_AMOUNT') NOT NULL COMMENT 'Loại giảm giá: PERCENT theo phần trăm, FIXED_AMOUNT theo số tiền cố định',
  `description`   NVARCHAR(255) NULL COMMENT 'Mô tả ngắn về chương trình khuyến mãi',
  `start_at`      DATETIME     NOT NULL COMMENT 'Thời điểm bắt đầu áp dụng khuyến mãi',
  `end_at`        DATETIME     NOT NULL COMMENT 'Thời điểm kết thúc khuyến mãi',
  `quantity`      INT UNSIGNED NULL COMMENT 'Số lượng mã được phát hành hoặc số lượt sử dụng (NULL nếu không giới hạn)',
  `is_available`  TINYINT(1)   NOT NULL DEFAULT 1 COMMENT 'Trạng thái còn hiệu lực (1: còn áp dụng, 0: tắt)',

  PRIMARY KEY (`discount_id`),
  UNIQUE KEY `uk_discount_code` (`discount_code`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin các chương trình khuyến mãi và mã giảm giá';

-- =========================================
-- BẢNG VAI TRÒ NGƯỜI DÙNG
-- =========================================
CREATE TABLE `role` (
  `role_id`   BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng vai trò (UUID v4 lưu dạng BINARY(16))',
  `role_name` VARCHAR(50)  NOT NULL COMMENT 'Tên vai trò (ví dụ: ROLE_ADMIN, ROLE_USER)',

  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_role_name` (`role_name`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu danh sách các vai trò của người dùng trong hệ thống';

-- =========================================
-- BẢNG TÀI KHOẢN NGƯỜI DÙNG
-- =========================================
CREATE TABLE `user_account` (
  `user_account_id` BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng tài khoản người dùng (UUID v4 lưu dạng BINARY(16))',
  `user_name`       VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị của người dùng',
  `hashed_password` VARCHAR(255) NOT NULL COMMENT 'Mật khẩu đã được mã hóa',
  `email`           VARCHAR(255) NOT NULL COMMENT 'Địa chỉ email dùng để đăng nhập',
  `phone_number`    VARCHAR(20)  NULL COMMENT 'Số điện thoại liên hệ',
  `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo tài khoản',
  `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật tài khoản gần nhất',
  `is_active`       TINYINT(1)   NOT NULL DEFAULT 1 COMMENT 'Trạng thái hoạt động của tài khoản (1: hoạt động, 0: khóa)',

  PRIMARY KEY (`user_account_id`),
  UNIQUE KEY `uk_user_email` (`email`),
  UNIQUE KEY `uk_user_phone` (`phone_number`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin đăng nhập và hồ sơ cơ bản của người dùng';

-- =========================================
-- BẢNG ĐƠN VỊ VẬN CHUYỂN
-- =========================================
CREATE TABLE `shipping_unit` (
  `shipping_unit_id`   BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng đơn vị vận chuyển (UUID v4 lưu dạng BINARY(16))',
  `shipping_unit_name` NVARCHAR(100) NOT NULL COMMENT 'Tên đơn vị vận chuyển',
  `hotline`            VARCHAR(20)  NULL COMMENT 'Số hotline hỗ trợ của đơn vị vận chuyển',

  PRIMARY KEY (`shipping_unit_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin các đơn vị vận chuyển hợp tác với hệ thống';

-- =========================================
-- BẢNG SẢN PHẨM
-- =========================================
CREATE TABLE `product` (
  `product_id`   BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng sản phẩm (UUID v4 lưu dạng BINARY(16))',
  `product_name` NVARCHAR(255) NOT NULL COMMENT 'Tên sản phẩm',
  `description`  TEXT         NULL COMMENT 'Mô tả chi tiết về sản phẩm',
  `quantity`     INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Số lượng tồn kho hiện tại',
  `price`        DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Giá niêm yết của sản phẩm',
  `is_available` TINYINT(1)   NOT NULL DEFAULT 1 COMMENT 'Trạng thái hiển thị sản phẩm (1: đang bán, 0: ẩn)',

  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin chi tiết về sản phẩm';

-- =========================================
-- BẢNG ĐỊA CHỈ GIAO HÀNG CỦA NGƯỜI DÙNG
-- =========================================
CREATE TABLE `user_address` (
  `user_address_id` BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng địa chỉ người dùng (UUID v4 lưu dạng BINARY(16))',
  `user_account_id` BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng user_account',
  `house_address`   NVARCHAR(255) NOT NULL COMMENT 'Số nhà, tên tòa, căn hộ',
  `street`          NVARCHAR(255) NOT NULL COMMENT 'Tên đường',
  `ward`            NVARCHAR(100) NULL COMMENT 'Phường hoặc xã',
  `district`        NVARCHAR(100) NULL COMMENT 'Quận hoặc huyện',
  `city`            NVARCHAR(100) NULL COMMENT 'Thành phố hoặc thị xã',
  `province`        NVARCHAR(100) NULL COMMENT 'Tỉnh hoặc thành phố trực thuộc trung ương',
  `receiver_name`   NVARCHAR(100) NOT NULL COMMENT 'Tên người nhận hàng',
  `receiver_phone`  VARCHAR(20)  NOT NULL COMMENT 'Số điện thoại người nhận hàng',
  `is_default`      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT 'Địa chỉ mặc định (1: mặc định, 0: không)',

  PRIMARY KEY (`user_address_id`),
  KEY `idx_user_address_user` (`user_account_id`),
  CONSTRAINT `fk_user_address_user`
    FOREIGN KEY (`user_account_id`)
    REFERENCES `user_account`(`user_account_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu danh sách địa chỉ giao hàng của từng người dùng';

-- =========================================
-- BẢNG DANH SÁCH YÊU THÍCH
-- =========================================
CREATE TABLE `wishlist` (
  `wishlist_id`     BINARY(16) NOT NULL COMMENT 'Khóa chính của bảng danh sách yêu thích (UUID v4 lưu dạng BINARY(16))',
  `user_account_id` BINARY(16) NOT NULL COMMENT 'Người sở hữu danh sách yêu thích',

  PRIMARY KEY (`wishlist_id`),
  KEY `idx_wishlist_user` (`user_account_id`),
  CONSTRAINT `fk_wishlist_user`
    FOREIGN KEY (`user_account_id`)
    REFERENCES `user_account`(`user_account_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Thông tin danh sách sản phẩm yêu thích của người dùng';

-- =========================================
-- BẢNG CHI TIẾT DANH SÁCH YÊU THÍCH
-- =========================================
CREATE TABLE `wishlist_item` (
  `wishlist_item_id` BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng chi tiết danh sách yêu thích (UUID v4 lưu dạng BINARY(16))',
  `wishlist_id`      BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng wishlist',
  `product_id`       BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng product',
  `quantity`         INT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Số lượng mong muốn cho sản phẩm trong wishlist',

  PRIMARY KEY (`wishlist_item_id`),
  KEY `idx_wishlist_item_wishlist` (`wishlist_id`),
  KEY `idx_wishlist_item_product` (`product_id`),
  CONSTRAINT `fk_wishlist_item_wishlist`
    FOREIGN KEY (`wishlist_id`)
    REFERENCES `wishlist`(`wishlist_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_wishlist_item_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `product`(`product_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Chi tiết các sản phẩm nằm trong danh sách yêu thích của người dùng';

-- =========================================
-- BẢNG QUAN HỆ SẢN PHẨM - THƯƠNG HIỆU (N-N)
-- =========================================
CREATE TABLE `product_brand` (
  `product_id` BINARY(16) NOT NULL COMMENT 'Khóa ngoại tới bảng product',
  `brand_id`   BINARY(16) NOT NULL COMMENT 'Khóa ngoại tới bảng brand',

  PRIMARY KEY (`product_id`, `brand_id`),
  KEY `idx_product_brand_brand` (`brand_id`),
  CONSTRAINT `fk_product_brand_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `product`(`product_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_product_brand_brand`
    FOREIGN KEY (`brand_id`)
    REFERENCES `brand`(`brand_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Bảng liên kết nhiều nhiều giữa sản phẩm và thương hiệu';

-- =========================================
-- BẢNG QUAN HỆ SẢN PHẨM - DANH MỤC (N-N)
-- =========================================
CREATE TABLE `product_category` (
  `product_id`  BINARY(16) NOT NULL COMMENT 'Khóa ngoại tới bảng product',
  `category_id` BINARY(16) NOT NULL COMMENT 'Khóa ngoại tới bảng category',

  PRIMARY KEY (`product_id`, `category_id`),
  KEY `idx_product_category_category` (`category_id`),
  CONSTRAINT `fk_product_category_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `product`(`product_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_product_category_category`
    FOREIGN KEY (`category_id`)
    REFERENCES `category`(`category_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Bảng liên kết nhiều nhiều giữa sản phẩm và danh mục';

-- =========================================
-- BẢNG HÌNH ẢNH SẢN PHẨM
-- =========================================
CREATE TABLE `product_image` (
  `product_image_id` BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng hình ảnh sản phẩm (UUID v4 lưu dạng BINARY(16))',
  `product_id`       BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng product',
  `image_url`        VARCHAR(500) NOT NULL COMMENT 'Đường dẫn ảnh sản phẩm',
  `is_default`       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT 'Ảnh đại diện chính của sản phẩm (1: đúng, 0: sai)',

  PRIMARY KEY (`product_image_id`),
  KEY `idx_product_image_product` (`product_id`),
  CONSTRAINT `fk_product_image_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `product`(`product_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu danh sách ảnh minh họa cho từng sản phẩm';

-- =========================================
-- BẢNG ĐÁNH GIÁ SẢN PHẨM
-- =========================================
CREATE TABLE `product_review` (
  `product_review_id` BINARY(16)  NOT NULL COMMENT 'Khóa chính của bảng đánh giá sản phẩm (UUID v4 lưu dạng BINARY(16))',
  `product_id`        BINARY(16)  NOT NULL COMMENT 'Khóa ngoại tới bảng product',
  `user_account_id`   BINARY(16)  NOT NULL COMMENT 'Khóa ngoại tới bảng user_account, người thực hiện đánh giá',
  `rating`            TINYINT UNSIGNED NOT NULL COMMENT 'Điểm đánh giá sản phẩm, thường trong khoảng 1 đến 5',
  `comment`           NVARCHAR(1000)         NULL COMMENT 'Nội dung nhận xét của người dùng',
  `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm người dùng gửi đánh giá',

  PRIMARY KEY (`product_review_id`),
  KEY `idx_product_review_product` (`product_id`),
  KEY `idx_product_review_user` (`user_account_id`),
  CONSTRAINT `fk_product_review_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `product`(`product_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_product_review_user`
    FOREIGN KEY (`user_account_id`)
    REFERENCES `user_account`(`user_account_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu đánh giá và nhận xét của người dùng cho từng sản phẩm';

-- =========================================
-- BẢNG PHÂN QUYỀN TÀI KHOẢN (N-N GIỮA USER VÀ ROLE)
-- =========================================
CREATE TABLE `user_account_role` (
  `user_account_id` BINARY(16) NOT NULL COMMENT 'Khóa ngoại tới bảng user_account (UUID v4 lưu dạng BINARY(16))',
  `role_id`         BINARY(16) NOT NULL COMMENT 'Khóa ngoại tới bảng role (UUID v4 lưu dạng BINARY(16))',

  PRIMARY KEY (`user_account_id`, `role_id`),
  KEY `idx_user_account_role_role` (`role_id`),
  CONSTRAINT `fk_user_account_role_user`
    FOREIGN KEY (`user_account_id`)
    REFERENCES `user_account`(`user_account_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_user_account_role_role`
    FOREIGN KEY (`role_id`)
    REFERENCES `role`(`role_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Bảng liên kết phân quyền giữa tài khoản người dùng và vai trò';

-- =========================================
-- BẢNG ĐƠN HÀNG
-- =========================================
CREATE TABLE `order` (
  `order_id`         BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng đơn hàng (UUID v4 lưu dạng BINARY(16))',
  `user_account_id`  BINARY(16)   NOT NULL COMMENT 'Khách hàng đặt đơn hàng',
  `user_address_id`  BINARY(16)   NOT NULL COMMENT 'Địa chỉ giao hàng được chọn',
  `shipping_unit_id` BINARY(16)   NOT NULL COMMENT 'Đơn vị vận chuyển xử lý đơn',
  `original_price`   DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng giá trị hàng hóa trước khi giảm giá',
  `discount_amount`  DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng số tiền giảm giá áp dụng cho đơn hàng',
  `final_price`      DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Số tiền khách phải thanh toán sau giảm giá',
  `order_status`     ENUM('PENDING','CONFIRMED','SHIPPING','COMPLETED','CANCELLED','RETURNED') NOT NULL DEFAULT 'PENDING'
                     COMMENT 'Trạng thái hiện tại của đơn hàng',
  `payment_method`   ENUM('COD','BANK_TRANSFER','CREDIT_CARD','EWALLET','OTHER') NOT NULL DEFAULT 'COD'
                     COMMENT 'Phương thức thanh toán của đơn hàng',
  `created_at`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo đơn hàng',

  PRIMARY KEY (`order_id`),
  KEY `idx_order_user` (`user_account_id`),
  KEY `idx_order_address` (`user_address_id`),
  KEY `idx_order_shipping_unit` (`shipping_unit_id`),
  CONSTRAINT `fk_order_user`
    FOREIGN KEY (`user_account_id`)
    REFERENCES `user_account`(`user_account_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_order_address`
    FOREIGN KEY (`user_address_id`)
    REFERENCES `user_address`(`user_address_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_order_shipping_unit`
    FOREIGN KEY (`shipping_unit_id`)
    REFERENCES `shipping_unit`(`shipping_unit_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin các đơn hàng được tạo trên hệ thống';

-- =========================================
-- BẢNG CHI TIẾT ĐƠN HÀNG
-- =========================================
CREATE TABLE `order_item` (
  `order_item_id` BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng chi tiết đơn hàng (UUID v4 lưu dạng BINARY(16))',
  `product_id`    BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng product',
  `order_id`      BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng order',
  `quantity`      INT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Số lượng sản phẩm trong dòng hàng',
  `unit_price`    DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Đơn giá tại thời điểm đặt hàng',
  `line_total`    DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Thành tiền của dòng hàng (sau khi áp dụng giảm giá nếu có)',

  PRIMARY KEY (`order_item_id`),
  KEY `idx_order_item_product` (`product_id`),
  KEY `idx_order_item_order` (`order_id`),
  CONSTRAINT `fk_order_item_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `product`(`product_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_order_item_order`
    FOREIGN KEY (`order_id`)
    REFERENCES `order`(`order_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Chi tiết từng sản phẩm nằm trong mỗi đơn hàng';

-- =========================================
-- BẢNG QUAN HỆ ĐƠN HÀNG - KHUYẾN MÃI (N-N)
-- =========================================
CREATE TABLE `discount_order` (
  `discount_id` BINARY(16) NOT NULL COMMENT 'Khóa ngoại tới bảng discount (UUID v4 lưu dạng BINARY(16))',
  `order_id`    BINARY(16) NOT NULL COMMENT 'Khóa ngoại tới bảng order (UUID v4 lưu dạng BINARY(16))',

  PRIMARY KEY (`discount_id`, `order_id`),
  KEY `idx_discount_order_order` (`order_id`),
  CONSTRAINT `fk_discount_order_discount`
    FOREIGN KEY (`discount_id`)
    REFERENCES `discount`(`discount_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_discount_order_order`
    FOREIGN KEY (`order_id`)
    REFERENCES `order`(`order_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Bảng liên kết nhiều nhiều giữa đơn hàng và chương trình khuyến mãi';
