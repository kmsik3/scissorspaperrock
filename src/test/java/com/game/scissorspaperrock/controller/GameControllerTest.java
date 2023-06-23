package com.game.scissorspaperrock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.scissorspaperrock.dto.request.GamePlayReq;
import com.game.scissorspaperrock.model.Game;
import com.game.scissorspaperrock.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository gameRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void checkConnection() throws Exception {
        mockMvc.perform(get("/api/v1/game/hello"))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(value = "spring")
    @Test
    void gameStart() throws Exception {
        GamePlayReq gamePlayReq = new GamePlayReq();
        gamePlayReq.setPlayerId("testEmail@gmail.com");
        gamePlayReq.setPlayerPick("ROCK");
        mockMvc.perform(post("/api/v1/game/start")
                        .content(objectMapper.writeValueAsString(gamePlayReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value("testEmail@gmail.com"))
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.playerPick").value("ROCK"));
    }

    @WithMockUser(value = "spring")
    @Test
    void gameStartWithWrongHand() throws Exception {
        GamePlayReq gamePlayReq = new GamePlayReq();
        gamePlayReq.setPlayerId("testEmail@gmail.com");
        gamePlayReq.setPlayerPick("WRONGHAND");
        mockMvc.perform(post("/api/v1/game/start")
                        .content(objectMapper.writeValueAsString(gamePlayReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Player's pick is not one of Scissors, Paper or Rock"));
    }

    @WithMockUser(value = "spring")
    @Test
    void calculateWinRatio() throws Exception {
        Game winGame = new Game();
        winGame.setPlayerId("testEmail@gmail.com");
        winGame.setPlayerPick("ROCK");
        winGame.setComputerPick("SCISSORS");
        winGame.setResult("WIN");

        gameRepository.save(winGame);

        Game drawGame = new Game();
        drawGame.setPlayerId("testEmail@gmail.com");
        drawGame.setPlayerPick("ROCK");
        drawGame.setComputerPick("ROCK");
        drawGame.setResult("DRAW");

        gameRepository.save(drawGame);

        Game lossGame = new Game();
        lossGame.setPlayerId("testEmail@gmail.com");
        lossGame.setPlayerPick("ROCK");
        lossGame.setComputerPick("PAPER");
        lossGame.setResult("LOSS");

        gameRepository.save(lossGame);

        Game drawGame2 = new Game();
        drawGame2.setPlayerId("testEmail@gmail.com");
        drawGame2.setPlayerPick("SCISSORS");
        drawGame2.setComputerPick("SCISSORS");
        drawGame2.setResult("DRAW");

        gameRepository.save(drawGame2);

        String playerId = "testEmail@gmail.com";
        mockMvc.perform(get("/api/v1/game/calculate/{playerId}", playerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winRate").value("25.00"))
                .andExpect(jsonPath("$.countGame").value(4))
                .andExpect(jsonPath("$.countWin").value(1))
                .andExpect(jsonPath("$.countLoss").value(1))
                .andExpect(jsonPath("$.countDraw").value(2));
    }
}