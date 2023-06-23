package com.game.scissorspaperrock.repository;

import com.game.scissorspaperrock.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query(value = """
            SELECT t from Token t inner join Player p
            on t.player.idx = p.idx
            WHERE p.idx = :idx and (t.expired = false or t.revoked = false)""")
    List<Token> findAllValidTokenByUser(Integer idx);

    Optional<Token> findByToken(String token);
}
