package com.game.scissorspaperrock.service;

import com.game.scissorspaperrock.dto.request.GamePlayReq;
import com.game.scissorspaperrock.dto.response.GamePlayResp;
import com.game.scissorspaperrock.dto.response.GameWinRateResp;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor
class GameServiceTest {

    @Autowired
    private GameService gameService;

    private GamePlayReq gamePlayReq;

    @BeforeEach
    void setUp() {
        gamePlayReq = new GamePlayReq();
        gamePlayReq.setPlayerId("testEmail@gmail.com");
        gamePlayReq.setPlayerPick("ROCK");
    }

    @Test
    void gameStart() {
        ResponseEntity<GamePlayResp> response = gameService.gameStart(gamePlayReq);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(gamePlayReq.getPlayerId(), response.getBody().getPlayerId());
    }

    @Test
    void getWinRate() {

        String playerId = "testEmail@gmail.com";

        ResponseEntity<GamePlayResp> response1 = gameService.gameStart(gamePlayReq);

        GamePlayReq gamePlayReq2 = new GamePlayReq();
        gamePlayReq2.setPlayerId("testEmail@gmail.com");
        gamePlayReq2.setPlayerPick("ROCK");
        ResponseEntity<GamePlayResp> response2 = gameService.gameStart(gamePlayReq);

        GamePlayReq gamePlayReq3 = new GamePlayReq();
        gamePlayReq3.setPlayerId("testEmail@gmail.com");
        gamePlayReq3.setPlayerPick("ROCK");
        ResponseEntity<GamePlayResp> response3 = gameService.gameStart(gamePlayReq);

        GamePlayReq gamePlayReq4 = new GamePlayReq();
        gamePlayReq4.setPlayerId("testEmail@gmail.com");
        gamePlayReq4.setPlayerPick("ROCK");
        ResponseEntity<GamePlayResp> response4 = gameService.gameStart(gamePlayReq);


        ResponseEntity<GameWinRateResp> response = gameService.getWinRate(playerId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4, response.getBody().getCountGame());
    }
}