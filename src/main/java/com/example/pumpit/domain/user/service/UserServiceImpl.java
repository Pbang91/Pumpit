package com.example.pumpit.domain.user.service;

import com.example.pumpit.domain.user.dto.req.LoginUserReqDto;
import com.example.pumpit.domain.user.dto.req.RegisterUserReqDto;
import com.example.pumpit.domain.user.dto.res.LoginUserResDto;
import com.example.pumpit.domain.user.enums.RegisterUserType;
import com.example.pumpit.domain.user.repository.UserRepository;
import com.example.pumpit.global.entity.User;
import com.example.pumpit.global.exception.CustomException;
import com.example.pumpit.global.exception.enums.CustomerExceptionData;
import com.example.pumpit.global.util.JwtService;
import com.example.pumpit.global.util.PasswordEncoder;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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

    public UserServiceImpl(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomerExceptionData.USER_NOT_FOUND));
    }

    private boolean existingUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomerExceptionData.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public LoginUserResDto registerUser(RegisterUserReqDto dto) {
        if (dto.type().equals(RegisterUserType.EMAIL)) {
            if (existingUserByEmail(dto.email())) {
                throw new CustomException(CustomerExceptionData.USER_DUPLICATED);
            }

            User user = User.builder()
                    .email(dto.email())
                    .password(PasswordEncoder.encode(dto.password()))
                    .nickName(dto.email().split("@")[0])
                    .build();

            User savedUser = userRepository.save(user);
            String accessToken = jwtService.generateAccessToken(savedUser.getId());

            return LoginUserResDto.builder().accessToken(accessToken).build();
        }

        return null;
    }

    @Override
    public LoginUserResDto loginUser(LoginUserReqDto dto) {
        if (dto.email() != null && dto.password() != null) {
            User user = findUserByEmail(dto.email());

            if (!PasswordEncoder.matches(dto.password(), user.getPassword())) {
                throw new CustomException(CustomerExceptionData.USER_PASSWORD_NOT_MATCH);
            }

            String accessToken = jwtService.generateAccessToken(user.getId());

            return LoginUserResDto.builder()
                    .accessToken(accessToken)
                    .build();
        } else {
            // TODO: Oatuh2 로그인 구현
            return null;
        }
    }
}
