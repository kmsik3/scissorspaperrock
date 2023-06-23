package com.game.scissorspaperrock.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.game.scissorspaperrock.entity.ErrorCode;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status;
    private String message;
    private String code;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.code = errorCode.getErrorCode();
    }

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
