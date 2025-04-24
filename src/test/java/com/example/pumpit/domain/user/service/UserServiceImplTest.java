package com.example.pumpit.domain.user.service;

import com.example.pumpit.domain.user.dto.request.LoginUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.request.RegisterUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.request.UpdateUserReqDto;
import com.example.pumpit.domain.user.dto.response.FindUserByIdResDto;
import com.example.pumpit.domain.user.dto.response.FindUserSignupInfoResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserRequestTokenResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserResDto;
import com.example.pumpit.domain.user.repository.UserRepository;
import com.example.pumpit.global.entity.User;
import com.example.pumpit.global.exception.CustomException;
import com.example.pumpit.global.util.AESCBCUtil;
import com.example.pumpit.global.util.JwtService;
import com.example.pumpit.global.util.BCryptService;
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
    private AESCBCUtil aescbcUtil;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        String testKey = "abcdefghoabcdefghoabcdefghoaq442";
        aescbcUtil = new AESCBCUtil(testKey);
        userService = new UserServiceImpl(userRepository, jwtService, redisService, aescbcUtil);
    }

    @DisplayName("이메일 회원가입 성공")
    @Test
    void registerUserByEmail_Success() {
        RegisterUserByEmailReqDto dto = new RegisterUserByEmailReqDto(
                "test@mail.com",
                "password123!",
                "얍얍얍",
                "01012345678"
        );

        String encEmail = aescbcUtil.encrypt(dto.email());

        given(userRepository.existsByEmail(encEmail)).willReturn(false);
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
        String encEmail = aescbcUtil.encrypt(email);
        RegisterUserByEmailReqDto dto = new RegisterUserByEmailReqDto(
                email,
                "password123!",
                "얍얍얍",
                "01012345678"
        );

        given(userRepository.existsByEmail(encEmail)).willReturn(true);

        assertThatThrownBy(() -> userService.registerUserByEmail(dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("이미 존재하는 사용자입니다");
    }

    @DisplayName("이메일 로그인 성공")
    @Test
    void loginUserByEmail_Success() {
        String email = "login@test.com";
        String password = "password123!";
        String encodedPassword = BCryptService.encode(password);
        String encEmail = aescbcUtil.encrypt(email);

        User user = User.builder().email(encEmail).password(encodedPassword).nickName("login").build();

        LoginUserByEmailReqDto dto = new LoginUserByEmailReqDto(email, password);

        given(userRepository.findByEmail(encEmail)).willReturn(Optional.of(user));
        willDoNothing().given(redisService).set(anyString(), any(), any());

        LoginUserRequestTokenResDto result = userService.loginUserByEmail(dto);

        assertThat(result.tempCode()).startsWith("tempCode:");
    }

    @DisplayName("이메일 로그인 실패 - 유저 없음")
    @Test
    void loginUserByEmail_Fail_userNotFound() {
        String email = "login@test.com";
        String encEmail = aescbcUtil.encrypt(email);
        given(userRepository.findByEmail(encEmail)).willReturn(Optional.empty());

        LoginUserByEmailReqDto dto = new LoginUserByEmailReqDto(email, "password123!");

        assertThatThrownBy(() -> userService.loginUserByEmail(dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @DisplayName("이메일 로그인 실패 - 비밀번호 불일치")
    @Test
    void loginUserByEmail_Fail_wrongPassword() {
        String email = "login@test.com";
        String encEmail = aescbcUtil.encrypt(email);

        User user = User
                .builder()
                .email(encEmail)
                .password(BCryptService.encode("failTest1234"))
                .nickName("login")
                .build();

        LoginUserByEmailReqDto dto = new LoginUserByEmailReqDto(email, "wrongPassword");

        given(userRepository.findByEmail(encEmail)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.loginUserByEmail(dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다");
    }

    @DisplayName("토큰 발급 성공")
    @Test
    void getToken_Success() {
        String tempCode = "tempCode:%s".formatted(UUID.randomUUID().toString());
        Long userId = 52L;

        given(userRepository.findById(userId)).willReturn(Optional.of(User.builder().id(userId).build()));
        given(redisService.get(tempCode, Long.class)).willReturn(userId);
        given(jwtService.generateAccessToken(userId)).willReturn("accessToken");
        given(jwtService.generateRefreshToken(userId, false)).willReturn("refreshToken");

        LoginUserResDto result = userService.getToken(tempCode, false);

        assertThat(result.accessToken()).isEqualTo("accessToken");
        assertThat(result.refreshToken()).isEqualTo("refreshToken");
    }

    @DisplayName("토큰 발급 실패 - 인증 코드 없음")
    @Test
    void getToken_Fail_authCodeNotFound() {
        String tempCode = "tempCode:%s".formatted(UUID.randomUUID().toString());

        given(redisService.get(tempCode, Long.class)).willReturn(null);

        assertThatThrownBy(() -> userService.getToken(tempCode, false))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("인증 코드가 존재하지 않습니다");
    }

    @DisplayName("내 정보 보기 성공")
    @Test
    void findUserById_Success() {
        Long userId = 52L;
        String email = "find@test.com";
        String phone = "01012345678";
        String encEmail = aescbcUtil.encrypt(email);
        String encPhone = aescbcUtil.encrypt(phone);

        User user = User
                .builder()
                .id(userId)
                .email(encEmail)
                .password("ddd")
                .nickName("find")
                .phone(encPhone)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        FindUserByIdResDto result = userService.findUserById(userId);

        assertThat(result.email()).isEqualTo(aescbcUtil.decrypt(user.getEmail()));
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

    @DisplayName("내 정보 업데이트 성공")
    @Test
    void updateUser_Success() {
        Long userId = 52L;
        String email = "update@test.com";
        String encEmail = aescbcUtil.encrypt(email);

        User user = User
                .builder()
                .id(userId)
                .email(encEmail)
                .password("ddd")
                .nickName("find")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        UpdateUserReqDto dto = new UpdateUserReqDto("changeName", null);

        userService.updateUser(userId, dto);

        FindUserByIdResDto res = userService.findUserById(userId);

        assertThat(res.nickName()).isEqualTo(dto.nickName());
    }

    @DisplayName("내 정보 업데이트 실패 - 유저 없음")
    @Test
    void updateUser_Fail_userNotFound() {
        Long userId = 52L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        UpdateUserReqDto dto = new UpdateUserReqDto("failName", null);

        assertThatThrownBy(() -> userService.updateUser(userId, dto))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @DisplayName("가입 정보 보기 성공")
    @Test
    void findUserSignupInfo_Success() {
        Long userId = 52L;
        String email = "find@test.com";
        String encEmail = aescbcUtil.encrypt(email);
        String recoveryCode = BCryptService.generateRecoveryCode();
        String encCode = aescbcUtil.encrypt(recoveryCode);

        User user = User
                .builder()
                .id(userId)
                .email(encEmail)
                .password("ddd")
                .nickName("find")
                .recoveryCode(encCode)
                .build();

        given(userRepository.findByRecoveryCode(encCode)).willReturn(Optional.of(user));

        FindUserSignupInfoResDto resDto = userService.findUserSignupInfo(recoveryCode);
        System.out.println(resDto);
        assertThat(resDto.email()).isEqualTo("fi**@test.com");
    }

    @DisplayName("가입 정보 보기 실패 - 복구코드 오류")
    @Test
    void findUserSignupInfo_Fail_InvalidCode() {
        String recoveryCode = BCryptService.generateRecoveryCode();
        String encCode = aescbcUtil.encrypt(recoveryCode);

        given(userRepository.findByRecoveryCode(encCode)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserSignupInfo(recoveryCode))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("잘못된 복구코드 입니다");
    }
}
