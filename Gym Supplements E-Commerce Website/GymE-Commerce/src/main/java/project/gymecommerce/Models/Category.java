package project.gymecommerce.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
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
@Table(name = "category")
public class Category {

    //Các thuộc tính của model: Category
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID category_id;

    @Column(name = "category_name", nullable = false)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String category_name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "product_category",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products = new LinkedHashSet<>();

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public UUID getCategory_id() {
        return category_id;
    }

    public void setCategory_id(UUID category_id) {
        this.category_id = category_id;
    }
}