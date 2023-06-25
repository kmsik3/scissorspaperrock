package com.game.scissorspaperrock.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.scissorspaperrock.configuration.JwtService;
import com.game.scissorspaperrock.dto.request.AuthenticationReq;
import com.game.scissorspaperrock.dto.request.SignUpReq;
import com.game.scissorspaperrock.dto.response.AuthenticationResp;
import com.game.scissorspaperrock.dto.response.PlayerSignupResp;
import com.game.scissorspaperrock.entity.ErrorCode;
import com.game.scissorspaperrock.entity.TokenType;
import com.game.scissorspaperrock.exception.EmailDuplicateException;
import com.game.scissorspaperrock.model.Player;
import com.game.scissorspaperrock.model.Role;
import com.game.scissorspaperrock.model.Token;
import com.game.scissorspaperrock.repository.PlayerRepository;
import com.game.scissorspaperrock.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ResponseEntity<PlayerSignupResp> signUp(SignUpReq signUpReq) {
        try {
            Player newPlayer = buildNewPlayer(signUpReq);
            Player result = playerRepository.save(newPlayer);
            String jwtToken = jwtService.generateToken(newPlayer);
            String refreshToken = jwtService.generateRefreshToken(newPlayer);
            savePlayerToken(result, jwtToken);
            return buildSignUpResponse(result, jwtToken, refreshToken);
        } catch (DataIntegrityViolationException e) {
            throw new EmailDuplicateException(String.format("Player's email: [%s] is duplicated", signUpReq.getEmail()), ErrorCode.EMAIL_DUPLICATION);
        }
    }

    private Player buildNewPlayer(SignUpReq signUpReq) {
        String encodedPassword = bCryptPasswordEncoder.encode(signUpReq.getPassword());
        Player newPlayer = new Player();
        newPlayer.setEmail(signUpReq.getEmail());
        newPlayer.setPassword(encodedPassword);
        newPlayer.setFirstName(signUpReq.getFirstName());
        newPlayer.setLastName(signUpReq.getLastName());
        if ("ADMIN".equalsIgnoreCase(signUpReq.getRole().name())) {
            newPlayer.setRole(Role.ADMIN);
        } else {
            newPlayer.setRole(Role.USER);
        }
        return newPlayer;
    }

    private void savePlayerToken(Player result, String jwtToken) {
        Token token = Token.builder()
                .player(result)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private ResponseEntity<PlayerSignupResp> buildSignUpResponse(Player result, String jwtToken, String refreshToken) {
        PlayerSignupResp playerSignupResp = new PlayerSignupResp();
        playerSignupResp.setIdx(result.getIdx());
        playerSignupResp.setEmail(result.getEmail());
        playerSignupResp.setAccessToken(jwtToken);
        playerSignupResp.setRefreshToken(refreshToken);
        playerSignupResp.setUserRole(result.getRole().name());
        return ResponseEntity.ok().body(playerSignupResp);
    }

    public ResponseEntity<AuthenticationResp> login(AuthenticationReq request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        Player player = playerRepository.findByEmail(request.getEmail())
                .orElseThrow();
        String jwtToken = jwtService.generateToken(player);
        String refreshToken = jwtService.generateRefreshToken(player);
        revokeAllUserTokens(player);
        savePlayerToken(player, jwtToken);
        return buildLogInResponse(jwtToken, refreshToken, player);
    }

    private ResponseEntity<AuthenticationResp> buildLogInResponse(String accessToken, String refreshToken, Player player) {
        AuthenticationResp authenticationResp = AuthenticationResp.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .player(player)
                .build();
        return ResponseEntity.ok().body(authenticationResp);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            Player player = this.playerRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, player)) {
                String accessToken = jwtService.generateToken(player);
                revokeAllUserTokens(player);
                savePlayerToken(player, accessToken);
                AuthenticationResp authenticationResp = AuthenticationResp.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authenticationResp);
            }
        }
    }

    private void revokeAllUserTokens(Player player) {
        List<Token> validPlayerTokens = tokenRepository.findAllValidTokenByUser(player.getIdx());
        if (validPlayerTokens.isEmpty())
            return;
        validPlayerTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validPlayerTokens);
    }
}
