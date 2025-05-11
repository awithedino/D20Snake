package gamesrc.ui;

import javax.swing.*;
import java.awt.*;

public class WinDialog extends JDialog {
    private boolean playAgainSelected = false;

    public WinDialog(Frame owner, String winnerName) {
        super(owner, "Game Over!", true); // true = modal
        initComponents(winnerName);
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents(String winnerName) {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setBackground(new Color(220, 220, 240));

        JLabel winMessageLabel = new JLabel(winnerName + " wins the game!", SwingConstants.CENTER);
        winMessageLabel.setFont(new Font("Arial", Font.BOLD, 24));
        winMessageLabel.setForeground(new Color(50, 50, 150));
        contentPanel.add(winMessageLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton playAgainButton = createStyledButton("Play Again");
        playAgainButton.addActionListener(e -> {
            playAgainSelected = true;
            dispose();
        });
        buttonPanel.add(playAgainButton);

        JButton exitButton = createStyledButton("Exit Game");
        exitButton.addActionListener(e -> {
            playAgainSelected = false;
            dispose(); // GameUI sẽ quyết định hành động thoát
        });
        buttonPanel.add(exitButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(contentPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(100, 150, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 100, 150), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        Color originalBg = button.getBackground();
        Color hoverBg = originalBg.brighter();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverBg);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalBg);
            }
        });
        return button;
    }

    public boolean isPlayAgainSelected() {
        return playAgainSelected;
    }
}