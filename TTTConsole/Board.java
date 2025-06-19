package TTTConsole;

import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    public static final int ROWS = 3;
    public static final int COLS = 3;
    public static final int CANVAS_WIDTH = Cell.SIZE * COLS;
    public static final int CANVAS_HEIGHT = Cell.SIZE * ROWS;

    private Cell[][] cells;
    private int[] winningLineCoords = null;
    private float winAnimationProgress = 0f;
    private Timer winAnimationTimer;
    private JPanel gameSurface; // Referensi ke panel game untuk repaint

    public Board(JPanel surface) {
        this.gameSurface = surface;
        cells = new Cell[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                cells[r][c] = new Cell(r, c);
            }
        }
    }

    // --- Metode lain seperti newGame(), isValidMove(), dll tetap sama ---
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

    public void startWinAnimation() {
        winAnimationTimer = new Timer(10, e -> {
            winAnimationProgress += 0.05f;
            if (winAnimationProgress >= 1.0f) {
                winAnimationProgress = 1.0f;
                winAnimationTimer.stop();
            }
            gameSurface.repaint();
        });
        winAnimationTimer.start();
    }

    private boolean hasWon(Seed p, int r, int c) {
        // Cek baris, kolom, dan diagonal
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
        for(Cell[] row : cells) for(Cell cell : row) if(cell.content == Seed.NO_SEED) return false;
        return true;
    }

    public void paint(Graphics2D g2d) {
        // ... kode paint() sama seperti sebelumnya ...
        for (int r = 0; r < ROWS; r++) for (int c = 0; c < COLS; c++) cells[r][c].paint(g2d);
        g2d.setColor(Theme.GRID);
        g2d.setStroke(new BasicStroke(4));
        for (int i = 1; i < ROWS; i++) g2d.drawLine(0, i * Cell.SIZE, CANVAS_WIDTH, i * Cell.SIZE);
        for (int i = 1; i < COLS; i++) g2d.drawLine(i * Cell.SIZE, 0, i * Cell.SIZE, CANVAS_HEIGHT);
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

    // --- LOGIKA AI BARU DIMULAI DI SINI ---
    public int[] findBestMove() {
        // 1. Menang: Jika AI bisa menang, ambil langkah itu.
        int[] move = findWinningMove(Seed.NOUGHT);
        if (move != null) return move;

        // 2. Blok: Jika pemain bisa menang, blokir.
        move = findWinningMove(Seed.CROSS);
        if (move != null) return move;

        // 3. Fork: Buat jebakan (2 cara untuk menang).
        move = findForkMove(Seed.NOUGHT);
        if (move != null) return move;

        // 4. Blok Fork Lawan:
        //    Cara paling efektif adalah dengan membuat 2-in-a-row untuk memaksa lawan bertahan,
        //    jika itu tidak memungkinkan, blokir fork secara langsung.
        move = findForkMove(Seed.CROSS);
        if (move != null) {
            // Coba blokir fork dengan cerdas
            // Ini adalah logika yang lebih rumit, untuk sekarang kita blokir saja
            return move;
        }

        // 5. Tengah: Ambil posisi tengah jika tersedia.
        if (isValidMove(1, 1)) {
            return new int[]{1, 1};
        }

        // 6. Sudut Berlawanan: Jika lawan di sudut, ambil sudut berlawanan.
        if (cells[0][0].content == Seed.CROSS && isValidMove(2, 2)) return new int[]{2, 2};
        if (cells[0][2].content == Seed.CROSS && isValidMove(2, 0)) return new int[]{2, 0};
        if (cells[2][0].content == Seed.CROSS && isValidMove(0, 2)) return new int[]{0, 2};
        if (cells[2][2].content == Seed.CROSS && isValidMove(0, 0)) return new int[]{0, 0};

        // 7. Sudut Kosong: Ambil posisi sudut mana pun yang kosong.
        List<int[]> corners = new ArrayList<>();
        if (isValidMove(0, 0)) corners.add(new int[]{0, 0});
        if (isValidMove(0, 2)) corners.add(new int[]{0, 2});
        if (isValidMove(2, 0)) corners.add(new int[]{2, 0});
        if (isValidMove(2, 2)) corners.add(new int[]{2, 2});
        if (!corners.isEmpty()) {
            Collections.shuffle(corners);
            return corners.get(0);
        }

        // 8. Sisi Kosong: Ambil posisi sisi mana pun yang kosong.
        List<int[]> sides = new ArrayList<>();
        if (isValidMove(0, 1)) sides.add(new int[]{0, 1});
        if (isValidMove(1, 0)) sides.add(new int[]{1, 0});
        if (isValidMove(1, 2)) sides.add(new int[]{1, 2});
        if (isValidMove(2, 1)) sides.add(new int[]{2, 1});
        if (!sides.isEmpty()) {
            Collections.shuffle(sides);
            return sides.get(0);
        }

        return null; // Seharusnya tidak pernah terjadi
    }

    // Helper untuk mencari langkah kemenangan untuk pemain tertentu
    private int[] findWinningMove(Seed player) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (isValidMove(r, c)) {
                    placeSeed(player, r, c);
                    boolean won = hasWon(player, r, c);
                    cells[r][c].newGame(); // Batalkan langkah
                    winningLineCoords = null; // Hapus jejak pengecekan
                    if (won) {
                        return new int[]{r, c};
                    }
                }
            }
        }
        return null;
    }

    // Helper untuk mencari langkah fork
    private int[] findForkMove(Seed player) {
        List<int[]> emptyCells = new ArrayList<>();
        for(int r = 0; r < ROWS; r++) for(int c = 0; c < COLS; c++) if(isValidMove(r,c)) emptyCells.add(new int[]{r,c});

        for(int[] move : emptyCells) {
            placeSeed(player, move[0], move[1]);
            int winningOpportunities = 0;
            // Cek semua kemungkinan langkah berikutnya untuk melihat apakah menciptakan kemenangan
            for(int[] nextMove : emptyCells) {
                if(move[0] == nextMove[0] && move[1] == nextMove[1]) continue;
                placeSeed(player, nextMove[0], nextMove[1]);
                if(hasWon(player, nextMove[0], nextMove[1])) {
                    winningOpportunities++;
                }
                cells[nextMove[0]][nextMove[1]].newGame();
                winningLineCoords = null;
            }
            cells[move[0]][move[1]].newGame();
            if(winningOpportunities >= 2) {
                return move;
            }
        }
        return null;
    }
}