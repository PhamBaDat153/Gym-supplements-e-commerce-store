package project.gymecommerce.Models.Product;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.gymecommerce.Models.User.UserAccount;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/*
CREATE TABLE `product_review` (
  `product_review_id` BINARY(16)  NOT NULL COMMENT 'Khóa chính của bảng đánh giá sản phẩm (UUID v4 lưu dạng BINARY(16))',
  `product_id`        BINARY(16)  NOT NULL COMMENT 'Khóa ngoại tới bảng product',
  `user_account_id`   BINARY(16)  NOT NULL COMMENT 'Khóa ngoại tới bảng user_account, người thực hiện đánh giá',
  `rating`            TINYINT UNSIGNED NOT NULL COMMENT 'Điểm đánh giá sản phẩm, thường trong khoảng 1 đến 5',
  `comment`           NVARCHAR(1000)         NULL COMMENT 'Nội dung nhận xét của người dùng',
  `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm người dùng gửi đánh giá',

  PRIMARY KEY (`product_review_id`),
  KEY `idx_product_review_product` (`product_id`),
  KEY `idx_product_review_user` (`user_account_id`),
  CONSTRAINT `fk_product_review_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `product`(`product_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_product_review_user`
    FOREIGN KEY (`user_account_id`)
    REFERENCES `user_account`(`user_account_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = 'Lưu đánh giá và nhận xét của người dùng cho từng sản phẩm';
 */
@Entity
@Table(name = "product_review")
public class ProductReview {

    /**
     * Khóa chính product_review_id, lưu dưới dạng UUID (Binary(16) trong DB).
     * - Ý nghĩa: định danh duy nhất cho một đánh giá sản phẩm.
     * - Lưu ý: Hibernate sinh UUID tự động khi persist nếu dùng GenerationType.UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_review_id", nullable = false)
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID productReviewId;

    /**
     * Sản phẩm được đánh giá (Product).
     * - Ý nghĩa: tham chiếu tới sản phẩm mà người dùng đánh giá.
     * - Lưu ý: ManyToOne, không cascade REMOVE để tránh vô ý xóa Product khi xóa review; optional = false.
     */
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Người dùng thực hiện đánh giá (UserAccount).
     * - Ý nghĩa: liên kết tới tác giả của review để hiển thị và kiểm soát quyền.
     * - Lưu ý: ManyToOne, không cascade REMOVE.
     */
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH},
            optional = false)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    /**
     * Điểm đánh giá sản phẩm, trong khoảng 1-5.
     * - Ý nghĩa: dùng để tính điểm trung bình sản phẩm, xếp hạng, hiển thị sao.
     * - Lưu ý: kết hợp Bean Validation (@NotNull, @Min, @Max) để đảm bảo giá trị hợp lệ.
     */
    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    @JdbcTypeCode(SqlTypes.TINYINT)
    private Integer rating;

    /**
     * Nội dung nhận xét (có thể null).
     * - Ý nghĩa: chứa bình luận chi tiết của người dùng về sản phẩm.
     * - Lưu ý: giới hạn chiều dài để tránh lưu trữ quá lớn; có thể sanitize trước khi lưu.
     */
    @Column(name = "comment", length = 1000)
    @JdbcTypeCode(SqlTypes.NVARCHAR)
    private String comment;

    /**
     * Thời điểm người dùng gửi đánh giá.
     * - Ý nghĩa: dùng cho audit và sắp xếp đánh giá theo thời gian.
     * - Lưu ý: được tự động khởi tạo trong @PrePersist nếu chưa có.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // --- Constructors ---
    public ProductReview() { }

    public ProductReview(Product product, UserAccount userAccount, Integer rating, String comment) {
        this.product = product;
        this.userAccount = userAccount;
        this.rating = rating;
        this.comment = comment;
    }

    // --- Lifecycle callbacks ---
    @PrePersist
    protected void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.rating == null) {
            this.rating = 5; // mặc định nếu không cung cấp (có thể thay đổi theo chính sách)
        }
    }

    // --- Getters & Setters ---
    public UUID getProductReviewId() {
        return productReviewId;
    }

    public void setProductReviewId(UUID productReviewId) {
        this.productReviewId = productReviewId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        if (rating == null) {
            this.rating = null;
            return;
        }
        if (rating < 1) this.rating = 1;
        else if (rating > 5) this.rating = 5;
        else this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // --- equals & hashCode ---
    /**
     * equals/hashCode dựa trên productReviewId nếu đã persist; nếu null thì fallback identity.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductReview)) return false;
        ProductReview that = (ProductReview) o;
        return productReviewId != null && Objects.equals(productReviewId, that.productReviewId);
    }

    @Override
    public int hashCode() {
        return productReviewId != null ? Objects.hash(productReviewId) : System.identityHashCode(this);
    }

    // --- toString (an toàn, không in toàn bộ product/user) ---
    @Override
    public String toString() {
        return "ProductReview{" +
                "productReviewId=" + productReviewId +
                ", productId=" + (product == null ? null : product.getProductId()) +
                ", userAccountId=" + (userAccount == null ? null : userAccount.getUserAccountId()) +
                ", rating=" + rating +
                ", comment='" + (comment == null ? "" : (comment.length() > 100 ? comment.substring(0, 100) + "..." : comment)) + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}