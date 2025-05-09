package gamesrc.entities.interfaces;

public interface IPlayer {
    public void move(int steps);

    public void setPosition(int position);

    public int getPosition();

    public String getName();

    public boolean hasWon(int boardSize);
}
