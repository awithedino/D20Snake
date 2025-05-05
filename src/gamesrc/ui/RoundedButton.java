package gamesrc.ui;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton{
    private int radius = 15;

    public RoundedButton(String text) {
        super(text);
        initialize();
    }

    public RoundedButton(String text, Icon icon) {
        super(text, icon);
        initialize();
    }

    private void initialize() {
        setContentAreaFilled(false); // I did it
        setFocusPainted(false);
        setBorderPainted(false); // I did it
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the background shape
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        // JButton did it, not me
        super.paintComponent(g);

        // Paint the outline
        g2.setColor(Color.darkGray);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        g2.dispose();
    }
}

