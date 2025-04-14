package gamesrc.game;
import java.util.*;

import gamesrc.entities.Snake;
import gamesrc.entities.Ladder;
import gamesrc.util.PresetLoader;

public class Board {
    private final static int size = 100;
    private final List<Snake> snakes;
    private final List<Ladder> ladders;
    private final String presetPath;

    public Board(String presetPath) {
        snakes = new ArrayList<>();
        ladders = new ArrayList<>();
        this.presetPath = presetPath;
        initBoard();
    }

    private void initBoard() {
        snakes.clear();
        ladders.clear();
        PresetLoader.loadPresets(presetPath);
        String randomPresetName = PresetLoader.getRandomPresetName();

        // Random
        snakes.addAll(PresetLoader.snakePresets.get(randomPresetName));
        ladders.addAll(PresetLoader.ladderPresets.get(randomPresetName));
    }

    public List<Snake> getSnakes() {
        return snakes;
    }

    public List<Ladder> getLadders() {
        return ladders;
    }

    public int checkSnakesAndLadders(int pos) {
        for (Snake s : snakes) {
            if (s.getStart() == pos) return s.getEnd();
        }
        for (Ladder l : ladders) {
            if (l.getStart() == pos) return l.getEnd();
        }
        return pos;
    }

    public int getSize() {
        return size;
    }
}
