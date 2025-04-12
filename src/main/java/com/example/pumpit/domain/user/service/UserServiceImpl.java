package com.example.pumpit.domain.user.service;

import com.example.pumpit.domain.user.dto.request.LoginUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.request.RegisterUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.response.FindUserByIdResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserRequestTokenResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserResDto;
import com.example.pumpit.domain.user.repository.UserRepository;
import com.example.pumpit.global.entity.User;
import com.example.pumpit.global.entity.UserOAuthAccount;
import com.example.pumpit.global.exception.CustomException;
import com.example.pumpit.global.exception.enums.CustomExceptionData;
import com.example.pumpit.global.util.JwtService;
import com.example.pumpit.global.util.PasswordEncoder;
import com.example.pumpit.global.util.RedisService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    /*
    Repository
     */
    private final UserRepository userRepository;

    /*
    Other Services
     */
    private final JwtService jwtService;
    private final RedisService redisService;

    public UserServiceImpl(UserRepository userRepository, JwtService jwtService, RedisService redisService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.redisService = redisService;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomExceptionData.USER_NOT_FOUND));
    }

    private boolean existingUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private String createAndSetTempCode(Long userId) {
        String tempCode = "tempCode:" + UUID.randomUUID();
        redisService.set(tempCode, userId, Duration.ofSeconds(10));

        return tempCode;
    }

    @Override
    public FindUserByIdResDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomExceptionData.USER_NOT_FOUND));

        return new FindUserByIdResDto(
                user.getId(),
                user.getEmail(),
                user.getNickName(),
                Optional.ofNullable(user.getOauthAccounts())
                        .map(accounts -> accounts
                                .stream()
                                .map(UserOAuthAccount::getProvider)
                                .toList())
                        .orElse(List.of()),
                user.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public LoginUserRequestTokenResDto registerUserByEmail(RegisterUserByEmailReqDto dto) {
        if (existingUserByEmail(dto.email())) {
            throw new CustomException(CustomExceptionData.USER_DUPLICATED);
        }

        User user = User.builder()
                .email(dto.email())
                .password(PasswordEncoder.encode(dto.password()))
                .nickName(dto.email().split("@")[0])
                .build();

        User savedUser = userRepository.save(user);

        try {
            String tempCode = createAndSetTempCode(savedUser.getId());
            return new LoginUserRequestTokenResDto(tempCode);
        } catch (Exception e) {
            throw new CustomException(CustomExceptionData.INTERVAL_SERVER_ERROR, "Redis Error: " + e.getMessage());
        }
    }

    @Override
    public LoginUserRequestTokenResDto loginUserByEmail(LoginUserByEmailReqDto dto) {
        User user = findUserByEmail(dto.email());

        if (!PasswordEncoder.matches(dto.password(), user.getPassword())) {
            throw new CustomException(CustomExceptionData.USER_PASSWORD_NOT_MATCH);
        }

        try {
            String tempCode = createAndSetTempCode(user.getId());

            return new LoginUserRequestTokenResDto(tempCode);
        } catch (Exception e) {
            throw new CustomException(CustomExceptionData.INTERVAL_SERVER_ERROR, "Redis Error: " + e.getMessage());
        }
    }

    @Override
    public LoginUserResDto getToken(String code) {
        Long userId = redisService.get(code, Long.class);

        if (userId == null) {
            throw new CustomException(CustomExceptionData.AUTH_CODE_NOT_FOUND);
        }

        String accessToken = jwtService.generateAccessToken(userId);
        String refreshToken = jwtService.generateRefreshToken(userId);

        redisService.set(refreshToken, true, Duration.ofDays(7));

        return new LoginUserResDto(accessToken, refreshToken);
    }
}
