package project.gymecommerce.Models.User;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.Order.Order;

import java.util.LinkedHashSet;
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
public class User_address {

    //Các thuộc tính của model: User_address`
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_address_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID user_address_id;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_account_id", nullable = false)
    private User_account user_account;

    @Column(name = "house_address", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String house_address;

    @Column(name = "street", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String street;

    @Column(name = "ward", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String ward;

    @Column(name = "district", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String district;

    @Column(name = "city", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String city;

    @Column(name = "province", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String province;

    @Column(name = "receiver_name", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String receiver_name;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiver_phone;

    @Column(name = "is_default", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean is_default = false;

    @OneToMany(mappedBy = "user_address", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new LinkedHashSet<>();

    //Constructor
    public User_address() {
    }

    public User_address(String receiver_phone, String receiver_name, String province, String city, String district, String ward, String street, String house_address, User_account user_account) {
        this.receiver_phone = receiver_phone;
        this.receiver_name = receiver_name;
        this.province = province;
        this.city = city;
        this.district = district;
        this.ward = ward;
        this.street = street;
        this.house_address = house_address;
        this.user_account = user_account;
        this.is_default = false;
    }

    //Getters & Setters
    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Boolean getIs_default() {
        return is_default;
    }

    public void setIs_default(Boolean is_default) {
        this.is_default = is_default;
    }

    public String getReceiver_phone() {
        return receiver_phone;
    }

    public void setReceiver_phone(String receiver_phone) {
        this.receiver_phone = receiver_phone;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse_address() {
        return house_address;
    }

    public void setHouse_address(String house_address) {
        this.house_address = house_address;
    }

    public User_account getUser_account() {
        return user_account;
    }

    public void setUser_account(User_account user_account) {
        this.user_account = user_account;
    }

    public UUID getUser_address_id() {
        return user_address_id;
    }

    public void setUser_address_id(UUID user_address_id) {
        this.user_address_id = user_address_id;
    }

    //toString
    @Override
    public String toString() {
        return "User_address{" +
                "user_address_id=" + user_address_id +
                ", user_account=" + user_account +
                ", house_address='" + house_address + '\'' +
                ", street='" + street + '\'' +
                ", ward='" + ward + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", receiver_name='" + receiver_name + '\'' +
                ", receiver_phone='" + receiver_phone + '\'' +
                ", is_default=" + is_default +
                ", orders=" + orders +
                '}';
    }
}