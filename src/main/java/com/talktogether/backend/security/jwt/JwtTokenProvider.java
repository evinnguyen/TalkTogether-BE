package com.talktogether.backend.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.talktogether.backend.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // Khởi tạo SecretKey từ chuỗi bí mật
    // Dùng để ký và xác thực JWT
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Sinh Access Token từ đối tượng User
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    // Trích xuất email từ JWT Token
    public String getEmailFromToken(String token) {

        // Dùng để parse và lấy thông tin từ JWT Token
        Claims claims = Jwts.parser() // Claims dùng để chứa thông tin người dùng sau khi được giải mã
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // Kiểm tra JWT Token có hợp lệ không
    public boolean validateToken(String token) {
        try { // Nếu Token hợp lệ thì trả về true, ngược lại trả về false
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) { // Nếu Token không hợp lệ thì trả về false
            return false;
        }
    }

}
