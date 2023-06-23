package com.game.scissorspaperrock.exception;

import com.game.scissorspaperrock.entity.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoGameRecordFoundException extends RuntimeException {
    private ErrorCode errorCode;

    public NoGameRecordFoundException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
