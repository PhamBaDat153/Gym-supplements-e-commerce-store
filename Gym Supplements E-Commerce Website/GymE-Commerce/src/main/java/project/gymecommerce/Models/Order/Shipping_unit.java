package project.gymecommerce.Models.Order;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/*
CREATE TABLE `shipping_unit` (
  `shipping_unit_id`   BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng đơn vị vận chuyển (UUID v4 lưu dạng BINARY(16))',
  `shipping_unit_name` NVARCHAR(100) NOT NULL COMMENT 'Tên đơn vị vận chuyển',
  `hotline`            VARCHAR(20)  NULL COMMENT 'Số hotline hỗ trợ của đơn vị vận chuyển',

  PRIMARY KEY (`shipping_unit_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin các đơn vị vận chuyển hợp tác với hệ thống';
 */

@Entity
@Table(name = "shipping_unit")
public class Shipping_unit {


    //Các thuộc tính của model: Shipping_unit
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shipping_unit_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID shipping_unit_id;

    @Column(name = "shipping_unit_name", nullable = false, length = 100)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String shipping_unit_name;

    @Column(name = "hotline", nullable = false, length = 20)
    private String hotline;

    @OneToMany(mappedBy = "shipping_unit", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new LinkedHashSet<>();

    //Constructor
    public Shipping_unit() {
    }

    public Shipping_unit(String shipping_unit_name, String hotline) {
        this.shipping_unit_name = shipping_unit_name;
        this.hotline = hotline;
    }

    //Getter & Setter
    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    public String getShipping_unit_name() {
        return shipping_unit_name;
    }

    public void setShipping_unit_name(String shipping_unit_name) {
        this.shipping_unit_name = shipping_unit_name;
    }

    public UUID getShipping_unit_id() {
        return shipping_unit_id;
    }

    public void setShipping_unit_id(UUID shipping_unit_id) {
        this.shipping_unit_id = shipping_unit_id;
    }

    //toString
    @Override
    public String toString() {
        return "Shipping_unit{" +
                "shipping_unit_id=" + shipping_unit_id +
                ", shipping_unit_name='" + shipping_unit_name + '\'' +
                ", hotline='" + hotline + '\'' +
                ", orders=" + orders +
                '}';
    }
}