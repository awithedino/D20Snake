package gamesrc.game.abstracts;

import gamesrc.entities.Ladder;
import gamesrc.entities.Snake;

import java.util.List;

public abstract class ABoard{
    private final int size = 100;
    private List<Snake> snakes;
    private List<Ladder> ladders;

    public void initBoard() {
        return;
    }

    public List<Snake> getSnakes() {
        return snakes;
    }

    public List<Ladder> getLadders() {
        return ladders;
    }

    public int checkSnakesAndLadders(int pos) {
        return pos;
    }

    public int getSize() {
        return size;
    }
}
