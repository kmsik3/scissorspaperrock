package com.game.scissorspaperrock.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerSignupResp {

    private long idx;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String userRole;
}
