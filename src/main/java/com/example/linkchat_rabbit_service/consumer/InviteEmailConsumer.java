package com.example.linkchat_rabbit_service.consumer;

import com.example.linkchat_rabbit_service.dto.InviteEmailPayload;
import com.example.linkchat_rabbit_service.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class InviteEmailConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmailService emailService;

    public InviteEmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "LinkChatQueueEmail")
    public void receiveMessage(String message) throws Exception {
        InviteEmailPayload payload = objectMapper.readValue(message, InviteEmailPayload.class);
        System.out.println("📩 Received payload for: " + payload.getRecipients());
        emailService.sendInviteEmail(payload);
    }
}