package project.gymecommerce.Controllers.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingPageController {

    @GetMapping("/hardfuel")
    public String landingPage(){
        return "LandingPage/landingPage";
    }

    @GetMapping("/about")
    public String about(){
        return "LandingPage/aboutUs";
    }

    @GetMapping("/contact")
    public String contact(){
        return "LandingPage/contact";
    }
}
