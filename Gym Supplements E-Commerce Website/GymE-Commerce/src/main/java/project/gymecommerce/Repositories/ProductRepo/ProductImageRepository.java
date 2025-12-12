package project.gymecommerce.Repositories.ProductRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gymecommerce.Models.Product.ProductImage;

import java.util.UUID;

@Repository
public interface ProductImageRepository extends JpaRepository <ProductImage, UUID>{
}
