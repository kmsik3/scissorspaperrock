package com.game.scissorspaperrock.repository;


import com.game.scissorspaperrock.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

    Optional<Player> findByEmail(String email);
}
