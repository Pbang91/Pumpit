package com.example.pumpit.domain.user.dto.response;

import com.example.pumpit.global.entity.UserOAuthProvider;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class FindUserByIdResDto {
    @Schema(description = "user id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    public Long id;

    @Schema(
            description = "user email",
            example = "test@mail.com",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true
    )
    public String email;

    @ArraySchema(
            schema = @Schema(
                    description = "사용자가 가입한 OAuth 서비스",
                    example = "GOOGLE",
                    requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                    nullable = true
            )
    )
    public List<UserOAuthProvider> oauthProviders;

    @Schema(description = "user createdAt", requiredMode = Schema.RequiredMode.REQUIRED)
    public LocalDateTime createdAt;

    public FindUserByIdResDto(
            Long id,
            String email,
            List<UserOAuthProvider> oauthProviders,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.email = email;
        this.oauthProviders = oauthProviders;
        this.createdAt = createdAt;
    }
}
