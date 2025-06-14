package TTTConsole;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    // Definisi konstanta
    public static final int ROWS = 3;
    public static final int COLS = 3;
    public static final int CANVAS_WIDTH = Cell.SIZE * COLS;
    public static final int CANVAS_HEIGHT = Cell.SIZE * ROWS;
    public static final int GRID_WIDTH = 8;
    public static final Color COLOR_GRID = Color.LIGHT_GRAY;

    Cell[][] cells;
    private int[] winningLine = null; // Menyimpan koordinat [baris1, kolom1, baris2, kolom2] dari garis kemenangan

    public Board() {
        initGame();
    }

    public void initGame() {
        cells = new Cell[ROWS][COLS];
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col] = new Cell(row, col);
            }
        }
    }

    public void newGame() {
        winningLine = null; // Reset garis kemenangan
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].newGame();
            }
        }
    }

    public boolean isValidMove(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS && cells[row][col].content == Seed.NO_SEED;
    }

    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        cells[selectedRow][selectedCol].content = player;

        if (hasWon(player, selectedRow, selectedCol)) {
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        }

        if (isDraw()) {
            return State.DRAW;
        }

        return State.PLAYING;
    }

    private boolean isDraw() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean hasWon(Seed player, int selectedRow, int selectedCol) {
        // Cek baris
        if (cells[selectedRow][0].content == player && cells[selectedRow][1].content == player && cells[selectedRow][2].content == player) {
            winningLine = new int[]{selectedRow, 0, selectedRow, 2};
            return true;
        }
        // Cek kolom
        if (cells[0][selectedCol].content == player && cells[1][selectedCol].content == player && cells[2][selectedCol].content == player) {
            winningLine = new int[]{0, selectedCol, 2, selectedCol};
            return true;
        }
        // Cek diagonal
        if (selectedRow == selectedCol && cells[0][0].content == player && cells[1][1].content == player && cells[2][2].content == player) {
            winningLine = new int[]{0, 0, 2, 2};
            return true;
        }
        // Cek diagonal terbalik
        if (selectedRow + selectedCol == 2 && cells[0][2].content == player && cells[1][1].content == player && cells[2][0].content == player) {
            winningLine = new int[]{0, 2, 2, 0};
            return true;
        }
        return false;
    }

    public void paint(Graphics g) {
        // Gambar garis-garis papan
        g.setColor(COLOR_GRID);
        for (int row = 1; row < ROWS; ++row) {
            g.fillRoundRect(0, Cell.SIZE * row - GRID_WIDTH / 2, CANVAS_WIDTH - 1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
        }
        for (int col = 1; col < COLS; ++col) {
            g.fillRoundRect(Cell.SIZE * col - GRID_WIDTH / 2, 0, GRID_WIDTH, CANVAS_HEIGHT - 1, GRID_WIDTH, GRID_WIDTH);
        }

        // Gambar semua sel
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].paint(g);
            }
        }

        // Gambar garis kemenangan jika ada
        if (winningLine != null) {
            g.setColor(GameMain.COLOR_WINNING_LINE);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int x1 = winningLine[1] * Cell.SIZE + Cell.SIZE / 2;
            int y1 = winningLine[0] * Cell.SIZE + Cell.SIZE / 2;
            int x2 = winningLine[3] * Cell.SIZE + Cell.SIZE / 2;
            int y2 = winningLine[2] * Cell.SIZE + Cell.SIZE / 2;
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    // Logika AI untuk menemukan langkah terbaik
    public int[] findBestMove() {
        // 1. Cek apakah AI bisa menang
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    cells[row][col].content = Seed.NOUGHT; // Coba langkah
                    if (hasWon(Seed.NOUGHT, row, col)) {
                        cells[row][col].content = Seed.NO_SEED; // Kembalikan
                        return new int[]{row, col};
                    }
                    cells[row][col].content = Seed.NO_SEED; // Kembalikan
                }
            }
        }

        // 2. Cek apakah pemain bisa menang, lalu blokir
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    cells[row][col].content = Seed.CROSS; // Coba langkah pemain
                    if (hasWon(Seed.CROSS, row, col)) {
                        cells[row][col].content = Seed.NO_SEED; // Kembalikan
                        return new int[]{row, col};
                    }
                    cells[row][col].content = Seed.NO_SEED; // Kembalikan
                }
            }
        }

        // 3. Jika tidak ada langkah kritis, pilih langkah acak
        List<int[]> emptyCells = new ArrayList<>();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            return emptyCells.get(new Random().nextInt(emptyCells.size()));
        }

        return null; // Tidak ada langkah yang tersedia
    }
}