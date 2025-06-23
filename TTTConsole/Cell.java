// afiftammam/pf25d01/pf25d01-3dfb4fb69b1a626b65b6f26d53b96147041a1c8f/TTTConsole/Cell.java
package TTTConsole;

import java.awt.*;
import java.awt.image.BufferedImage;
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

    /**
     * --- [PERUBAHAN] ---
     * Metode paint sekarang menerima cellSize sebagai parameter.
     * Ini memungkinkan simbol untuk digambar dengan ukuran yang benar
     * berdasarkan ukuran sel yang dihitung secara dinamis.
     */
    public void paint(Graphics2D g2d, int cellSize) {
        if (content == Seed.NO_SEED) {
            return;
        }

        BufferedImage symbolImage = null;
        if (content == Seed.CROSS) {
            symbolImage = AssetManager.getImage("CROSS");
        } else if (content == Seed.NOUGHT) {
            symbolImage = AssetManager.getImage("NOUGHT");
        }

        if (symbolImage != null) {
            int padding = (int) (cellSize * 0.15);
            int x = col * cellSize + padding;
            int y = row * cellSize + padding;
            int size = cellSize - (2 * padding);

            g2d.drawImage(symbolImage, x, y, size, size, null);
        }
    }
}