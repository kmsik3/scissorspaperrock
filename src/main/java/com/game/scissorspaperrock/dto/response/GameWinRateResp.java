package com.game.scissorspaperrock.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameWinRateResp {

    private String playerId;

    private int countGame;

    private int countWin;

    private int countLoss;

    private int countDraw;

    private String winRate;
}
