package gamesrc.ui;

import gamesrc.game.Board;
import gamesrc.game.Dice;
import gamesrc.entities.Player;
import gamesrc.entities.Snake;
import gamesrc.entities.Ladder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameUI extends JFrame {
    private Board board;
    private Dice dice;
    private Player[] players;
    private int currentPlayerIndex;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton rollButton;
    private final int cellSize = 60;

    public GameUI() {
        board = new Board();
        dice = new Dice();
        players = new Player[4];
        currentPlayerIndex = 0;
        initPlayers();
        initUI();
    }

    private void initPlayers() {
        players[0] = new Player("Player 1");
        players[1] = new Player("Player 2");
        players[2] = new Player("Player 3");
        players[3] = new Player("Player 4");
    }

    private void initUI() {
        setTitle("Snakes and Ladders - D20 Edition");
                setSize(650, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
                drawSnakesAndLadders(g);
                drawPlayers(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(600, 600));

        rollButton = new JButton("Roll D20");
                rollButton.addActionListener(e -> takeTurn());

        statusLabel = new JLabel("Welcome! Player 1's turn.");
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(rollButton, BorderLayout.CENTER);
        controlPanel.add(statusLabel, BorderLayout.SOUTH);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void drawBoard(Graphics g) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int x = j * cellSize;
                int y = (9 - i) * cellSize;
                g.drawRect(x, y, cellSize, cellSize);
                int number = i * 10 + ((i % 2 == 0) ? j + 1 : 10 - j);
                g.drawString(String.valueOf(number), x + 5, y + 15);
            }
        }
    }

    private void drawSnakesAndLadders(Graphics g) {
        g.setColor(Color.RED);
        for (Snake s : board.getSnakes()) drawLineBetweenCells(g, s.getStart(), s.getEnd());
        g.setColor(Color.GREEN);
        for (Ladder l : board.getLadders()) drawLineBetweenCells(g, l.getStart(), l.getEnd());
    }

    private void drawLineBetweenCells(Graphics g, int start, int end) {
        Point p1 = getCellCenter(start);
        Point p2 = getCellCenter(end);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    private void drawPlayers(Graphics g) {
        Color[] colors = {Color.BLUE, Color.MAGENTA, Color.ORANGE, Color.BLACK};
        for (int i = 0; i < players.length; i++) {
            Point p = getCellCenter(players[i].getPosition());
            g.setColor(colors[i]);
            g.fillOval(p.x - 10 + (i * 10), p.y - 10, 10, 10);
        }
    }

    private Point getCellCenter(int position) {
        if (position > 100) position = 100;
        int row = (position - 1) / 10;
        int col = (position - 1) % 10;
        if (row % 2 == 1) col = 9 - col;
        int x = col * cellSize + cellSize / 2;
        int y = (9 - row) * cellSize + cellSize / 2;
        return new Point(x, y);
    }

    private void takeTurn() {
        Player currentPlayer = players[currentPlayerIndex];
        int roll = dice.roll();
        statusLabel.setText(currentPlayer.getName() + " rolled a " + roll);

                currentPlayer.move(roll);
        if (currentPlayer.getPosition() > 100) {
            int excess = currentPlayer.getPosition() - 100;
            currentPlayer.setPosition(100 - excess);
        }

        int newPos = board.checkSnakesAndLadders(currentPlayer.getPosition());
        currentPlayer.setPosition(newPos);
        boardPanel.repaint();

        if (currentPlayer.hasWon(100)) {
            statusLabel.setText(currentPlayer.getName() + " wins!");
                    rollButton.setEnabled(false);
            return;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
        statusLabel.setText(currentPlayer.getName() + " rolled a " + roll + ". Next: " + players[currentPlayerIndex].getName());
    }

    public static void launchGame() {
        SwingUtilities.invokeLater(GameUI::new);
    }
}
