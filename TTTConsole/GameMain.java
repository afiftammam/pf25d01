package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GameMain extends JPanel {

    public enum GameMode { PLAYER_VS_PLAYER, PLAYER_VS_AI }

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private GameMode gameMode;

    private int scoreX = 0;
    private int scoreO = 0;
    private String nameX = "Player X";
    private String nameO = "Player O";

    private Point mousePos; // Untuk efek hover

    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    public GameMain(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.board = new Board(this); // Beri referensi panel ini ke board

        setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 70));
        setBackground(Theme.BG_MAIN);
        setLayout(new BorderLayout());

        addMouseListeners();
    }

    public void startNewGame(GameMode mode) {
        this.gameMode = mode;
        this.scoreX = 0;
        this.scoreO = 0;
        resetGame();
    }

    private void resetGame() {
        board.newGame();
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
        nameX = JOptionPane.showInputDialog(this, "Enter name for Player X:", "Player X");
        if (nameX == null || nameX.trim().isEmpty()) nameX = "Player X";

        if (gameMode == GameMode.PLAYER_VS_PLAYER) {
            nameO = JOptionPane.showInputDialog(this, "Enter name for Player O:", "Player O");
            if (nameO == null || nameO.trim().isEmpty()) nameO = "Player O";
        } else {
            nameO = "Skynet AI";
        }
        repaint();
    }

    private void addMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getY() > Board.CANVAS_HEIGHT) return; // Abaikan klik di status bar

                if (currentState == State.PLAYING) {
                    if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Seed.NOUGHT) return;

                    int row = e.getY() / Cell.SIZE;
                    int col = e.getX() / Cell.SIZE;

                    if (board.isValidMove(row, col)) {
                        updateGame(currentPlayer, row, col);
                    }
                } else {
                    // Jika game selesai, klik di mana saja akan memulai ronde baru
                    resetGame();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
                repaint(); // Perlu repaint untuk menggambar efek hover
            }
        });
    }

    private void updateGame(Seed player, int row, int col) {
        board.placeSeed(player, row, col);
        currentState = board.checkGameState(player, row, col);

        if (currentState == State.PLAYING) {
            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
            if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Seed.NOUGHT) {
                triggerAIMove();
            }
        } else {
            // Update skor jika ada pemenang
            if(currentState == State.CROSS_WON) scoreX++;
            if(currentState == State.NOUGHT_WON) scoreO++;
            board.startWinAnimation(); // Mulai animasi garis kemenangan
        }
        repaint();
    }

    private void triggerAIMove() {
        // Timer untuk AI "berpikir"
        Timer timer = new Timer(500, e -> {
            int[] move = board.findBestMove();
            if (move != null) {
                updateGame(Seed.NOUGHT, move[0], move[1]);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        board.paint(g2d);
        paintStatusBar(g2d);
        paintHoverEffect(g2d);
    }

    private void paintHoverEffect(Graphics2D g2d) {
        if (mousePos != null && currentState == State.PLAYING) {
            if (mousePos.y > Board.CANVAS_HEIGHT) return;
            int row = mousePos.y / Cell.SIZE;
            int col = mousePos.x / Cell.SIZE;
            if (board.isValidMove(row, col)) {
                g2d.setColor(Theme.HOVER);
                g2d.fillRect(col * Cell.SIZE, row * Cell.SIZE, Cell.SIZE, Cell.SIZE);
            }
        }
    }

    private void paintStatusBar(Graphics2D g2d) {
        g2d.setFont(Theme.FONT_STATUS);
        g2d.setColor(Theme.TEXT_LIGHT);

        String status;
        if (currentState == State.PLAYING) {
            status = (currentPlayer == Seed.CROSS ? nameX : nameO) + "'s Turn";
        } else if (currentState == State.CROSS_WON) {
            status = nameX + " Wins!";
        } else if (currentState == State.NOUGHT_WON) {
            status = nameO + " Wins!";
        } else {
            status = "It's a Draw!";
        }

        FontMetrics fm = g2d.getFontMetrics();
        int statusWidth = fm.stringWidth(status);
        g2d.drawString(status, (Board.CANVAS_WIDTH - statusWidth) / 2, Board.CANVAS_HEIGHT + 45);

        // Gambar skor
        g2d.drawString(nameX + ": " + scoreX, 20, Board.CANVAS_HEIGHT + 45);
        int nameOWidth = fm.stringWidth(nameO + ": " + scoreO);
        g2d.drawString(nameO + ": " + scoreO, Board.CANVAS_WIDTH - nameOWidth - 20, Board.CANVAS_HEIGHT + 45);
    }
}