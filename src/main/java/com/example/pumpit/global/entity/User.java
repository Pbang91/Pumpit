package com.example.pumpit.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickName;

    @Column(unique = true)
    private String email;

    private String password;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserOAuthAccount> oauthAccounts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<WorkoutRecord> workoutRecords;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Post> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Like> likes;

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    private Set<UserFollower> followers;

    @OneToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private Set<UserFollower> following;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<ChatRoomMember> chatRoomMembers;

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private Set<ChatMessage> chatMessageSenders;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<ChatMessageRead> chatMessageReads;
}
