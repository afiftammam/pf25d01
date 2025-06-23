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
     * Versi final: Metode paint hanya menggambar gambar simbol X atau O.
     * Latar belakang dan grid digambar oleh kelas Board.
     */
    public void paint(Graphics2D g2d) {
        // Jangan menggambar apa pun jika sel kosong
        if (content == Seed.NO_SEED) {
            return;
        }


        BufferedImage symbolImage = null;
        if (content == Seed.CROSS) {
            symbolImage = AssetManager.getImage("CROSS");
        } else if (content == Seed.NOUGHT) {
            symbolImage = AssetManager.getImage("NOUGHT");
        }


        // Gambar simbol jika ada
        if (symbolImage != null) {
            int cellSize = Board.CELL_SIZE;
            // Padding agar gambar tidak terlalu mepet ke tepi sel
            int padding = (int) (cellSize * 0.15); // 15% padding


            int x = col * cellSize + padding;
            int y = row * cellSize + padding;
            int size = cellSize - (2 * padding);


            g2d.drawImage(symbolImage, x, y, size, size, null);
        }
    }
}

