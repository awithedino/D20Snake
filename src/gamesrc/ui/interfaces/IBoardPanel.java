package gamesrc.ui.interfaces;

import java.awt.*;

public interface IBoardPanel {
    public void drawBoard(Graphics g);
    
    public void drawLineBetweenCells(Graphics g, int start, int end);
    
    public Point getCellCenter(int position);
}
