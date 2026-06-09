package com.ejercicio8.service;

import com.ejercicio8.dto.*;
import com.ejercicio8.entity.User;
import com.ejercicio8.exception.InvalidTokenException;
import com.ejercicio8.exception.UserAlreadyExistsException;
import com.ejercicio8.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException(request.username());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new TokenResponse(accessToken, refreshToken,
                jwtService.getAccessTokenExpiration(),
                jwtService.getRefreshTokenExpiration());
    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails user = userDetailsService.loadUserByUsername(request.username());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new TokenResponse(accessToken, refreshToken,
                jwtService.getAccessTokenExpiration(),
                jwtService.getRefreshTokenExpiration());
    }

    public AccessTokenResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();

        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            throw new InvalidTokenException("El refresh token es inválido o está expirado");
        }

        if (!jwtService.isRefreshToken(token)) {
            throw new InvalidTokenException("El token proporcionado no es un refresh token");
        }

        UserDetails user = userDetailsService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(token, user)) {
            throw new InvalidTokenException("El refresh token es inválido o está expirado");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        return new AccessTokenResponse(newAccessToken, jwtService.getAccessTokenExpiration());
    }
}
