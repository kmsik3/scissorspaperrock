package com.game.scissorspaperrock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.scissorspaperrock.dto.request.AuthenticationReq;
import com.game.scissorspaperrock.dto.request.SignUpReq;
import com.game.scissorspaperrock.model.Player;
import com.game.scissorspaperrock.model.Role;
import com.game.scissorspaperrock.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlayerRepository playerRepository;

    private BCryptPasswordEncoder passwordEncoder;

    private AuthenticationReq authenticationReq;

    private SignUpReq signUpReq;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        signUpReq = new SignUpReq();
        signUpReq.setEmail("testEmail@gmail.com");
        signUpReq.setFirstName("Tester");
        signUpReq.setLastName("Kim");
        signUpReq.setPassword("testtest");

        authenticationReq = new AuthenticationReq();
        authenticationReq.setEmail("testEmail@gmail.com");
        authenticationReq.setPassword("testtest");


    }

    @Test
    void checkConnection() throws Exception {
        mockMvc.perform(get("/api/v1/player"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, you are connected"));
    }

    @Test
    void signUp() throws Exception {
        mockMvc.perform(post("/api/v1/player/signup")
                        .content(objectMapper.writeValueAsString(signUpReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testEmail@gmail.com"));
    }

    @Test
    void login() throws Exception {
        if (playerRepository.findByEmail(signUpReq.getEmail()).isEmpty()) {
            Player newPlayer = new Player();
            newPlayer.setEmail("testEmail@gmail.com");
            newPlayer.setFirstName("Tester");
            newPlayer.setLastName("Kim");
            newPlayer.setPassword(passwordEncoder.encode("testtest"));
            newPlayer.setRole(Role.USER);
            playerRepository.save(newPlayer);
        }
        authenticationReq.setEmail(signUpReq.getEmail());
        authenticationReq.setPassword("testtest");

        mockMvc.perform(post("/api/v1/player/login")
                        .content(objectMapper.writeValueAsString(authenticationReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void signUp_emailDuplicatedException() throws Exception {
        if (playerRepository.findByEmail(signUpReq.getEmail()).isEmpty()) {
            Player newPlayer = new Player();
            newPlayer.setEmail("testEmail@gmail.com");
            newPlayer.setFirstName("Tester");
            newPlayer.setLastName("Kim");
            newPlayer.setPassword(passwordEncoder.encode("testtest"));
            newPlayer.setRole(Role.USER);
            playerRepository.save(newPlayer);
        }
        mockMvc.perform(post("/api/v1/player/signup")
                        .content(objectMapper.writeValueAsString(signUpReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Player email is duplicated"));
    }
}