package project.gymecommerce.Models.Product;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/*
CREATE TABLE `product` (
  `product_id`   BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng sản phẩm (UUID v4 lưu dạng BINARY(16))',
  `product_name` NVARCHAR(255) NOT NULL COMMENT 'Tên sản phẩm',
  `description`  TEXT         NULL COMMENT 'Mô tả chi tiết về sản phẩm',
  `quantity`     INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Số lượng tồn kho hiện tại',
  `price`        DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'Giá niêm yết của sản phẩm',
  `is_available` TINYINT(1)   NOT NULL DEFAULT 1 COMMENT 'Trạng thái hiển thị sản phẩm (1: đang bán, 0: ẩn)',

  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin chi tiết về sản phẩm';
 */

@Entity
@Table(name = "product")
public class Product {

    /**
     * Khóa chính product_id, lưu dưới dạng UUID (Binary(16) trong DB).
     * - Ý nghĩa: định danh duy nhất cho một sản phẩm.
     * - Lưu ý: Hibernate sinh UUID tự động khi persist nếu dùng GenerationType.UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID productId;

    /**
     * Tên sản phẩm (Unicode).
     * - Ý nghĩa: giá trị hiển thị cho UI và tìm kiếm.
     * - Lưu ý: giới hạn 255 ký tự.
     */
    @Column(name = "product_name", nullable = false, length = 255)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String productName;

    /**
     * Mô tả chi tiết sản phẩm. Cho phép null.
     * - Ý nghĩa: chứa thông tin dài về đặc tính, hướng dẫn sử dụng, v.v.
     * - Lưu ý: dùng NVARCHAR để hỗ trợ Unicode; giới hạn chiều dài tuỳ implementation.
     */
    @Column(name = "description", nullable = true, length = 2000)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String description;

    /**
     * Số lượng tồn kho hiện tại (INT UNSIGNED).
     * - Ý nghĩa: dùng để kiểm soát việc hiển thị và cho phép đặt hàng.
     * - Lưu ý: giá trị mặc định 0; setter/prePersist đảm bảo không null.
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    /**
     * Giá niêm yết của sản phẩm.
     * - Ý nghĩa: dùng cho hiển thị và làm cơ sở tính toán giá bán thực tế.
     * - Lưu ý: sử dụng BigDecimal với precision/scale phù hợp để đảm bảo chính xác tài chính.
     */
    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private BigDecimal price = BigDecimal.ZERO;

    /**
     * Cờ hiển thị sản phẩm: true = đang bán, false = ẩn.
     * - Ý nghĩa: điều khiển việc hiển thị trên storefront.
     * - Lưu ý: mặc định true.
     */
    @Column(name = "is_available", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean isAvailable = true;

    /**
     * Ảnh liên quan tới sản phẩm.
     * - Ý nghĩa: chứa các URL/metadata ảnh sản phẩm.
     * - Lưu ý: mappedBy nếu ProductImage có trường product; cascade ALL + orphanRemoval để quản lý lifecycle ảnh.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProductImage> productImages = new LinkedHashSet<>();

    /**
     * Các thương hiệu liên kết với sản phẩm (ManyToMany).
     * - Ý nghĩa: biểu diễn mối quan hệ nhiều-nhiều giữa Product và Brand.
     * - Lưu ý: mappedBy trỏ tới thuộc tính 'products' trong Brand.
     */
    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<Brand> brands = new LinkedHashSet<>();

    /**
     * Các danh mục liên kết với sản phẩm (ManyToMany).
     * - Ý nghĩa: cho phép phân loại sản phẩm theo nhiều danh mục.
     * - Lưu ý: mappedBy trỏ tới thuộc tính 'products' trong Category.
     */
    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<Category> categories = new LinkedHashSet<>();

    /**
     * Các đánh giá liên quan tới sản phẩm.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProductReview> productReviews = new LinkedHashSet<>();

    /**
     * Thời điểm tạo và cập nhật để hỗ trợ audit đơn giản.
     * - Ý nghĩa: dùng cho báo cáo, đồng bộ cache, và xác định lịch sử thay đổi.
     * - Lưu ý: được quản lý tự động qua @PrePersist và @PreUpdate.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- Constructors ---
    public Product() {
    }

    public Product(String productName,
                   String description,
                   Integer quantity,
                   BigDecimal price) {
        this.productName = productName;
        this.description = description;
        this.quantity = quantity == null ? 0 : quantity;
        this.price = price == null ? BigDecimal.ZERO : price;
        this.isAvailable = true;
    }

    // --- Lifecycle callbacks ---
    @PrePersist
    protected void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        if (this.updatedAt == null) this.updatedAt = now;
        if (this.quantity == null) this.quantity = 0;
        if (this.price == null) this.price = BigDecimal.ZERO;
        if (this.isAvailable == null) this.isAvailable = true;
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Helper methods để duy trì quan hệ hai chiều ---
    public void addProductImage(ProductImage image) {
        if (image == null) return;
        productImages.add(image);
        image.setProduct(this);
    }

    public void removeProductImage(ProductImage image) {
        if (image == null) return;
        productImages.remove(image);
        image.setProduct(null);
    }

    public void addBrand(Brand brand) {
        if (brand == null) return;
        brands.add(brand);
        if (!brand.getProducts().contains(this)) {
            brand.getProducts().add(this);
        }
    }

    public void removeBrand(Brand brand) {
        if (brand == null) return;
        brands.remove(brand);
        brand.getProducts().remove(this);
    }

    public void addCategory(Category category) {
        if (category == null) return;
        categories.add(category);
        if (!category.getProducts().contains(this)) {
            category.getProducts().add(this);
        }
    }

    public void removeCategory(Category category) {
        if (category == null) return;
        categories.remove(category);
        category.getProducts().remove(this);
    }

    public void addProductReview(ProductReview review) {
        if (review == null) return;
        productReviews.add(review);
        review.setProduct(this);
    }

    public void removeProductReview(ProductReview review) {
        if (review == null) return;
        productReviews.remove(review);
        review.setProduct(null);
    }

    // --- Getters & Setters ---
    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity == null ? 0 : quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price == null ? BigDecimal.ZERO : price;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }

    public Set<ProductImage> getProductImages() {
        return productImages;
    }

    public void setProductImages(Set<ProductImage> productImages) {
        this.productImages = productImages;
    }

    public Set<Brand> getBrands() {
        return brands;
    }

    public void setBrands(Set<Brand> brands) {
        this.brands = brands;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<ProductReview> getProductReviews() {
        return productReviews;
    }

    public void setProductReviews(Set<ProductReview> productReviews) {
        this.productReviews = productReviews;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // createdAt/updatedAt thường do JPA quản lý, cung cấp setter nếu cần
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // --- equals & hashCode ---
    /**
     * equals/hashCode dựa trên productId nếu đã persist; nếu null thì fallback identity.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;
        return productId != null && Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return productId != null ? Objects.hash(productId) : System.identityHashCode(this);
    }

    // --- toString (an toàn, không in collection chi tiết) ---
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                ", productImageCount=" + (productImages == null ? 0 : productImages.size()) +
                ", brandCount=" + (brands == null ? 0 : brands.size()) +
                ", categoryCount=" + (categories == null ? 0 : categories.size()) +
                ", productReviewCount=" + (productReviews == null ? 0 : productReviews.size()) +
                '}';
    }
}
