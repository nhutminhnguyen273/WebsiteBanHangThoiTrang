package com.nhom15.fashion.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class WebRTCController {
    private final SimpMessageSendingOperations messagingTemplate;

    public WebRTCController(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/call")
    public void handleCall(@Payload String role) {
        if ("viewer".equals(role)) {
            // Gửi thông báo cho broadcaster khi có người xem kết nối
            messagingTemplate.convertAndSend("/topic/stream", "viewer-connected");
        }
    }
}
