package com.example.pumpit.domain.user.dto.response;

import com.example.pumpit.global.entity.UserOAuthProvider;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record FindUserByIdResDto (
        @Schema(description = "user id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long id,

        @Schema(
                description = "user email",
                example = "test@mail.com",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true
        )
        String email,

        @Schema(
                description = "user nickname",
                example = "nickname",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                nullable = true
        )
        String nickName,

        @ArraySchema(
                schema = @Schema(
                        description = "사용자가 가입한 OAuth 서비스",
                        example = "GOOGLE",
                        requiredMode = Schema.RequiredMode.NOT_REQUIRED
                )
        )
        List<UserOAuthProvider> oauthProviders,

        @Schema(description = "user createdAt", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt
) {
}
