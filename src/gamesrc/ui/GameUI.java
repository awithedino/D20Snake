package gamesrc.ui;

import gamesrc.game.Board;
import gamesrc.game.Dice;
import gamesrc.entities.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class GameUI extends JFrame {
    private static final int NUM_ROWS = 10;
    private static final int NUM_COLS = 10;
    private static final int NUM_PLAYERS = 4;
    private static final String DICE_ICON_PATH = "diceicon.png";
    private static final String ROLL_BUTTON_FONT = "Arial Black"; // Đổi thành Arial Black


    private Board board;
    private Dice dice;
    private Player[] players;
    private int currentPlayerIndex;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton rollButton;
    private JLabel diceAnimationLabel;  // Declare the animation label here

    public GameUI() {
        board = new Board("src/gamesrc/assets/presets/boardPresets.json");
        dice = new Dice();
        players = new Player[NUM_PLAYERS];
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
        setSize(650, 750);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        boardPanel = new BoardPanel(board, players);

        statusLabel = createStatusLabel();
        rollButton = createRollButton();

        JPanel controlPanel = createControlPanel();

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Đặt màu nền tối cho toàn bộ JFrame
        getContentPane().setBackground(new Color(30, 30, 30));

        setVisible(true);
    }

    private JLabel createDiceAnimationLabel() {
        // Tạo GIF cho animation
        ImageIcon diceIcon = new ImageIcon(getClass().getResource("roll_dice_animation.gif"));
        JLabel label = new JLabel(diceIcon);
        label.setBounds(0, 0, getWidth(), getHeight());  // Đặt size đầy đủ
        label.setVisible(false);  // Lúc bắt đầu thì không hiển thị animation
        return label;
    }

    private JLabel createStatusLabel() {
        JLabel label = new JLabel("Welcome, Adventurer!");
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(new Color(255, 255, 150)); // Màu vàng nhạt
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JButton createRollButton() {
        ImageIcon icon = new ImageIcon(getClass().getResource("diceicon.png"));
        Image scaledIcon = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JButton button = new JButton("Roll!", new ImageIcon(scaledIcon));
        button.setFont(new Font(ROLL_BUTTON_FONT, Font.BOLD, 24)); // Sử dụng Arial Black
        button.setBackground(new Color(150, 100, 0)); // Màu vàng sẫm
        button.setForeground(new Color(255, 255, 180)); // Màu vàng nhạt hơn
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                new SharpRoundedBorder(15), // Bo góc
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        button.setPreferredSize(new Dimension(180, 60));
        button.setHorizontalTextPosition(SwingConstants.LEFT);
        button.setIconTextGap(15);
        button.addActionListener(e -> takeTurn());
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(180, 120, 0));
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

        if (currentPlayer.hasWon(NUM_ROWS * NUM_COLS)) {
            statusLabel.setText(currentPlayer.getName() + " wins!");
            rollButton.setEnabled(false);
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
            statusLabel.setText("Next: " + players[currentPlayerIndex].getName());
            rollButton.setEnabled(true);

            // Hiển thị animation khi quay xúc xắc
            diceAnimationLabel.setVisible(true);
            Timer timer = new Timer(1000, e -> diceAnimationLabel.setVisible(false));  // 1s sau khi quay thì ẩn animation
            timer.setRepeats(false);
            timer.start();
        }
    }

    public static void launchGame() {
        SwingUtilities.invokeLater(GameUI::new);
    }
}
