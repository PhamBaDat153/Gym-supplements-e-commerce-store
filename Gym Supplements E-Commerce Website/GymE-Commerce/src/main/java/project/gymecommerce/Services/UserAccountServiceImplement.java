package project.gymecommerce.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.gymecommerce.Models.User.Role;
import project.gymecommerce.Models.User.UserAccount;
import project.gymecommerce.Repositories.UserRepo.RoleRepository;
import project.gymecommerce.Repositories.UserRepo.UserAccountRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserAccountServiceImplement implements UserAccountService{

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(UserAccountServiceImplement.class);

    @Autowired
    public UserAccountServiceImplement(UserAccountRepository userAccountRepository,
                                       RoleRepository roleRepository,
                                       PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Tìm tài khoản bằng ID
    @Override
    public Optional<UserAccount> findById(UUID id) {
        log.info("Đang tìm tài khoản với ID: {} tại thời điểm: {}", id, LocalDateTime.now());
        Optional<UserAccount> userAccount = userAccountRepository.findById(id);
        if (userAccount.isEmpty()) {
            log.warn("Không tìm thấy tài khoản với ID: {}", id);
        } else {
            log.info("Đã tìm thấy tài khoản với ID: {} tại thời điểm: {}", id, LocalDateTime.now());
        }
        return userAccount;
    }

    // Tìm tài khoản bằng username (giả sử username là userName trong model)
    @Override
    public Optional<UserAccount> findByUsername(String username) {
        log.info("Đang tìm tài khoản với username: {} tại thời điểm: {}", username, LocalDateTime.now());
        Optional<UserAccount> userAccount = userAccountRepository.findByUserName(username);
        if (userAccount.isEmpty()) {
            log.warn("Không tìm thấy tài khoản với username: {}", username);
        } else {
            log.info("Đã tìm thấy tài khoản với username: {} tại thời điểm: {}", username, LocalDateTime.now());
        }
        return userAccount;
    }

    // Tìm tài khoản bằng số điện thoại
    @Override
    public Optional<UserAccount> findByPhoneNumber(String phoneNumber) {
        log.info("Đang tìm tài khoản với số điện thoại: {} tại thời điểm: {}", phoneNumber, LocalDateTime.now());
        Optional<UserAccount> userAccount = userAccountRepository.findByPhoneNumber(phoneNumber);
        if (userAccount.isEmpty()) {
            log.warn("Không tìm thấy tài khoản với số điện thoại: {}", phoneNumber);
        } else {
            log.info("Đã tìm thấy tài khoản với số điện thoại: {} tại thời điểm: {}", phoneNumber, LocalDateTime.now());
        }
        return userAccount;
    }

    // Trả về toàn bộ tài khoản
    @Override
    public Set<UserAccount> findAll() {
        log.info("Đang thực hiện lấy toàn bộ danh sách tài khoản tại thời điểm: " + LocalDateTime.now());
        Iterable<UserAccount> iterable = userAccountRepository.findAll();
        Set<UserAccount> userAccounts = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toUnmodifiableSet());
        if (userAccounts.isEmpty()) {
            log.error("Danh sách tài khoản trống.");
            throw new RuntimeException("Empty userAccounts List");
        }

        log.info("Đã lấy danh sách tài khoản thành công vào lúc: " + LocalDateTime.now());
        return userAccounts;
    }

    @Override
    public void save(UserAccount userAccount) {

    }

    // Phương thức đăng ký tài khoản mới và tự động gán vai trò "ROLE_CUSTOMER"
    @Override
    public UserAccount register(UserAccount userAccount) {
        log.info("Đang thực hiện đăng ký tài khoản mới vào lúc: " + LocalDateTime.now());

        // Kiểm tra xem email đã tồn tại chưa
        if (userAccountRepository.existsByEmail(userAccount.getEmail())) {
            log.error("Email đã tồn tại: " + userAccount.getEmail());
            throw new RuntimeException("Email already exists");
        }

        // Kiểm tra xem tên tài khoản đã tồn tại chưa
        if (userAccountRepository.existsByUserName(userAccount.getUserName())) {
            log.error("Tên tài khoản đã tồn tại: " + userAccount.getUserName());
            throw new RuntimeException("UserName already exists");
        }

        // Kiểm tra xem tên số điện thoại đã tồn tại chưa
        if (userAccountRepository.existsByPhoneNumber(userAccount.getPhoneNumber())) {
            log.error("Tên số điện thoại đã tồn tại: " + userAccount.getPhoneNumber());
            throw new RuntimeException("PhoneNumber already exists");
        }


            // Hash mật khẩu
        userAccount.setHashedPassword(passwordEncoder.encode(userAccount.getHashedPassword()));

        // Tìm vai trò "ROLE_CUSTOMER"
        Role customerRole = (Role) roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Customer role not found"));

        // Gán vai trò cho tài khoản
        userAccount.getRoles().add(customerRole);

        // Lưu tài khoản mới
        UserAccount savedUser = userAccountRepository.save(userAccount);

        log.info("Đã đăng ký tài khoản thành công cho email: " + userAccount.getEmail() + " vào lúc: " + LocalDateTime.now());
        return savedUser;
    }

    // Cập nhật tài khoản
    @Override
    public void update(UserAccount userAccount, String idString) {
        UUID id = UUID.fromString(idString);
        log.info("Đang cập nhật tài khoản với ID: {} tại thời điểm: {}", id, LocalDateTime.now());

        Optional<UserAccount> existingOpt = userAccountRepository.findById(id);
        if (existingOpt.isEmpty()) {
            log.error("Không tìm thấy tài khoản với ID: {}", id);
            throw new RuntimeException("User not found");
        }

        UserAccount existing = existingOpt.get();

        // Cập nhật các trường nếu được cung cấp
        if (userAccount.getUserName() != null) {
            existing.setUserName(userAccount.getUserName());
        }
        if (userAccount.getHashedPassword() != null) {
            existing.setHashedPassword(passwordEncoder.encode(userAccount.getHashedPassword()));
        }
        if (userAccount.getEmail() != null) {
            if (userAccountRepository.existsByEmail(userAccount.getEmail()) && !userAccount.getEmail().equals(existing.getEmail())) {
                log.error("Email đã tồn tại: {}", userAccount.getEmail());
                throw new RuntimeException("Email already exists");
            }
            existing.setEmail(userAccount.getEmail());
        }
        if (userAccount.getPhoneNumber() != null) {
            existing.setPhoneNumber(userAccount.getPhoneNumber());
        }
        if (userAccount.getIsActive() != null) {
            existing.setIsActive(userAccount.getIsActive());
        }
        // Có thể thêm cập nhật roles nếu cần, nhưng giả sử không cập nhật roles ở đây

        userAccountRepository.save(existing);
        log.info("Đã cập nhật tài khoản với ID: {} thành công tại thời điểm: {}", id, LocalDateTime.now());
    }

    // Xóa tài khoản bằng ID
    @Override
    public void delete(UUID id) {
        log.info("Đang xóa tài khoản với ID: {} tại thời điểm: {}", id, LocalDateTime.now());

        if (!userAccountRepository.existsById(id)) {
            log.error("Không tìm thấy tài khoản với ID: {}", id);
            throw new RuntimeException("User not found");
        }

        userAccountRepository.deleteById(id);
        log.info("Đã xóa tài khoản với ID: {} thành công tại thời điểm: {}", id, LocalDateTime.now());
    }
}