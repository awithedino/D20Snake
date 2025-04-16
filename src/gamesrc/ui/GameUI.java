package gamesrc.ui;

import gamesrc.game.Board;
import gamesrc.game.Dice;
import gamesrc.entities.Player;
import gamesrc.ui.interfaces.IGameUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameUI extends JFrame implements IGameUI {
    private final Board board;
    private final Dice dice;
    private final Player[] players;
    private int currentPlayerIndex;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton rollButton;

    public GameUI() {
        board = new Board("src/gamesrc/assets/presets/boardPresets.json");
        dice = new Dice();
        players = new Player[4];
        currentPlayerIndex = 0;
        initPlayers();
        initUI();
    }

    @Override
    public void initPlayers() {
        players[0] = new Player("Blue");
        players[1] = new Player("Green");
        players[2] = new Player("Purple");
        players[3] = new Player("Orange");
    }

    @Override
    public void initUI() {
        setTitle("Snakes and Ladders - D20 Edition");
        setSize(613, 685);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        boardPanel = new BoardPanel(board, players);

        rollButton = new JButton("Roll D20");
        rollButton.addActionListener(e -> takeTurn());

        statusLabel = new JLabel("Welcome! Blue's turn first.");
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(rollButton, BorderLayout.CENTER);
        controlPanel.add(statusLabel, BorderLayout.SOUTH);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void takeTurn() {
        rollButton.setEnabled(false);

        Player currentPlayer = players[currentPlayerIndex];
        int roll = dice.roll();

        statusLabel.setText(currentPlayer.getName() + " rolled a " + roll);

        int from = currentPlayer.getPosition();
        int to = from + roll;

        // In case of reaching 100 with steps left
        if (to > 100) {
            int excess = to - 100;
            to = 100 - excess;
        }

        // Snakes/ladder apply after movement
        // to = board.checkSnakesAndLadders(to);

        currentPlayer.setMovementPath(from, to);

        Timer timer = getTimer(currentPlayer);
        timer.start();
    }

    private Timer getTimer(Player currentPlayer) {
        return new Timer (300, new ActionListener() {
            boolean teleportDone = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                Timer timer = (Timer) e.getSource();

                if (currentPlayer.step()) {
                    boardPanel.repaint();
                } else if (!teleportDone) {
                    // Snake/ladder teleport
                    int finalPos = board.checkSnakesAndLadders(currentPlayer.getPosition());
                    if (finalPos != currentPlayer.getPosition()) {
                        currentPlayer.setPosition(finalPos);
                        boardPanel.repaint(); // Show teleport
                    }
                    teleportDone = true; // Prevent looping
                } else {
                    timer.stop();

                    if (currentPlayer.hasWon(100)) {
                        statusLabel.setText(currentPlayer.getName() + " wins!");
                        rollButton.setEnabled(false);
                    } else {
                        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
                        statusLabel.setText("Next: " + players[currentPlayerIndex].getName());
                        rollButton.setEnabled(true);
                    }
                }
            }
        });
    }

    public static void launchGame() {
        SwingUtilities.invokeLater(GameUI::new);
    }
}
