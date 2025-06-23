// afiftammam/pf25d01/pf25d01-3dfb4fb69b1a626b65b6f26d53b96147041a1c8f/TTTConsole/Board.java
package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Board implements Serializable {
    public int ROWS;
    public int COLS;
    public int WIN_STREAK;

    // --- [PERUBAHAN] ---
    // Menghilangkan CANVAS_WIDTH dan CANVAS_HEIGHT.
    // CELL_SIZE tidak lagi 'static final' agar bisa diubah.
    public int CELL_SIZE = 120; // Nilai default awal
    private static final int GRID_THICKNESS = 8;

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
        this.WIN_STREAK = size;

        this.backgroundImage = AssetManager.getImage("BACKGROUND");

        cells = new Cell[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c] = new Cell(r, c);
            }
        }
    }

    // --- [BARU] ---
    // Metode untuk mengatur CELL_SIZE dari luar (oleh GameMain)
    public void setCellSize(int newSize) {
        this.CELL_SIZE = newSize;
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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Lebar dan tinggi total papan yang akan digambar
        int totalWidth = COLS * CELL_SIZE;
        int totalHeight = ROWS * CELL_SIZE;

        // Posisi start (x, y) untuk menggambar papan di tengah panel
        int startX = (gameSurface.getWidth() - totalWidth) / 2;
        int startY = (gameSurface.getHeight() - totalHeight) / 2;

        // Menggeser titik nol grafis ke posisi start
        g2d.translate(startX, startY);

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, totalWidth, totalHeight, null);
        } else {
            g2d.setColor(Theme.BG_MAIN);
            g2d.fillRect(0, 0, totalWidth, totalHeight);
        }

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                // --- [PERUBAHAN] ---
                // Mengirim CELL_SIZE yang dinamis ke metode paint sel
                cells[r][c].paint(g2d, CELL_SIZE);
            }
        }

        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.setStroke(new BasicStroke(GRID_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 1; i < ROWS; i++) {
            g2d.drawLine(0, i * CELL_SIZE, totalWidth, i * CELL_SIZE);
        }
        for (int i = 1; i < COLS; i++) {
            g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, totalHeight);
        }

        if (winningLineCoords != null) {
            drawWinningLine(g2d);
        }

        // Mengembalikan titik nol grafis ke posisi semula
        g2d.translate(-startX, -startY);
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

    private boolean hasWon(Seed player, int r, int c) {
        return checkLine(player, r, c, 0, 1)
                || checkLine(player, r, c, 1, 0)
                || checkLine(player, r, c, 1, 1)
                || checkLine(player, r, c, 1, -1);
    }

    private boolean checkLine(Seed player, int r, int c, int dr, int dc) {
        int count = 1;
        int rStart = r, cStart = c, rEnd = r, cEnd = c;

        for (int i = 1; i < WIN_STREAK; i++) {
            int nr = r + i * dr;
            int nc = c + i * dc;
            if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && cells[nr][nc].content == player) {
                count++;
                rEnd = nr; cEnd = nc;
            } else {
                break;
            }
        }

        for (int i = 1; i < WIN_STREAK; i++) {
            int nr = r - i * dr;
            int nc = c - i * dc;
            if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && cells[nr][nc].content == player) {
                count++;
                rStart = nr; cStart = nc;
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

    private boolean hasWon(Seed p) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (cells[r][c].content == p) {
                    if (c + WIN_STREAK <= COLS) {
                        int count = 0;
                        for (int i = 0; i < WIN_STREAK; i++) if (cells[r][c + i].content == p) count++;
                        if (count == WIN_STREAK) return true;
                    }
                    if (r + WIN_STREAK <= ROWS) {
                        int count = 0;
                        for (int i = 0; i < WIN_STREAK; i++) if (cells[r + i][c].content == p) count++;
                        if (count == WIN_STREAK) return true;
                    }
                    if (r + WIN_STREAK <= ROWS && c + WIN_STREAK <= COLS) {
                        int count = 0;
                        for (int i = 0; i < WIN_STREAK; i++) if (cells[r + i][c + i].content == p) count++;
                        if (count == WIN_STREAK) return true;
                    }
                    if (r + WIN_STREAK <= ROWS && c - WIN_STREAK + 1 >= 0) {
                        int count = 0;
                        for (int i = 0; i < WIN_STREAK; i++) if (cells[r + i][c - i].content == p) count++;
                        if (count == WIN_STREAK) return true;
                    }
                }
            }
        }
        return false;
    }
}