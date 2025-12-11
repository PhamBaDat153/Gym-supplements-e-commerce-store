package project.gymecommerce.Models.Order;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.Product.Product;

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
public class Order_item {

    // Các thuộc tính của model: Order_item
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_item_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID order_item_id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private Double unit_price;

    @Column(name = "line_total", nullable = false)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private Double line_total;

    // Constructor
    public Order_item() {
    }

    public Order_item(Product product, Order order, Integer quantity) {
        this.product = product;
        this.order = order;
        this.quantity = quantity;
        this.unit_price = product.getPrice();
        this.line_total = quantity * unit_price;
    }

    //Getter & Setter
    public Double getLine_total() {
        return line_total;
    }

    public void setLine_total(Double line_total) {
        this.line_total = line_total;
    }

    public Double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(Double unit_price) {
        this.unit_price = unit_price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public UUID getOrder_item_id() {
        return order_item_id;
    }

    public void setOrder_item_id(UUID order_item_id) {
        this.order_item_id = order_item_id;
    }

    //toString
    @Override
    public String toString() {
        return "Order_item{" +
                "order_item_id=" + order_item_id +
                ", product=" + product +
                ", order=" + order +
                ", quantity=" + quantity +
                ", unit_price=" + unit_price +
                ", line_total=" + line_total +
                '}';
    }
}