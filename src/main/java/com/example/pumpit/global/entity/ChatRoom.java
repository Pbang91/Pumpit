package com.example.pumpit.global.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ColumnDefault("false")
    private boolean isGroup = false;

    private String name;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom")
    private Set<ChatRoomMember> members;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom")
    private Set<ChatMessage> messages;
}
