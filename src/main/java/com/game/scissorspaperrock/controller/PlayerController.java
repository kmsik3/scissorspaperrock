package com.game.scissorspaperrock.controller;


import com.game.scissorspaperrock.dto.request.AuthenticationReq;
import com.game.scissorspaperrock.dto.request.SignUpReq;
import com.game.scissorspaperrock.dto.response.AuthenticationResp;
import com.game.scissorspaperrock.dto.response.PlayerSignupResp;
import com.game.scissorspaperrock.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * This controller is for registration and login of users
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/player")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth management APIs")
@CrossOrigin
public class PlayerController {

    private final PlayerService playerService;

    @Operation(description = "request for registration",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully registered"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            })
    @PostMapping("/signup")
    public ResponseEntity<PlayerSignupResp> signUp(@RequestBody @Valid SignUpReq signUpReq) {
        log.info("Signup endpoint is reached with email {}", signUpReq.getEmail());
        return playerService.signUp(signUpReq);
    }

    @Operation(description = "request for logging in",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully logged in"),
                    @ApiResponse(responseCode = "401", description = "Login data is wrong"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResp> login(@RequestBody AuthenticationReq request) {
        log.info("Login endpoint is reached with email {}", request.getEmail());
        return playerService.login(request);
    }

    @Operation(description = "request for refreshing token",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully obtaining new token"),
                    @ApiResponse(responseCode = "401", description = "refresh token is not valid"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            })
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        playerService.refreshToken(request, response);
    }
}
