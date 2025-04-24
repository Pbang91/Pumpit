package com.example.pumpit.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserByEmailReqDto(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Schema(
                description = "이메일 가입 시 email",
                example = "test@mail.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Email(message = "이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Schema(
                description = "이메일 가입 시 password. 최소 8자 , 최대 16자 입니다",
                example = "password1234",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 설정해야 합니다")
        String password,

        @NotBlank(message = "사용할 이름을 입력해주세요")
        @Schema(
                description = "프로필 표시 이름. 최소 5글자, 최대 10자 입니다",
                example = "펌프펌프잇",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String nickName,

        @Schema(
                description = "무선(모바일) 번호입니다. PW 찾기 시 활용됩니다.",
                example = "01012345678",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true
        )
        @Size(max = 11)
        String phone
) {
}
