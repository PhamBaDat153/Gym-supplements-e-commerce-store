package project.gymecommerce.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/*
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
 */

@Entity
@Table(name = "product_image")
public class Product_image {

    // Các thuộc tính của model: Product_image
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_image_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID product_image_id;

    @Column(name = "image_url", nullable = false, length = 500)
    private String image_url;

    @Column(name = "is_default", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean is_default = false;

    public Boolean getIs_default() {
        return is_default;
    }

    public void setIs_default(Boolean is_default) {
        this.is_default = is_default;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public UUID getProduct_image_id() {
        return product_image_id;
    }

    public void setProduct_image_id(UUID product_image_id) {
        this.product_image_id = product_image_id;
    }
}