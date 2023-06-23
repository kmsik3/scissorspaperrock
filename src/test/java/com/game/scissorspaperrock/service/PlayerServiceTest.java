package com.game.scissorspaperrock.service;

import com.game.scissorspaperrock.dto.request.AuthenticationReq;
import com.game.scissorspaperrock.dto.request.SignUpReq;
import com.game.scissorspaperrock.dto.response.AuthenticationResp;
import com.game.scissorspaperrock.dto.response.PlayerSignupResp;
import com.game.scissorspaperrock.model.Player;
import com.game.scissorspaperrock.model.Role;
import com.game.scissorspaperrock.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerRepository playerRepository;

    private SignUpReq signUpReq;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        signUpReq = new SignUpReq();
        signUpReq.setEmail("testEmail@gmail.com");
        signUpReq.setFirstName("Tester");
        signUpReq.setLastName("Kim");
        signUpReq.setPassword("testtest");
    }

    @Test
    void signUp() {
        ResponseEntity<PlayerSignupResp> response = playerService.signUp(signUpReq);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(signUpReq.getEmail(), response.getBody().getEmail());

    }

    @Test
    void login() {
        if (playerRepository.findByEmail(signUpReq.getEmail()).isEmpty()) {
            Player newPlayer = new Player();
            newPlayer.setEmail(signUpReq.getEmail());
            newPlayer.setPassword(passwordEncoder.encode(signUpReq.getPassword()));
            newPlayer.setFirstName(signUpReq.getFirstName());
            newPlayer.setLastName(signUpReq.getLastName());
            newPlayer.setRole(Role.USER);
            playerRepository.save(newPlayer);
        }


        AuthenticationReq authenticationReq = new AuthenticationReq();
        authenticationReq.setEmail(signUpReq.getEmail());
        authenticationReq.setPassword(signUpReq.getPassword());

        ResponseEntity<AuthenticationResp> response = playerService.login(authenticationReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}