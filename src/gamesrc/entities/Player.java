package gamesrc.entities;

import gamesrc.entities.interfaces.IPlayer;

public class Player implements IPlayer {
    private String name;
    private int position;

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
}
