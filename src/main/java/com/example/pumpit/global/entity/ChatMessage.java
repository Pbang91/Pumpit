package com.example.pumpit.global.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // message type. image, text, file, etc

    private String content;

    @ColumnDefault("false")
    private boolean isDeleted;

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "sender_id")
    private User sender;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "message")
    private Set<ChatMessageFile> files;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "message")
    private Set<ChatMessageRead> reads;
}
