package project.gymecommerce.Models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
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
@Table(name = "brand")
public class Brand {

    //Các thuộc tính của model: Brand
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "brand_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID brand_id;

    @Column(name = "brand_name", nullable = false, unique = true, length = 100)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String brand_name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "product_brand",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> products = new LinkedHashSet<>();

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public UUID getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(UUID brand_id) {
        this.brand_id = brand_id;
    }

}