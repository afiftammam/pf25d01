package TTTConsole;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Board implements Serializable {
    public int ROWS;
    public int COLS;
    public int WIN_STREAK;

    public int CANVAS_WIDTH;
    public int CANVAS_HEIGHT;
    public static final int CELL_SIZE = 120;
    private static final int GRID_THICKNESS = 8; // Ketebalan garis grid

    public Cell[][] cells;
    private int[] winningLineCoords = null;
    private float winAnimationProgress = 0f;

    private transient Timer winAnimationTimer;
    private transient JPanel gameSurface;
    private transient BufferedImage backgroundImage;

    public Board(JPanel surface, int size) {
        this.gameSurface = surface;
        this.ROWS = size;
        this.COLS = size;
        this.WIN_STREAK = (size > 5) ? 5 : size; // Win streak 5 untuk board > 5

        this.CANVAS_WIDTH = CELL_SIZE * COLS;
        this.CANVAS_HEIGHT = CELL_SIZE * ROWS;

        // Muat gambar background
        this.backgroundImage = AssetManager.getImage("BACKGROUND");

        cells = new Cell[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c] = new Cell(r, c);
            }
        }
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

    public void paint(Graphics2D g2d) {
        // Mengaktifkan anti-aliasing untuk gambar dan garis yang lebih halus
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Gambar background
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT, null);
        } else {
            // Latar belakang polos jika gambar tidak ada
            g2d.setColor(Theme.BG_MAIN);
            g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        }

        // 2. Gambar semua simbol (X dan O)
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c].paint(g2d);
            }
        }

        // 3. Gambar garis grid di atas simbol
        g2d.setColor(new Color(255, 255, 255, 100)); // Warna grid putih transparan
        g2d.setStroke(new BasicStroke(GRID_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 1; i < ROWS; i++) {
            g2d.drawLine(0, i * CELL_SIZE, CANVAS_WIDTH, i * CELL_SIZE);
        }
        for (int i = 1; i < COLS; i++) {
            g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, CANVAS_HEIGHT);
        }

        // 4. Gambar garis kemenangan (jika ada)
        if (winningLineCoords != null) {
            drawWinningLine(g2d);
        }
    }

    private void drawWinningLine(Graphics2D g2d) {
        g2d.setColor(Theme.WIN_LINE);
        g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int x1 = winningLineCoords[1] * CELL_SIZE + CELL_SIZE / 2;
        int y1 = winningLineCoords[0] * CELL_SIZE + CELL_SIZE / 2;
        int x2 = winningLineCoords[3] * CELL_SIZE + CELL_SIZE / 2;
        int y2 = winningLineCoords[2] * CELL_SIZE + CELL_SIZE / 2;

        int animatedX2 = x1 + (int)((x2 - x1) * winAnimationProgress);
        int animatedY2 = y1 + (int)((y2 - y1) * winAnimationProgress);

        g2d.drawLine(x1, y1, animatedX2, animatedY2);
    }

    public void startWinAnimation() {
        if (winAnimationTimer != null && winAnimationTimer.isRunning()) {
            winAnimationTimer.stop();
        }
        winAnimationTimer = new Timer(15, e -> {
            winAnimationProgress += 0.05f;
            if (winAnimationProgress >= 1.0f) {
                winAnimationProgress = 1.0f;
                winAnimationTimer.stop();
            }
            if (gameSurface != null) gameSurface.repaint();
        });
        winAnimationTimer.start();
    }

    // --- Logika Game (diambil dari kode asli Anda) ---

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

    public boolean isDraw() {
        for(Cell[] row : cells) {
            for(Cell cell : row) {
                if(cell.content == Seed.NO_SEED) return false;
            }
        }
        return true;
    }

    public State getCurrentGameState() {
        if (hasWon(Seed.NOUGHT)) return State.NOUGHT_WON;
        if (hasWon(Seed.CROSS)) return State.CROSS_WON;
        if (isDraw()) return State.DRAW;
        return State.PLAYING;
    }

    private boolean hasWon(Seed p) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (checkLine(r, c, 1, 0, p) || // Horizontal
                        checkLine(r, c, 0, 1, p) || // Vertikal
                        checkLine(r, c, 1, 1, p) || // Diagonal \
                        checkLine(r, c, 1, -1, p)) { // Diagonal /
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasWon(Seed p, int r, int c) {
        return checkLine(r, c, 1, 0, p) ||
                checkLine(r, c, 0, 1, p) ||
                checkLine(r, c, 1, 1, p) ||
                checkLine(r, c, 1, -1, p);
    }

    private boolean checkLine(int r, int c, int dr, int dc, Seed p) {
        int count = 0;
        int rStart = -1, cStart = -1, rEnd = -1, cEnd = -1;

        // Hitung ke satu arah
        for (int i = 0; i < WIN_STREAK; i++) {
            int nr = r + i * dr;
            int nc = c + i * dc;
            if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && cells[nr][nc].content == p) {
                if (count == 0) {
                    rStart = nr;
                    cStart = nc;
                }
                count++;
                rEnd = nr;
                cEnd = nc;
            } else {
                break;
            }
        }

        if (count >= WIN_STREAK) {
            winningLineCoords = new int[]{rStart, cStart, rEnd, cEnd};
            return true;
        }
        return false;
    }
}