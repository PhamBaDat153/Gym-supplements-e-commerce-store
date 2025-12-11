package project.gymecommerce.Models.Product;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/*
CREATE TABLE `product_image` (
  `product_image_id` BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng hình ảnh sản phẩm (UUID v4 lưu dạng BINARY(16))',
  `product_id`       BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng product',
  `image_url`        VARCHAR(500) NOT NULL COMMENT 'Đường dẫn ảnh sản phẩm',
  `is_default`       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT 'Ảnh đại diện chính của sản phẩm (1: đúng, 0: sai)',

  PRIMARY KEY (`product_image_id`),
  KEY `idx_product_image_product` (`product_id`),
  CONSTRAINT `fk_product_image_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `product`(`product_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu danh sách ảnh minh họa cho từng sản phẩm';
 */

@Entity
@Table(name = "product_image")
public class ProductImage {

    /**
     * Khóa chính của bảng product_image, lưu dưới dạng UUID (Binary(16) trong DB).
     * - Ý nghĩa: định danh duy nhất cho một ảnh sản phẩm.
     * - Lưu ý: Hibernate sẽ sinh UUID tự động khi persist nếu dùng GenerationType.UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_image_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID productImageId;

    /**
     * Đường dẫn (URL) của ảnh sản phẩm.
     * - Ý nghĩa: dùng để hiển thị ảnh trên UI hoặc lưu trữ tham chiếu đến CDN/storage.
     * - Lưu ý: tối đa 500 ký tự; trường bắt buộc (nullable = false).
     */
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    /**
     * Cờ đánh dấu ảnh đại diện mặc định của sản phẩm.
     * - Ý nghĩa: khi hiển thị danh sách hoặc chi tiết sản phẩm, ảnh đánh dấu default có thể được dùng làm avatar.
     * - Lưu ý: lưu ở DB dưới dạng TINYINT (0/1); mặc định false.
     */
    @Column(name = "is_default", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean isDefault = false;

    /**
     * Quan hệ nhiều hình ảnh thuộc về một sản phẩm (ManyToOne).
     * - Ý nghĩa: mỗi ảnh liên kết tới một Product thông qua khóa ngoại product_id.
     * - Lưu ý: Fetch LAZY để tránh tải Product khi chỉ cần metadata ảnh; optional = false vì ảnh phải thuộc về một product.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Constructors
    public ProductImage() {}

    public ProductImage(String imageUrl, Product product) {
        this.imageUrl = imageUrl;
        this.product = product;
        this.isDefault = false;
    }

    // Getters & Setters
    public UUID getProductImageId() {
        return productImageId;
    }

    public void setProductImageId(UUID productImageId) {
        this.productImageId = productImageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // toString
    @Override
    public String toString() {
        return "ProductImage{" +
                "productImageId=" + productImageId +
                ", imageUrl='" + imageUrl + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
