package gamesrc.ui;

import javax.swing.*;
import java.awt.*;
//import java.awt.event.ActionEvent;

public class PlayerSelectionDialog extends JDialog {

    private int selectPlayerCount = 0; // Default
//    private JRadioButton rb2Players, rb3Players, rb4Players;

    public PlayerSelectionDialog() {
        super((Frame) null, "Select Players", true); // Modal dialog
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
//        setSize(250, 150);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Close this only

        // Icon
        try {
            java.net.URL iconURL = getClass().getResource("/gamesrc/assets/sprites/diceicon.png");
            if (iconURL != null) {
                setIconImage(new ImageIcon(iconURL).getImage());
            }
        } catch (Exception e) {
            System.err.println("Could not load dialog icon: " + e.getMessage());
        }

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel label = new JLabel("How many players?");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);

        add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btn2Players = createPlayerButton("2 Players", 2);
        JButton btn3Players = createPlayerButton("3 Players", 3);
        JButton btn4Players = createPlayerButton("4 Players", 4);

        btn2Players.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn3Players.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn4Players.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(btn2Players);
        add(Box.createRigidArea(new Dimension(0, 5))); // Space between buttons
        add(btn3Players);
        add(Box.createRigidArea(new Dimension(0, 5))); // Space between buttons
        add(btn4Players);

        pack(); // Adjust size
        setResizable(false);
    }

    private JButton createPlayerButton(String text, int playerCount) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40)); // Size
        button.setMaximumSize(new Dimension(120, 40)); // Locked the size
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        // Set player count + close dialog
        button.addActionListener(e -> {
            selectPlayerCount = playerCount;
            dispose();
        });
        return button;
    }

    // Method to get the result after the dialog is closed
    public int getSelectPlayerCount() {
        return selectPlayerCount;
    }
}
