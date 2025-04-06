package com.example.pumpit.domain.user.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterUserResDto {
    @Schema(description = "로그인 성공 시 전달받는 토큰", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    public String accessToken;

    @Schema(description = "Oauth2 진행 시 전달 받는 redirect url", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true)
    public String redirectUrl;
}
