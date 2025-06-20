package TTTConsole;

import java.awt.*;
import java.io.Serializable;

public class Cell implements Serializable {
    Seed content;
    int row, col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        newGame();
    }

    public void newGame() {
        content = Seed.NO_SEED;
    }

    public void paint(Graphics2D g2d) {
        int margin = 8;
        int cellSize = Board.CELL_SIZE;
        int x = col * cellSize + margin;
        int y = row * cellSize + margin;
        int size = cellSize - (2 * margin);
        int cornerRadius = 20;

        g2d.setColor(Theme.BG_PANEL);
        g2d.fillRoundRect(x, y, size, size, cornerRadius, cornerRadius);

        int symbolPadding = 25;
        g2d.setStroke(new BasicStroke(18, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (content == Seed.CROSS) {
            g2d.setColor(Theme.CROSS);
            g2d.drawLine(x + symbolPadding, y + symbolPadding, x + size - symbolPadding, y + size - symbolPadding);
            g2d.drawLine(x + size - symbolPadding, y + symbolPadding, x + symbolPadding, y + size - symbolPadding);
        } else if (content == Seed.NOUGHT) {
            g2d.setColor(Theme.NOUGHT);
            g2d.drawOval(x + symbolPadding, y + symbolPadding, size - 2 * symbolPadding, size - 2 * symbolPadding);
        }
    }
}