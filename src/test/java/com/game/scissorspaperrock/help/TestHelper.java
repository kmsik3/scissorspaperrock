package com.game.scissorspaperrock.help;

import com.game.scissorspaperrock.model.Game;
import com.game.scissorspaperrock.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestHelper {

    private final GameRepository gameRepository;

    public void setGameData() {
        Game game1 = new Game();
        game1.setPlayerId("testEmail@gmail.com");
        game1.setPlayerPick("ROCK");
        game1.setComputerPick("PAPER");
        game1.setResult("LOSS");
        gameRepository.save(game1);

        Game game2 = new Game();
        game2.setPlayerId("testEmail@gmail.com");
        game2.setPlayerPick("ROCK");
        game2.setComputerPick("PAPER");
        game2.setResult("LOSS");
        gameRepository.save(game2);

        Game game3 = new Game();
        game3.setPlayerId("testEmail@gmail.com");
        game3.setPlayerPick("SCISSORS");
        game3.setComputerPick("PAPER");
        game3.setResult("WIN");
        gameRepository.save(game3);

        Game game4 = new Game();
        game4.setPlayerId("testEmail@gmail.com");
        game4.setPlayerPick("PAPER");
        game4.setComputerPick("PAPER");
        game4.setResult("DRAW");
        gameRepository.save(game4);
    }

}
