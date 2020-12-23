package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    public static final Logger log = LoggerFactory.getLogger("splunk.logger");

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // Time to expire since issuing of JWT Token, in millis
    public static final long TIME_TO_EXPIRE = TimeUnit.DAYS.toMillis(2);

    // Secret to sign JWT
    public static final String SIGN_SECRET = "I'm a Java developer!";

    @Autowired
    private final AuthenticationManager manager;

    private final ObjectMapper mapper;

    public AuthFilter(AuthenticationManager manager) {
        this.manager = manager;
        this.mapper = new ObjectMapper();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Only POST requests allowed for authorization");
        }

        try {
            // Extracts User object from request using Jackson's mapper
            User requestBody = mapper.readValue(request.getReader(), User.class);

            // Creates token from User contained in servlet request
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(requestBody.getUsername(),
                    requestBody.getPassword());

            setDetails(request, token);

            return manager.authenticate(token);

        } catch (IOException e) {
            log.error("Could not read Servlet request for authentication");
            throw new AuthenticationServiceException(
                    "Could not read authentication details from request");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) authResult.getPrincipal();
        String jwtToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + TIME_TO_EXPIRE))
                .sign(Algorithm.HMAC512(SIGN_SECRET.getBytes()));

        response.addHeader(AUTHORIZATION_HEADER, TOKEN_PREFIX + jwtToken);
    }
}
