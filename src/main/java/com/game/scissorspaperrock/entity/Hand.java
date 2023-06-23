package com.game.scissorspaperrock.entity;

import java.util.Random;

public enum Hand {

    SCISSORS {
        @Override
        public boolean isWin(Hand hand) {
            return PAPER.equals(hand);
        }
    },

    PAPER {
        @Override
        public boolean isWin(Hand hand) {
            return ROCK.equals(hand);
        }
    },

    ROCK {
        @Override
        public boolean isWin(Hand hand) {
            return SCISSORS.equals(hand);
        }
    };

    public static Hand getRandomPick() {
        int pick = new Random().nextInt(Hand.values().length);
        return Hand.values()[pick];
    }

    public abstract boolean isWin(Hand hand);
}
