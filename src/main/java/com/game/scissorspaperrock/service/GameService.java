package com.game.scissorspaperrock.service;

import com.game.scissorspaperrock.dto.request.GamePlayReq;
import com.game.scissorspaperrock.dto.response.GamePlayResp;
import com.game.scissorspaperrock.dto.response.GameWinRateResp;
import com.game.scissorspaperrock.entity.ErrorCode;
import com.game.scissorspaperrock.entity.Hand;
import com.game.scissorspaperrock.entity.Result;
import com.game.scissorspaperrock.exception.NoGameRecordFoundException;
import com.game.scissorspaperrock.model.Game;
import com.game.scissorspaperrock.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public ResponseEntity<GamePlayResp> gameStart(GamePlayReq gamePlayReq) {
        Hand computerPick = Hand.getRandomPick();
        log.info("This is what computer picks: {}", computerPick.name());
        Result result = checkResult(Hand.valueOf(gamePlayReq.getPlayerPick()), computerPick);
        log.info("This is the result of the game: {}", result.name());
        Game game = setGame(gamePlayReq, computerPick, result);
        Game saveResult = gameRepository.save(game);
        return buildGamePlayResponse(saveResult);
    }

    public ResponseEntity<GameWinRateResp> getWinRate(String playerId) {
        List<Game> gameResults = gameRepository.findByPlayerId(playerId);
        if (gameResults.isEmpty()) {
            throw new NoGameRecordFoundException(String.format("There is no game record with player id [%s]", playerId), ErrorCode.NO_GAME_FOUND);
        }
        return calculateWinRate(gameResults, playerId);
    }


    private Game setGame(GamePlayReq gamePlayReq, Hand computerPick, Result result) {
        Game game = new Game();
        game.setPlayerId(gamePlayReq.getPlayerId());
        game.setPlayerPick(gamePlayReq.getPlayerPick());
        game.setComputerPick(computerPick.name());
        game.setResult(result.name());
        return game;
    }

    private Result checkResult(Hand playerPick, Hand computerPick) {
        if (playerPick.isWin(computerPick)) {
            return Result.WIN;
        } else if (computerPick.isWin(playerPick)) {
            return Result.LOSS;
        } else {
            return Result.DRAW;
        }
    }

    private ResponseEntity<GamePlayResp> buildGamePlayResponse(Game game) {
        GamePlayResp responseBody = new GamePlayResp();
        responseBody.setPlayerId(game.getPlayerId());
        responseBody.setPlayerPick(game.getPlayerPick());
        responseBody.setComputerPick(game.getComputerPick());
        responseBody.setResult(game.getResult());
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    private ResponseEntity<GameWinRateResp> calculateWinRate(List<Game> results, String playerId) {
        double countGame = results.size();
        double countWin = results.stream().filter(i -> "WIN".equalsIgnoreCase(i.getResult())).count();
        int countDraw = Long.valueOf(results.stream().filter(i -> "DRAW".equalsIgnoreCase(i.getResult())).count()).intValue();
        int countLoss = Long.valueOf(results.stream().filter(i -> "LOSS".equalsIgnoreCase(i.getResult())).count()).intValue();
        double winRate = (countWin / countGame) * 100;
        String winningPercentage = String.format("%.2f", winRate);

        return buildWinRateResponse(playerId, countGame, countWin, countDraw, countLoss, winningPercentage);
    }

    private ResponseEntity<GameWinRateResp> buildWinRateResponse(String playerId,
                                                                 double countGame, double countWin,
                                                                 int countDraw, int countLoss,
                                                                 String winningPercentage) {
        GameWinRateResp gameWinRateResp = new GameWinRateResp();
        gameWinRateResp.setPlayerId(playerId);
        gameWinRateResp.setWinRate(winningPercentage);
        gameWinRateResp.setCountGame((int) countGame);
        gameWinRateResp.setCountWin((int) countWin);
        gameWinRateResp.setCountDraw(countDraw);
        gameWinRateResp.setCountLoss(countLoss);
        return ResponseEntity.ok().body(gameWinRateResp);
    }
}
