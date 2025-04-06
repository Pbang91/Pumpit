package com.example.pumpit.domain.user.service;

import com.example.pumpit.domain.user.dto.req.RegisterUserReqDto;
import com.example.pumpit.domain.user.dto.res.RegisterUserResDto;

public interface UserService {
    RegisterUserResDto registerUser(RegisterUserReqDto registerUserReqDto);
}
