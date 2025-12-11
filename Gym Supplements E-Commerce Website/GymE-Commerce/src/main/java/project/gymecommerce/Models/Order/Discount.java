package project.gymecommerce.Models.Order;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/*
CREATE TABLE `discount` (
  `discount_id`   BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng khuyến mãi (UUID v4 lưu dạng BINARY(16))',
  `discount_code` VARCHAR(50)  NOT NULL COMMENT 'Mã giảm giá hiển thị cho người dùng',
  `discount_type` ENUM('PERCENT','FIXED_AMOUNT') NOT NULL COMMENT 'Loại giảm giá: PERCENT theo phần trăm, FIXED_AMOUNT theo số tiền cố định',
  `description`   NVARCHAR(255) NULL COMMENT 'Mô tả ngắn về chương trình khuyến mãi',
  `start_at`      DATETIME     NOT NULL COMMENT 'Thời điểm bắt đầu áp dụng khuyến mãi',
  `end_at`        DATETIME     NOT NULL COMMENT 'Thời điểm kết thúc khuyến mãi',
  `quantity`      INT UNSIGNED NULL COMMENT 'Số lượng mã được phát hành hoặc số lượt sử dụng (NULL nếu không giới hạn)',
  `is_available`  TINYINT(1)   NOT NULL DEFAULT 1 COMMENT 'Trạng thái còn hiệu lực (1: còn áp dụng, 0: tắt)',

  PRIMARY KEY (`discount_id`),
  UNIQUE KEY `uk_discount_code` (`discount_code`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin các chương trình khuyến mãi và mã giảm giá';
 */
@Entity
@Table(
        name = "discount",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_discount_code", columnNames = {"discount_code"})
        }
)
public class Discount {

    public enum DiscountType {
        PERCENT, FIXED_AMOUNT
    }

    /**
     * Khóa chính của thực thể Discount, lưu dưới dạng UUID (Binary(16) trong DB).
     * - Ý nghĩa: định danh duy nhất cho một chương trình khuyến mãi.
     * - Lưu ý: sử dụng GenerationType.UUID để Hibernate tự sinh UUID (UUID v4) khi persist.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "discount_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID discountId;

    /**
     * Mã giảm giá hiển thị cho người dùng (ví dụ: SPRING2025).
     * - Ý nghĩa: chuỗi dùng để áp mã khi checkout.
     * - Lưu ý: phải là duy nhất trong bảng (unique); tối đa 50 ký tự.
     */
    @Column(name = "discount_code", nullable = false, unique = true, length = 50)
    private String discountCode;

    /**
     * Loại khuyến mãi xác định cách tính giá trị giảm:
     * - PERCENT: giảm theo phần trăm của tổng đơn hàng hoặc mặt hàng.
     * - FIXED_AMOUNT: giảm theo một số tiền cố định.
     * - Lưu ý: lưu enum dưới dạng STRING để bảo toàn giá trị khi enum thay đổi thứ tự.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    /**
     * Mô tả ngắn về chương trình khuyến mãi.
     * - Ý nghĩa: thông tin hiển thị cho đội marketing hoặc admin (không ảnh hưởng logic áp dụng).
     * - Lưu ý: có thể null; dùng NVARCHAR để hỗ trợ Unicode.
     */
    @Column(name = "description", nullable = true)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String description;

    /**
     * Thời điểm bắt đầu khuyến mãi (ngày giờ hệ thống).
     * - Ý nghĩa: chỉ các đơn hàng có thời gian >= startAt mới đủ điều kiện áp dụng.
     * - Lưu ý: nếu null trước khi persist thì @PrePersist sẽ đặt mặc định là thời điểm hiện tại.
     */
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    /**
     * Thời điểm kết thúc khuyến mãi (ngày giờ hệ thống).
     * - Ý nghĩa: chương trình chỉ áp dụng cho đơn hàng có thời gian < endAt (tuỳ quy ước).
     * - Lưu ý: phải đảm bảo endAt >= startAt; nếu null sẽ được đặt bằng startAt trong @PrePersist.
     */
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    /**
     * Số lượng mã có thể sử dụng (NULL nghĩa không giới hạn).
     * - Ý nghĩa: dùng để kiểm soát số lần phát/áp mã (inventory của coupon).
     * - Lưu ý: giá trị âm nên được cấm ở tầng service/validate; trong DB có thể để NULL để biểu thị không giới hạn.
     */
    @Column(name = "quantity", nullable = true)
    private Integer quantity;

    /**
     * Cờ trạng thái cho biết mã hiện còn hiệu lực hay đã tắt.
     * - Ý nghĩa: true = có thể sử dụng; false = đã vô hiệu.
     * - Lưu ý: mặc định true; kiểu Boolean tiện cho mapping, lưu trong DB là TINYINT.
     */
    @Column(name = "is_available", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean isAvailable = true;

    /**
     * Bộ các đơn hàng đã áp dụng khuyến mãi này.
     * - Ý nghĩa: quan hệ nhiều-nhiều giữa Discount và Order, mappedBy phía Order.
     * - Lưu ý: fetch LAZY để tránh n+1; duy trì LinkedHashSet để giữ thứ tự chèn (nếu cần).
     */
    @ManyToMany(mappedBy = "discounts", fetch = FetchType.LAZY)
    private Set<Order> orders = new LinkedHashSet<>();

    // --- Constructors ---
    public Discount() {
    }

    /**
     * Constructor tiện lợi.
     *
     * @param discountCode mã giảm giá
     * @param discountType loại giảm giá
     * @param description  mô tả (nullable)
     * @param quantity     số lượng (nullable -> không giới hạn)
     * @param endAfterMonths số tháng hiệu lực kể từ now (nếu <=0 thì không set endAt)
     */
    public Discount(String discountCode, DiscountType discountType, String description, Integer quantity, int endAfterMonths) {
        this.discountCode = discountCode;
        this.discountType = discountType;
        this.description = description;
        this.quantity = quantity;
        this.isAvailable = true;
        this.startAt = LocalDateTime.now();
        this.endAt = endAfterMonths > 0 ? LocalDateTime.now().plusMonths(endAfterMonths) : LocalDateTime.now();
    }

    // --- Lifecycle callbacks để set mặc định nếu cần ---
    @PrePersist
    protected void prePersist() {
        // Nếu các trường thời gian hoặc trạng thái chưa được đặt trước khi lưu lần đầu,
        // phương thức này sẽ đảm bảo giá trị mặc định hợp lý được gán.
        // - isAvailable: mặc định true nếu null.
        // - startAt: đặt bằng thời điểm hiện tại nếu null.
        // - endAt: nếu chưa được set, sẽ dùng startAt để tránh giá trị null trong DB.
        if (this.isAvailable == null) this.isAvailable = true;
        if (this.startAt == null) this.startAt = LocalDateTime.now();
        if (this.endAt == null) this.endAt = this.startAt;
    }

    // --- Helper methods để duy trì quan hệ hai chiều với Order ---
    /**
     * Thêm một Order vào tập orders của Discount đồng thời đảm bảo mối quan hệ hai chiều được duy trì.
     * - Hành vi: nếu order null thì không làm gì. Nếu order chưa chứa discount này thì sẽ thêm vào
     *   collection discounts của order để hai phía nhất quán.
     * - Lưu ý: không thực hiện persist/remove trên entity khác ở đây; chỉ duy trì collection tại cấp bộ nhớ.
     */
    public void addOrder(Order order) {
        if (order == null) return;
        orders.add(order);
        if (!order.getDiscounts().contains(this)) {
            order.getDiscounts().add(this);
        }
    }

    /**
     * Loại bỏ một Order khỏi tập orders và đồng bộ trạng thái ở phía Order.
     * - Hành vi: nếu order null thì không làm gì. Sau khi xóa khỏi collection của discount,
     *   cũng sẽ xóa discount khỏi collection discounts của order để giữ nhất quán hai chiều.
     */
    public void removeOrder(Order order) {
        if (order == null) return;
        orders.remove(order);
        order.getDiscounts().remove(this);
    }

    // --- Getters & Setters ---
    public UUID getDiscountId() {
        return discountId;
    }

    public void setDiscountId(UUID discountId) {
        this.discountId = discountId;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    // --- equals & hashCode ---
    /**
     * equals được định nghĩa dựa trên discountCode vì đây là thuộc tính duy nhất có ràng buộc unique
     * và phù hợp để so sánh danh tính nghiệp vụ trong nhiều trường hợp (ví dụ: kiểm tra trùng mã).
     * - Lưu ý: nếu discountCode có thể thay đổi sau khi entity được dùng trong các collection hash-based,
     *   sẽ gây ra vấn đề; tốt nhất giữ discountCode bất biến sau khi tạo.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Discount)) return false;
        Discount discount = (Discount) o;
        return Objects.equals(discountCode, discount.discountCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discountCode);
    }

    // --- toString ---
    /**
     * toString trả về chuỗi tóm tắt các trường quan trọng của Discount.
     * - Mục đích: hỗ trợ debug/logging; không nên in thông tin nhạy cảm.
     * - Lưu ý: tránh in toàn bộ collection để không gây recursive hoặc quá dài; ở đây chỉ in số lượng orders.
     */
    @Override
    public String toString() {
        return "Discount{" +
                "discountId=" + discountId +
                ", discountCode='" + discountCode + '\'' +
                ", discountType=" + discountType +
                ", description='" + description + '\'' +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                ", quantity=" + quantity +
                ", isAvailable=" + isAvailable +
                ", orderCount=" + (orders == null ? 0 : orders.size()) +
                '}';
    }
}
