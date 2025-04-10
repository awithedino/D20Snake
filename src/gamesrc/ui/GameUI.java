package gamesrc.ui;

import gamesrc.game.Board;
import gamesrc.game.Dice;
import gamesrc.entities.Player;

import javax.swing.*;
import java.awt.*;

public class GameUI extends JFrame {
    private final Board board;
    private final Dice dice;
    private final Player[] players;
    private int currentPlayerIndex;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton rollButton;

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

        boardPanel = new BoardPanel(board, players);

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

    private void takeTurn() {
        Player currentPlayer = players[currentPlayerIndex];
        int roll = dice.roll();

        statusLabel.setText(currentPlayer.getName() + " rolled a " + roll);
        currentPlayer.move(roll);

        // In case of reaching 100 with steps left
        if (currentPlayer.getPosition() > 100) {
            int excess = currentPlayer.getPosition() - 100;
            currentPlayer.setPosition(100 - excess);
        }

        // Snakes/ladder check
        int newPos = board.checkSnakesAndLadders(currentPlayer.getPosition());
        currentPlayer.setPosition(newPos);

        // Repaint board
        boardPanel.repaint();

        // Win check
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
