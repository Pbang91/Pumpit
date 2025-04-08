package com.example.pumpit.domain.user.controller;

import com.example.pumpit.domain.user.dto.request.LoginUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.request.RegisterUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.request.RegisterUserByOAuthReqDto;
import com.example.pumpit.domain.user.dto.response.LoginUserRequestTokenResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserResDto;
import com.example.pumpit.domain.user.service.UserService;
import com.example.pumpit.global.dto.ApiSuccessResDto;
import com.example.pumpit.global.entity.User;
import com.example.pumpit.global.exception.annotation.ApiExceptionData;
import com.example.pumpit.global.exception.annotation.ApiExceptionResponse;
import com.example.pumpit.global.exception.enums.CustomExceptionData;
import com.example.pumpit.global.util.ApiSuccessResUtil;
import com.example.pumpit.global.util.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/email")
    @SecurityRequirements
    @Operation(summary = "이메일 회원가입 API", description = "email을 통한 회원가입을 진행합니다.")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiExceptionResponse(
            value = {
                    @ApiExceptionData(errorCode = CustomExceptionData.USER_DUPLICATED)
            }
    )
    public ResponseEntity<ApiSuccessResDto<LoginUserRequestTokenResDto>> registerUserByEmail(
            @Valid @RequestBody RegisterUserByEmailReqDto dto
    ) {
        return ApiSuccessResUtil.success(
                userService.registerUserByEmail(dto),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/email/login")
    @SecurityRequirements
    @Operation(summary = "email 로그인 API", description = "email 로그인 API입니다")
    @ResponseStatus(HttpStatus.OK)
    @ApiExceptionResponse(
            value = {
                    @ApiExceptionData(errorCode = CustomExceptionData.USER_PASSWORD_NOT_MATCH),
                    @ApiExceptionData(errorCode = CustomExceptionData.USER_NOT_FOUND)
            }
    )
    public ResponseEntity<ApiSuccessResDto<LoginUserRequestTokenResDto>> loginUser(
            @Valid @RequestBody LoginUserByEmailReqDto dto
    ) {
        return ApiSuccessResUtil.success(
                userService.loginUserByEmail(dto),
                HttpStatus.OK
        );
    }

    @PostMapping("/oauth2")
    @SecurityRequirements
    @Operation(summary = "OAuth2 회원가입 API", description = "OAuth2를 통한 회원가입을 진행합니다.")
    @ResponseStatus(HttpStatus.OK)
    @ApiExceptionResponse(
            value = {
                    @ApiExceptionData(errorCode = CustomExceptionData.AUTH_CODE_NOT_FOUND)
            }
    )
    public ResponseEntity<ApiSuccessResDto<?>> registerUserByOauth2(
            @Valid @RequestBody RegisterUserByOAuthReqDto dto
    ) {
        // TODO : OAuth2 회원가입 API 구현
        return null;
    }

    @GetMapping("/auth")
    @SecurityRequirements
    @Operation(summary = "token을 발급받기 위한 API", description = "auth 최종 단계 API 입니다. accessToken 및 refreshToken을 발급받습니다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiSuccessResDto<LoginUserResDto>> getAccessToken(
            @Parameter(name = "c", description = "temp code", required = true)
            @RequestParam(name = "c") String code
    ) {
        return ApiSuccessResUtil.success(
                userService.getToken(code),
                HttpStatus.OK
        );
    }

    @GetMapping
    @Operation(summary = "회원 정보 조회 API", description = "본인 정보를 조회합니다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiSuccessResDto<User>> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();
        return ApiSuccessResUtil.success(userService.findUserById(userId), HttpStatus.OK);
    }
}
