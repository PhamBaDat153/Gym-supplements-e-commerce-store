package project.gymecommerce.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
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
@Table(name = "user_account")
public class User_account {

    //Các thuộc tính của model: User_account
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_account_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID user_account_id;

    @Column(name = "user_name", nullable = false, length = 100)
    private String user_name;

    @Column(name = "hashed_password", nullable = false)
    private String hashed_password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phone_number;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at;

    @Column(name = "is_active", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean is_active = false;

    @OneToMany(mappedBy = "user_account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product_review> product_reviews = new LinkedHashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "user_account_role",
            joinColumns = @JoinColumn(name = "user_account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user_account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new LinkedHashSet<>();

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Product_review> getProduct_reviews() {
        return product_reviews;
    }

    public void setProduct_reviews(Set<Product_review> product_reviews) {
        this.product_reviews = product_reviews;
    }


    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashed_password() {
        return hashed_password;
    }

    public void setHashed_password(String hashed_password) {
        this.hashed_password = hashed_password;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public UUID getUser_account_id() {
        return user_account_id;
    }

    public void setUser_account_id(UUID user_account_id) {
        this.user_account_id = user_account_id;
    }
}