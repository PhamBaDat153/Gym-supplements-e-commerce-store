package project.gymecommerce.Models.Order;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.User.User_account;
import project.gymecommerce.Models.User.User_address;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/*
CREATE TABLE `order` (
  `order_id`         BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng đơn hàng (UUID v4 lưu dạng BINARY(16))',
  `user_account_id`  BINARY(16)   NOT NULL COMMENT 'Khách hàng đặt đơn hàng',
  `user_address_id`  BINARY(16)   NOT NULL COMMENT 'Địa chỉ giao hàng được chọn',
  `shipping_unit_id` BINARY(16)   NOT NULL COMMENT 'Đơn vị vận chuyển xử lý đơn',
  `original_price`   DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng giá trị hàng hóa trước khi giảm giá',
  `discount_amount`  DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng số tiền giảm giá áp dụng cho đơn hàng',
  `final_price`      DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Số tiền khách phải thanh toán sau giảm giá',
  `order_status`     ENUM('PENDING','CONFIRMED','SHIPPING','COMPLETED','CANCELLED','RETURNED') NOT NULL DEFAULT 'PENDING'
                     COMMENT 'Trạng thái hiện tại của đơn hàng',
  `payment_method`   ENUM('COD','BANK_TRANSFER','CREDIT_CARD','EWALLET','OTHER') NOT NULL DEFAULT 'COD'
                     COMMENT 'Phương thức thanh toán của đơn hàng',
  `created_at`       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo đơn hàng',

  PRIMARY KEY (`order_id`),
  KEY `idx_order_user` (`user_account_id`),
  KEY `idx_order_address` (`user_address_id`),
  KEY `idx_order_shipping_unit` (`shipping_unit_id`),
  CONSTRAINT `fk_order_user`
    FOREIGN KEY (`user_account_id`)
    REFERENCES `user_account`(`user_account_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_order_address`
    FOREIGN KEY (`user_address_id`)
    REFERENCES `user_address`(`user_address_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_order_shipping_unit`
    FOREIGN KEY (`shipping_unit_id`)
    REFERENCES `shipping_unit`(`shipping_unit_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin các đơn hàng được tạo trên hệ thống';
 */

@Entity
@Table(name = "order")
public class Order {

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order_item> order_items = new LinkedHashSet<>();

    private enum OrderStatus {PENDING, CONFIRMED, SHIPPING, COMPLETED, CANCELLED, RETURNED}

    private enum PaymentMethod {COD, BANK_TRANSFER, CREDIT_CARD, EWALLET, OTHER}

    //Các thuôc tính của model: Order
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private UUID order_id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, optional = false)
    @JoinColumn(name = "user_account_id", nullable = false)
    private User_account user_account;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, optional = false)
    @JoinColumn(name = "user_address_id", nullable = false)
    private User_address user_address;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "shipping_unit_id")
    private Shipping_unit shipping_unit;

    @Column(name = "original_price", nullable = false)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private Double original_price;

    @Column(name = "discount_amount", nullable = false)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private Double discount_amount;

    @Column(name = "final_price", nullable = false)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private Double final_price;

    @Enumerated
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Enumerated
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "discount_order",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_id"))
    private Set<Discount> discounts = new LinkedHashSet<>();

    //Constructor
    public Order() {
    }

    public Order(Set<Order_item> order_items, User_account user_account, User_address user_address, Shipping_unit shipping_unit, Double original_price, OrderStatus orderStatus, PaymentMethod paymentMethod) {
        this.order_items = order_items;
        this.user_account = user_account;
        this.user_address = user_address;
        this.shipping_unit = shipping_unit;
        this.original_price = original_price;
        this.discount_amount = (double) 0;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.created_at = LocalDateTime.now();
        this.final_price = original_price - discount_amount;
    }

    //Getter & Setter
    public Set<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Set<Discount> discounts) {
        this.discounts = discounts;
    }

    public Set<Order_item> getOrder_items() {
        return order_items;
    }

    public void setOrder_items(Set<Order_item> order_items) {
        this.order_items = order_items;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Double getFinal_price() {
        return final_price;
    }

    public void setFinal_price(Double final_price) {
        this.final_price = final_price;
    }

    public Double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(Double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public Double getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(Double original_price) {
        this.original_price = original_price;
    }

    public Shipping_unit getShipping_unit() {
        return shipping_unit;
    }

    public void setShipping_unit(Shipping_unit shipping_unit) {
        this.shipping_unit = shipping_unit;
    }

    public User_address getUser_address() {
        return user_address;
    }

    public void setUser_address(User_address user_address) {
        this.user_address = user_address;
    }

    public User_account getUser_account() {
        return user_account;
    }

    public void setUser_account(User_account user_account) {
        this.user_account = user_account;
    }

    public UUID getOrder_id() {
        return order_id;
    }

    public void setOrder_id(UUID order_id) {
        this.order_id = order_id;
    }

    //toString
    @Override
    public String toString() {
        return "Order{" +
                "order_items=" + order_items +
                ", order_id=" + order_id +
                ", user_account=" + user_account +
                ", user_address=" + user_address +
                ", shipping_unit=" + shipping_unit +
                ", original_price=" + original_price +
                ", discount_amount=" + discount_amount +
                ", final_price=" + final_price +
                ", orderStatus=" + orderStatus +
                ", paymentMethod=" + paymentMethod +
                ", created_at=" + created_at +
                ", discounts=" + discounts +
                '}';
    }
}