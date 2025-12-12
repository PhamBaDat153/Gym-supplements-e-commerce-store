//package project.gymecommerce.Configuration;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.JdbcUserDetailsManager;
//import org.springframework.security.provisioning.UserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class SecurityConfiguration {
//
//    @Bean
//    public UserDetailsManager userDetailsManager(DataSource dataSource) {
//        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
//
//        // Query lấy user theo email
//        manager.setUsersByUsernameQuery(
//                "SELECT email, hashed_password, is_active FROM user_account WHERE email = ?"
//        );
//
//        // QUAN TRỌNG: role_name trong DB là ADMIN, EMPLOYEE, CUSTOMER → phải tự thêm tiền tố ROLE_
//        manager.setAuthoritiesByUsernameQuery(
//                "SELECT ua.email, CONCAT('ROLE_', r.role_name) AS authority " +
//                        "FROM user_account ua " +
//                        "JOIN user_account_role uar ON ua.user_account_id = uar.user_account_id " +
//                        "JOIN role r ON r.role_id = uar.role_id " +
//                        "WHERE ua.email = ?"
//        );
//
//        return manager;
//    }
//
//}