package utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public class JWTUtil {
    
    // Define a secret key for signing JWT tokens
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("my-super-secret-key-for-jwt-signing".getBytes());

    // Generate a JWT token
    public static String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)  // Set the subject as the email (user identifier)
                .claim("role", role) // Set the user role as a claim
                .setIssuedAt(new Date()) // Set the token issued date
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiry
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Sign with the secret key
                .compact(); // Generate the JWT token
    }

    // Verify the JWT token and return the claims
    public static Claims verifyToken(String token) {
        try {
            // Parse and validate the token
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)  // Use the secret key to verify
                    .build()
                    .parseClaimsJws(token)  // Parse the JWT and get the claims
                    .getBody(); // Return the claims (subject, role, etc.)
        } catch (JwtException e) {
            // Handle the case of an invalid token
            throw new RuntimeException("Invalid or expired token", e);
        }
    }

    // Get the role from the JWT token
    public static String getRole(String token) {
        Claims claims = verifyToken(token);
        return claims.get("role", String.class); // Extract the role from the claims
    }

    // Get the email from the JWT token
    public static String getEmail(String token) {
        Claims claims = verifyToken(token);
        return claims.getSubject(); // Extract the email (subject) from the claims
    }
}
