package com.example.pumpit.domain.user.controller;

import com.example.pumpit.domain.user.dto.request.LoginUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.request.RegisterUserByEmailReqDto;
import com.example.pumpit.domain.user.dto.request.RegisterUserByOAuthReqDto;
import com.example.pumpit.domain.user.dto.request.UpdateUserReqDto;
import com.example.pumpit.domain.user.dto.response.FindUserByIdResDto;
import com.example.pumpit.domain.user.dto.response.FindUserSignupInfoResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserRequestTokenResDto;
import com.example.pumpit.domain.user.dto.response.LoginUserResDto;
import com.example.pumpit.domain.user.service.UserService;
import com.example.pumpit.global.dto.ApiSuccessResDto;
import com.example.pumpit.global.exception.annotation.ApiExceptionData;
import com.example.pumpit.global.exception.annotation.ApiExceptionResponse;
import com.example.pumpit.global.exception.enums.CustomExceptionData;
import com.example.pumpit.global.util.ApiSuccessResUtil;
import com.example.pumpit.global.util.CustomUserDetails;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "사용자 회원가입, 조회, 수정을 진행하는 API")
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

    // TODO : OAuth2 회원가입 API 구현
    @Hidden
    @PostMapping("/oauth2")
    @SecurityRequirements
    @Operation(summary = "OAuth2 회원가입 API", description = "OAuth2를 통한 회원가입을 진행합니다.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiSuccessResDto<?>> registerUserByOauth2(
            @Valid @RequestBody RegisterUserByOAuthReqDto dto
    ) {
        return null;
    }

    @GetMapping("/auth")
    @SecurityRequirements
    @Operation(summary = "token을 발급받기 위한 API", description = "auth 최종 단계 API 입니다. accessToken 및 refreshToken을 발급받습니다.")
    @ResponseStatus(HttpStatus.OK)
    @ApiExceptionResponse(
            value = {
                    @ApiExceptionData(errorCode = CustomExceptionData.AUTH_CODE_NOT_FOUND)
            }
    )
    public ResponseEntity<ApiSuccessResDto<LoginUserResDto>> getAccessToken(
            @Parameter(name = "c", description = "temp code", required = true)
            @RequestParam(name = "c") String code,

            @Parameter(name = "r", description = "remember. default value false")
            @RequestParam(name = "r", defaultValue = "false") boolean remember
    ) {
        return ApiSuccessResUtil.success(
                userService.getToken(code,remember),
                HttpStatus.OK
        );
    }

    @GetMapping("/me")
    @Operation(summary = "본인 정보 조회 API", description = "본인 정보를 조회합니다.")
    @ResponseStatus(HttpStatus.OK)
    @ApiExceptionResponse(
            value = {
                    @ApiExceptionData(errorCode = CustomExceptionData.USER_NOT_FOUND)
            }
    )
    public ResponseEntity<ApiSuccessResDto<FindUserByIdResDto>> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();
        return ApiSuccessResUtil.success(userService.findUserById(userId), HttpStatus.OK);
    }

    @PatchMapping("/me")
    @Operation(summary = "본인 정보 수정", description = "본인 정보를 수정합니다")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiExceptionResponse(
            value = {
                    @ApiExceptionData(errorCode = CustomExceptionData.USER_NOT_FOUND)
            }
    )
    public ResponseEntity<Void> updateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateUserReqDto dto
    ) {
        Long userId = userDetails.getId();
        userService.updateUser(userId, dto);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/signup-info")
    @Operation(
            summary = "가입한 정보 조회 API",
            description = "가입한 정보(빙법)를 조회합니다\n\n" +
                    "1. 이메일 정보가 있을 시 일부 스키마된 email을 전달합니다\n" +
                    "2. 소셜 채널 정보가 있을 시 소셜 채널 타입을 전달합니다"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiExceptionResponse(
            value = {
                    @ApiExceptionData(errorCode = CustomExceptionData.INVALID_PARAMETER, details = "잘못된 복구코드 입니다")
            }
    )
    public ResponseEntity<ApiSuccessResDto<FindUserSignupInfoResDto>> findUserSignupInfo(
            @Parameter(name = "rc", description = "recoveryCode",required = true)
            @RequestParam(name = "rc") String recoveryCode
    ) {
        return ApiSuccessResUtil.success(userService.findUserSignupInfo(recoveryCode), HttpStatus.OK);
    }

}
