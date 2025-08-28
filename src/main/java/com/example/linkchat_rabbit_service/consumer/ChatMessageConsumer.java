package com.example.linkchat_rabbit_service.consumer;

import com.example.linkchat_rabbit_service.dto.ChatMessagePayload;
import com.example.linkchat_rabbit_service.model.ChatMessage;
import com.example.linkchat_rabbit_service.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageConsumer {
    private static final String MESSAGE_QUEUE = "LinkChatQueueChatMessages";

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @RabbitListener(queues = MESSAGE_QUEUE)
    public void receive(String messageJson) throws Exception {
        ChatMessagePayload payload = new ObjectMapper().readValue(messageJson, ChatMessagePayload.class);

        if (payload.getSender() == null || payload.getRecipient() == null) {
            System.err.println("Skipping message: sender or recipient is null");
            return;
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageText(payload.getMessageText());
        chatMessage.setSender(payload.getSender());
        chatMessage.setRecipient(payload.getRecipient());
        chatMessage.setTimestamp(payload.getTimestamp());

        chatMessageRepository.save(chatMessage);
    }
}
