package gamesrc.ui;

import java.awt.*;
import javax.swing.border.Border;

public class SharpRoundedBorder implements Border {
    private final int radius;

    public SharpRoundedBorder(int radius) {
        this.radius = radius;
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius + 1, this.radius + 1, this.radius + 1, this.radius + 1);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}
