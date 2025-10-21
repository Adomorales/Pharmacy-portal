package com.pharmacy.controller;

import com.pharmacy.dto.LoginDto;
import com.pharmacy.dto.LoginResponseDto;
import com.pharmacy.dao.UserDao;
import com.pharmacy.exception.DaoException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private UserDao userDao;

    public AuthController(TokenProvider tokenProvider, AuthenticationManager authenticationManager, UserDao userDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userDao = userDao;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Valid LoginDto req) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            String token = tokenProvider.createToken(authentication);
            return new LoginResponseDto(token);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid username or password");
        }
    }

    @GetMapping("/me")
    public LoginResponseDto me(@RequestHeader("Authorization") String token) {
        try {
            String authToken = token != null && token.startsWith("Bearer ")
                    ? token.substring(7)
                    : token;
            return tokenProvider.validateToken(authToken);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token");
        }
    }
}
