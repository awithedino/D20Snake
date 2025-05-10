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
    private String currentActivePresetName;

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
        this.currentActivePresetName = PresetLoader.getRandomPresetName();

        // Random
        if (this.currentActivePresetName != null) {
            snakes.addAll(PresetLoader.snakePresets.get(this.currentActivePresetName));
            ladders.addAll(PresetLoader.ladderPresets.get(this.currentActivePresetName));
        } else {
            System.err.println("No preset name found to load ladders/snakes.");
        }
    }

    public String getCurrentActivePresetName() {
        return currentActivePresetName;
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

    public void resetBoard() {
        initBoard();
    }

    public int getSize() {
        return size;
    }
}
