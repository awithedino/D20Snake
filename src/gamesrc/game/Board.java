package gamesrc.game;

import gamesrc.entities.Snake;
import gamesrc.entities.Ladder;
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Board extends JPanel {
    private final int size = 100;
    private List<Snake> snakes;
    private List<Ladder> ladders;
    private final int cellSize = 50;
    private final int boardWidth = 10;


    public Board() {
        snakes = new ArrayList<>();
        ladders = new ArrayList<>();
        initBoard();
        setPreferredSize(new Dimension(cellSize * boardWidth, cellSize * boardWidth));
    }

    private void initBoard() {
        snakes.add(new Snake(99, 54));
        snakes.add(new Snake(70, 55));
        snakes.add(new Snake(52, 42));
        snakes.add(new Snake(25, 2));
        snakes.add(new Snake(95, 72));
        ladders.add(new Ladder(6, 25));
        ladders.add(new Ladder(11, 40));
        ladders.add(new Ladder(60, 85));
        ladders.add(new Ladder(46, 90));
        ladders.add(new Ladder(17, 69));
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
}
//
//    public int getSize() {
//        return size;
//    }

