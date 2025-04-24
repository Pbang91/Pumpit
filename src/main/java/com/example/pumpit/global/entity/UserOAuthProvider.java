package com.example.pumpit.global.entity;

public enum UserOAuthProvider {
    GOOGLE,
    FACEBOOK,
    KAKAO,
    NAVER,
    APPLE;

    public String getProviderKoreanName() {
        return switch (this) {
            case GOOGLE -> "구글";
            case FACEBOOK -> "페이스북";
            case KAKAO -> "카카오";
            case NAVER -> "네이버";
            case APPLE -> "애플";
        };
    }
}
