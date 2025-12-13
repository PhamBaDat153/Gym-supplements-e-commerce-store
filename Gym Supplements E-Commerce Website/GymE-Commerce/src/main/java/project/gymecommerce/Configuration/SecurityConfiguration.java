package project.gymecommerce.Configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class SecurityConfiguration {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);

        // Query lấy user theo email
        manager.setUsersByUsernameQuery(
                "SELECT email, hashed_password, is_active FROM user_account WHERE email = ?"
        );

        // Sửa query: role_name trong DB đã bao gồm ROLE_ (ví dụ: ROLE_ADMIN, ROLE_CUSTOMER) → không cần CONCAT('ROLE_')
        manager.setAuthoritiesByUsernameQuery(
                "SELECT ua.email, r.role_name AS authority " +
                        "FROM user_account ua " +
                        "JOIN user_account_role uar ON ua.user_account_id = uar.user_account_id " +
                        "JOIN role r ON r.role_id = uar.role_id " +
                        "WHERE ua.email = ?"
        );

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/hardfuel","/about","/contact").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        .anyRequest().authenticated()

                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(authenticationSuccessHandler())  // Xử lý redirect dựa trên role sau login thành công
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException {
                String redirectUrl = "/";  // Default redirect

                // Lấy roles của user và redirect dựa trên role chính
                for (GrantedAuthority authority : authentication.getAuthorities()) {
                    if (authority.getAuthority().equals("ROLE_ADMIN")) {
                        redirectUrl = "/admin/dashboard";
                        break;
                    } else if (authority.getAuthority().equals("ROLE_EMPLOYEE")) {
                        redirectUrl = "/employee/dashboard";
                        break;
                    } else if (authority.getAuthority().equals("ROLE_CUSTOMER")) {
                        redirectUrl = "/customer/home";
                        break;
                    }
                }

                response.sendRedirect(redirectUrl);
            }
        };
    }
}