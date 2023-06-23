package com.game.scissorspaperrock.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GamePlayResp {

    private String playerId;
    private String playerPick;
    private String computerPick;
    private String result;
}
