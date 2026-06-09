package com.ejercicio8.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long accessExpiresIn,
        long refreshExpiresIn
) {
    public TokenResponse(String accessToken, String refreshToken, long accessExpiresIn, long refreshExpiresIn) {
        this(accessToken, refreshToken, "Bearer", accessExpiresIn, refreshExpiresIn);
    }
}
