package project.gymecommerce.Repositories.UserRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gymecommerce.Models.User.UserAddress;

import java.util.UUID;

@Repository
public interface UserAddressRepository extends JpaRepository <UserAddress, UUID>{
}
