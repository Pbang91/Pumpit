package com.example.pumpit.domain.user.service;

import com.example.pumpit.domain.user.dto.request.*;
import com.example.pumpit.domain.user.dto.response.FindUserByIdResDto;
import com.example.pumpit.domain.user.dto.response.FindUserSignupInfoResDto;
import com.example.pumpit.domain.user.dto.response.IssueTempCodeResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserResDto;

public interface UserService {
    FindUserByIdResDto findUserById(Long userId);

    IssueTempCodeResDto registerUserByEmail(RegisterUserByEmailReqDto dto);

    IssueTempCodeResDto loginUserByEmail(LoginUserByEmailReqDto dto);

    LoginUserResDto getToken(String code, boolean remember);

    void updateUser(Long userId, UpdateUserReqDto dto);

    void changePassword(ChangePasswordReqDto dto);

    IssueTempCodeResDto validateUserPassword(Long userId, ValidatePasswordReqDto dto);

    FindUserSignupInfoResDto findUserSignupInfo(String recoveryCode);

    IssueTempCodeResDto findUserSignupInfoPassword(String recoveryCode, String email);
}
