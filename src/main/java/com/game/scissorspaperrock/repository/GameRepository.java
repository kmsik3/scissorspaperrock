package com.game.scissorspaperrock.repository;


import com.game.scissorspaperrock.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Integer> {

    List<Game> findByPlayerId(String playerId);
}
