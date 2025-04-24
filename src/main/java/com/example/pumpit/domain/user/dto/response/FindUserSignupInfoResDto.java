package com.example.pumpit.domain.user.dto.response;

import com.example.pumpit.global.entity.UserOAuthProvider;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record FindUserSignupInfoResDto(
        @Schema(description = "마스킹된 이메일", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
        String email,

        @ArraySchema(
                schema = @Schema(
                        description = "OAuth 가입 타입. 없으면 빈 리스트",
                        implementation = String.class,
                        nullable = true
                )
        )
        List<UserOAuthProvider> oauthTypeList
) {
}
