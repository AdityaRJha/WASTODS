package com.solevictus.adityaroshanjha.filter;

import com.solevictus.adityaroshanjha.jwtUtil.JwtUtil;
import com.solevictus.adityaroshanjha.service.impl.AppUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AppUserDetailsService appUserDetailsService;
    private  final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_URLS = List.of("/login", "/register",  "/send-reset-otp", "/reset-password", "/logout");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws java.io.IOException, ServletException {
        String path = request.getServletPath();

        if (
                //PUBLIC_URLS.contains(path)
                PUBLIC_URLS.stream().anyMatch(path::startsWith)
        ) {
            filterChain.doFilter(request, response);
            return;
        }


        String jwtToken = null;
        String email = null;

        //1. check the authorization header
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
        }

        //2. if not found in header, check the cookies
        if (jwtToken == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("jwt")) {
                        jwtToken = cookie.getValue();
                        break;
                    }
                }
            }
        }


        //3. Validate the token and set the security context
        if (jwtToken != null) {
            try {
                email = jwtUtil.extractEmail(jwtToken);
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);
                    if (jwtUtil.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                        var context = SecurityContextHolder.createEmptyContext();
//                        context.setAuthentication(authenticationToken);
//                        SecurityContextHolder.setContext(context);
//                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Login session expired. Please login again.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
