package project.gymecommerce.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/*
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
 */

@Entity
@Table(name = "wishlist_item")
public class Wishlist_item {

    //các thuộc tính của model: Wishlist_item
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wishlist_item_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID wishlist_item_id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, optional = false)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, optional = false, orphanRemoval = true)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public UUID getWishlist_item_id() {
        return wishlist_item_id;
    }

    public void setWishlist_item_id(UUID wishlist_item_id) {
        this.wishlist_item_id = wishlist_item_id;
    }
}