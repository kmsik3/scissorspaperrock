package com.game.scissorspaperrock.service;

import com.game.scissorspaperrock.dto.request.GamePlayReq;
import com.game.scissorspaperrock.dto.response.GamePlayResp;
import com.game.scissorspaperrock.dto.response.GameWinRateResp;
import com.game.scissorspaperrock.entity.ErrorCode;
import com.game.scissorspaperrock.entity.Hand;
import com.game.scissorspaperrock.entity.Result;
import com.game.scissorspaperrock.exception.NoGameRecordFoundException;
import com.game.scissorspaperrock.model.Game;
import com.game.scissorspaperrock.model.Role;
import com.game.scissorspaperrock.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * This is a service class for all logic related to playing the game, checking win rate and changing percentage of win.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    /**
     * This handles game play. Firstly, it checks the role of user
     * and also check using the advantage of winning percentage.
     * After that, it checks the result of the game and save it to database and return the result.
     * @param gamePlayReq
     * @return ResponseEntity with GamePlayResp
     */
    public ResponseEntity<GamePlayResp> gameStart(GamePlayReq gamePlayReq) {
        Hand computerPick;
        log.info("This is role value from the requestBody: {}", gamePlayReq.getRole().name());
        if (Role.ADMIN.name().equalsIgnoreCase(gamePlayReq.getRole().name()) &&
                gamePlayReq.isUseAdminAdvantage()) {
            log.info("Using Admin advantage for winning percentage");
            computerPick = setHandPercentage(gamePlayReq.getDesiredWinPercentage(), gamePlayReq.getPlayerPick());
        } else {
            log.info("random percentage of winning");
            computerPick = Hand.getRandomPick();
        }
        Result result = checkResult(Hand.valueOf(gamePlayReq.getPlayerPick()), computerPick);
        Game game = setGame(gamePlayReq, computerPick, result);
        Game saveResult = gameRepository.save(game);
        return buildGamePlayResponse(saveResult);
    }

    /**
     * This handles getting win rate of a user.
     * Firstly, it gets all game results of a user
     * and if there is no game record, it throws an exception named NoGameRecordFoundException.
     * It triggers calculateWinRate function to get win rate and some other data
     * (E.g. total game count, total win count, etc)
     * @param playerId
     * @return ResponseEntity with GameWinRateResp
     */
    public ResponseEntity<GameWinRateResp> getWinRate(String playerId) {
        List<Game> gameResults = gameRepository.findByPlayerId(playerId);
        if (gameResults.isEmpty()) {
            throw new NoGameRecordFoundException(String.format("There is no game record with player id [%s]", playerId), ErrorCode.NO_GAME_FOUND);
        }
        return calculateWinRate(gameResults, playerId);
    }

    /**
     * This sets Game entity to save it to database
     *
     * @param gamePlayReq
     * @param computerPick
     * @param result
     * @return Game
     */
    private Game setGame(GamePlayReq gamePlayReq, Hand computerPick, Result result) {
        Game game = new Game();
        game.setPlayerId(gamePlayReq.getPlayerId());
        game.setPlayerPick(gamePlayReq.getPlayerPick());
        game.setComputerPick(computerPick.name());
        game.setResult(result.name());
        return game;
    }

    /**
     * This checks the result of a game.
     * It uses function "isWin" in Enum.
     *
     * @param playerPick
     * @param computerPick
     * @return Result enum
     */
    private Result checkResult(Hand playerPick, Hand computerPick) {
        if (playerPick.isWin(computerPick)) {
            return Result.WIN;
        } else if (computerPick.isWin(playerPick)) {
            return Result.LOSS;
        } else {
            return Result.DRAW;
        }
    }

    /**
     * This builds final return for Game playing.
     * It builds GamePlayResp entity and covers it with ResponseEntity.
     *
     * @param game
     * @return ResponseEntity with GamePlayResp
     */
    private ResponseEntity<GamePlayResp> buildGamePlayResponse(Game game) {
        GamePlayResp responseBody = new GamePlayResp();
        responseBody.setPlayerId(game.getPlayerId());
        responseBody.setPlayerPick(game.getPlayerPick());
        responseBody.setComputerPick(game.getComputerPick());
        responseBody.setResult(game.getResult());
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    /**
     * This calculates win rate and collect some other data like total game count, total win count etc.
     * After that, it triggers buildWinRateResponse function
     * in order to build final response for calculating win rate response.
     *
     * @param results
     * @param playerId
     * @return ResponseEntity with GameWinRateResp
     */
    private ResponseEntity<GameWinRateResp> calculateWinRate(List<Game> results, String playerId) {
        double countGame = results.size();
        double countWin = results.stream().filter(i -> "WIN".equalsIgnoreCase(i.getResult())).count();
        int countDraw = Long.valueOf(results.stream().filter(i -> "DRAW".equalsIgnoreCase(i.getResult())).count()).intValue();
        int countLoss = Long.valueOf(results.stream().filter(i -> "LOSS".equalsIgnoreCase(i.getResult())).count()).intValue();
        double winRate = (countWin / countGame) * 100;
        String winningPercentage = String.format("%.2f", winRate);

        return buildWinRateResponse(playerId, countGame, countWin, countDraw, countLoss, winningPercentage);
    }

    /**
     * This builds final response for calculating win rate with parameters from calculateWinRate function.
     *
     * @param playerId
     * @param countGame
     * @param countWin
     * @param countDraw
     * @param countLoss
     * @param winningPercentage
     * @return ResponseEntity with GameWinRateResp
     */
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

    /**
     * This sets specific percentage of what computer picks (Scissors, paper or rock).
     * For this, it divides three cases of what a user picks and adds percentage of random function for computer's pick.
     * There is an adjustment function for some cases that desiredWinPercentage is less than 0 or more than 100
     *
     * @param desiredWinPercentage
     * @param playerPick
     * @return Hand enum
     */
    private Hand setHandPercentage(int desiredWinPercentage, String playerPick) {
        desiredWinPercentage = adjustDesiredWinPercentage(desiredWinPercentage);
        Random randomSelectHand = new Random();
        int randomNum = randomSelectHand.nextInt(100);
        int drawAndLossPercentage = (100 - desiredWinPercentage) / 2;

        switch (playerPick) {
            case "SCISSORS" -> {
                if (randomNum <= desiredWinPercentage - 1) {
                    return Hand.PAPER;
                } else if (randomNum >= desiredWinPercentage && randomNum <= desiredWinPercentage + drawAndLossPercentage - 1) {
                    return Hand.ROCK;
                } else {
                    return Hand.SCISSORS;
                }
            }
            case "PAPER" -> {
                if (randomNum <= desiredWinPercentage - 1) {
                    return Hand.ROCK;
                } else if (randomNum >= desiredWinPercentage && randomNum <= desiredWinPercentage + drawAndLossPercentage - 1) {
                    return Hand.SCISSORS;
                } else {
                    return Hand.PAPER;
                }
            }
            case "ROCK" -> {
                if (randomNum <= desiredWinPercentage - 1) {
                    return Hand.SCISSORS;
                } else if (randomNum >= desiredWinPercentage && randomNum <= desiredWinPercentage + drawAndLossPercentage - 1) {
                    return Hand.PAPER;
                } else {
                    return Hand.ROCK;
                }
            }
            default -> {
                log.info("Something went wrong and desiredWinPercentage is not applied");
                return Hand.getRandomPick();
            }
        }
    }

    /**
     * This checks desiredWinPercentage value and if it is less than 0, make it 0
     * and if it is more than 100, make it 100
     * or just return desiredWinPercentage value
     *
     * @param desiredWinPercentage
     * @return int desiredWinPercentage
     */
    private int adjustDesiredWinPercentage(int desiredWinPercentage) {
        if (desiredWinPercentage < 0) {
            return 0;
        } else return Math.min(desiredWinPercentage, 100);
    }

}
