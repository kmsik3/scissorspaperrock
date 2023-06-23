package com.game.scissorspaperrock.dto.response;

import com.game.scissorspaperrock.model.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResp {

    private String accessToken;

    private String refreshToken;

    private Player player;
}
