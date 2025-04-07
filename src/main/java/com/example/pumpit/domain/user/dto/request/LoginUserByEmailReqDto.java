package com.example.pumpit.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserByEmailReqDto(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Schema(description = "email 로그인 시 사용하는 email", requiredMode = Schema.RequiredMode.REQUIRED)
        @Email(message = "이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(description = "email 로그인 시 사용하는 password", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
