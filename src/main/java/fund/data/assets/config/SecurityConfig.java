package fund.data.assets.config;

import fund.data.assets.jwt.JWTAuthenticationFilter;
import fund.data.assets.jwt.JWTAuthorizationFilter;
import fund.data.assets.jwt.JWTHelper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

/**
 * @version 0.6-a
 * @author MarkDementev a.k.a JavaMarkDem
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String baseUrl;
    private final RequestMatcher loginRequest;
    private final JWTHelper jwtHelper;
    private final RequestMatcher publicUrls;

    public SecurityConfig(@Value("${base-url}") final String baseUrl, final JWTHelper jwtHelper) {
        this.baseUrl = baseUrl;
        this.loginRequest = new AntPathRequestMatcher(baseUrl + "/login", HttpMethod.POST.toString());
        this.jwtHelper = jwtHelper;
        this.publicUrls = new OrRequestMatcher(
                loginRequest,
                new NegatedRequestMatcher(new AntPathRequestMatcher(baseUrl + "/**"))
        );
    }

    @Bean
    public UserDetailsService adminService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("password"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(baseUrl + "/login",
                                HttpMethod.POST.toString())).permitAll()
                        .requestMatchers(baseUrl + "/**").hasRole("ADMIN")
                        .anyRequest().denyAll()
                )
                .addFilter(
                        new JWTAuthenticationFilter(
                                authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)),
                                loginRequest,
                                jwtHelper)
                )
                .addFilterBefore(
                        new JWTAuthorizationFilter(publicUrls, jwtHelper),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
