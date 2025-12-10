package project.gymecommerce.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/*
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

 */

@Entity
@Table(name = "order_item")
public class Order_item {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}