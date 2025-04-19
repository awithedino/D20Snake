package gamesrc.ui;

import gamesrc.game.Board;
import gamesrc.game.Dice;
import gamesrc.entities.Player;
import gamesrc.entities.Snake;
import gamesrc.entities.Ladder;

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

    private final Board board;
    private final Dice dice;
    private final Player[] players;
    private int currentPlayerIndex;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JButton rollButton;
    private JLabel diceAnimationLabel;  // Declare the animation label here

    public GameUI() {
        board = new Board();
        dice = new Dice();
        players = new Player[NUM_PLAYERS];
        currentPlayerIndex = 0;
        initPlayers();
        initUI();
    }

    private void initPlayers() {
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player("Player " + (i + 1));
        }
    }

    private void initUI() {
        setTitle("Snakes and Ladders"); // Loại bỏ "Dungeon Edition"
        setSize(650, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        boardPanel = createBoardPanel();
        statusLabel = createStatusLabel();
        rollButton = createRollButton();

        JPanel controlPanel = createControlPanel();

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Đặt màu nền tối cho toàn bộ JFrame
        getContentPane().setBackground(new Color(30, 30, 30));

        setVisible(true);
        setResizable(false);
    }

    private JPanel createBoardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int width = getWidth();
                int height = getHeight();
                int cellSize = Math.min(width / NUM_COLS, height / NUM_ROWS);
                drawBoard(g, cellSize);
                drawSnakesAndLadders(g, cellSize);
                drawPlayers(g, cellSize);
            }
        };
        panel.setLayout(null);

        // Khởi tạo diceAnimationLabel sau khi boardPanel đã được tạo xong
        diceAnimationLabel = createDiceAnimationLabel();
        panel.add(diceAnimationLabel);  // Đảm bảo dice animation được thêm vào panel

        return panel;
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

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(150, 100, 0));
            }
        });
        return button;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(40, 40, 40)); // Màu đen cho panel điều khiển
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(rollButton, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void drawBoard(Graphics g, int cellSize) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                int x = j * cellSize;
                int y = (NUM_ROWS - 1 - i) * cellSize;
                g.drawRect(x, y, cellSize, cellSize);
                int number = i * NUM_COLS + ((i % 2 == 0) ? j + 1 : NUM_COLS - j);
                g.drawString(String.valueOf(number), x + 5, y + 15);
            }
        }
    }

    private void drawSnakesAndLadders(Graphics g, int cellSize) {
        g.setColor(Color.RED);
        for (Snake s : board.getSnakes()) drawLineBetweenCells(g, s.getStart(), s.getEnd(), cellSize);
        g.setColor(Color.GREEN);
        for (Ladder l : board.getLadders()) drawLineBetweenCells(g, l.getStart(), l.getEnd(), cellSize);
    }

    private void drawLineBetweenCells(Graphics g, int start, int end, int cellSize) {
        Point p1 = getCellCenter(start, cellSize);
        Point p2 = getCellCenter(end, cellSize);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    private void drawPlayers(Graphics g, int cellSize) {
        Color[] colors = {Color.BLUE, Color.MAGENTA, Color.ORANGE, Color.BLACK};
        int playerSize = cellSize / 3;
        for (int i = 0; i < players.length; i++) {
            Point p = getCellCenter(players[i].getPosition(), cellSize);
            g.setColor(colors[i]);
            g.fillOval(p.x - playerSize / 2 + (i * playerSize / NUM_PLAYERS), p.y - playerSize / 2, playerSize / NUM_PLAYERS, playerSize / NUM_PLAYERS);
        }
    }

    private Point getCellCenter(int position, int cellSize) {
        if (position > NUM_ROWS * NUM_COLS) position = NUM_ROWS * NUM_COLS;
        int row = (position - 1) / NUM_COLS;
        int col = (position - 1) % NUM_COLS;
        if (row % 2 == 1) col = NUM_COLS - 1 - col;
        int x = col * cellSize + cellSize / 2;
        int y = (NUM_ROWS - 1 - row) * cellSize + cellSize / 2;
        return new Point(x, y);
    }

    private void takeTurn() {
        Player currentPlayer = players[currentPlayerIndex];
        int roll = dice.roll();
        statusLabel.setText(currentPlayer.getName() + " rolled a " + roll);

        currentPlayer.move(roll);
        if (currentPlayer.getPosition() > NUM_ROWS * NUM_COLS) {
            currentPlayer.setPosition(2 * NUM_ROWS * NUM_COLS - currentPlayer.getPosition()); // Bounce back
        }

        int newPos = board.checkSnakesAndLadders(currentPlayer.getPosition());
        currentPlayer.setPosition(newPos);
        boardPanel.repaint(); // Yêu cầu vẽ lại để cập nhật vị trí người chơi

        if (currentPlayer.hasWon(NUM_ROWS * NUM_COLS)) {
            statusLabel.setText(currentPlayer.getName() + " wins!");
            rollButton.setEnabled(false);
            return;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        statusLabel.setText("Next: " + players[currentPlayerIndex].getName() + "'s turn.");

        // Hiển thị animation khi quay xúc xắc
        diceAnimationLabel.setVisible(true);
        Timer timer = new Timer(1000, e -> diceAnimationLabel.setVisible(false));  // 1s sau khi quay thì ẩn animation
        timer.setRepeats(false);
        timer.start();
    }

    public static void launchGame() {
        SwingUtilities.invokeLater(GameUI::new);
    }
}

// Lớp tùy chỉnh để tạo viền bo góc sắc nhọn
class SharpRoundedBorder implements Border {
    private final int radius;

    SharpRoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c.getForeground());
        g2.drawLine(x, y + radius, x + radius, y);
        g2.drawLine(x + width - radius, y, x + width, y + radius);
        g2.drawLine(x, y + height - radius, x + radius, y + height);
        g2.drawLine(x + width - radius, y + height, x + width, y + height - radius);
        g2.drawLine(x + radius, y, x + width - radius, y);
        g2.drawLine(x, y + radius, x, y + height - radius);
        g2.drawLine(x + width, y + radius, x + width, y + height - radius);
        g2.drawLine(x + radius, y + height, x + width - radius, y + height);
        g2.dispose();
    }
}
