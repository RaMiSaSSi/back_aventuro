package com.example.demo.Config;

                    import com.example.demo.Filters.JwtRequestFilter; // Assuming this is the filter class
                    import org.springframework.context.annotation.Bean;
                    import org.springframework.context.annotation.Configuration;
                    import org.springframework.http.HttpMethod;
                    import org.springframework.security.config.Customizer;
                    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
                    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
                    import org.springframework.security.web.SecurityFilterChain;
                    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
                    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
                    import org.springframework.security.crypto.password.PasswordEncoder;
                    import org.springframework.security.authentication.AuthenticationManager;
                    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
                    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

                    @Configuration
                    @EnableWebSecurity
                    @EnableMethodSecurity // Enable method-level security
                    public class SecurityConfig {

                        private final JwtRequestFilter jwtRequestFilter;

                        public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
                            this.jwtRequestFilter = jwtRequestFilter;
                        }

                        @Bean
                        public PasswordEncoder passwordEncoder() {
                            return new BCryptPasswordEncoder();
                        }

                        @Bean
                        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                            return authConfig.getAuthenticationManager();
                        }

                        @Bean
                        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                            http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth

                                    .anyRequest().permitAll()
                                )
                                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter
                                .httpBasic(Customizer.withDefaults());

                            return http.build();
                        }
                    }