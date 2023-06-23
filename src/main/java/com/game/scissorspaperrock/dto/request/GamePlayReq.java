package com.game.scissorspaperrock.dto.request;

import com.game.scissorspaperrock.entity.HandCustomConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
public class GamePlayReq {

    @NotBlank
    @Email
    private String playerId;

    @NotBlank
    @HandCustomConstraint
    private String playerPick;
}
