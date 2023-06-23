package com.game.scissorspaperrock.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Entity
@Data
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idx;

    @NotBlank
    private String playerId;

    @NotBlank
    private String playerPick;

    @NotBlank
    private String computerPick;

    @NotBlank
    private String result;
}
