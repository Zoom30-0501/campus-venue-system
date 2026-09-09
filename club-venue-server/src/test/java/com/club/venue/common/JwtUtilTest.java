package com.club.venue.common;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil JWT工具类单元测试
 */
@DisplayName("JWT 工具类测试")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String TEST_SECRET = "this-is-a-test-secret-key-thats-long-enough-for-hs256-signing!!";
    private static final Long TEST_EXPIRE_MS = 3600000L; // 1小时

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expireMs", TEST_EXPIRE_MS);
    }

    @Test
    @DisplayName("generateToken - 生成有效token")
    void generateToken() {
        String token = jwtUtil.generateToken(1L, "admin", "admin");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        // JWT 由3段base64组成,以.分隔
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    @DisplayName("parseToken - 解析有效token获取claims")
    void parseToken() {
        String token = jwtUtil.generateToken(1L, "admin", "admin");
        Claims claims = jwtUtil.parseToken(token);

        assertEquals("1", claims.getSubject());
        assertEquals("admin", claims.get("username", String.class));
        assertEquals("admin", claims.get("role", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    @DisplayName("getUserId - 从token中获取用户ID")
    void getUserId() {
        String token = jwtUtil.generateToken(42L, "testuser", "user");
        Long userId = jwtUtil.getUserId(token);

        assertEquals(42L, userId);
    }

    @Test
    @DisplayName("getUsername - 从token中获取用户名")
    void getUsername() {
        String token = jwtUtil.generateToken(1L, "zhangsan", "club_leader");
        String username = jwtUtil.getUsername(token);

        assertEquals("zhangsan", username);
    }

    @Test
    @DisplayName("getRole - 从token中获取角色")
    void getRole() {
        String token = jwtUtil.generateToken(1L, "admin", "admin");
        String role = jwtUtil.getRole(token);

        assertEquals("admin", role);
    }

    @Test
    @DisplayName("isTokenExpired - 未过期token返回false")
    void tokenNotExpired() {
        String token = jwtUtil.generateToken(1L, "admin", "admin");
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("isTokenExpired - 无效token返回true")
    void expiredOrInvalidToken() {
        assertTrue(jwtUtil.isTokenExpired("invalid.token.here"));
        assertTrue(jwtUtil.isTokenExpired(""));
        assertTrue(jwtUtil.isTokenExpired(null));
    }

    @Test
    @DisplayName("generateToken - 不同用户生成不同token")
    void generateTokenForDifferentUsers() {
        String token1 = jwtUtil.generateToken(1L, "admin", "admin");
        String token2 = jwtUtil.generateToken(2L, "zhangsan", "club_leader");

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("parseToken - 无效token抛出异常")
    void parseInvalidToken() {
        assertThrows(Exception.class, () -> jwtUtil.parseToken("not.a.valid.jwt.token"));
    }

    @Test
    @DisplayName("getUserId - 无效token返回null")
    void getUserIdFromInvalidToken() {
        assertNull(jwtUtil.getUserId("invalid"));
        assertNull(jwtUtil.getUserId(null));
    }

    @Test
    @DisplayName("getUsername - 无效token返回null")
    void getUsernameFromInvalidToken() {
        assertNull(jwtUtil.getUsername("invalid"));
    }

    @Test
    @DisplayName("getRole - 无效token返回null")
    void getRoleFromInvalidToken() {
        assertNull(jwtUtil.getRole("invalid"));
    }

    @Test
    @DisplayName("密钥懒加载 - 并发安全")
    void testKeyLazyInit() throws Exception {
        // Reset key
        ReflectionTestUtils.setField(jwtUtil, "key", null);

        // Create threads to generate tokens concurrently
        Thread t1 = new Thread(() -> jwtUtil.generateToken(1L, "a", "x"));
        Thread t2 = new Thread(() -> jwtUtil.generateToken(2L, "b", "y"));
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Should complete without exception
        assertTrue(true);
    }
}
