package com.example.pumpit.domain.user.controller;

import com.example.pumpit.domain.user.dto.req.LoginUserReqDto;
import com.example.pumpit.domain.user.dto.req.RegisterUserReqDto;
import com.example.pumpit.domain.user.dto.res.LoginUserResDto;
import com.example.pumpit.domain.user.service.UserService;
import com.example.pumpit.global.dto.ApiSuccessResDto;
import com.example.pumpit.global.entity.User;
import com.example.pumpit.global.exception.annotation.ApiExceptionData;
import com.example.pumpit.global.exception.annotation.ApiExceptionResponse;
import com.example.pumpit.global.exception.enums.CustomerExceptionData;
import com.example.pumpit.global.util.ApiSuccessResUtil;
import com.example.pumpit.global.util.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements
    @Operation(summary = "회원가입 API", description = "email 또는 Oauth2를 통한 회원가입을 진행합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiExceptionResponse(
            value = {
                    @ApiExceptionData(errorCode = CustomerExceptionData.USER_DUPLICATED)
            }
    )
    public ResponseEntity<ApiSuccessResDto<LoginUserResDto>> registerUser(@RequestBody RegisterUserReqDto dto) {
        LoginUserResDto response = userService.registerUser(dto);

        return ApiSuccessResUtil.success(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements
    @Operation(summary = "로그인 API", description = "로그인 API입니다")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiSuccessResDto<LoginUserResDto>> loginUser(@RequestBody LoginUserReqDto dto) {
        LoginUserResDto response = userService.loginUser(dto);

        return ApiSuccessResUtil.success(response, HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원 정보 조회 API", description = "본인 정보를 조회합니다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiSuccessResDto<User>> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();
        return ApiSuccessResUtil.success(userService.findUserById(userId), HttpStatus.OK);
    }
}
