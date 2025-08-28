package com.example.linkchat_rabbit_service.repository;

import com.example.linkchat_rabbit_service.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Find messages by chatId (using the plain chatId column)
    List<ChatMessage> findByChatIdOrderByTimestampAsc(Long chatId);

    // Optional: native query if you want SQL instead of JPQL
    @Query(value = "SELECT * FROM messages WHERE chat_id = ?1 ORDER BY timestamp ASC", nativeQuery = true)
    List<ChatMessage> getMessagesByChatIdNative(Long chatId);

    // Find all messages ordered by timestamp
    List<ChatMessage> findAllByOrderByTimestampAsc();

    // Find messages between two users
    List<ChatMessage> findBySenderAndRecipientOrderByTimestampAsc(String sender, String recipient);

    // Filter by message type
    List<ChatMessage> findByMessageType(ChatMessage.MessageType messageType);

    // Count total messages
    @Query("SELECT COUNT(m) FROM ChatMessage m")
    long count();
}
