package TTTConsole;

import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.*;
import java.io.Serializable;

public class Board implements Serializable {
    public int ROWS;
    public int COLS;
    public int WIN_STREAK;

    public int CANVAS_WIDTH;
    public int CANVAS_HEIGHT;
    public static final int CELL_SIZE = 120;

    public Cell[][] cells;
    private int[] winningLineCoords = null;
    private float winAnimationProgress = 0f;

    private transient Timer winAnimationTimer;
    private transient JPanel gameSurface;

    public Board(JPanel surface, int size) {
        this.gameSurface = surface;
        this.ROWS = size;
        this.COLS = size;
        this.WIN_STREAK = size;

        this.CANVAS_WIDTH = CELL_SIZE * COLS;
        this.CANVAS_HEIGHT = CELL_SIZE * ROWS;

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

    public State checkGameState(Seed player, int row, int col) {
        if (hasWon(player, row, col)) {
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        }
        if (isDraw()) {
            return State.DRAW;
        }
        return State.PLAYING;
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
                if (cells[r][c].content == p) {
                    if (checkDirection(r, c, 1, 0, p) ||
                            checkDirection(r, c, 0, 1, p) ||
                            checkDirection(r, c, 1, 1, p) ||
                            checkDirection(r, c, 1, -1, p)) {
                        return true;
                    }
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
        int count = 1;
        for (int i = 1; i < WIN_STREAK; i++) {
            int nr = r + i * dr;
            int nc = c + i * dc;
            if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && cells[nr][nc].content == p) {
                count++;
            } else {
                break;
            }
        }
        for (int i = 1; i < WIN_STREAK; i++) {
            int nr = r - i * dr;
            int nc = c - i * dc;
            if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && cells[nr][nc].content == p) {
                count++;
            } else {
                break;
            }
        }

        if (count >= WIN_STREAK) {
            int r1 = r - (WIN_STREAK - 1) * dr;
            int c1 = c - (WIN_STREAK - 1) * dc;
            while(r1 < 0 || r1 >= ROWS || c1 < 0 || c1 >= COLS || cells[r1][c1].content != p) {
                r1 += dr; c1 += dc;
            }
            int r2 = r1 + (WIN_STREAK - 1) * dr;
            int c2 = c1 + (WIN_STREAK - 1) * dc;
            winningLineCoords = new int[]{r1, c1, r2, c2};
            return true;
        }
        return false;
    }

    private boolean checkDirection(int r, int c, int dr, int dc, Seed p) {
        for (int i = 0; i < WIN_STREAK; i++) {
            int nr = r + i * dr;
            int nc = c + i * dc;
            if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS || cells[nr][nc].content != p) {
                return false;
            }
        }
        return true;
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

    public boolean isDraw() {
        for(Cell[] row : cells) {
            for(Cell cell : row) {
                if(cell.content == Seed.NO_SEED) return false;
            }
        }
        return true;
    }

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

    public void paint(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c].paint(g2d);
            }
        }

        if (winningLineCoords != null) {
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
    }
}