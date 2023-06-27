package com.game.scissorspaperrock.dto.request;

import com.game.scissorspaperrock.entity.HandCustomConstraint;
import com.game.scissorspaperrock.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class GamePlayReq {

    @Schema(name = "player Email", example = "test@gmail.com")
    @NotBlank
    @Email
    private String playerId;

    private Role role;

    @NotBlank
    @HandCustomConstraint
    private String playerPick;

    @Schema(name = "Desired percentage of win", example = "0 ~ 100")
    @NotNull
    private Integer desiredWinPercentage;

    private boolean useAdminAdvantage = false;
}
