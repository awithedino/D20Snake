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
    private static final int fixedWidth = 714;
    private static final int fixedHeight = 817;

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
        setSize(fixedWidth, fixedHeight);
        setLocationRelativeTo(null);
        showPlayerSelectionScreen();
        setVisible(true);
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
        contentPane.revalidate();
        contentPane.repaint();
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
        contentPane.revalidate();
        contentPane.repaint();
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
        int from = currentPlayer.getPosition();
        int rawTarget = from + roll;

        statusLabel.setText(currentPlayer.getName() + " rolled a " + roll);
        int firstPathEnd;
        int bounceBackTargetSquare = -1; // indicate if we need to go back

        if (rawTarget > boardSize) {
            // --- Overshot ---
            firstPathEnd = boardSize; // Move to 100
            int excess = rawTarget - boardSize;
            bounceBackTargetSquare = Math.max(1, boardSize - excess); // Calculate where to bounce back

            // Set movement path ONLY to 100 for the first animation phase
            currentPlayer.setMovementPath(from, firstPathEnd);
            statusLabel.setText(currentPlayer.getName() + " hits 100..."); // Update status

        } else {
            // Did not pass 100
            firstPathEnd = rawTarget; // Move directly to the target
            currentPlayer.setMovementPath(from, firstPathEnd);
        }

        // Start the timer
        Timer timer = getTimer(currentPlayer, bounceBackTargetSquare);
        timer.start();
    }

    private Timer getTimer(Player currentPlayer, int bounceBackTargetSquare) {
        // Use a slightly longer delay maybe? Optional.
        return new Timer(200, new ActionListener() {
            // State flags
            boolean isAnimatingMovement = true; // Am I moving?
            boolean needsToStartBounceBack = (bounceBackTargetSquare != -1); // Do I have to return?
            boolean teleportCheckPending = true; // Check snakes or ladders

            @Override
            public void actionPerformed(ActionEvent e) {
                Timer timer = (Timer) e.getSource();

                // Movement (forwards and potentially backwards)
                if (isAnimatingMovement) {
                    if (currentPlayer.step()) {
                        // Continue
                        boardPanel.repaint();
                        return;
                    } else {
                        // Finished the current movement path (either to 100 or to target/bounce-back spot)
                        isAnimatingMovement = false; // Stop animating for now
                        boardPanel.repaint();

                        // First IF: Do I have to turn back?
                        if (needsToStartBounceBack) {
                            statusLabel.setText(currentPlayer.getName() + " bounces back from 100!");
                            // Set the *new* path for the backward movement
                            currentPlayer.setMovementPath(boardSize, bounceBackTargetSquare);
                            needsToStartBounceBack = false; // Don't trigger bounce again
                            isAnimatingMovement = true; // Walk backwards
                            return;
                        }
                    }
                }

                // Check Snakes / Ladders
                // Second last ordered
                if (teleportCheckPending) {
                    int currentPos = currentPlayer.getPosition();
                    int finalPos = board.checkSnakesAndLadders(currentPos);
                    teleportCheckPending = false; // Check only once per turn

                    if (finalPos != currentPos) {
                        // Found a snake or ladder.
                        statusLabel.setText(currentPlayer.getName() + (finalPos > currentPos ? " climbed a ladder!" : " slid down a snake!"));
                        // Animate this? Or snap? Snap.
                        currentPlayer.setPosition(finalPos);
                        boardPanel.repaint();
                    } else {
                        statusLabel.setText(currentPlayer.getName() + " landed on " + currentPos);
                    }
                }

                // End turn
                // Last order
                timer.stop(); // Stop the timer

                if (currentPlayer.hasWon(boardSize)) {
                    statusLabel.setText(currentPlayer.getName() + " wins!!!");
                    rollButton.setEnabled(false);
                } else {
                    // Switch to the next player
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
                    statusLabel.setText("Next: " + players[currentPlayerIndex].getName() + "'s turn.");
                    rollButton.setEnabled(true);
                }
            }
        });
    }
}
