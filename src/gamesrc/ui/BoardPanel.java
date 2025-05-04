package gamesrc.ui;

import gamesrc.entities.*;
import gamesrc.game.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class BoardPanel extends JPanel{
    private static final int numRows = 10;
    private static final int numCols = 10;
    private static final int numPlayers = 4;

    private final Board board;
    private final Player[] players;
    private final int cellSize = 60;
    private final Font boardFont;
    private Image[] playerSprites = new Image[numPlayers];
    private Image lightWoodTexture;
    private Image darkWoodTexture;

    public BoardPanel(Board board, Player[] players) {
        this.board = board;
        this.players = players;
        loadPlayerSprites();
        loadTiles();
        setPreferredSize(new Dimension(numRows * cellSize, numCols * cellSize));
        this.boardFont = new Font("Arial", Font.BOLD, 30);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // For the pawns
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        drawBoard(g);
        drawSnakesAndLadders(g2);
        drawPlayers(g);

        for (Player p : players) {
            if (p.isMoving()) {
                Timer t = new Timer(200, e -> {
                    ((Timer) e.getSource()).stop();
                    repaint();
                });
                t.setRepeats(false);
                t.start();
                break;
            }
        }
    }

    private void drawBoard(Graphics g) {
        g.setFont(boardFont);
        FontMetrics metrics = g.getFontMetrics(boardFont);
        int gap = 2;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                int x = j * cellSize;
                int y = (9 - i) * cellSize;

                int number = i * 10 + ((i % 2 == 0) ? j + 1 : 10 - j);
                String numStr = String.valueOf(number);

                // Cell background
                if (number % 2 == 1 && lightWoodTexture != null) {
                    g.drawImage(lightWoodTexture, x, y, cellSize - gap, cellSize - gap, null);
                    g.setColor(Color.darkGray);
                } else if (darkWoodTexture != null){
                    g.drawImage(darkWoodTexture, x, y, cellSize - gap, cellSize - gap, null);
                    g.setColor(Color.lightGray);
                }

                // Number's position
                int textWidth = metrics.stringWidth(numStr);
                int textHeight = metrics.getAscent();
                int textX = x + (cellSize - gap - textWidth) / 2;
                int textY = y + (cellSize - gap + textHeight) / 2 - metrics.getDescent();
                g.drawString(numStr, textX, textY);
            }
        }
    }

    private void drawSnakesAndLadders (Graphics2D g) {
        g.setColor(Color.red);
        g.setStroke(new BasicStroke(5));
        for (Snake s : board.getSnakes()) {
            drawLineBetweenCells(g, s.getStart(), s.getEnd());
        }

        g.setColor(Color.green);
        for (Ladder l : board.getLadders()) {
            drawLineBetweenCells(g, l.getStart(), l.getEnd());
        }
    }

    private void drawLineBetweenCells (Graphics g, int start, int end) {
        Point p1 = getCellCenter(start);
        Point p2 = getCellCenter(end);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    private Point getCellCenter(int position) {
        if (position > (numCols * numRows)) position = (numCols * numRows);
        int row = (position - 1) / 10;
        int col = (position - 1) % 10;
        if (row % 2 == 1) col = 9 - col;
        int x =  col * cellSize + cellSize / 2;
        int y = (9 - row) * cellSize + cellSize / 2;
        return new Point(x, y);
    }

    private void drawPlayers (Graphics g) {
        final int drawSize = 32;
        final int offset = 16;

        for (int cell = 1; cell <= (numCols * numRows); cell++) {
            int finalCell = cell;

            Player[] sameCellPlayers = Arrays.stream(players)
                    .filter(p -> p.getPosition() == finalCell)
                    .toArray(Player[]::new);
            if (sameCellPlayers.length == 0) continue;

            Point center = getCellCenter(cell);
            if (sameCellPlayers.length == 1) {
                // Original size
                int x = center.x - drawSize / 2;
                int y = center.y - drawSize / 2;
                g.drawImage(playerSprites[getPlayerIndex(sameCellPlayers[0])], x, y, drawSize, drawSize, null);
            } else {
                // Arrange
                for (int i = 0; i < sameCellPlayers.length; i++) {
                    int row = i / 2;
                    int col = i % 2;
                    int x = center.x - offset + col * drawSize / 2;
                    int y = center.y - offset - row * drawSize / 2 + 10;
                    g.drawImage(playerSprites[getPlayerIndex(sameCellPlayers[i])], x, y, drawSize / 2, drawSize / 2, null);
                }
            }
        }
    }

    // Get the right sprite
    private int getPlayerIndex (Player p) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == p) return i;
        }
        return 0;
    }

    private void loadPlayerSprites() {
        try {
            for (int i = 0; i < numPlayers; i++) {
                String path = "/gamesrc/assets/sprites/Player" + (i + 1) + ".png";
                playerSprites[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTiles() {
        try {
            lightWoodTexture = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/gamesrc/assets/tileset/Wood1.jpg")));
            darkWoodTexture = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/gamesrc/assets/tileset/Wood2.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
