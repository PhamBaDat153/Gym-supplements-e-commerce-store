package project.gymecommerce.Models.Product;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/*
CREATE TABLE `brand` (
  `brand_id`   BINARY(16)    NOT NULL COMMENT 'Khóa chính của bảng thương hiệu (UUID v4 lưu dạng BINARY(16))',
  `brand_name` NVARCHAR(100)  NOT NULL COMMENT 'Tên thương hiệu (ví dụ: Nike, Adidas)',

  PRIMARY KEY (`brand_id`),
  UNIQUE KEY `uk_brand_name` (`brand_name`)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu thông tin các thương hiệu sản phẩm';
 */

@Entity
@Table(
        name = "brand",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_brand_name", columnNames = {"brand_name"})
        }
)
public class Brand {

    /**
     * Khóa chính brand_id lưu dạng BINARY(16) (UUID v4).
     * - Ý nghĩa: định danh duy nhất cho một thương hiệu trong hệ thống.
     * - Lưu ý: Hibernate sẽ sinh UUID tự động khi persist nếu dùng GenerationType.UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "brand_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID brandId;

    /**
     * Tên thương hiệu (ví dụ: Nike, Adidas).
     * - Ý nghĩa: giá trị hiển thị và là business key để nhận diện thương hiệu.
     * - Lưu ý: phải là duy nhất (unique) trong bảng; hỗ trợ Unicode bằng NVARCHAR; giới hạn 100 ký tự.
     */
    @Column(name = "brand_name", nullable = false, unique = true, length = 100)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String brandName;

    /**
     * Tập các sản phẩm thuộc thương hiệu này.
     * - Ý nghĩa: quan hệ nhiều-nhiều giữa Brand và Product thông qua bảng trung gian product_brand.
     * - Lưu ý: Fetch LAZY để tránh tải collection không cần thiết; cascade hạn chế để tránh tác động không mong muốn
     *   khi sửa Brand.
     */
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "product_brand",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products = new LinkedHashSet<>();

    // --- Constructors ---
    public Brand() {
    }

    public Brand(String brandName) {
        this.brandName = brandName;
    }

    // --- Helper methods để duy trì quan hệ hai chiều ---
    /**
     * Thêm một Product vào tập products của Brand và đảm bảo quan hệ hai chiều được duy trì.
     * - Hành vi: nếu product null thì không làm gì; thêm vào collection products và nếu cần
     *   thì thêm Brand vào tập brands của Product để đồng bộ hai phía.
     * - Lưu ý: phương thức chỉ cập nhật trạng thái ở bộ nhớ; việc persist/flush được quản lý bởi service/DAO.
     */
    public void addProduct(Product product) {
        if (product == null) return;
        products.add(product);
        if (!product.getBrands().contains(this)) {
            product.getBrands().add(this);
        }
    }

    /**
     * Loại bỏ một Product khỏi Brand và đồng bộ phía Product.
     * - Hành vi: nếu product null thì bỏ qua; xóa khỏi collection products và xóa Brand khỏi collection brands của Product.
     * - Lưu ý: không thực hiện xóa bản ghi Product trong DB; chỉ cập nhật quan hệ trong bộ nhớ.
     */
    public void removeProduct(Product product) {
        if (product == null) return;
        products.remove(product);
        product.getBrands().remove(this);
    }

    // --- Getters & Setters ---
    public UUID getBrandId() {
        return brandId;
    }

    public void setBrandId(UUID brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    // --- equals & hashCode (dùng brandName làm business key) ---
    /**
     * equals/hashCode dựa trên brandName vì đây là business key và có ràng buộc unique.
     * - Lưu ý: nếu brandName có khả năng thay đổi sau khi entity được dùng trong các collection hash-based,
     *   điều này có thể gây lỗi; cân nhắc giữ brandName ổn định hoặc dùng brandId cho equals/hashCode.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Brand)) return false;
        Brand brand = (Brand) o;
        return Objects.equals(brandName, brand.brandName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brandName);
    }

    // --- toString (không in toàn bộ products để tránh recursion / log lớn) ---
    @Override
    public String toString() {
        return "Brand{" +
                "brandId=" + brandId +
                ", brandName='" + brandName + '\'' +
                ", productCount=" + (products == null ? 0 : products.size()) +
                '}';
    }
}
