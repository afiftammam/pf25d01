package TTTConsole;

import java.awt.*;
import java.io.Serializable;

public class Cell implements Serializable { // Tambahkan Serializable
    public static final int SIZE = 140;
    private static final int PADDING = 25;

    Seed content;
    int row, col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        content = Seed.NO_SEED;
    }

    public void newGame() {
        content = Seed.NO_SEED;
    }

    public void paint(Graphics2D g2d) {
        int x = col * SIZE;
        int y = row * SIZE;

        g2d.setColor(Theme.BG_PANEL);
        g2d.fillRect(x, y, SIZE, SIZE);

        g2d.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (content == Seed.CROSS) {
            g2d.setColor(Theme.CROSS);
            g2d.drawLine(x + PADDING, y + PADDING, x + SIZE - PADDING, y + SIZE - PADDING);
            g2d.drawLine(x + SIZE - PADDING, y + PADDING, x + PADDING, y + SIZE - PADDING);
        } else if (content == Seed.NOUGHT) {
            g2d.setColor(Theme.NOUGHT);
            g2d.drawOval(x + PADDING, y + PADDING, SIZE - 2 * PADDING, SIZE - 2 * PADDING);
        }
    }
}