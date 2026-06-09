package com.abhiram.sessionsmanager.service;

import com.abhiram.sessionsmanager.config.JwtConfig;
import com.abhiram.sessionsmanager.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecret("test-secret-that-is-long-enough-for-hs256-minimum-length-ok");
        jwtConfig.setExpirationMs(60000);
        jwtService = new JwtService(jwtConfig);
    }

    @Test
    void generateTokenProducesValidParseableJwt() {
        String token = jwtService.generateToken("user-42");

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);

        Claims claims = jwtService.validateToken(token);
        assertThat(claims.getSubject()).isEqualTo("user-42");
    }

    @Test
    void validateTokenReturnsFalseForExpiredToken() {
        JwtConfig shortLived = new JwtConfig();
        shortLived.setSecret("test-secret-that-is-long-enough-for-hs256-minimum-length-ok");
        shortLived.setExpirationMs(1);
        JwtService shortLivedService = new JwtService(shortLived);
        String token = shortLivedService.generateToken("user-1");

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertThatThrownBy(() -> shortLivedService.validateToken(token))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid or expired JWT token");
    }

    @Test
    void validateTokenReturnsFalseForTamperedToken() {
        String token = jwtService.generateToken("user-1");
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("A") ? "B" : "A");

        assertThatThrownBy(() -> jwtService.validateToken(tampered))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid or expired JWT token");
    }

    @Test
    void extractUserIdReturnsSubject() {
        String token = jwtService.generateToken("player-99");

        String userId = jwtService.extractUserId(token);

        assertThat(userId).isEqualTo("player-99");
    }
}
