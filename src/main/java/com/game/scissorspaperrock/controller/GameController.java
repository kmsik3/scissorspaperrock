package com.game.scissorspaperrock.controller;


import com.game.scissorspaperrock.dto.request.GamePlayReq;
import com.game.scissorspaperrock.dto.response.GamePlayResp;
import com.game.scissorspaperrock.dto.response.GameWinRateResp;
import com.game.scissorspaperrock.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This controller is for starting a game, getting win rate and changing percentage of computer's pick
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game")
@CrossOrigin
public class GameController {

    private final GameService gameService;

    @Operation(description = "request for starting scissors paper rock game",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(responseCode = "201", description = "A result of the game is returned and the game is saved to DB"),
                    @ApiResponse(responseCode = "401", description = "You are not authenticated"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            })
    @PostMapping("/start")
    public ResponseEntity<GamePlayResp> gameStart(@RequestBody @Valid GamePlayReq gamePlayReq) {
        log.info("Player name: [{}] is joined", gamePlayReq.getPlayerId());
        return gameService.gameStart(gamePlayReq);
    }

    @Operation(description = "request for calculating the percentage of winning",
            security = {@SecurityRequirement(name = "bearer-key")},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Percentage of winning is returned"),
                    @ApiResponse(responseCode = "401", description = "You are not authenticated"),
                    @ApiResponse(responseCode = "500", description = "internal server error")
            })
    @GetMapping("/calculate/{playerId}")
    public ResponseEntity<GameWinRateResp> calculateWinRatio(@PathVariable("playerId") String playerId) {
        log.info("Player name: [{}] tries to calculate win ratio", playerId);
        return gameService.getWinRate(playerId);
    }
}
