package gamesrc.ui;

import gamesrc.game.Board;
import gamesrc.game.Dice;
import gamesrc.entities.Player;
import gamesrc.ui.SharpRoundedBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;

public class GameUI extends JFrame {
    private static final int numRows = 10;
    private static final int numCols = 10;
    private static final int numPlayers = 4;

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
        players = new Player[numPlayers];
        currentPlayerIndex = 0;
        initPlayers();
        initUI();
    }

    private void initPlayers() {
        players[0] = new Player("Blue");
        players[1] = new Player("Green");
        players[2] = new Player("Purple");
        players[3] = new Player("Orange");
    }

    private void initUI() {
        setTitle("Snakes and Ladders - D20 Edition");
        setSize(613, 720);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        boardPanel = new BoardPanel(board, players);

        rollButton = createStyledRollButton(e -> takeTurn());

        statusLabel = new JLabel("Welcome! Blue's turn first.");
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(rollButton, BorderLayout.CENTER);
        controlPanel.add(statusLabel, BorderLayout.SOUTH);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createStyledRollButton(ActionListener existingRollListener) {
        URL iconURL = getClass().getResource("/gamesrc/assets/sprites/diceicon.png");
        JButton button;

        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/gamesrc/assets/sprites/diceicon.png")));
            Image scaledIcon = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            button = new JButton("Roll!", new ImageIcon(scaledIcon));
        } else {
            System.err.println("diceicon.png not found");
            button = new JButton("Roll!");
        }

        button.setFont(new Font("Arial Black", Font.BOLD, 24));
        button.setBackground(new Color(150, 100, 0));
        button.setForeground(new Color(255, 255, 180));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Use fallback if SharpRoundedBorder is missing
        try {
            button.setBorder(BorderFactory.createCompoundBorder(
                    new SharpRoundedBorder(15),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15))
            );
        } catch (Exception ex) {
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        }

        button.setPreferredSize(new Dimension(180, 60));
        button.setHorizontalTextPosition(SwingConstants.LEFT);
        button.setIconTextGap(15);

        // Connect your original logic
        button.addActionListener(existingRollListener);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(180, 120, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(150, 100, 0));
            }
        });

        return button;
    }

    private void takeTurn() {
        rollButton.setEnabled(false);

        Player currentPlayer = players[currentPlayerIndex];
        int roll = dice.roll();

        statusLabel.setText(currentPlayer.getName() + " rolled a " + roll);

        int from = currentPlayer.getPosition();
        int to = from + roll;

        // In case of reaching 100 with steps left
        if (to > (numRows * numCols)) {
            int excess = to - (numRows * numCols);
            to = (numRows * numCols) - excess;
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

                    if (currentPlayer.hasWon(numRows * numCols)) {
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
