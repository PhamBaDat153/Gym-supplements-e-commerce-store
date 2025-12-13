package project.gymecommerce.Repositories.UserRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gymecommerce.Models.User.Role;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository <Role, UUID>{
    Optional<Object> findByRoleName(String roleCustomer);
}
