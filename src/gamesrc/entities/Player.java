package gamesrc.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Player {
    private String name;
    private int position;
    private Queue<Integer> movementPath = new LinkedList<>();

    public Player(String name) {
        this.name = name;
        this.position = 1;
    }

    public void move(int steps) {
        position += steps;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public boolean hasWon(int boardSize) {
        return position == boardSize;
    }

    public void setMovementPath(int from, int to) {
        movementPath.clear();
        if (from < to) {
            for (int i = from + 1; i <= to; i++) {
                movementPath.add(i);
            }
        } else if (from > to) { // for moving backward
            for (int i = from - 1; i >= to; i--) {
                movementPath.add(i);
            }
        }
    }

    public boolean step() {
        if (!movementPath.isEmpty()) {
            position = movementPath.poll();
            return true;
        }
        return false;
    }

    public boolean isMoving() {
        return !movementPath.isEmpty();
    }
}
