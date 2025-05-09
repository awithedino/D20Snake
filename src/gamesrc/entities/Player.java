package gamesrc.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import gamesrc.entities.interfaces.IPlayer;

public class Player implements IPlayer {
    private String name;
    private int position;
    private Queue<Integer> movementPath = new LinkedList<>();

    public Player(String name) {
        this.name = name;
        this.position = 1;
    }

    @Override
    public void move(int steps) {
        position += steps;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
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
