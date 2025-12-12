package project.gymecommerce.Repositories.OrderRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import project.gymecommerce.Models.Order.ShippingUnit;

import java.util.UUID;

public interface ShippingUnitRepository extends JpaRepository <ShippingUnit, UUID>{
}
