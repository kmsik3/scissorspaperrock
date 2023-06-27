package com.game.scissorspaperrock.dto.request;

import com.game.scissorspaperrock.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpReq {

    @Schema(name = "player Email", example = "test@gmail.com")
    @NotBlank
    @Email
    private String email;

    @Schema(name = "player first name", example = "Peter")
    @NotBlank
    private String firstName;

    @Schema(name = "player last name", example = "Kim")
    @NotBlank
    private String lastName;

    @NotBlank
    private String password;

    @Schema(name = "player role", example = "USER")
    private Role role = Role.USER;
}
