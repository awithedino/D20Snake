package gamesrc.game;
import gamesrc.abstracts.ADice;

import java.util.Random;

public class Dice extends ADice {
    private Random random;
    private final int sides = 20;

    public Dice() {
        random = new Random();
    }

    @Override
    public int roll() {
        return random.nextInt(sides) + 1;
    }
}
