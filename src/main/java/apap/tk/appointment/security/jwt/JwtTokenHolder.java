package apap.tk.appointment.security.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenHolder {
    private static final ThreadLocal<String> currentToken = new ThreadLocal<>();

    public void setToken(String token) {
        currentToken.set(token);
    }

    public String getToken() {
        return currentToken.get();
    }

    public void clear() {
        currentToken.remove();
    }
}