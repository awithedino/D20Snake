package gamesrc.entities;

public class Player {
    private String name;
    private int position;

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
}
