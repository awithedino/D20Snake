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
    private Board board;
    private Dice dice;
    private Player[] players;
    private int currentPlayerIndex = 0;

    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton rollButton; // Sẽ là RoundedButton

    private static final int BOARD_SIZE = 100;

    public GameUI() {
        setTitle("Snakes and Ladders - D20 Edition");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // setLocationRelativeTo(null); // Sẽ gọi sau pack()

        showPlayerSelectionScreen();
    }

    private void showPlayerSelectionScreen() {
        Container contentPane = getContentPane();
        contentPane.removeAll();

        Color softerBackground = new Color(210, 210, 210);
        contentPane.setBackground(softerBackground);

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        ((JPanel) contentPane).setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Snakes & Ladders!");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(titleLabel);
        contentPane.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel label = new JLabel("How many players?");
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(label);
        contentPane.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btn2Players = createPlayerButton("2 Players", 2);
        JButton btn3Players = createPlayerButton("3 Players", 3);
        JButton btn4Players = createPlayerButton("4 Players", 4);

        btn2Players.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn3Players.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn4Players.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPane.add(btn2Players);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPane.add(btn3Players);
        contentPane.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPane.add(btn4Players);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createPlayerButton(String text, int playerCount) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        Dimension buttonSize = new Dimension(150, 50);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        button.addActionListener(e -> startGame(playerCount));
        return button;
    }

    private void startGame(int numberOfPlayers) {
        board = new Board("src/gamesrc/assets/presets/boardPresets.json");
        dice = new Dice();
        players = new Player[numberOfPlayers];
        initPlayers(numberOfPlayers);

        // boardPanel phải được tạo trước khi nó được dùng làm parent cho dialog (nếu cần)
        // hoặc trước khi GameUI được dùng làm parent cho dialog (nếu dialog gọi trong context của GameUI)
        boardPanel = new BoardPanel(board, players);
        rollButton = createStyledRollButton(e -> takeTurn());
        statusLabel = new JLabel("Welcome! " + players[0].getName() + "'s turn first.");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setOpaque(false);

        JPanel controlPanel = new JPanel(new BorderLayout(15, 0));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        Color softerBackground = new Color(210, 210, 210);
        controlPanel.setBackground(softerBackground);

        controlPanel.add(rollButton, BorderLayout.WEST);
        controlPanel.add(statusLabel, BorderLayout.CENTER);

        Container contentPane = getContentPane();
        contentPane.removeAll();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(softerBackground);

        contentPane.add(boardPanel, BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        // Frame đã hiển thị từ showPlayerSelectionScreen, không cần setVisible(true) ở đây nữa
        // Nếu bạn muốn đảm bảo, có thể gọi revalidate và repaint
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
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setRadius(15);

        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        button.setPreferredSize(new Dimension(180, 60));
        button.setHorizontalTextPosition(SwingConstants.LEFT);
        button.setIconTextGap(15);

        button.addActionListener(existingRollListener);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(180, 120, 0));
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(150, 100, 0));
                button.repaint();
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
        int bounceBackTargetSquare = -1;

        if (rawTarget > BOARD_SIZE) {
            firstPathEnd = BOARD_SIZE;
            int excess = rawTarget - BOARD_SIZE;
            bounceBackTargetSquare = Math.max(1, BOARD_SIZE - excess);
            currentPlayer.setMovementPath(from, firstPathEnd);
            statusLabel.setText(currentPlayer.getName() + " hits 100...");
        } else {
            firstPathEnd = rawTarget;
            currentPlayer.setMovementPath(from, firstPathEnd);
        }

        Timer timer = getTimer(currentPlayer, bounceBackTargetSquare);
        timer.start();
    }

    private Timer getTimer(Player currentPlayer, int bounceBackTargetSquare) {
        return new Timer(100, new ActionListener() {
            boolean isAnimatingMovement = true;
            boolean needsToStartBounceBack = (bounceBackTargetSquare != -1);
            boolean teleportCheckPending = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                Timer timer = (Timer) e.getSource();

                if (isAnimatingMovement) {
                    if (currentPlayer.step()) {
                        boardPanel.repaint();
                        return;
                    } else {
                        isAnimatingMovement = false;
                        boardPanel.repaint();
                        if (needsToStartBounceBack) {
                            statusLabel.setText(currentPlayer.getName() + " bounces back from 100!");
                            currentPlayer.setMovementPath(BOARD_SIZE, bounceBackTargetSquare);
                            needsToStartBounceBack = false;
                            isAnimatingMovement = true;
                            return;
                        }
                    }
                }

                if (teleportCheckPending) {
                    int currentPos = currentPlayer.getPosition();
                    int finalPos = board.checkSnakesAndLadders(currentPos);
                    teleportCheckPending = false;

                    if (finalPos != currentPos) {
                        statusLabel.setText(currentPlayer.getName() + (finalPos > currentPos ? " climbed a ladder!" : " slid down a snake!"));
                        currentPlayer.setPosition(finalPos);
                        boardPanel.repaint();
                    } else {
                        statusLabel.setText(currentPlayer.getName() + " landed on " + currentPos);
                    }
                }

                timer.stop();

                if (currentPlayer.hasWon(BOARD_SIZE)) {
                    rollButton.setEnabled(false); // Vô hiệu hóa nút trước

                    WinDialog winDialog = new WinDialog(GameUI.this, currentPlayer.getName());
                    winDialog.setVisible(true); // Hiển thị dialog và chờ

                    if (winDialog.isPlayAgainSelected()) {
                        resetGameAndShowPlayerSelection();
                    } else {
                        System.exit(0); // Thoát game
                    }
                } else {
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
                    statusLabel.setText("Next: " + players[currentPlayerIndex].getName() + "'s turn.");
                    rollButton.setEnabled(true);
                }
            }
        });
    }

    // Phương thức để reset game và hiển thị lại màn hình chọn người chơi
    private void resetGameAndShowPlayerSelection() {
        // Đặt lại các biến game
        this.board = null; // Hoặc gọi board.initBoard() nếu có để reset preset
        this.dice = new Dice(); // Tạo xúc xắc mới
        this.players = null; // Sẽ được tạo lại
        this.currentPlayerIndex = 0;

        // Xóa các component UI cũ của game
        Container contentPane = getContentPane();
        if (boardPanel != null) {
            contentPane.remove(boardPanel);
            boardPanel = null;
        }
        // statusLabel và rollButton sẽ được tạo lại trong startGame khi contentPane được thiết lập lại

        // Hiển thị lại màn hình chọn người chơi
        showPlayerSelectionScreen(); // Phương thức này sẽ removeAll và thiết lập lại contentPane
    }
}