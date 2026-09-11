package com.example.demo.users;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import tools.jackson.databind.ObjectMapper;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    // 保存所有在線用戶的 WebSocket 會話 (Key: userId)
    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
        }
    }

    /**
     * 後端業務觸發時，主動推播給指定用戶
     */
    public void sendToUser(Long userId, Object payload) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(payload);
                session.sendMessage(new TextMessage(jsonMessage));
            } catch (IOException e) {
                System.err.println("WebSocket 發送失敗給用戶 " + userId + ": " + e.getMessage());
            }
        }
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        try {
            URI uri = session.getUri();
            if (uri != null && uri.getQuery() != null) {
                String query = uri.getQuery();
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length > 1 && "userId".equals(pair[0])) {
                        return Long.parseLong(pair[1]);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("解析 WebSocket URI 失敗: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 僅做記錄，忽略前端傳來的訊息
        System.out.println("收到來自用戶 Session " + session.getId() + " 的文字訊息: " + message.getPayload());
    }
}
