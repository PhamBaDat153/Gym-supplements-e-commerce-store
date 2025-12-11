package project.gymecommerce.Models.Product;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.User.User_account;

import java.time.LocalDateTime;
import java.util.UUID;

/*
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
 */

@Entity
@Table(name = "product_review")
public class Product_review {

    // Các thuộc tính của model: Product_review
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_review_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID product_review_id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, optional = false)
    @JoinColumn(name = "user_account_id", nullable = false)
    private User_account user_account;

    @Column(name = "rating", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Short rating;

    @Column(name = "comment", length = 1000)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    // Constructor
    public Product_review() { }

    public Product_review(Product product, User_account user_account, Short rating, String comment) {
        this.product = product;
        this.user_account = user_account;
        this.rating = rating;
        this.comment = comment;
        this.created_at = LocalDateTime.now();
    }

    //Getter & Setter
    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public User_account getUser_account() {
        return user_account;
    }

    public void setUser_account(User_account user_account) {
        this.user_account = user_account;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public UUID getProduct_review_id() {
        return product_review_id;
    }

    public void setProduct_review_id(UUID product_review_id) {
        this.product_review_id = product_review_id;
    }

    //toString
    @Override
    public String toString() {
        return "Product_review{" +
                "product_review_id=" + product_review_id +
                ", product=" + product +
                ", user_account=" + user_account +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}