package project.gymecommerce.Models.Order;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.User.User_account;

import java.util.UUID;

/*
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
 */

@Entity
@Table(name = "wishlist")
public class Wishlist {

    //các thuộc tính của model: Wishlist
    @Id
    @Column(name = "wishlist_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID wishlist_id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, optional = false, orphanRemoval = true)
    @JoinColumn(name = "user_account_id", nullable = false)
    private User_account user_account;


    //Constructor
    public Wishlist() {
    }

    public Wishlist(User_account user_account) {
        this.user_account = user_account;
    }

    //Getters & Setters
    public User_account getUser_account() {
        return user_account;
    }

    public void setUser_account(User_account user_account) {
        this.user_account = user_account;
    }

    public UUID getWishlist_id() {
        return wishlist_id;
    }

    public void setWishlist_id(UUID wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    //toString
    @Override
    public String toString() {
        return "Wishlist{" +
                "wishlist_id=" + wishlist_id +
                ", user_account=" + user_account +
                '}';
    }
}