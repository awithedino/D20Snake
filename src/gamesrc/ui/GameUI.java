package gamesrc.ui;

import gamesrc.game.Board;
import gamesrc.game.Dice;
import gamesrc.entities.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private Player[] players; // Store the number of players from last game if needed for restart
    private int currentPlayerIndex = 0;
    private int lastGamePlayerCount = 0; // To remember for "Play Again"

    // UI components
    private BoardPanel boardPanel; // JPanel that draws the board, players, S/L
    private JLabel statusLabel;    // Displays game messages
    private JButton rollButton;    // The main "Roll!" button

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
        // Get the content pane
        Container contentPane = getContentPane();
        contentPane.removeAll();
        Color bgContentPane = new Color(210, 210, 210); // Softer background
        contentPane.setBackground(bgContentPane);

        // Use BoxLayout for vertical arrangement of selection components
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        // Add some padding around the content
        ((JPanel) contentPane).setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Game Title Label
        JLabel titleLabel = new JLabel("Snakes & Ladders!");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 32)); // Slightly larger
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(titleLabel);
        contentPane.add(Box.createRigidArea(new Dimension(0, 40))); // More space

        // "How many players?" Label
        JLabel promptLabel = new JLabel("How many players?");
        promptLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(promptLabel);
        contentPane.add(Box.createRigidArea(new Dimension(0, 20)));


        // Player count selection buttons
        JButton btn2Players = createPlayerButton("2 Players", 2);
        JButton btn3Players = createPlayerButton("3 Players", 3);
        JButton btn4Players = createPlayerButton("4 Players", 4);

        // Ensure buttons are centered
        btn2Players.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn3Players.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn4Players.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPane.add(btn2Players);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10))); // Space between buttons
        contentPane.add(btn3Players);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10))); // Space between buttons
        contentPane.add(btn4Players);

        // Refresh the layout
        contentPane.revalidate();
        contentPane.repaint();
    }

    // Helper to create player selection buttons
    private JButton createPlayerButton(String text, int playerCount) {
        // Using RoundedButton for consistency, or JButton if preferred for this screen
        RoundedButton button = new RoundedButton(text); // Use RoundedButton
        button.setFont(new Font("Arial", Font.BOLD, 18));
        Dimension buttonSize = new Dimension(180, 55); // Slightly larger
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize); // For BoxLayout
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false); // From RoundedButton
        button.setRadius(10);
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));


        // Action: Start the game with the selected player count
        button.addActionListener(e -> {
            lastGamePlayerCount = playerCount; // Store for "Play Again"
            startGame(playerCount);
        });
        return button;
    }

    private void startGame(int numberOfPlayers) {
        // Initialize game logic objects
        board = new Board("src/gamesrc/assets/presets/boardPresets.json"); // Path to your presets
        dice = new Dice();
        players = new Player[numberOfPlayers];
        initPlayers(numberOfPlayers); // Create player instances

        // Initialize/Re-initialize UI components for the game screen
        if (boardPanel == null) { // Create only if it doesn't exist
            boardPanel = new BoardPanel(board, players);
        } else { // Update existing board panel
            boardPanel.updateBoard(board, players); // Add this method to BoardPanel
        }


        if (rollButton == null) {
            rollButton = createStyledRollButton(e -> takeTurn());
        }
        rollButton.setEnabled(true); // Ensure it's enabled at game start/restart

        if (statusLabel == null) {
            statusLabel = new JLabel();
        }
        statusLabel.setText("Welcome! " + players[0].getName() + "'s turn first.");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setOpaque(false); // To let panel background show

        // Control panel for button and status label
        JPanel controlPanel = new JPanel(new BorderLayout(15, 0)); // Horizontal gap
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Padding
        Color controlBgColor = new Color(210, 210, 210);
        controlPanel.setBackground(controlBgColor);

        controlPanel.add(rollButton, BorderLayout.WEST);
        controlPanel.add(statusLabel, BorderLayout.CENTER);

        // Update the main frame's content pane
        Container contentPane = getContentPane();
        contentPane.removeAll();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(controlBgColor); // Match control panel for consistency

        contentPane.add(boardPanel, BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.SOUTH);

        // Refresh the layout
        contentPane.revalidate();
        contentPane.repaint();
    }

    // Initialize player objects
    private void initPlayers(int numberOfPlayers) {
        String[] names = {"Blue", "Green", "Purple", "Orange", "Red", "Yellow"}; // More names if needed
        players = new Player[numberOfPlayers]; // Re-create array to correct size
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = new Player(names[i % names.length]);
        }
        currentPlayerIndex = 0; // Reset to the first player
    }

    // Creates the styled "Roll!" button
    private JButton createStyledRollButton(ActionListener existingRollListener) {
        URL iconURL = getClass().getResource("/gamesrc/assets/sprites/diceicon.png");
        RoundedButton button; // Use your custom RoundedButton

        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/gamesrc/assets/sprites/diceicon.png")));
            Image scaledIcon = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            button = new RoundedButton("Roll!", new ImageIcon(scaledIcon));
        } else {
            System.err.println("diceicon.png not found, using text-only button.");
            button = new RoundedButton("Roll!");
        }

        button.setFont(new Font("Arial Black", Font.BOLD, 24));
        button.setBackground(new Color(150, 100, 0)); // Dark yellow/brown
        button.setForeground(new Color(255, 255, 180)); // Light yellow text
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setRadius(15); // Corner radius for RoundedButton
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Internal padding

        button.setPreferredSize(new Dimension(180, 60));
        button.setHorizontalTextPosition(SwingConstants.LEFT);
        button.setIconTextGap(15);

        button.addActionListener(existingRollListener); // Connect game logic
        return button;
    }

    private void takeTurn() {
        if (rollButton != null) rollButton.setEnabled(false);

        Player currentPlayer = players[currentPlayerIndex];
        int roll = dice.roll();
        int from = currentPlayer.getPosition();
        int rawTarget = from + roll;

        statusLabel.setText(currentPlayer.getName() + " rolled a " + roll + "!");

        int firstPathEnd;
        int bounceBackTargetSquare = -1; // -1 indicates no bounce back needed

        if (rawTarget > boardSize) {
            firstPathEnd = boardSize; // Path 1: Move TO 100
            int excess = rawTarget - boardSize;
            bounceBackTargetSquare = Math.max(1, boardSize - excess); // Calculate where to bounce back TO
            currentPlayer.setMovementPath(from, firstPathEnd);
            statusLabel.setText(currentPlayer.getName() + " hits 100...");
        } else {
            firstPathEnd = rawTarget; // Path 1: Move directly to the target
            currentPlayer.setMovementPath(from, firstPathEnd);
        }

        Timer timer = getTimer(currentPlayer, bounceBackTargetSquare);
        timer.start();
    }

    private Timer getTimer(Player currentPlayer, int bounceBackTargetSquare) {
        return new Timer(1, new ActionListener() { // Animation step delay
            boolean isAnimatingMovement = true;
            boolean needsToStartBounceBack = (bounceBackTargetSquare != -1);
            boolean teleportCheckPending = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                Timer timer = (Timer) e.getSource();

                if (isAnimatingMovement) {
                    if (currentPlayer.step()) {
                        boardPanel.repaint();
                        return; // Continue animation on next tick
                    } else {
                        // Finished current movement path
                        isAnimatingMovement = false;
                        boardPanel.repaint(); // Show final step of this path

                        if (needsToStartBounceBack) {
                            statusLabel.setText(currentPlayer.getName() + " bounces back from 100!");
                            currentPlayer.setMovementPath(boardSize, bounceBackTargetSquare);
                            needsToStartBounceBack = false; // Bounce initiated
                            isAnimatingMovement = true;    // Start bounce-back animation
                            // teleportCheckPending = false; // Optional: No S/L after bounce (depends on rules)
                            return; // Animate bounce on next tick
                        }
                        // If no bounce, or bounce finished, proceed to S/L check
                    }
                }

                // Check for Snakes and Ladders (only after all movement is done)
                if (teleportCheckPending) {
                    int currentPos = currentPlayer.getPosition();
                    int finalPos = board.checkSnakesAndLadders(currentPos);
                    teleportCheckPending = false; // Check only once

                    if (finalPos != currentPos) {
                        statusLabel.setText(currentPlayer.getName() + (finalPos > currentPos ? " climbed a ladder!" : " slid down a snake!"));
                        currentPlayer.setPosition(finalPos); // Snap for now
                        boardPanel.repaint();
                        // Optional: Could add a small delay or animation for S/L
                    } else {
                        // Only update "landed on" if no S/L and not just bounced
                        if (bounceBackTargetSquare == -1 || currentPlayer.getPosition() == bounceBackTargetSquare) {
                            statusLabel.setText(currentPlayer.getName() + " landed on " + currentPos + ".");
                        }
                    }
                }

                // End turn logic (all animations and checks complete)
                timer.stop();

                if (currentPlayer.hasWon(boardSize)) {
                    // Player wins - show win screen
                    showWinScreen(currentPlayer.getName());
                } else {
                    // Next player's turn
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
                    statusLabel.setText("Next: " + players[currentPlayerIndex].getName() + "'s turn.");
                    if (rollButton != null) rollButton.setEnabled(true);
                }
            }
        });
    }

    // --- Win Screen Logic ---
    private void showWinScreen(String winnerName) {
        Container contentPane = getContentPane();
        contentPane.removeAll();
        contentPane.setLayout(new BorderLayout()); // Use BorderLayout for centering win panel

        // Main panel for win screen content
        JPanel winContentPanel = new JPanel(new BorderLayout(10, 30)); // Gaps
        winContentPanel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60)); // Padding
        winContentPanel.setBackground(new Color(220, 220, 240)); // Light lavender background

        // Winner Message Label
        JLabel winMessageLabel = new JLabel(winnerName + " wins the game!!!", SwingConstants.CENTER);
        winMessageLabel.setFont(new Font("Arial Black", Font.BOLD, 32));
        winMessageLabel.setForeground(new Color(50, 50, 150)); // Dark blue text
        winContentPanel.add(winMessageLabel, BorderLayout.NORTH);

        // Panel for the action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10)); // Centered buttons
        buttonPanel.setOpaque(false); // Inherit winContentPanel background

        // "Play Again" Button
        JButton playAgainBtn = createWinScreenStyledButton("Play Again");
        playAgainBtn.addActionListener(e -> {
            resetGameLogic();
            startGame(lastGamePlayerCount); // Restart with the same number of players
        });
        buttonPanel.add(playAgainBtn);

        // "Main Menu" Button
        JButton mainMenuBtn = createWinScreenStyledButton("Main Menu");
        mainMenuBtn.addActionListener(e -> showPlayerSelectionScreen()); // Go back to player selection
        buttonPanel.add(mainMenuBtn);

        winContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the win panel to the center of the content pane to fill space
        contentPane.add(winContentPanel, BorderLayout.CENTER);

        // Refresh the UI
        contentPane.revalidate();
        contentPane.repaint();
    }

    // Helper to style buttons for the win screen
    private JButton createWinScreenStyledButton(String text) {
        RoundedButton button = new RoundedButton(text); // Using your RoundedButton
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(100, 150, 200));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));// Nice blue
        button.setForeground(Color.WHITE);
        button.setRadius(12); // Slightly different radius for these buttons
        button.setPreferredSize(new Dimension(160, 50));
        // Assuming RoundedButton handles its own hover effects and internal padding via setBorder()
        button.setBorder(BorderFactory.createEmptyBorder(8,20,8,20));

        return button;
    }

    // Resets the game's logical state for a new game
    private void resetGameLogic() {
        if (board != null) {
            board.resetBoard(); // Get new random preset, snakes, ladders
            if (boardPanel != null) {
                boardPanel.updateBoard(board, players); // Call new method in BoardPanel
            }
        }
        if (players != null) {
            for (Player p : players) {
                if (p != null) {
                    p.setPosition(1);        // Reset position
                    p.setMovementPath(1, 1); // Clear any old movement path queue
                }
            }
        }
        currentPlayerIndex = 0; // Reset to the first player
    }
}