package com.example.pumpit.domain.user.service;

import com.example.pumpit.domain.user.dto.request.LoginUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.request.RegisterUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.response.FindUserByIdResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserRequestTokenResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserResDto;
import com.example.pumpit.domain.user.repository.UserRepository;
import com.example.pumpit.global.entity.User;
import com.example.pumpit.global.exception.CustomException;
import com.example.pumpit.global.util.JwtService;
import com.example.pumpit.global.util.PasswordEncoder;
import com.example.pumpit.global.util.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private RedisService redisService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, jwtService, redisService);
    }

    @DisplayName("이메일 회원가입 성공")
    @Test
    void registerUserByEmail_Success() {
        RegisterUserByEmailReqDto dto = new RegisterUserByEmailReqDto(
                "test@mail.com",
                "password123!"
        );

        given(userRepository.existsByEmail(dto.email())).willReturn(false);
        given(userRepository.save(any())).willAnswer(inv -> {
            User user = inv.getArgument(0);

            return User.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .nickName(user.getNickName())
                    .build();
        });

        willDoNothing().given(redisService).set(anyString(), any(), any());

        LoginUserRequestTokenResDto result = userService.registerUserByEmail(dto);

        assertThat(result.tempCode()).startsWith("tempCode:");
        verify(redisService).set(startsWith("tempCode:"), any(), any());
    }

    @DisplayName("이메일 회원가입 실패 - 중복된 이메일")
    @Test
    void registerUserByEmail_Fail_userAlreadyExist() {
        String email = "test@exist.com";
        RegisterUserByEmailReqDto dto = new RegisterUserByEmailReqDto(
                email,
                "password123!"
        );

        given(userRepository.existsByEmail(dto.email())).willReturn(true);

        assertThatThrownBy(() -> userService.registerUserByEmail(dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("이미 존재하는 사용자입니다");
    }

    @DisplayName("이메일 로그인 성공")
    @Test
    void loginUserByEmail_Success() {
        String email = "login@test.com";
        String password = "password123!";
        String encodedPassword = PasswordEncoder.encode(password);
        User user = User.builder().email(email).password(encodedPassword).nickName("login").build();

        LoginUserByEmailReqDto dto = new LoginUserByEmailReqDto(email, password);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        willDoNothing().given(redisService).set(anyString(), any(), any());

        LoginUserRequestTokenResDto result = userService.loginUserByEmail(dto);

        assertThat(result.tempCode()).startsWith("tempCode:");
    }

    @DisplayName("이메일 로그인 실패 - 유저 없음")
    @Test
    void loginUserByEmail_Fail_userNotFound() {
        String email = "login@test.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        LoginUserByEmailReqDto dto = new LoginUserByEmailReqDto(email, "password123!");

        assertThatThrownBy(() -> userService.loginUserByEmail(dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @DisplayName("이메일 로그인 실패 - 비밀번호 불일치")
    @Test
    void loginUserByEmail_Fail_wrongPassword() {
        String email = "login@test.com";
        User user = User
                .builder()
                .email(email)
                .password(PasswordEncoder.encode("failTest1234"))
                .nickName("login")
                .build();

        LoginUserByEmailReqDto dto = new LoginUserByEmailReqDto(email, "wrongPassword");

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.loginUserByEmail(dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다");
    }

    @DisplayName("토큰 발급 성공")
    @Test
    void getToken_Success() {
        String tempCode = "tempCode:%s".formatted(UUID.randomUUID().toString());
        Long userId = 52L;

        given(redisService.get(tempCode, Long.class)).willReturn(userId);
        given(jwtService.generateAccessToken(userId)).willReturn("accessToken");
        given(jwtService.generateRefreshToken(userId)).willReturn("refreshToken");

        LoginUserResDto result = userService.getToken(tempCode);

        assertThat(result.accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");
    }

    @DisplayName("토큰 발급 실패 - 인증 코드 없음")
    @Test
    void getToken_Fail_authCodeNotFound() {
        String tempCode = "tempCode:%s".formatted(UUID.randomUUID().toString());

        given(redisService.get(tempCode, Long.class)).willReturn(null);

        assertThatThrownBy(() -> userService.getToken(tempCode))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("인증 코드가 존재하지 않습니다");
    }

    @DisplayName("내 정보 보기 성공")
    @Test
    void findUserById_Success() {
        Long userId = 52L;
        User user = User
                .builder()
                .email("find@test.com")
                .password("ddd")
                .nickName("find")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        FindUserByIdResDto result = userService.findUserById(userId);

        assertThat(result.email()).isEqualTo(user.getEmail());
        assertThat(result.nickName()).isEqualTo(user.getNickName());
    }

    @DisplayName("내 정보 보기 실패 - 유저 없음")
    @Test
    void findUserById_Fail_userNotFound() {
        Long userId = 52L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(userId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }
}
