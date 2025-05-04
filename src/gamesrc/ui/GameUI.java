package gamesrc.ui;

import gamesrc.game.Board;
import gamesrc.game.Dice;
import gamesrc.entities.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;

public class GameUI extends JFrame {
    // Game constants
    private static final int numRows = 10;
    private static final int numCols = 10;
    private static final int boardSize = numRows * numCols;

    // Game variables
    private Board board;
    private Dice dice;
    private Player[] players;
    private int currentPlayerIndex = 0;

    // UI components
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton rollButton;

    public GameUI() {
        setTitle("Snakes and Ladders - D20 Edition");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showPlayerSelectionScreen();
    }

    private void showPlayerSelectionScreen() {
        // Content pane
        Container contentPane = getContentPane();
        contentPane.removeAll();
//        contentPane.setBackground(Color.lightGray);
        Color bgContentPane = new Color(210, 210, 210);
        contentPane.setBackground(bgContentPane);

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        // Padding
        ((JPanel) contentPane).setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Title
        JLabel titleLabel = new JLabel("Snakes & Ladders!");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(titleLabel);
        contentPane.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btn2Players = createPlayerButton("2 Players", 2);
        JButton btn3Players = createPlayerButton("3 Players", 3);
        JButton btn4Players = createPlayerButton("4 Players", 4);

        btn2Players.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn3Players.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn4Players.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPane.add(btn2Players);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10))); // Space between buttons
        contentPane.add(btn3Players);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10))); // Space between buttons
        contentPane.add(btn4Players);

        // Refresh
        pack(); // Set size itself
        contentPane.revalidate();
        contentPane.repaint();
        setLocationRelativeTo(null); // Recenter after casting pack()
        setVisible(true);
    }

    // Players' choice button
    private JButton createPlayerButton(String text, int playerCount) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        Dimension buttonSize = new Dimension(150, 50);
        button.setPreferredSize(buttonSize); // Size
        button.setMaximumSize(buttonSize); // Locked the size
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        // Set player count + close dialog
        button.addActionListener(e -> startGame(playerCount));
        return button;
    }

    private void startGame(int numberOfPlayers) {
        // Init game objects
        board = new Board("src/gamesrc/assets/presets/boardPresets.json");
        dice = new Dice();
        players = new Player[numberOfPlayers];
        initPlayers(numberOfPlayers);

        // Setup UI components
        boardPanel = new BoardPanel(board, players);
        rollButton = createStyledRollButton(e -> takeTurn());
        statusLabel = new JLabel("Welcome! " + players[0].getName() + "'s turn first.");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel controlPanel = new JPanel(new BorderLayout(15, 0));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Color
        Color bgContentPane = new Color(210, 210, 210);
        controlPanel.setBackground(bgContentPane);
        statusLabel.setOpaque(false);

        controlPanel.add(rollButton, BorderLayout.WEST);
        controlPanel.add(statusLabel, BorderLayout.CENTER);

        // Update Content Pane
        Container contentPane = getContentPane();
        contentPane.removeAll(); // Clear
        contentPane.setLayout(new BorderLayout()); // Set layout
        contentPane.setBackground(bgContentPane); // Set background

        contentPane.add(boardPanel, BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.SOUTH);

        // Refresh it
        pack(); // I cast pack() again
        contentPane.revalidate();
        contentPane.repaint();
        setLocationRelativeTo(null); // Recenter after casting pack()
    }

    private void initPlayers(int numberOfPlayers) {
        String[] names = {"Blue", "Green", "Purple", "Orange"};
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = new Player(names[i % names.length]);
        }
        currentPlayerIndex = 0;
    }

    private JButton createStyledRollButton(ActionListener existingRollListener) {
        URL iconURL = getClass().getResource("/gamesrc/assets/sprites/diceicon.png");
        RoundedButton button;

        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/gamesrc/assets/sprites/diceicon.png")));
            Image scaledIcon = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            button = new RoundedButton("Roll!", new ImageIcon(scaledIcon));
        } else {
            System.err.println("diceicon.png not found");
            button = new RoundedButton("Roll!");
        }

        button.setFont(new Font("Arial Black", Font.BOLD, 24));
        button.setBackground(new Color(150, 100, 0));
        button.setForeground(new Color(255, 255, 180));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setRadius(15); // Corner radius

        button.setBorder(BorderFactory.createEmptyBorder(5,15,5,15));

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
        if (to > boardSize) {
            int excess = to - boardSize;
            to = Math.max(1, boardSize - excess);
        }
        currentPlayer.setMovementPath(from, to);

        Timer timer = getTimer(currentPlayer);
        timer.start();
    }

    // --- Your existing getTimer method ---
    private Timer getTimer(Player currentPlayer) {
        return new Timer (1, new ActionListener() {
            boolean movementPathFinished = false;
            boolean teleportChecked = false;     // Teleport check happens once after movement

            @Override
            public void actionPerformed(ActionEvent e) {
                Timer timer = (Timer) e.getSource();

                if (!movementPathFinished) {
                    if (currentPlayer.step()) {
                        boardPanel.repaint();
                    } else {
                        // Movement path is done, but don't stop timer yet
                        movementPathFinished = true;
                        boardPanel.repaint();
                    }
                } else if (!teleportChecked) {
                    // Movement animation finished, now check for snakes/ladders
                    int currentPos = currentPlayer.getPosition();
                    int finalPos = board.checkSnakesAndLadders(currentPos);

                    if (finalPos != currentPos) {
                        // Teleport: Set position directly.
                        currentPlayer.setPosition(finalPos);
                        boardPanel.repaint();
                        statusLabel.setText(currentPlayer.getName() + (finalPos > currentPos ? " climbed a ladder!" : " slid down a snake!"));
                    } else {
                        // No snake or ladder, just update status normally after movement
                        statusLabel.setText(currentPlayer.getName() + " landed on " + currentPos);
                    }
                    teleportChecked = true; // Prevent re-checking teleport

                } else {
                    // Both movement and teleport check are done
                    timer.stop(); // Now stop the timer

                    if (currentPlayer.hasWon(boardSize)) {
                        statusLabel.setText(currentPlayer.getName() + " wins! *\\o/*");
                        rollButton.setEnabled(false); // Disable roll button on win
                    } else {
                        // Switch to the next player
                        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
                        statusLabel.setText("Next: " + players[currentPlayerIndex].getName() + "'s turn.");
                        rollButton.setEnabled(true);
                    }
                }
            }
        });
    }
}
