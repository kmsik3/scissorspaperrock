package com.game.scissorspaperrock.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class PlayerIdNotUniqueException extends DataIntegrityViolationException {
    public PlayerIdNotUniqueException(String msg) {
        super(String.format("The player id: [%s] is not unique, please try again with different email address", msg));
    }
}
