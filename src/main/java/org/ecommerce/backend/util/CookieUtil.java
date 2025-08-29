package org.ecommerce.backend.util;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${jwt.expiration-ms}")
    private int tokenExpiry;
    public Cookie createCookie(String tokenVal){
        Cookie cookie = new Cookie("token", tokenVal);
        cookie.setHttpOnly(true);
        cookie.setPath("/api"); // base of request paths (context-path) the browser will send the cookie to.
        cookie.setMaxAge(tokenExpiry/1000);
        return cookie;
    }

    public Cookie clearCookie(){
        Cookie cookie = new Cookie("token", null);
        // To clear token cookie, we must set the cookie with the same settings as when it was created except setMaxAge
        cookie.setHttpOnly(true);
        cookie.setPath("/api");
        cookie.setMaxAge(0);
        return cookie;
    }



}
