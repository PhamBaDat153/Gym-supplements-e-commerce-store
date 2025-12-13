package project.gymecommerce.Services;

import project.gymecommerce.Models.User.UserAccount;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserAccountService {

    //tìm tài khoản bằng ID
    public Optional<UserAccount> findById(UUID id);

    //tìm tài khoản bằng username
    public Optional<UserAccount> findByUsername(String username);

    //tìm tài khoản bằng số điện thoại
    public Optional<UserAccount> findByPhoneNumber(String phoneNumber);

    //trả về toàn bộ tài khoản
    public Set<UserAccount> findAll();

    //thêm tài khoản mới
    public void save(UserAccount userAccount);

    // Phương thức đăng ký tài khoản mới và tự động gán vai trò "ROLE_CUSTOMER"
    UserAccount register(UserAccount userAccount);

    //cập nhật tài khooản
    public void update(UserAccount userAccount, String UUID);

    //Xóa tài khoản bằng ID
    public void delete(UUID UUID);
}
