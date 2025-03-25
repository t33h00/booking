package com.lotus.booking.Config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.stereotype.Component;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = request.getURI().getQuery();

        if (token != null && token.startsWith("token")) {
            token = token.substring(6);
            if (jwtUtil.validateAccessToken(token)) {
                System.out.println("Token validated successfully");
                return true;
            } else {
                System.out.println("Token validation failed");
            }
        } else {
            System.out.println("Token not found or invalid format");
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // No implementation needed
    }
}
