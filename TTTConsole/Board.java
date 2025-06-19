package TTTConsole;

import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.*;
import java.io.Serializable;

/**
 * Kelas Board yang telah direfaktor.
 * Menambahkan metode publik untuk mendapatkan state permainan saat ini.
 */
public class Board implements Serializable { // Tambahkan Serializable untuk fitur Save/Load di masa depan
    public static final int ROWS = 3;
    public static final int COLS = 3;
    public static final int CANVAS_WIDTH = Cell.SIZE * COLS;
    public static final int CANVAS_HEIGHT = Cell.SIZE * ROWS;

    public Cell[][] cells;
    private int[] winningLineCoords = null;
    private float winAnimationProgress = 0f;

    // Objek Swing tidak bisa diserialisasi, tandai sebagai 'transient'
    private transient Timer winAnimationTimer;
    private transient JPanel gameSurface;

    public Board(JPanel surface) {
        this.gameSurface = surface;
        cells = new Cell[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c] = new Cell(r, c);
            }
        }
    }

    public void setGameSurface(JPanel surface) {
        this.gameSurface = surface;
    }

    // Metode checkGameState yang ada tidak berubah
    public State checkGameState(Seed player, int row, int col) {
        if (hasWon(player, row, col)) {
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        }
        if (isDraw()) {
            return State.DRAW;
        }
        return State.PLAYING;
    }

    // --- METODE BARU UNTUK REFAKTORISASI ---

    /**
     * Memeriksa keadaan permainan saat ini secara keseluruhan.
     * Berguna untuk simulasi AI tanpa memerlukan info langkah terakhir.
     * @return State permainan saat ini (PLAYING, DRAW, CROSS_WON, NOUGHT_WON).
     */
    public State getCurrentGameState() {
        if (hasWon(Seed.NOUGHT)) return State.NOUGHT_WON;
        if (hasWon(Seed.CROSS)) return State.CROSS_WON;
        if (isDraw()) return State.DRAW;
        return State.PLAYING;
    }

    /**
     * Overload metode hasWon untuk memeriksa kemenangan pemain tertentu di seluruh papan.
     * @param p Bidak pemain (CROSS atau NOUGHT).
     * @return boolean true jika pemain p menang.
     */
    private boolean hasWon(Seed p) {
        // Cek 3 baris dan 3 kolom
        for (int i = 0; i < 3; i++) {
            if ((cells[i][0].content == p && cells[i][1].content == p && cells[i][2].content == p) ||
                    (cells[0][i].content == p && cells[1][i].content == p && cells[2][i].content == p)) {
                return true;
            }
        }
        // Cek 2 diagonal
        return (cells[0][0].content == p && cells[1][1].content == p && cells[2][2].content == p) ||
                (cells[0][2].content == p && cells[1][1].content == p && cells[2][0].content == p);
    }

    // --- Sisa metode tidak berubah ---

    public void startWinAnimation() {
        winAnimationTimer = new Timer(10, e -> {
            winAnimationProgress += 0.05f;
            if (winAnimationProgress >= 1.0f) {
                winAnimationProgress = 1.0f;
                winAnimationTimer.stop();
            }
            if (gameSurface != null) gameSurface.repaint();
        });
        winAnimationTimer.start();
    }

    public void newGame() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c].newGame();
            }
        }
        winningLineCoords = null;
        winAnimationProgress = 0;
        if (winAnimationTimer != null) {
            winAnimationTimer.stop();
        }
    }

    public boolean isValidMove(int row, int col) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            return cells[row][col].content == Seed.NO_SEED;
        }
        return false;
    }

    public void placeSeed(Seed player, int row, int col) {
        cells[row][col].content = player;
    }

    private boolean hasWon(Seed p, int r, int c) {
        if (cells[r][0].content == p && cells[r][1].content == p && cells[r][2].content == p) {
            winningLineCoords = new int[]{r, 0, r, 2}; return true;
        }
        if (cells[0][c].content == p && cells[1][c].content == p && cells[2][c].content == p) {
            winningLineCoords = new int[]{0, c, 2, c}; return true;
        }
        if (r == c && cells[0][0].content == p && cells[1][1].content == p && cells[2][2].content == p) {
            winningLineCoords = new int[]{0, 0, 2, 2}; return true;
        }
        if (r + c == 2 && cells[0][2].content == p && cells[1][1].content == p && cells[2][0].content == p) {
            winningLineCoords = new int[]{0, 2, 2, 0}; return true;
        }
        return false;
    }

    public boolean isDraw() {
        for(Cell[] row : cells) {
            for(Cell cell : row) {
                if(cell.content == Seed.NO_SEED) return false;
            }
        }
        return true;
    }

    public void paint(Graphics2D g2d) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c].paint(g2d);
            }
        }
        g2d.setColor(Theme.GRID);
        g2d.setStroke(new BasicStroke(4));
        for (int i = 1; i < ROWS; i++) {
            g2d.drawLine(0, i * Cell.SIZE, CANVAS_WIDTH, i * Cell.SIZE);
        }
        for (int i = 1; i < COLS; i++) {
            g2d.drawLine(i * Cell.SIZE, 0, i * Cell.SIZE, CANVAS_HEIGHT);
        }

        if (winningLineCoords != null) {
            g2d.setColor(Theme.WIN_LINE);
            g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int x1 = winningLineCoords[1] * Cell.SIZE + Cell.SIZE / 2;
            int y1 = winningLineCoords[0] * Cell.SIZE + Cell.SIZE / 2;
            int x2 = winningLineCoords[3] * Cell.SIZE + Cell.SIZE / 2;
            int y2 = winningLineCoords[2] * Cell.SIZE + Cell.SIZE / 2;

            int animatedX2 = x1 + (int)((x2 - x1) * winAnimationProgress);
            int animatedY2 = y1 + (int)((y2 - y1) * winAnimationProgress);

            g2d.drawLine(x1, y1, animatedX2, animatedY2);
        }
    }
}