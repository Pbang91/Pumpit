package com.example.pumpit.domain.user.service;

import com.example.pumpit.domain.user.dto.req.LoginUserReqDto;
import com.example.pumpit.domain.user.dto.req.RegisterUserReqDto;
import com.example.pumpit.domain.user.dto.res.LoginUserResDto;
import com.example.pumpit.global.entity.User;

public interface UserService {
    User findUserById(Long userId);

    LoginUserResDto registerUser(RegisterUserReqDto dto);

    LoginUserResDto loginUser(LoginUserReqDto dto);
}
