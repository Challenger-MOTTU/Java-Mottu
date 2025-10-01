package com.motogrid.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // públicos
                        .requestMatchers(
                                "/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**",
                                "/console/**","/error",
                                "/css/**","/img/**","/webjars/**",
                                "/favicon.ico",
                                "/login"
                        ).permitAll()

                        // Páginas Thymeleaf (/web/**)
                        .requestMatchers(HttpMethod.GET, "/web/**").hasAnyRole("ADMIN","OPERADOR")
                        // criar/atualizar via form  -> SOMENTE ADMIN
                        .requestMatchers(HttpMethod.POST, "/web/**/salvar").hasRole("ADMIN")
                        // excluir via form
                        .requestMatchers(HttpMethod.POST, "/web/**/excluir", "/web/**/excluir/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/web/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/web/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/web/**").hasRole("ADMIN")
                        .requestMatchers("/web/**").hasRole("ADMIN")

                        // REST
                        .requestMatchers(HttpMethod.GET, "/motos/**","/patios/**").hasAnyRole("ADMIN","OPERADOR")
                        // -> SOMENTE ADMIN em POST/PUT/DELETE
                        .requestMatchers(HttpMethod.POST, "/motos/**","/patios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,  "/motos/**","/patios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/motos/**","/patios/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                // CSRF: manter em /web/**; ignorar APIs/Swagger/H2
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/motos/**","/patios/**",
                        "/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html",
                        "/console/**"
                ))

                // H2 console
                .headers(h -> h.frameOptions(f -> f.disable()))

                // Login/Logout
                .formLogin(login -> login
                        .loginPage("/login").permitAll()
                        .defaultSuccessUrl("/web", true)
                )

                // 403 -> sua página
                .exceptionHandling(ex -> ex.accessDeniedHandler((req, res, e) -> res.sendRedirect("/acesso-negado")))

                .logout(l -> l
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout").permitAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("123"))
                .roles("ADMIN").build();

        UserDetails operador = User.withUsername("operador")
                .password(encoder.encode("123"))
                .roles("OPERADOR").build();

        return new InMemoryUserDetailsManager(admin, operador);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
