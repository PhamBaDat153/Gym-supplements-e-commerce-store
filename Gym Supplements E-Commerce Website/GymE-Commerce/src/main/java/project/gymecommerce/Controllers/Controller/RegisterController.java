package project.gymecommerce.Controllers.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.gymecommerce.Models.User.UserAccount;
import project.gymecommerce.Services.UserAccountServiceImplement;

@Controller
public class RegisterController {

    private final UserAccountServiceImplement userAccountService;

    @Autowired
    public RegisterController(UserAccountServiceImplement userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("userAccount")) {
            model.addAttribute("userAccount", new UserAccount());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Validated @ModelAttribute("userAccount") UserAccount userAccount,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userAccount", result);
            redirectAttributes.addFlashAttribute("userAccount", userAccount);
            return "redirect:/register";
        }

        try {
            userAccountService.register(userAccount);

            // Đăng ký thành công → quay lại trang đăng ký với tham số status=success
            // Người dùng sẽ thấy popup thành công trước khi tự động chuyển sang /login
            redirectAttributes.addAttribute("status", "success");
            return "redirect:/register";

        } catch (RuntimeException e) {
            String errorMsg;
            if (e.getMessage().equals("Email already exists")) {
                errorMsg = "Email này đã được sử dụng!";
            } else if (e.getMessage().equals("UserName already exists")) {
                errorMsg = "Tên đăng nhập đã tồn tại!";
            } else if (e.getMessage().equals("PhoneNumber already exists")) {
                errorMsg = "Số điện thoại này đã được đăng ký!";
            } else {
                errorMsg = "Đăng ký thất bại. Vui lòng thử lại!";
            }

            redirectAttributes.addAttribute("status", "error");
            redirectAttributes.addAttribute("message", errorMsg);
            redirectAttributes.addFlashAttribute("userAccount", userAccount);
            return "redirect:/register";
        }
    }
}
