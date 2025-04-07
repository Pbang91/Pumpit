package com.example.pumpit.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserByEmailReqDto(
        @NotBlank
        @Schema(
                description = "이메일 가입 시 email",
                example = "example@mail.com",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true
        )
        @Email(message = "이메일 형식이 아닙니다")
        String email,

        @NotBlank
        @Schema(
                description = "이메일 가입 시 password. 최소 8자 이상, 최대 16자 입니다",
                example = "password1234",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true
        )
        @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 설정해야 합니다")
        String password
) {
}
