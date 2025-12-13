package project.gymecommerce.Repositories.UserRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gymecommerce.Models.User.UserAccount;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {


    boolean existsByEmail(String email);

    Optional<UserAccount> findByPhoneNumber(String phoneNumber);

    Optional<UserAccount> findByUserName(String username);

    boolean existsByUserName(String userName);

    boolean existsByPhoneNumber(String phoneNumber);
}
