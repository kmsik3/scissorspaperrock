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

/**
 * This is a service class for all logic related to registering player and logging in player.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * This handles signing up a user. It firstly, builds a player object with data from request body.
     * After that, it gets a new jwt and refresh token and save jwt to database.
     * Finally, it returns created user data with jwt.
     * @param signUpReq
     * @return ResponseEntity with PlayerSignupResp
     */
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

    /**
     * This builds a player object and returns it.
     *
     * @param signUpReq
     * @return Player
     */
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

    /**
     * This saves a jwt to database with player object.
     *
     * @param result
     * @param jwtToken
     */
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

    /**
     * This builds final response for signing up.
     * Firstly, it builds PlayerSignupResp and covers it with ResponseEntity.
     *
     * @param result
     * @param jwtToken
     * @param refreshToken
     * @return ResponseEntity with PlayerSignupResp
     */
    private ResponseEntity<PlayerSignupResp> buildSignUpResponse(Player result, String jwtToken, String refreshToken) {
        PlayerSignupResp playerSignupResp = new PlayerSignupResp();
        playerSignupResp.setIdx(result.getIdx());
        playerSignupResp.setEmail(result.getEmail());
        playerSignupResp.setAccessToken(jwtToken);
        playerSignupResp.setRefreshToken(refreshToken);
        playerSignupResp.setUserRole(result.getRole().name());
        return ResponseEntity.ok().body(playerSignupResp);
    }

    /**
     * This handles user login.
     * Firstly, it checks user email and password are matching correctly with authenticationManager.
     * Secondly, it gets the user data from database and builds a new jwt and refresh token.
     * Thirdly, it revokes all tokens which was assigned to the user previously.
     * Fourthly, it saves the new jwt with player data.
     * Lastly, it calls a function to build final response for logging.
     *
     * @param request
     * @return ResponseEntity with AuthenticationResp
     */
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

    /**
     * This builds final response for logging in request.
     *
     * @param accessToken
     * @param refreshToken
     * @param player
     * @return ResponseEntity with AuthenticationResp
     */
    private ResponseEntity<AuthenticationResp> buildLogInResponse(String accessToken, String refreshToken, Player player) {
        AuthenticationResp authenticationResp = AuthenticationResp.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .player(player)
                .build();
        return ResponseEntity.ok().body(authenticationResp);
    }

    /**
     * This handles re-issuing jwt with refresh token.
     * In this function, FrontEnd changes expired jwt to refresh token and pass it in Authorization in HttpHeaders.
     * It uses refresh token to generate a new jwt.
     *
     * @param request
     * @param response
     * @throws IOException
     */
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

    /**
     * This revokes all tokens related to a player passed in parameter.
     *
     * @param player
     */
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
