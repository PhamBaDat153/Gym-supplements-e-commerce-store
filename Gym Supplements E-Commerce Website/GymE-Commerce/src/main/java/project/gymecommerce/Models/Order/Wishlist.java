package project.gymecommerce.Models.Order;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.User.UserAccount;

import java.util.Objects;
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

    /**
     * Khóa chính wishlist_id, lưu dưới dạng UUID (Binary(16) trong DB).
     * - Ý nghĩa: định danh duy nhất cho danh sách yêu thích của một user.
     * - Lưu ý: Hibernate sinh UUID tự động khi persist nếu dùng GenerationType.UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wishlist_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID wishlistId;

    /**
     * Chủ sở hữu của wishlist (UserAccount).
     * - Ý nghĩa: liên kết một-một giữa UserAccount và Wishlist; mỗi user có tối đa một wishlist.
     * - Lưu ý: @JoinColumn đặt unique = true để đảm bảo ràng buộc 1-1 ở tầng DB; không cascade REMOVE
     *   để tránh vô tình xóa user khi xóa wishlist.
     */
    @OneToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            optional = false)
    @JoinColumn(name = "user_account_id", nullable = false, unique = true)
    private UserAccount userAccount;

    // --- Constructors ---
    public Wishlist() {
    }

    public Wishlist(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    // --- Getters & Setters ---
    public UUID getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(UUID wishlistId) {
        this.wishlistId = wishlistId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    /**
     * Setter userAccount.
     * - Lưu ý: nếu hệ thống có quan hệ hai chiều (UserAccount chứa trường wishlist),
     *   cần đồng bộ cả hai phía tại service hoặc cập nhật cả hai bên khi set ở đây để tránh inconsistency.
     */
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    // --- equals & hashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wishlist)) return false;
        Wishlist wishlist = (Wishlist) o;
        return wishlistId != null && Objects.equals(wishlistId, wishlist.wishlistId);
    }

    @Override
    public int hashCode() {
        return wishlistId != null ? Objects.hash(wishlistId) : System.identityHashCode(this);
    }

    // --- toString (an toàn, không in toàn bộ userAccount) ---
    @Override
    public String toString() {
        return "Wishlist{" +
                "wishlistId=" + wishlistId +
                ", userAccountId=" + (userAccount == null ? null : userAccount.getUserAccountId()) +
                '}';
    }
}
