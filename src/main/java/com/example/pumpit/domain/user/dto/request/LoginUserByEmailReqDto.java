package com.example.pumpit.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginUserByEmailReqDto(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Schema(
                description = "email 로그인 시 사용하는 email",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "test@mail.com"
        )
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(
                description = "email 로그인 시 사용하는 password",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "password1234"
        )
        @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 설정해야 합니다")
        String password
) {
}
