package com.example.pumpit.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiExceptionResDto {
    @Schema(description = "에러 코드", example = "0001")
    private String code;

    @Schema(description = "에러 설명", example = "해당 샵을 찾을 수 없습니다")
    private String description;

    @Schema(description = "에러 추가 설명", example = "ID가 존재하지 않습니다")
    private String details;
}
