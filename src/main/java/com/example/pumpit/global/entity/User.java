package com.example.pumpit.global.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
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

    @Setter
    @Column(nullable = false)
    private String nickName;

    @Setter
    @Column(unique = true)
    private String email; // 암호화된 이메일

    @Setter
    private String password; // 암호화된 비밀번호

    @Setter
    private String phone; // 암호화된 번호 - PW 찾기 시 활용

    @Setter
    @Column(unique = true)
    private String recoveryCode; // 암호화된 복구코드 - ID,PW 찾기 시 활용

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<UserOAuthAccount> oauthAccounts = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<WorkoutRecord> workoutRecords = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    private Set<UserFollower> followers = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private Set<UserFollower> following = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<ChatRoomMember> chatRoomMembers = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    private Set<ChatMessage> chatMessageSenders = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<ChatMessageRead> chatMessageReads = new HashSet<>();
}
