package project.gymecommerce.Repositories.OrderRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gymecommerce.Models.Order.OrderItem;

import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository <OrderItem, UUID>{
}
