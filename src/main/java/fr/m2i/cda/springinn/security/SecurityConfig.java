package fr.m2i.cda.springinn.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    SecurityFilterChain accessControl(HttpSecurity http) throws Exception {

        http.httpBasic(Customizer.withDefaults());

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        http.logout(logout -> logout.logoutUrl("/api/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)));

        http.csrf(csrf -> csrf.disable());

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.authorizeHttpRequests(request -> request
            .requestMatchers(HttpMethod.GET, "/api/account").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/booking").hasRole("CUSTOMER")
            .requestMatchers("/api/booking/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/room/**").permitAll()
            .requestMatchers("/api/room/**").hasRole("ADMIN")
            .anyRequest().permitAll()

        );

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(frontendUrl));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}
