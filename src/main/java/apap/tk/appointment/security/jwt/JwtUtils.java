package apap.tk.appointment.security.jwt;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
 
@Component
public class JwtUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
 
    @Value("${appointment.app.jwtSecret}")
    private String jwtSecret;

    public String getUserNameFromJwtToken(String token) {
        try {
            JwtParser jwtParser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build();
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String username = claims.getSubject();
            logger.debug("Extracted username from token: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Error extracting username from token", e);
            throw e;
        }
    }

    public String getRoleFromToken(String token) {
        try {
            JwtParser jwtParser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build();
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String role = claims.get("role", String.class);
            logger.debug("Extracted role from token: {}", role);
            return role;
        } catch (Exception e) {
            logger.error("Error extracting role from token", e);
            throw e;
        }
    }
 
    public boolean validateJwtToken(String authToken) {
        try {
            JwtParser parser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build();
            parser.parseSignedClaims(authToken);
            logger.debug("JWT token is valid");
            return true;
        } catch(SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch(IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch(MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch(ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch(UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }
        return false;
    }
}
