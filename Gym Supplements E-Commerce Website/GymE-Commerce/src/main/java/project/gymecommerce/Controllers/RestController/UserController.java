package project.gymecommerce.Controllers.RestController;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.gymecommerce.Models.User.UserAccount;
import project.gymecommerce.Services.UserAccountServiceImplement;

import java.util.Set;

@RestController
public class UserController {

    private final UserAccountServiceImplement userAccountService;

    @Autowired
    public UserController(UserAccountServiceImplement userAccountService) {
        this.userAccountService = userAccountService;
    }

    // lấy toàn bộ userAccount
    @GetMapping("/users")
    public ResponseEntity<Set<UserAccount>> getAllUserAccounts() {
        return ResponseEntity.ok(userAccountService.findAll());
    }

    //Tạo user mới
    @PostMapping("user")
    public void createUserAccount(UserAccount userAccount){
        userAccountService.save(userAccount);
    }

}
