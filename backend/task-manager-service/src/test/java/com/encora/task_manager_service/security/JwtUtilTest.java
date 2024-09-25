//package com.encora.task_manager_service.security;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class JwtUtilTest {
//    private JwtUtil jwtUtil;
//    private UserDetails userDetails;
//
//    @BeforeEach
//    public void setUp() {
//        jwtUtil = new JwtUtil();
//        userDetails = new User("testuser", "testpassword", new ArrayList<>());
//    }
//
//    @Test
//    public void testGenerateAndValidateToken() throws Exception {
//        String token = jwtUtil.generateToken(userDetails);
//        assertNotNull(token);
//        assertTrue(jwtUtil.validateToken(token, userDetails));
//    }
//
//    @Test
//    public void testExtractUsernameFromToken() throws Exception{
//        String token = jwtUtil.generateToken(userDetails);
//        String extractedUsername = jwtUtil.extractUsername(token);
//        assertEquals(userDetails.getUsername(), extractedUsername);
//    }
//
//    @Test
//    public void testExpiredToken() {
//        // Create a token that expires very soon (e.g., in 1 millisecond)
//        Date expirationDate = new Date(System.currentTimeMillis() + 1); // Expires in 1ms
//
//        String expiredToken = Jwts.builder()
//                .setSubject("testuser")
//                .setIssuedAt(new Date())
//                .setExpiration(expirationDate)
//                .signWith(SignatureAlgorithm.HS256, jwtUtil.getSecretKey()) // Use the actual secret key
//                .compact();
//
//        // Wait for the token to expire
//        try {
//            Thread.sleep(2); // Wait for 2ms (longer than the expiration time)
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        // Now try to validate the expired token
//        assertFalse(jwtUtil.validateToken(expiredToken, userDetails));
//    }
//
//    @Test
//    public void testInvalidToken() {
//        // Token from different secret key
//        String differentSecretKey = "anothersecretkeyfortestingareallyLongCharacterTokenThatShouldBeDifferentThantTHeOther";
//        String invalidToken = Jwts.builder()
//                .setSubject("testuser")
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
//                .signWith(SignatureAlgorithm.HS256, differentSecretKey)
//                .compact();
//        assertFalse(jwtUtil.validateToken(invalidToken, userDetails));
//    }
//}
//
