package project.gymecommerce.Models.Product;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/*
CREATE TABLE `category` (
  `category_id`   BINARY(16)    NOT NULL COMMENT 'Khóa chính của bảng danh mục (UUID v4 lưu dạng BINARY(16))',
  `category_name` NVARCHAR(100)  NOT NULL COMMENT 'Tên danh mục sản phẩm',

  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uk_category_name` (`category_name`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin danh mục phân loại sản phẩm';
 */

@Entity
@Table(
        name = "category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_name", columnNames = {"category_name"})
        }
)
public class Category {

    /**
     * Khóa chính category_id, lưu dưới dạng UUID (Binary(16) trong DB).
     * - Ý nghĩa: định danh duy nhất cho một danh mục sản phẩm.
     * - Lưu ý: Hibernate sinh UUID tự động khi persist nếu dùng GenerationType.UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID categoryId;

    /**
     * Tên danh mục sản phẩm (Unicode).
     * - Ý nghĩa: giá trị hiển thị và business key để nhận diện danh mục.
     * - Lưu ý: phải là duy nhất trong bảng; hỗ trợ Unicode bằng NVARCHAR; giới hạn 100 ký tự.
     */
    @Column(name = "category_name", nullable = false, length = 100)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String categoryName;

    /**
     * Tập các sản phẩm thuộc danh mục này.
     * - Ý nghĩa: quan hệ nhiều-nhiều giữa Category và Product thông qua bảng trung gian product_category.
     * - Lưu ý: Fetch LAZY để tránh tải collection không cần thiết; cascade hạn chế để tránh tác động không mong muốn khi sửa Category.
     */
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "product_category",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products = new LinkedHashSet<>();

    // --- Constructors ---
    public Category() {
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    // --- Helper methods để duy trì quan hệ hai chiều ---
    /**
     * Thêm một Product vào tập products của Category và đảm bảo quan hệ hai chiều được duy trì.
     * - Hành vi: nếu product null thì không làm gì; thêm vào collection products và nếu cần
     *   thì thêm Category vào tập categories của Product để đồng bộ hai phía.
     * - Lưu ý: phương thức chỉ cập nhật trạng thái ở bộ nhớ; việc persist/flush được quản lý bởi service/DAO.
     */
    public void addProduct(Product product) {
        if (product == null) return;
        products.add(product);
        if (!product.getCategories().contains(this)) {
            product.getCategories().add(this);
        }
    }

    /**
     * Loại bỏ một Product khỏi Category và đồng bộ phía Product.
     * - Hành vi: nếu product null thì bỏ qua; xóa khỏi collection products và xóa Category khỏi collection categories của Product.
     * - Lưu ý: không thực hiện xóa bản ghi Product trong DB; chỉ cập nhật quan hệ trong bộ nhớ.
     */
    public void removeProduct(Product product) {
        if (product == null) return;
        products.remove(product);
        product.getCategories().remove(this);
    }

    // --- Getters & Setters ---
    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    // --- equals & hashCode (dùng categoryName làm business key) ---
    /**
     * equals/hashCode dựa trên categoryName vì đây là business key và có ràng buộc unique.
     * - Lưu ý: nếu categoryName có khả năng thay đổi sau khi entity được dùng trong các collection hash-based,
     *   điều này có thể gây lỗi; cân nhắc giữ categoryName ổn định hoặc dùng categoryId cho equals/hashCode.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return Objects.equals(categoryName, category.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryName);
    }

    // --- toString (không in toàn bộ products) ---
    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", productCount=" + (products == null ? 0 : products.size()) +
                '}';
    }
}
