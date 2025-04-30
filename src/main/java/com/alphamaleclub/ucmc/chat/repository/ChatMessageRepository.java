package com.alphamaleclub.ucmc.chat.repository;


import com.alphamaleclub.ucmc.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByChatRoomId(Long chatRoomId);
    Page<Chat> findByChatRoomId(Long chatRoomId, Pageable pageable);
    Slice<Chat> findByChatRoomIdOrderByTimestampDesc(Long ChatRoomId, Pageable pageable);
}

