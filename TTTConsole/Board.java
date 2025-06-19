package TTTConsole;

import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    public static final int ROWS = 3;
    public static final int COLS = 3;
    public static final int CANVAS_WIDTH = Cell.SIZE * COLS;
    public static final int CANVAS_HEIGHT = Cell.SIZE * ROWS;

    private Cell[][] cells;
    private int[] winningLineCoords = null;
    private float winAnimationProgress = 0f;
    private Timer winAnimationTimer;
    private JPanel gameSurface; // Diubah menjadi JPanel generik

    public Board(JPanel surface) { // Menerima JPanel
        this.gameSurface = surface;
        cells = new Cell[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c] = new Cell(r, c);
            }
        }
    }

    public void startWinAnimation() {
        winAnimationTimer = new Timer(10, e -> {
            winAnimationProgress += 0.05f;
            if (winAnimationProgress >= 1.0f) {
                winAnimationProgress = 1.0f;
                winAnimationTimer.stop();
            }
            gameSurface.repaint(); // Memicu repaint pada panel game
        });
        winAnimationTimer.start();
    }

    // ... sisa kode di kelas Board sama seperti sebelumnya ...
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

    public State checkGameState(Seed player, int row, int col) {
        if (hasWon(player, row, col)) {
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        }
        if (isDraw()) {
            return State.DRAW;
        }
        return State.PLAYING;
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

    private boolean isDraw() {
        for(Cell[] row : cells) {
            for(Cell cell : row) {
                if(cell.content == Seed.NO_SEED) return false;
            }
        }
        return true;
    }

    public void paint(Graphics2D g2d) {
        // Gambar sel
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c].paint(g2d);
            }
        }
        // Gambar garis grid
        g2d.setColor(Theme.GRID);
        g2d.setStroke(new BasicStroke(4));
        for (int i = 1; i < ROWS; i++) {
            g2d.drawLine(0, i * Cell.SIZE, CANVAS_WIDTH, i * Cell.SIZE);
        }
        for (int i = 1; i < COLS; i++) {
            g2d.drawLine(i * Cell.SIZE, 0, i * Cell.SIZE, CANVAS_HEIGHT);
        }

        // Gambar animasi garis kemenangan
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

    public int[] findBestMove() {
        // Cek Menang
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) {
            if (isValidMove(r, c)) {
                placeSeed(Seed.NOUGHT, r, c);
                if (hasWon(Seed.NOUGHT, r, c)) {
                    cells[r][c].newGame(); winningLineCoords = null; return new int[]{r, c};
                }
                cells[r][c].newGame(); winningLineCoords = null;
            }
        }
        // Cek Blok
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) {
            if (isValidMove(r, c)) {
                placeSeed(Seed.CROSS, r, c);
                if (hasWon(Seed.CROSS, r, c)) {
                    cells[r][c].newGame(); winningLineCoords = null; return new int[]{r, c};
                }
                cells[r][c].newGame(); winningLineCoords = null;
            }
        }
        // Acak
        List<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) {
            if (isValidMove(r, c)) emptyCells.add(new int[]{r, c});
        }
        return emptyCells.isEmpty() ? null : emptyCells.get(new Random().nextInt(emptyCells.size()));
    }
}