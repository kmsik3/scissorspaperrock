package com.game.scissorspaperrock.dto.request;

import com.game.scissorspaperrock.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpReq {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String password;

    private Role role = Role.USER;
}
