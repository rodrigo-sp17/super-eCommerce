package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        // Gets Authorization Header from HTTP request
        String header = request.getHeader(AuthFilter.AUTHORIZATION_HEADER);

        // If the header is not according to our specified format, proceeds to next filter
        if (header == null || !header.startsWith(AuthFilter.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken token = parseToken(header);
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken parseToken(String tokenHeader) {
        // Sanitizes input
        String token = tokenHeader.replace(AuthFilter.TOKEN_PREFIX, "");

        String username = JWT.require(Algorithm.HMAC512(AuthFilter.SIGN_SECRET.getBytes()))
                .build()
                .verify(token)
                .getSubject();

        if (username == null) {
            return null;
        }

        return new UsernamePasswordAuthenticationToken(username,
                null,
                new ArrayList<>());
    }

}
