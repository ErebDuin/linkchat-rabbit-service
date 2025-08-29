package com.example.linkchat_rabbit_service.repository;

import com.example.linkchat_rabbit_service.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByLink(String link);

    boolean existsByTitle(String title);
}
