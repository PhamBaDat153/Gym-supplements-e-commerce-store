package project.gymecommerce.Models.Order;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.Product.Product;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/*
CREATE TABLE `order_item` (
  `order_item_id` BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng chi tiết đơn hàng (UUID v4 lưu dạng BINARY(16))',
  `product_id`    BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng product',
  `order_id`      BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng order',
  `quantity`      INT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Số lượng sản phẩm trong dòng hàng',
  `unit_price`    DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Đơn giá tại thời điểm đặt hàng',
  `line_total`    DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Thành tiền của dòng hàng (sau khi áp dụng giảm giá nếu có)',

  PRIMARY KEY (`order_item_id`),
  KEY `idx_order_item_product` (`product_id`),
  KEY `idx_order_item_order` (`order_id`),
  CONSTRAINT `fk_order_item_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `product`(`product_id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_order_item_order`
    FOREIGN KEY (`order_id`)
    REFERENCES `order`(`order_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Chi tiết từng sản phẩm nằm trong mỗi đơn hàng';

 */
@Entity
@Table(name = "order_item")
public class OrderItem {

    /**
     * Khóa chính của order item, lưu dưới dạng UUID (Binary(16) trong DB).
     * - Ý nghĩa: định danh duy nhất cho một dòng hàng trong đơn.
     * - Lưu ý: Hibernate sinh UUID tự động khi persist.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_item_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID orderItemId;

    /**
     * Sản phẩm tham chiếu bởi dòng hàng (Product).
     * - Ý nghĩa: liên kết tới thông tin sản phẩm gốc (tên, SKU, giá kho, v.v.).
     * - Lưu ý: không cascade REMOVE để tránh xóa bản ghi product khi xóa order item; optional = false.
     */
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Đơn hàng chứa dòng hàng này (Order).
     * - Ý nghĩa: quan hệ sở hữu; order là chủ quan hệ.
     * - Lưu ý: cascade hạn chế để tránh ảnh hưởng ngoài ý muốn lên Order khi thao tác OrderItem.
     */
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Số lượng sản phẩm trong dòng hàng (INT UNSIGNED).
     * - Ý nghĩa: số lượng khách đặt cho sản phẩm này.
     * - Lưu ý: giá trị tối thiểu là 1; setter và preSave đảm bảo ràng buộc này.
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    /**
     * Đơn giá áp dụng tại thời điểm đặt hàng.
     * - Ý nghĩa: dùng để tính lineTotal và giữ lịch sử giá (không phụ thuộc giá hiện tại của product).
     * - Lưu ý: sử dụng BigDecimal để đảm bảo chính xác cho tính toán tài chính.
     */
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    /**
     * Thành tiền của dòng hàng = unitPrice * quantity (sau khi áp dụng khuyến mãi nếu có).
     * - Ý nghĩa: thể hiện giá trị dòng hàng đã được quy về tiền tệ.
     * - Lưu ý: luôn được tính lại trong preSave và khi thay đổi các trường liên quan.
     */
    @Column(name = "line_total", nullable = false, precision = 15, scale = 2)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private BigDecimal lineTotal = BigDecimal.ZERO;

    // --- Constructors ---
    public OrderItem() {
    }

    /**
     * Constructor tiện lợi: khởi tạo OrderItem từ Product, Order và quantity.
     * - Hành vi: nếu quantity null hoặc < 1, sẽ đặt mặc định là 1; unitPrice được lấy từ product.getPrice() nếu có.
     * - Lưu ý: phương thức không persist entity; chỉ khởi tạo đối tượng ở bộ nhớ.
     */
    public OrderItem(Product product, Order order, Integer quantity) {
        this.product = product;
        this.order = order;
        this.quantity = (quantity == null || quantity < 1) ? 1 : quantity;
        this.unitPrice = (product != null && product.getPrice() != null)
                ? product.getPrice()
                : BigDecimal.ZERO;
        recalculateLineTotal();
    }


    // --- Lifecycle callbacks: đảm bảo lineTotal luôn đúng trước khi persist/update ---
    @PrePersist
    @PreUpdate
    protected void preSave() {
        // Trước khi lưu hoặc cập nhật:
        // - Đảm bảo unitPrice không null.
        // - Đảm bảo quantity >= 1.
        // - Tính lại lineTotal để lưu vào DB luôn đúng.
        if (this.unitPrice == null) this.unitPrice = BigDecimal.ZERO;
        if (this.quantity == null || this.quantity < 1) this.quantity = 1;
        recalculateLineTotal();
    }

    // --- Business method để tính lại lineTotal ---
    /**
     * Tính lại lineTotal theo unitPrice và quantity.
     * - Công thức: lineTotal = unitPrice * quantity.
     * - Lưu ý: bảo đảm không trả về giá trị âm; nếu âm sẽ đặt về 0.
     */
    public void recalculateLineTotal() {
        this.lineTotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        if (this.lineTotal.compareTo(BigDecimal.ZERO) < 0) {
            this.lineTotal = BigDecimal.ZERO;
        }
    }

    // --- Getters & Setters ---
    public UUID getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        // cập nhật unitPrice nếu muốn lấy giá hiện tại của product
        if (product != null && product.getPrice() != null) {
            this.unitPrice = product.getPrice();
            recalculateLineTotal();
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity == null || quantity < 1 ? 1 : quantity;
        recalculateLineTotal();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Setter unitPrice: cho phép điều chỉnh đơn giá khi cần (ví dụ: áp giá khuyến mãi hoặc sửa lỗi giá).
     * - Lưu ý: setter gọi recalculateLineTotal() để cập nhật lineTotal tương ứng.
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice == null ? BigDecimal.ZERO : unitPrice;
        recalculateLineTotal();
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal == null ? BigDecimal.ZERO : lineTotal;
    }

    // --- equals & hashCode (dựa vào orderItemId nếu đã persist) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem that = (OrderItem) o;
        return orderItemId != null && Objects.equals(orderItemId, that.orderItemId);
    }

    @Override
    public int hashCode() {
        return orderItemId != null ? Objects.hash(orderItemId) : System.identityHashCode(this);
    }

    // --- toString (tránh print toàn bộ product/order để không gây recursion) ---
    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", productId=" + (product == null ? null : product.getProductId()) +
                ", orderId=" + (order == null ? null : order.getOrderId()) +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", lineTotal=" + lineTotal +
                '}';
    }
}