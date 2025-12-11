package project.gymecommerce.Models.User;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/*
CREATE TABLE `role` (
  `role_id`   BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng vai trò (UUID v4 lưu dạng BINARY(16))',
  `role_name` VARCHAR(50)  NOT NULL COMMENT 'Tên vai trò (ví dụ: ROLE_ADMIN, ROLE_USER)',

  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_role_name` (`role_name`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu danh sách các vai trò của người dùng trong hệ thống';
 */

@Entity
@Table(name = "role")
public class Role {

    //Các thuộc tính của model: Role
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID role_id;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String role_name;

    @ManyToMany(mappedBy = "roles", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<User_account> user_accounts = new LinkedHashSet<>();

    //Constructor
    public Role() {
    }

    public Role(String role_name) {
        this.role_name = role_name;
    }

    //Getters & Setters
    public Set<User_account> getUser_accounts() {
        return user_accounts;
    }

    public void setUser_accounts(Set<User_account> user_accounts) {
        this.user_accounts = user_accounts;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public UUID getRole_id() {
        return role_id;
    }

    public void setRole_id(UUID role_id) {
        this.role_id = role_id;
    }

    //toString
    @Override
    public String toString() {
        return "Role{" +
                "role_id=" + role_id +
                ", role_name='" + role_name + '\'' +
                ", user_accounts=" + user_accounts +
                '}';
    }
}