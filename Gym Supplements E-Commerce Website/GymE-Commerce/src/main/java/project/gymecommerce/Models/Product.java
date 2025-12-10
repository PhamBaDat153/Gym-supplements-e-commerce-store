package project.gymecommerce.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
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

    //các thuộc tính cửa model: Product
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID product_id;

    @Column(name = "product_name", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String product_name;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    @JdbcTypeCode(SqlTypes.DECIMAL)
    private Double price;

    @Column(name = "is_available", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Boolean is_available = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_id", nullable = false)
    private Set<Product_image> product_images = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "products", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<Brand> brands = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "products", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private Set<Category> categories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product_review> product_reviews = new LinkedHashSet<>();

    public Set<Product_review> getProduct_reviews() {
        return product_reviews;
    }

    public void setProduct_reviews(Set<Product_review> product_reviews) {
        this.product_reviews = product_reviews;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Set<Brand> getBrands() {
        return brands;
    }

    public void setBrands(Set<Brand> brands) {
        this.brands = brands;
    }

    public Set<Product_image> getProduct_images() {
        return product_images;
    }

    public void setProduct_images(Set<Product_image> product_images) {
        this.product_images = product_images;
    }

    public Boolean getIs_available() {
        return is_available;
    }

    public void setIs_available(Boolean is_available) {
        this.is_available = is_available;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public UUID getProduct_id() {
        return product_id;
    }

    public void setProduct_id(UUID product_id) {
        this.product_id = product_id;
    }
}