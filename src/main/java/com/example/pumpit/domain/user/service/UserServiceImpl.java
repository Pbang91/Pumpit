package com.example.pumpit.domain.user.service;

import com.example.pumpit.domain.user.dto.request.*;
import com.example.pumpit.domain.user.dto.response.FindUserByIdResDto;
import com.example.pumpit.domain.user.dto.response.FindUserSignupInfoResDto;
import com.example.pumpit.domain.user.dto.response.IssueTempCodeResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserResDto;
import com.example.pumpit.domain.user.repository.UserRepository;
import com.example.pumpit.global.entity.User;
import com.example.pumpit.global.entity.UserOAuthAccount;
import com.example.pumpit.global.entity.UserOAuthProvider;
import com.example.pumpit.global.exception.CustomException;
import com.example.pumpit.global.exception.enums.CustomExceptionData;
import com.example.pumpit.global.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

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
    private final AESUtil aesUtil;

    public UserServiceImpl(
            UserRepository userRepository,
            JwtService jwtService,
            RedisService redisService,
            AESUtil aesUtil
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.aesUtil = aesUtil;
    }

    private User findUserByEmail(String email) {
        String encryptedEmail = aesUtil.encrypt(email);

        return userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new CustomException(CustomExceptionData.USER_NOT_FOUND));
    }

    private boolean existingUserByEmail(String email) {
        String encryptedEmail = aesUtil.encrypt(email);

        return userRepository.existsByEmail(encryptedEmail);
    }

    private String createAndSetTempCode(String prefix, Long userId) {
        String randomCode = UUID.randomUUID().toString();
        String redisKey = prefix + ":" + randomCode;

        redisService.set(redisKey, userId, Duration.ofSeconds(10));

        return randomCode;
    }

    @Override
    @Transactional(readOnly = true)
    public FindUserByIdResDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomExceptionData.USER_NOT_FOUND));

        return new FindUserByIdResDto(
                user.getId(),
                user.getEmail() != null ? aesUtil.decrypt(user.getEmail()) : null,
                user.getPhone() != null ? aesUtil.decrypt(user.getPhone()) : null,
                user.getNickName(),
                user.getOauthAccounts() != null
                        ? user.getOauthAccounts().stream().map(UserOAuthAccount::getProvider).toList()
                        : null,
                user.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public IssueTempCodeResDto registerUserByEmail(RegisterUserByEmailReqDto dto) {
        if (existingUserByEmail(dto.email())) {
            throw new CustomException(CustomExceptionData.USER_DUPLICATED);
        }

        String encryptedEmail = aesUtil.encrypt(dto.email());
        String encryptedPhone = dto.phone() != null ? aesUtil.encrypt(dto.phone()) : null;

        User user = User.builder()
                .email(encryptedEmail)
                .password(BCryptService.encode(dto.password()))
                .nickName(dto.nickName())
                .phone(encryptedPhone)
                .build();

        User savedUser = userRepository.save(user);

        try {
            String tempCode = createAndSetTempCode("auth:temp", savedUser.getId());
            return new IssueTempCodeResDto(tempCode);
        } catch (Exception e) {
            throw new CustomException(CustomExceptionData.INTERVAL_SERVER_ERROR, "Redis Error: " + e.getMessage());
        }
    }

    @Override
    public IssueTempCodeResDto loginUserByEmail(LoginUserByEmailReqDto dto) {
        User user = findUserByEmail(dto.email());

        if (!BCryptService.matches(dto.password(), user.getPassword())) {
            throw new CustomException(CustomExceptionData.USER_PASSWORD_NOT_MATCH);
        }

        try {
            String tempCode = createAndSetTempCode("auth:temp", user.getId());
            return new IssueTempCodeResDto(tempCode);
        } catch (Exception e) {
            throw new CustomException(CustomExceptionData.INTERVAL_SERVER_ERROR, "Redis Error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public LoginUserResDto getToken(String code, boolean remember) {
        String redisKey = String.format("auth:temp:%s", code);

        Long userId = redisService.get(redisKey, Long.class);

        if (userId == null) {
            throw new CustomException(CustomExceptionData.AUTH_CODE_NOT_FOUND);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(CustomExceptionData.USER_NOT_FOUND));
        String recoveryCode = null;

        if (user.getRecoveryCode() == null) {
            recoveryCode = BCryptService.generateRecoveryCode();
            String encryptedCode = aesUtil.encrypt(recoveryCode);

            user.setRecoveryCode(encryptedCode);
        }

        String accessToken = jwtService.generateAccessToken(userId);
        String refreshToken = jwtService.generateRefreshToken(userId, remember);

        redisService.set(refreshToken, true, Duration.ofDays(7));

        return new LoginUserResDto(accessToken, refreshToken, recoveryCode);
    }

    @Override
    @Transactional
    public void updateUser(Long userId, UpdateUserReqDto dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(CustomExceptionData.USER_NOT_FOUND));

        if (dto.nickName() != null) {
            user.setNickName(dto.nickName());
        }

        if (dto.phone() != null) {
            String encryptedPhone = aesUtil.encrypt(dto.phone());
            user.setPhone(encryptedPhone);
        }
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordReqDto dto) {
        String redisKey = String.format("pw:temp:%s", dto.code());

        Long userId = redisService.get(redisKey, Long.class);

        if (userId == null) {
            throw new CustomException(CustomExceptionData.AUTH_CODE_NOT_FOUND);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(CustomExceptionData.USER_NOT_FOUND));

        String encryptedPassword = BCryptService.encode(dto.password());

        user.setPassword(encryptedPassword);
    }

    @Override
    public IssueTempCodeResDto validateUserPassword(Long userId, ValidatePasswordReqDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomExceptionData.USER_NOT_FOUND));

        if (!BCryptService.matches(dto.password(), user.getPassword())) {
            throw new CustomException(CustomExceptionData.USER_PASSWORD_NOT_MATCH);
        }

        String tempCode = createAndSetTempCode("pw:temp", userId);

        return new IssueTempCodeResDto(tempCode);
    }

    private String generateMaskedEmail(String email) {
        int atIdx = email.indexOf('@');

        if (atIdx <= 2) {
            return "**" + email.substring(atIdx);
        }

        String prefix = email.substring(0, 2);
        String maskFix = "*".repeat(email.substring(2, atIdx).length());

        return prefix + maskFix + email.substring(atIdx);
    }

    @Override
    @Transactional
    public FindUserSignupInfoResDto findUserSignupInfo(String recoveryCode) {
        String codeEncrypted = aesUtil.encrypt(recoveryCode);

        User user = userRepository.findByRecoveryCode(codeEncrypted)
                .orElseThrow(() -> new CustomException(CustomExceptionData.INVALID_PARAMETER, "잘못된 복구코드 입니다"));

        String bcryptEmail = user.getEmail() != null ? aesUtil.decrypt(user.getEmail()) : null;
        String maskedEmail = bcryptEmail != null ? generateMaskedEmail(bcryptEmail) : null;
        List<UserOAuthProvider> oauthTypeList = user.getOauthAccounts().stream().map(UserOAuthAccount::getProvider).toList();

        String newRecoveryCode = BCryptService.generateRecoveryCode();
        String encryptedCode = aesUtil.encrypt(newRecoveryCode);

        user.setRecoveryCode(encryptedCode);

        return new FindUserSignupInfoResDto(maskedEmail, oauthTypeList);
    }

    @Override
    public IssueTempCodeResDto findUserSignupInfoPassword(String recoveryCode, String email) {
        String codeEncrypted = aesUtil.encrypt(recoveryCode);

        User user = userRepository.findByRecoveryCode(codeEncrypted)
                .orElseThrow(() -> new CustomException(CustomExceptionData.INVALID_PARAMETER, "잘못된 정보 입니다"));

        String emailEncrypted = aesUtil.encrypt(email);

        if (user.getEmail() == null || user.getPassword() == null || !user.getEmail().equals(emailEncrypted)) {
            throw new CustomException(CustomExceptionData.INVALID_PARAMETER, "잘못된 정보 입니다");
        }

        String tempCode = createAndSetTempCode("pw:temp", user.getId());

        return new IssueTempCodeResDto(tempCode);
    }
}
