package com.game.scissorspaperrock.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(404, "ERROR-COMMON-404", "PAGE NOT FOUND"),
    Unauthorized(401, "ERROR-COMMON-401", "YOUR EMAIL OR PASSWORD IS WRONG, PLEASE TRY AGAIN"),
    FORBIDDEN(403, "ERROR-COMMON-403", "YOU ARE NOT AUTHORIZED"),
    INTER_SERVER_ERROR(500, "ERROR-COMMON-500", "INTER SERVER ERROR"),
    EMAIL_DUPLICATION(400, "ERROR-PLAYER-400", "Player email is duplicated"),
    NO_GAME_FOUND(400, "ERROR-GAME-400", "No game record is found"),
    WRONG_HAND_CHOICE(400, "ERROR-GAME-400", "Player's pick is not one of Scissors, Paper or Rock"),
    ;

    private int status;
    private String errorCode;
    private String message;
}
