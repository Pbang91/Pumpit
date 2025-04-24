package com.example.pumpit.domain.user.service;

import com.example.pumpit.domain.user.dto.request.LoginUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.request.RegisterUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.request.UpdateUserReqDto;
import com.example.pumpit.domain.user.dto.response.FindUserByIdResDto;
import com.example.pumpit.domain.user.dto.response.FindUserSignupInfoResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserRequestTokenResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserResDto;

public interface UserService {
    FindUserByIdResDto findUserById(Long userId);

    LoginUserRequestTokenResDto registerUserByEmail(RegisterUserByEmailReqDto dto);

    LoginUserRequestTokenResDto loginUserByEmail(LoginUserByEmailReqDto dto);

    LoginUserResDto getToken(String code, boolean remember);

    void updateUser(Long userId, UpdateUserReqDto dto);

    FindUserSignupInfoResDto findUserSignupInfo(String recoveryCode);
}
