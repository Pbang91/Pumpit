package com.example.pumpit.global.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_message_files")
public class ChatMessageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage message;
}
