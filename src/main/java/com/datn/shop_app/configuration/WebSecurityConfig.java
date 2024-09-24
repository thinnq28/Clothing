package com.datn.shop_app.configuration;

import com.datn.shop_app.filter.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
    @Value("${api.prefix}")
    private String apiPrefix;

    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(customizer -> customizer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/admin/register", apiPrefix),
                                    String.format("%s/users/admin/login", apiPrefix),
                                    String.format("%s/users/login", apiPrefix),
                                    String.format("%s/commodities/by-name", apiPrefix),
                                    String.format("%s/products/images/**", apiPrefix),
                                    String.format("%s/variants/images/**", apiPrefix),
                                    String.format("%s/variants/by-ids", apiPrefix),
                                    String.format("%s/variants/by-product/**", apiPrefix),
                                    String.format("%s/products/details/**", apiPrefix),
                                    String.format("%s/vouchers/by-code", apiPrefix),
                                    String.format("%s/orders", apiPrefix),
                                    String.format("%s/products/**", apiPrefix))
                            .permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
//                .cors(Customizer.withDefaults())
        ;
        //cho phép nhận requets không đáng tin cậy
        http.cors(httpSecurityCorsConfigurer -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(List.of("*"));
            corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "x-auth-token"));
            corsConfiguration.setExposedHeaders(List.of("x-auth-token"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", corsConfiguration);
            httpSecurityCorsConfigurer.configurationSource(source);
        });
        return http.build();
    }
}
