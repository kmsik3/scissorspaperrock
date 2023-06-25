package com.game.scissorspaperrock.dto.request;

import com.game.scissorspaperrock.entity.HandCustomConstraint;
import com.game.scissorspaperrock.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class GamePlayReq {

    @NotBlank
    @Email
    private String playerId;

    private Role role;

    @NotBlank
    @HandCustomConstraint
    private String playerPick;

    @NotNull
    private Integer desiredWinPercentage;

    private boolean useAdminAdvantage = false;
}
