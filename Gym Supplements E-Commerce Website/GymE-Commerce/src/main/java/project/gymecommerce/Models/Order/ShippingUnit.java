package project.gymecommerce.Models.Order;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
import java.util.Objects;
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
public class ShippingUnit {

    /**
     * Khóa chính của đơn vị vận chuyển, lưu dưới dạng UUID (Binary(16) trong DB).
     * - Ý nghĩa: định danh duy nhất cho một hãng/cơ sở vận chuyển trong hệ thống.
     * - Lưu ý: Hibernate sẽ sinh UUID tự động khi persist nếu sử dụng GenerationType.UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shipping_unit_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID shippingUnitId;

    /**
     * Tên hiển thị của đơn vị vận chuyển (hỗ trợ Unicode).
     * - Ý nghĩa: dùng cho UI, báo cáo, và lựa chọn đơn vị vận chuyển khi xử lý đơn.
     * - Lưu ý: giới hạn độ dài 100 ký tự.
     */
    @Column(name = "shipping_unit_name", nullable = false, length = 100)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String shippingUnitName;

    /**
     * Số hotline hỗ trợ/kênh liên hệ của đơn vị vận chuyển.
     * - Ý nghĩa: cung cấp thông tin liên hệ khi cần hỗ trợ giao hàng.
     * - Lưu ý: có thể null nếu không có thông tin hoặc không bắt buộc.
     */
    @Column(name = "hotline", nullable = true, length = 20)
    private String hotline;

    /**
     * Các đơn hàng được hãng này xử lý.
     * - Ý nghĩa: quan hệ một-nhiều từ ShippingUnit tới Order.
     * - Lưu ý: Fetch LAZY để tránh tải danh sách đơn hàng theo mặc định; không cascade REMOVE
     *   để tránh vô tình xóa đơn khi xóa bản ghi đơn vị vận chuyển (hành vi có thể điều chỉnh theo nghiệp vụ).
     */
    @OneToMany(mappedBy = "shippingUnit", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = false, fetch = FetchType.LAZY)
    private Set<Order> orders = new LinkedHashSet<>();

    // --- Constructors ---
    public ShippingUnit() {
    }

    public ShippingUnit(String shippingUnitName, String hotline) {
        this.shippingUnitName = shippingUnitName;
        this.hotline = hotline;
    }

    // --- Helper methods để duy trì quan hệ hai chiều ---
    /**
     * Thêm một Order vào danh sách orders của ShippingUnit và set shippingUnit của Order tương ứng.
     * - Hành vi: nếu order là null sẽ bỏ qua; đảm bảo mối quan hệ hai chiều nhất quán ở bộ nhớ.
     * - Lưu ý: phương thức không thực hiện persist; chỉ cập nhật collection liên kết.
     */
    public void addOrder(Order order) {
        if (order == null) return;
        this.orders.add(order);
        order.setShippingUnit(this);
    }

    /**
     * Loại bỏ một Order khỏi danh sách orders.
     * - Hành vi: nếu order là null sẽ bỏ qua; sau khi xóa khỏi collection, nếu order đang trỏ về
     *   shippingUnit này thì sẽ clear tham chiếu để tránh tham chiếu tới entity không tồn tại.
     */
    public void removeOrder(Order order) {
        if (order == null) return;
        this.orders.remove(order);
        if (order.getShippingUnit() == this) {
            order.setShippingUnit(null);
        }
    }

    // --- Getters & Setters ---
    public UUID getShippingUnitId() {
        return shippingUnitId;
    }

    public void setShippingUnitId(UUID shippingUnitId) {
        this.shippingUnitId = shippingUnitId;
    }

    public String getShippingUnitName() {
        return shippingUnitName;
    }

    public void setShippingUnitName(String shippingUnitName) {
        this.shippingUnitName = shippingUnitName;
    }

    public String getHotline() {
        return hotline;
    }

    public void setHotline(String hotline) {
        this.hotline = hotline;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    // --- equals & hashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShippingUnit)) return false;
        ShippingUnit that = (ShippingUnit) o;
        return shippingUnitId != null && Objects.equals(shippingUnitId, that.shippingUnitId);
    }

    @Override
    public int hashCode() {
        return shippingUnitId != null ? Objects.hash(shippingUnitId) : System.identityHashCode(this);
    }

    // --- toString (không in toàn bộ orders để tránh recursion / log lớn) ---
    @Override
    public String toString() {
        return "ShippingUnit{" +
                "shippingUnitId=" + shippingUnitId +
                ", shippingUnitName='" + shippingUnitName + '\'' +
                ", hotline='" + hotline + '\'' +
                ", orderCount=" + (orders == null ? 0 : orders.size()) +
                '}';
    }
}