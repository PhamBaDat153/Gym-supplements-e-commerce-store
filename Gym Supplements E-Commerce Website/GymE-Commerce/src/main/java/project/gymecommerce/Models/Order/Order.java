package project.gymecommerce.Models.Order;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.User.UserAccount;
import project.gymecommerce.Models.User.UserAddress;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
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
@Table(name = "orders")
public class Order {

    // --- Enum lưu ra DB dưới dạng STRING để dễ đọc / tránh bị sai khi vị trí enum thay đổi ---
    public enum OrderStatus {PENDING, CONFIRMED, SHIPPING, COMPLETED, CANCELLED, RETURNED}
    public enum PaymentMethod {COD, BANK_TRANSFER, CREDIT_CARD, EWALLET, OTHER}

    // --- Các thuộc tính chính ---
    /**
     * Khóa chính của đơn hàng, lưu dưới dạng UUID (Binary(16) trong DB).
     * - Ý nghĩa: định danh duy nhất cho một đơn hàng.
     * - Lưu ý: sử dụng GenerationType.UUID để Hibernate tự sinh UUID (UUID v4) khi persist.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID orderId;

    /**
     * Tập các dòng hàng (OrderItem) thuộc đơn này.
     * - Ý nghĩa: mô tả chi tiết từng sản phẩm/mặt hàng trong đơn cùng số lượng, giá, v.v.
     * - Lưu ý: cascade ALL + orphanRemoval để quản lý vòng đời item (xóa đơn -> xóa item; xóa item khỏi collection -> bị remove).
     * - Fetch LAZY để tránh tải dữ liệu con không cần thiết khi chỉ quan tâm thông tin đơn.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new LinkedHashSet<>();

    /**
     * Người dùng (UserAccount) tạo đơn.
     * - Ý nghĩa: liên kết tới chủ sở hữu đơn hàng.
     * - Lưu ý: không cascade REMOVE để tránh vô tình xóa tài khoản khi xóa đơn; optional = false nghĩa là bắt buộc.
     */
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            optional = false)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    /**
     * Địa chỉ giao hàng được chọn (UserAddress).
     * - Ý nghĩa: dùng để xác định nơi giao nhận khi thực hiện vận chuyển.
     * - Lưu ý: optional = false nếu bắt buộc phải chọn địa chỉ khi tạo đơn.
     */
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            optional = false)
    @JoinColumn(name = "user_address_id", nullable = false)
    private UserAddress userAddress;

    /**
     * Đơn vị vận chuyển xử lý đơn này; có thể null nếu chưa gán.
     * - Ý nghĩa: tham chiếu tới nhà vận chuyển để theo dõi trạng thái giao hàng.
     * - Lưu ý: không bắt buộc tại thời điểm tạo đơn; thường được gán sau khi xác nhận.
     */
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "shipping_unit_id")
    private ShippingUnit shippingUnit;

    /**
     * Tổng giá trị hàng hoá trước khi áp dụng các chương trình giảm giá.
     * - Ý nghĩa: cơ sở để tính discountAmount và finalPrice.
     * - Lưu ý: dùng BigDecimal với precision/scale phù hợp để đảm bảo chính xác về tài chính.
     */
    @Column(name = "original_price", nullable = false, precision = 15, scale = 2)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private BigDecimal originalPrice = BigDecimal.ZERO;

    /**
     * Tổng tiền được giảm cho đơn (tổng của tất cả mã/chiết khấu áp dụng).
     * - Ý nghĩa: số tiền sẽ bị trừ khỏi originalPrice khi tính finalPrice.
     * - Lưu ý: luôn >= 0; kiểm tra ở tầng service/validate.
     */
    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * Số tiền khách hàng phải thanh toán sau khi trừ các khoản giảm giá.
     * - Ý nghĩa: finalPrice = max(0, originalPrice - discountAmount).
     * - Lưu ý: trường này thường do hệ thống tính toán, không nên cho phép set trực tiếp trừ khi có lý do cụ thể.
     */
    @Column(name = "final_price", nullable = false, precision = 15, scale = 2)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private BigDecimal finalPrice = BigDecimal.ZERO;

    /**
     * Trạng thái hiện tại của đơn hàng.
     * - Ý nghĩa: theo dõi luồng xử lý đơn (chờ xử lý -> xác nhận -> vận chuyển -> hoàn thành/huỷ/hoàn trả).
     * - Lưu ý: lưu dưới dạng STRING để dễ đọc trong DB và an toàn khi sửa enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    /**
     * Phương thức thanh toán của khách hàng cho đơn này.
     * - Ý nghĩa: ảnh hưởng đến luồng xử lý (ví dụ: COD không cần xử lý thanh toán trước).
     * - Lưu ý: lưu dưới dạng STRING.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    /**
     * Thời điểm tạo đơn hàng (ngày giờ hệ thống).
     * - Ý nghĩa: dùng cho audit, lọc báo cáo, và điều kiện thời gian khuyến mãi.
     * - Lưu ý: được khởi tạo tự động trong @PrePersist nếu chưa có giá trị.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Các mã giảm giá áp dụng cho đơn hàng.
     * - Ý nghĩa: biểu diễn quan hệ many-to-many giữa Order và Discount (bảng trung gian discount_order).
     * - Lưu ý: fetch LAZY và cascade hạn chế để tránh tác động không mong muốn khi sửa discount.
     */
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "discount_order",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "discount_id"))
    private Set<Discount> discounts = new LinkedHashSet<>();

    // --- Constructors ---
    public Order() {
    }

    public Order(UserAccount userAccount,
                 UserAddress userAddress,
                 ShippingUnit shippingUnit,
                 BigDecimal originalPrice,
                 OrderStatus orderStatus,
                 PaymentMethod paymentMethod) {
        this.userAccount = userAccount;
        this.userAddress = userAddress;
        this.shippingUnit = shippingUnit;
        this.originalPrice = originalPrice == null ? BigDecimal.ZERO : originalPrice;
        this.discountAmount = BigDecimal.ZERO;
        this.finalPrice = this.originalPrice.subtract(this.discountAmount);
        this.orderStatus = orderStatus == null ? OrderStatus.PENDING : orderStatus;
        this.paymentMethod = paymentMethod == null ? PaymentMethod.COD : paymentMethod;
    }

    // --- Lifecycle callbacks ---
    @PrePersist
    protected void prePersist() {
        // Trước khi persist lần đầu:
        // - Đảm bảo createdAt có giá trị (nếu chưa set) = thời điểm hiện tại.
        // - Đảm bảo các trường tiền tệ không null để tránh lỗi DB.
        // - Tính finalPrice mặc định nếu chưa được set: originalPrice - discountAmount (>= 0).
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.originalPrice == null) this.originalPrice = BigDecimal.ZERO;
        if (this.discountAmount == null) this.discountAmount = BigDecimal.ZERO;
        if (this.finalPrice == null) this.finalPrice = this.originalPrice.subtract(this.discountAmount);
    }

    // --- Helper methods để duy trì quan hệ hai chiều ---
    /**
     * Thêm một OrderItem vào đơn.
     * - Hành vi: nếu item là null sẽ bỏ qua; thêm item vào collection và set quan hệ ngược item.order = this.
     * - Tác động: gọi recalculateFinalPrice() để cập nhật lại finalPrice theo thay đổi.
     */
    public void addOrderItem(OrderItem item) {
        if (item == null) return;
        orderItems.add(item);
        item.setOrder(this);
        recalculateFinalPrice();
    }

    /**
     * Loại bỏ một OrderItem khỏi đơn.
     * - Hành vi: nếu item là null sẽ bỏ qua; xóa item khỏi collection và clear quan hệ ngược (item.order = null).
     * - Tác động: cập nhật lại finalPrice sau khi xóa.
     */
    public void removeOrderItem(OrderItem item) {
        if (item == null) return;
        orderItems.remove(item);
        item.setOrder(null);
        recalculateFinalPrice();
    }

    /**
     * Áp một mã giảm giá (Discount) lên đơn.
     * - Hành vi: nếu discount null thì bỏ qua; thêm vào collection discounts và đồng bộ danh sách orders bên Discount.
     * - Tác động: gọi recalculateFinalPrice() sau khi thêm (giả sử discountAmount đã được tính trước hoặc tính ở tầng service).
     */
    public void addDiscount(Discount discount) {
        if (discount == null) return;
        discounts.add(discount);
        discount.getOrders().add(this);
        recalculateFinalPrice();
    }

    /**
     * Bỏ áp dụng một mã giảm giá khỏi đơn.
     * - Hành vi: loại bỏ discount khỏi collection của Order và đồng bộ ở phía Discount.
     * - Tác động: cập nhật lại finalPrice sau khi loại bỏ.
     */
    public void removeDiscount(Discount discount) {
        if (discount == null) return;
        discounts.remove(discount);
        discount.getOrders().remove(this);
        recalculateFinalPrice();
    }

    /**
     * Tính lại finalPrice dựa trên originalPrice và discountAmount.
     * - Công thức: finalPrice = originalPrice - discountAmount; nếu kết quả âm thì đặt về 0.
     * - Lưu ý: phương thức không tự động tính discountAmount từ discounts; giả định giá trị discountAmount
     *   đã được tính ở tầng service/business trước khi gọi phương thức này.
     */
    public void recalculateFinalPrice() {
        if (this.originalPrice == null) this.originalPrice = BigDecimal.ZERO;
        if (this.discountAmount == null) this.discountAmount = BigDecimal.ZERO;
        this.finalPrice = this.originalPrice.subtract(this.discountAmount);
        if (this.finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            this.finalPrice = BigDecimal.ZERO;
        }
    }

    // --- Getters & Setters ---
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public UserAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(UserAddress userAddress) {
        this.userAddress = userAddress;
    }

    public ShippingUnit getShippingUnit() {
        return shippingUnit;
    }

    public void setShippingUnit(ShippingUnit shippingUnit) {
        this.shippingUnit = shippingUnit;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice == null ? BigDecimal.ZERO : originalPrice;
        recalculateFinalPrice();
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount == null ? BigDecimal.ZERO : discountAmount;
        recalculateFinalPrice();
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    // finalPrice thường do hệ thống tính, không set trực tiếp trừ khi có lý do
    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // createdAt thường do JPA quản lý
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Set<Discount> discounts) {
        this.discounts = discounts;
    }

    // --- equals & hashCode ---
    /**
     * equals/hashCode dựa trên orderId (UUID) để phản ánh danh tính thực thể.
     * - Ý nghĩa: sau khi persist, orderId là định danh duy nhất; trước khi persist (orderId == null)
     *   phương thức equals sẽ trả về false so với các entity khác và hashCode dùng identityHashCode để tránh va chạm.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;
        return orderId != null && Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return orderId != null ? Objects.hash(orderId) : System.identityHashCode(this);
    }

    // --- toString (tránh in toàn bộ collection để không gây recursion) ---
    /**
     * Trả về chuỗi tóm tắt các thông tin chính của đơn để phục vụ logging/debug.
     * - Lưu ý: không in trực tiếp các collection hoặc entity liên quan đầy đủ để tránh recursion và dữ liệu quá lớn.
     */
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userAccountId=" + (userAccount == null ? null : userAccount.getUserAccountId()) +
                ", userAddressId=" + (userAddress == null ? null : userAddress.getUserAddressId()) +
                ", shippingUnitId=" + (shippingUnit == null ? null : shippingUnit.getShippingUnitId()) +
                ", originalPrice=" + originalPrice +
                ", discountAmount=" + discountAmount +
                ", finalPrice=" + finalPrice +
                ", orderStatus=" + orderStatus +
                ", paymentMethod=" + paymentMethod +
                ", createdAt=" + createdAt +
                ", orderItemCount=" + (orderItems == null ? 0 : orderItems.size()) +
                ", discountCount=" + (discounts == null ? 0 : discounts.size()) +
                '}';
    }
}