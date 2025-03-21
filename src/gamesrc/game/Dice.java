package gamesrc.game;
import java.util.Random;

public class Dice {
    private Random random;
    private final int sides = 20;

    public Dice() {
        random = new Random();
    }

    public int roll() {
        return random.nextInt(sides) + 1;
    }
}
