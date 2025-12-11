package project.gymecommerce.Models.User;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.Order.Order;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/*
CREATE TABLE `user_address` (
  `user_address_id` BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng địa chỉ người dùng (UUID v4 lưu dạng BINARY(16))',
  `user_account_id` BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng user_account',
  `house_address`   NVARCHAR(255) NOT NULL COMMENT 'Số nhà, tên tòa, căn hộ',
  `street`          NVARCHAR(255) NOT NULL COMMENT 'Tên đường',
  `ward`            NVARCHAR(100) NULL COMMENT 'Phường hoặc xã',
  `district`        NVARCHAR(100) NULL COMMENT 'Quận hoặc huyện',
  `city`            NVARCHAR(100) NULL COMMENT 'Thành phố hoặc thị xã',
  `province`        NVARCHAR(100) NULL COMMENT 'Tỉnh hoặc thành phố trực thuộc trung ương',
  `receiver_name`   NVARCHAR(100) NOT NULL COMMENT 'Tên người nhận hàng',
  `receiver_phone`  VARCHAR(20)  NOT NULL COMMENT 'Số điện thoại người nhận hàng',
  `is_default`      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT 'Địa chỉ mặc định (1: mặc định, 0: không)',

  PRIMARY KEY (`user_address_id`),
  KEY `idx_user_address_user` (`user_account_id`),
  CONSTRAINT `fk_user_address_user`
    FOREIGN KEY (`user_account_id`)
    REFERENCES `user_account`(`user_account_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu danh sách địa chỉ giao hàng của từng người dùng';
 */

@Entity
@Table(name = "user_address")
public class UserAddress {

    /**
     * Khóa chính user_address_id, lưu dưới dạng UUID (Binary(16) trong DB).
     * - Ý nghĩa: định danh duy nhất cho một địa chỉ người dùng.
     * - Lưu ý: Hibernate sinh UUID tự động khi persist nếu dùng GenerationType.UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_address_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID userAddressId;

    /**
     * Chủ sở hữu của địa chỉ (UserAccount).
     * - Ý nghĩa: liên kết đến tài khoản sở hữu địa chỉ này.
     * - Lưu ý: không cascade REMOVE để tránh vô tình xóa tài khoản khi xóa địa chỉ; fetch LAZY để tránh join không cần thiết.
     */
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            optional = false)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    /**
     * Số nhà / tên tòa / thông tin chi tiết địa chỉ.
     */
    @Column(name = "house_address", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String houseAddress;

    /**
     * Tên đường.
     */
    @Column(name = "street", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String street;

    /**
     * Phường / xã (có thể null).
     */
    @Column(name = "ward", nullable = true)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String ward;

    /**
     * Quận / huyện (có thể null).
     */
    @Column(name = "district", nullable = true)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String district;

    /**
     * Thành phố / thị xã (có thể null).
     */
    @Column(name = "city", nullable = true)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String city;

    /**
     * Tỉnh / đơn vị hành chính cấp cao hơn (có thể null).
     */
    @Column(name = "province", nullable = true)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String province;

    /**
     * Tên người nhận hàng cho địa chỉ này.
     */
    @Column(name = "receiver_name", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String receiverName;

    /**
     * Số điện thoại người nhận hàng.
     * - Ý nghĩa: dùng cho liên hệ giao nhận và thông báo giao hàng.
     * - Lưu ý: tuân thủ ràng buộc unique ở tầng DB nếu cần; validate ở service trước khi lưu.
     */
    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    /**
     * Cờ địa chỉ mặc định: true = địa chỉ mặc định của user.
     * - Ý nghĩa: hệ thống có thể dùng để ưu tiên địa chỉ khi tạo đơn.
     * - Lưu ý: mặc định false.
     */
    @Column(name = "is_default", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean isDefault = false;

    /**
     * Các đơn hàng sử dụng địa chỉ này.
     * - Ý nghĩa: quan hệ một-nhiều từ UserAddress tới Order.
     * - Lưu ý: cascade ALL + orphanRemoval tuỳ theo nghiệp vụ; ở đây đặt để xóa liên quan khi địa chỉ bị xóa.
     */
    @OneToMany(mappedBy = "userAddress", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Order> orders = new LinkedHashSet<>();

    /**
     * Thời điểm tạo địa chỉ (tuỳ chọn, dùng cho audit).
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // --- Constructors ---
    public UserAddress() {
    }

    public UserAddress(String receiverPhone,
                       String receiverName,
                       String province,
                       String city,
                       String district,
                       String ward,
                       String street,
                       String houseAddress,
                       UserAccount userAccount) {
        this.receiverPhone = receiverPhone;
        this.receiverName = receiverName;
        this.province = province;
        this.city = city;
        this.district = district;
        this.ward = ward;
        this.street = street;
        this.houseAddress = houseAddress;
        this.userAccount = userAccount;
        this.isDefault = false;
    }

    // --- Lifecycle callbacks ---
    @PrePersist
    protected void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isDefault == null) {
            this.isDefault = false;
        }
    }

    // --- Helper methods để duy trì quan hệ hai chiều với Order ---
    /**
     * Thêm một Order sử dụng địa chỉ này và đồng bộ quan hệ ngược.
     */
    public void addOrder(Order order) {
        if (order == null) return;
        orders.add(order);
        order.setUserAddress(this);
    }

    /**
     * Loại bỏ một Order khỏi danh sách và clear tham chiếu ngược.
     */
    public void removeOrder(Order order) {
        if (order == null) return;
        orders.remove(order);
        order.setUserAddress(null);
    }

    // --- Getters & Setters ---
    public UUID getUserAddressId() {
        return userAddressId;
    }

    public void setUserAddressId(UUID userAddressId) {
        this.userAddressId = userAddressId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // createdAt thường do JPA quản lý; vẫn cung cấp setter nếu cần
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // --- equals & hashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAddress that = (UserAddress) o;
        return userAddressId != null && Objects.equals(userAddressId, that.userAddressId);
    }

    @Override
    public int hashCode() {
        return userAddressId != null ? Objects.hash(userAddressId) : System.identityHashCode(this);
    }

    // --- toString (tránh in toàn bộ entity liên quan để không gây recursion) ---
    @Override
    public String toString() {
        return "UserAddress{" +
                "userAddressId=" + userAddressId +
                ", userAccountId=" + (userAccount == null ? null : userAccount.getUserAccountId()) +
                ", houseAddress='" + houseAddress + '\'' +
                ", street='" + street + '\'' +
                ", ward='" + ward + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", isDefault=" + isDefault +
                ", orderCount=" + (orders == null ? 0 : orders.size()) +
                '}';
    }
}
