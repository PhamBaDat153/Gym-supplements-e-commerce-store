package project.gymecommerce.Models.User;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
import java.util.Objects;
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
@Table(
        name = "role",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_name", columnNames = {"role_name"})
        }
)
public class Role {

    /**
     * Khóa chính role_id lưu dưới dạng BINARY(16) (UUID v4).
     * - Ý nghĩa: định danh duy nhất cho một vai trò trong hệ thống.
     * - Lưu ý: Hibernate có thể tự sinh UUID khi persist nếu dùng GenerationType.UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID roleId;

    /**
     * Tên vai trò (ví dụ: ROLE_ADMIN, ROLE_USER).
     * - Ý nghĩa: business key dùng để phân quyền và so sánh giữa các vai trò.
     * - Lưu ý: có ràng buộc unique trên DB; giới hạn độ dài 50 ký tự.
     */
    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    /**
     * Tập các tài khoản người dùng có vai trò này.
     * - Ý nghĩa: quan hệ nhiều-nhiều giữa Role và UserAccount.
     * - Lưu ý: mappedBy trỏ tới thuộc tính 'roles' trong UserAccount; Fetch LAZY để tránh tải collection mặc định;
     *   không cascade REMOVE để tránh vô tình xóa User khi xóa Role.
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<UserAccount> userAccounts = new LinkedHashSet<>();

    // --- Constructors ---
    public Role() {
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }

    // --- Helper methods để duy trì quan hệ hai chiều ---
    /**
     * Thêm một UserAccount vào tập userAccounts và đồng bộ phía UserAccount nếu cần.
     * - Hành vi: nếu user null thì bỏ qua; đảm bảo quan hệ hai chiều nhất quán tại bộ nhớ.
     */
    public void addUserAccount(UserAccount user) {
        if (user == null) return;
        this.userAccounts.add(user);
        if (!user.getRoles().contains(this)) {
            user.getRoles().add(this);
        }
    }

    /**
     * Loại bỏ một UserAccount khỏi tập userAccounts và đồng bộ phía UserAccount.
     */
    public void removeUserAccount(UserAccount user) {
        if (user == null) return;
        this.userAccounts.remove(user);
        user.getRoles().remove(this);
    }

    // --- Getters & Setters ---
    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(Set<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }

    // --- equals & hashCode ---
    /**
     * equals/hashCode dựa trên roleName (business key) vì roleId có thể null trước khi persist.
     * - Lưu ý: nếu roleName có thể thay đổi sau khi entity được dùng trong collection hash-based,
     *   điều này có thể gây lỗi; cân nhắc sử dụng roleId cho equals/hashCode nếu roleName thay đổi.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return Objects.equals(roleName, role.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName);
    }

    // --- toString (không in toàn bộ userAccounts để tránh recursion / log lớn) ---
    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", userCount=" + (userAccounts == null ? 0 : userAccounts.size()) +
                '}';
    }
}