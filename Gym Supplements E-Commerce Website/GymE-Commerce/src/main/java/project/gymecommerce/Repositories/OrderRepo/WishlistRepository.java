package project.gymecommerce.Repositories.OrderRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gymecommerce.Models.Order.Wishlist;

import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository <Wishlist, UUID>{
}
