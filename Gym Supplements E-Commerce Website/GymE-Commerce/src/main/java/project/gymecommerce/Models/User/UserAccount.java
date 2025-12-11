package project.gymecommerce.Models.User;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.Order.Order;
import project.gymecommerce.Models.Product.ProductReview;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/*
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
 */
@Entity
@Table(
        name = "user_account",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = {"email"}),
                @UniqueConstraint(name = "uk_user_phone", columnNames = {"phone_number"})
        }
)
public class UserAccount {

    /**
     * Khóa chính user_account_id lưu dạng BINARY(16) (UUID v4).
     * Hibernate sẽ tự sinh UUID khi insert (GenerationType.UUID).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_account_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID userAccountId;

    /**
     * Tên hiển thị của người dùng.
     */
    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    /**
     * Mật khẩu đã hash (không lưu plaintext).
     */
    @Column(name = "hashed_password", nullable = false, length = 255)
    private String hashedPassword;

    /**
     * Email dùng để đăng nhập. Là duy nhất.
     */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /**
     * Số điện thoại liên hệ. Có thể để null nhưng ở schema gốc là unique.
     */
    @Column(name = "phone_number", nullable = true, length = 20)
    private String phoneNumber;

    /**
     * Thời điểm tạo tài khoản. Thiết lập tự động trong @PrePersist.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Thời điểm cập nhật gần nhất. Cập nhật tự động trong @PreUpdate.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Trạng thái hoạt động của tài khoản (1: hoạt động, 0: khóa).
     * Lưu ở dạng TINYINT trong MySQL.
     */
    @Column(name = "is_active", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean isActive = true;

    /**
     * Một tài khoản có thể có nhiều đánh giá sản phẩm.
     * Cascade ALL + orphanRemoval để quản lý lifecycle review khi xóa tài khoản.
     */
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProductReview> productReviews = new LinkedHashSet<>();

    /**
     * Quan hệ nhiều-nhiều giữa user_account và role (bảng trung gian: user_account_role).
     * Không cascade REMOVE để tránh vô tình xóa role khi xóa user.
     */
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "user_account_role",
            joinColumns = @JoinColumn(name = "user_account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

    /**
     * Một tài khoản có thể có nhiều đơn hàng.
     */
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Order> orders = new LinkedHashSet<>();

    // --- Constructors ---
    public UserAccount() {
    }

    public UserAccount(String userName, String hashedPassword, String email, String phoneNumber) {
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isActive = true;
    }

    // --- Lifecycle callbacks để set createdAt/updatedAt ---
    @PrePersist
    protected void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Helper methods để duy trì quan hệ hai chiều ---

    // ProductReview
    public void addProductReview(ProductReview review) {
        if (review == null) return;
        productReviews.add(review);
        review.setUserAccount(this);
    }

    public void removeProductReview(ProductReview review) {
        if (review == null) return;
        productReviews.remove(review);
        review.setUserAccount(null);
    }

    // Role
    public void addRole(Role role) {
        if (role == null) return;
        roles.add(role);
        if (!role.getUserAccounts().contains(this)) {
            role.getUserAccounts().add(this);
        }
    }

    public void removeRole(Role role) {
        if (role == null) return;
        roles.remove(role);
        role.getUserAccounts().remove(this);
    }

    // Order
    public void addOrder(Order order) {
        if (order == null) return;
        orders.add(order);
        order.setUserAccount(this);
    }

    public void removeOrder(Order order) {
        if (order == null) return;
        orders.remove(order);
        order.setUserAccount(null);
    }

    // --- Getters & Setters ---
    public UUID getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(UUID userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // createdAt không nên set công khai thường xuyên, nhưng vẫn cung cấp setter nếu cần
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // updatedAt thường do JPA quản lý
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Set<ProductReview> getProductReviews() {
        return productReviews;
    }

    public void setProductReviews(Set<ProductReview> productReviews) {
        this.productReviews = productReviews;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    // --- equals & hashCode ---
    /**
     * Dùng email làm business key cho equals/hashCode:
     * - email là duy nhất theo schema
     * - roleId/ userAccountId có thể null trước khi persist
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAccount that = (UserAccount) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    // --- toString (không in toàn bộ collection để tránh recursion) ---
    @Override
    public String toString() {
        return "UserAccount{" +
                "userAccountId=" + userAccountId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isActive=" + isActive +
                ", productReviewCount=" + (productReviews == null ? 0 : productReviews.size()) +
                ", roleCount=" + (roles == null ? 0 : roles.size()) +
                ", orderCount=" + (orders == null ? 0 : orders.size()) +
                '}';
    }
}