package project.gymecommerce.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
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
@Table(name = "discount")
public class Discount {

    public enum DiscountType {
        PERCENT, FIXED_AMOUNT
    }

    //Các thuộc tính của model: Discount
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "discount_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID discount_id;

    @Column(name = "discount_code", nullable = false, unique = true, length = 50)
    private String discount_code;

    @Enumerated
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "description", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String description;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime start_at;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime end_at;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "is_available")
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean is_available;

    public Boolean getIs_available() {
        return is_available;
    }

    public void setIs_available(Boolean is_available) {
        this.is_available = is_available;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getEnd_at() {
        return end_at;
    }

    public void setEnd_at(LocalDateTime end_at) {
        this.end_at = end_at;
    }

    public LocalDateTime getStart_at() {
        return start_at;
    }

    public void setStart_at(LocalDateTime start_at) {
        this.start_at = start_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public String getDiscount_code() {
        return discount_code;
    }

    public void setDiscount_code(String discount_code) {
        this.discount_code = discount_code;
    }

    public UUID getDiscount_id() {
        return discount_id;
    }

    public void setDiscount_id(UUID discount_id) {
        this.discount_id = discount_id;
    }

}