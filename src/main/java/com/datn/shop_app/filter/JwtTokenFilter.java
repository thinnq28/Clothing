package com.datn.shop_app.filter;

import com.datn.shop_app.entity.User;
import com.datn.shop_app.utils.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            if(isBypassToken(request)) {
                filterChain.doFilter(request, response); //enable bypass - cho đi qua hết
                return;
            }

            String authHeader = request.getHeader("Authorization");
            if(authHeader == null && !authHeader.startsWith("Bearer ")) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED, "authHeader null or not started with Bearer");
                return;
            }

            String token = authHeader.substring(7);
            String phoneNumber = jwtTokenUtil.getSubject(token);

            boolean isNullPhoneNumber = phoneNumber == null;
            boolean isNullAuthentication = SecurityContextHolder.getContext().getAuthentication() == null;
            if(!isNullPhoneNumber && isNullAuthentication) {
                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);

                boolean isValid = jwtTokenUtil.validateToken(token, userDetails);
                if(isValid) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(request, response); //enable bypass
        }catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }
    }


    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                // Healthcheck request, no JWT token required
                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/admin/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/admin/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/products/images/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/variants/images/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/variants/by-product/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/variants/by-ids", apiPrefix), "GET"),
                Pair.of(String.format("%s/products", apiPrefix), "GET"),
                Pair.of(String.format("%s/commodities/by-name", apiPrefix), "GET"),
                Pair.of(String.format("%s/vouchers/by-code", apiPrefix), "GET"),
                Pair.of(String.format("%s/orders", apiPrefix), "POST"),
                Pair.of(String.format("%s/products/details/**", apiPrefix), "GET")
        );

        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        for (Pair<String, String> token : bypassTokens) {
            String path = token.getFirst();
            String method = token.getSecond();
            // Check if the request path and method match any pair in the bypassTokens list
            if (requestPath.matches(path.replace("**", ".*"))
                    && requestMethod.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }
}
