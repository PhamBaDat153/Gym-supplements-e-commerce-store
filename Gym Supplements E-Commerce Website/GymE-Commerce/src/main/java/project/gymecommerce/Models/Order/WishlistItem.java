package project.gymecommerce.Models.Order;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.Product.Product;

import java.util.Objects;
import java.util.UUID;

/*
CREATE TABLE `wishlist_item` (
  `wishlist_item_id` BINARY(16)   NOT NULL COMMENT 'Khóa chính của bảng chi tiết danh sách yêu thích (UUID v4 lưu dạng BINARY(16))',
  `wishlist_id`      BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng wishlist',
  `product_id`       BINARY(16)   NOT NULL COMMENT 'Khóa ngoại tới bảng product',
  `quantity`         INT UNSIGNED NOT NULL DEFAULT 1 COMMENT 'Số lượng mong muốn cho sản phẩm trong wishlist',

  PRIMARY KEY (`wishlist_item_id`),
  KEY `idx_wishlist_item_wishlist` (`wishlist_id`),
  KEY `idx_wishlist_item_product` (`product_id`),
  CONSTRAINT `fk_wishlist_item_wishlist`
    FOREIGN KEY (`wishlist_id`)
    REFERENCES `wishlist`(`wishlist_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_wishlist_item_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `product`(`product_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Chi tiết các sản phẩm nằm trong danh sách yêu thích của người dùng';
 */

@Entity
@Table(name = "wishlist_item")
public class WishlistItem {

    /**
     * Khóa chính của wishlist item, lưu dưới dạng UUID (Binary(16) trong DB).
     * - Ý nghĩa: định danh duy nhất cho một mục trong danh sách yêu thích.
     * - Lưu ý: Hibernate sinh UUID tự động khi persist nếu dùng GenerationType.UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "wishlist_item_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID wishlistItemId;

    /**
     * Danh sách yêu thích chứa item này (Wishlist).
     * - Ý nghĩa: quan hệ nhiều-một; một wishlist có thể chứa nhiều wishlistItem.
     * - Lưu ý: không cascade REMOVE để tránh vô ý xóa wishlist khi xóa item; quản lý ràng buộc ở tầng service.
     */
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            optional = false)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    /**
     * Sản phẩm được thêm vào wishlist.
     * - Ý nghĩa: tham chiếu tới thông tin sản phẩm (Product) để hiển thị/diễn giải trong UI.
     * - Lưu ý: không cascade REMOVE để tránh vô ý xóa bản ghi product khi thao tác wishlist item.
     */
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Số lượng mong muốn cho sản phẩm trong wishlist (mặc định = 1).
     * - Ý nghĩa: cho phép người dùng lưu số lượng dự kiến mua hoặc ưu tiên số lượng.
     * - Lưu ý: giá trị tối thiểu là 1; prePersist và setter đảm bảo ràng buộc này.
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    // --- Constructors ---
    public WishlistItem() {
    }

    /**
     * Constructor tiện lợi: khởi tạo wishlist item với wishlist, product và số lượng.
     * - Hành vi: nếu quantity null hoặc < 1 sẽ đặt mặc định bằng 1.
     * - Lưu ý: phương thức chỉ khởi tạo đối tượng ở bộ nhớ, không thực hiện persist.
     */
    public WishlistItem(Wishlist wishlist, Product product, Integer quantity) {
        this.wishlist = wishlist;
        this.product = product;
        this.quantity = (quantity == null || quantity < 1) ? 1 : quantity;
    }

    // --- Lifecycle callback để đảm bảo giá trị mặc định ---
    @PrePersist
    protected void prePersist() {
        // Trước khi persist: đảm bảo quantity >= 1.
        if (this.quantity == null || this.quantity < 1) {
            this.quantity = 1;
        }
    }

    // --- Helper methods (tuỳ chọn, dùng nếu bạn duy trì quan hệ hai chiều) ---
    /**
     * Gắn item vào một Wishlist và (tuỳ implementation) đồng bộ collection phía Wishlist.
     * - Lưu ý: nếu Wishlist duy trì collection các item, cần thêm logic để cập nhật cả hai phía
     *   nhằm giữ nhất quán bộ nhớ (hoặc xử lý tại service layer).
     */
    public void attachToWishlist(Wishlist wishlist) {
        if (wishlist == null) return;
        this.wishlist = wishlist;
        // nếu Wishlist có collection items, hãy thêm đồng bộ ở đó (service layer hoặc tại đây)
        // ví dụ: wishlist.getItems().add(this);
    }

    // --- Getters & Setters ---
    public UUID getWishlistItemId() {
        return wishlistItemId;
    }

    public void setWishlistItemId(UUID wishlistItemId) {
        this.wishlistItemId = wishlistItemId;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = (quantity == null || quantity < 1) ? 1 : quantity;
    }

    // --- equals & hashCode (dùng wishlistItemId nếu có) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WishlistItem)) return false;
        WishlistItem that = (WishlistItem) o;
        return wishlistItemId != null && Objects.equals(wishlistItemId, that.wishlistItemId);
    }

    @Override
    public int hashCode() {
        return wishlistItemId != null ? Objects.hash(wishlistItemId) : System.identityHashCode(this);
    }

    // --- toString (an toàn, không in toàn bộ wishlist/product) ---
    @Override
    public String toString() {
        return "WishlistItem{" +
                "wishlistItemId=" + wishlistItemId +
                ", wishlistId=" + (wishlist == null ? null : wishlist.getWishlistId()) +
                ", productId=" + (product == null ? null : product.getProductId()) +
                ", quantity=" + quantity +
                '}';
    }
}
