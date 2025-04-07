package com.example.pumpit.domain.user.service;

import com.example.pumpit.domain.user.dto.request.LoginUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.request.RegisterUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.response.LoginUserRequestTokenResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserResDto;
import com.example.pumpit.global.entity.User;

public interface UserService {
    User findUserById(Long userId);

    LoginUserRequestTokenResDto registerUserByEmail(RegisterUserByEmailReqDto dto);

    LoginUserRequestTokenResDto loginUserByEmail(LoginUserByEmailReqDto dto);

    LoginUserResDto getToken(String code);
}
