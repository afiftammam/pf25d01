package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GameMain extends JPanel {

    public enum GameMode { PLAYER_VS_PLAYER, PLAYER_VS_AI }
    public enum Difficulty { EASY, MEDIUM, HARD }

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private GameMode gameMode;
    private Difficulty currentDifficulty = Difficulty.HARD;
    private int boardSize = 3; // Ukuran default

    private String nameX = "Player X";
    private String nameO = "Player O";

    private Point mousePos;

    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private final AIPlayer aiPlayer;
    private final DatabaseManager dbManager;

    private JButton playAgainButton;
    private JPanel gameBoardPanel; // Deklarasikan di sini agar bisa diakses

    public GameMain(JPanel mainPanel, CardLayout cardLayout, DatabaseManager dbManager) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.dbManager = dbManager;
        this.aiPlayer = new AIPlayer();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);

        // Panel papan permainan akan diinisialisasi ulang setiap game baru
        gameBoardPanel = new JPanel();
        add(gameBoardPanel, BorderLayout.CENTER);

        // Panel bawah dengan tombol-tombol
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        SoundEffect.initGame();
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(Theme.BG_PANEL);
        backButton.setForeground(Theme.TEXT_LIGHT);
        backButton.addActionListener(e -> handleBackButton());
        bottomPanel.add(backButton, BorderLayout.WEST);

        playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        playAgainButton.setBackground(Theme.WIN_LINE);
        playAgainButton.setForeground(Theme.BG_MAIN);
        playAgainButton.setVisible(false);
        playAgainButton.addActionListener(e -> resetGame());
        bottomPanel.add(playAgainButton, BorderLayout.EAST);

        JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_STATUS);
        statusLabel.setForeground(Theme.TEXT_LIGHT);
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        return bottomPanel;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }

    // --- PERUBAHAN: Menerima ukuran papan sebagai parameter ---
    public void startNewGame(GameMode mode, int size) {
        this.gameMode = mode;
        this.boardSize = size;

        // Buat board baru dengan ukuran yang dipilih
        this.board = new Board(this, boardSize);

        // Atur ulang ukuran panel utama
        setPreferredSize(new Dimension(board.CANVAS_WIDTH, board.CANVAS_HEIGHT + 70));

        // Hapus panel game lama dan buat yang baru
        remove(gameBoardPanel);
        gameBoardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Theme.BG_MAIN);
                board.paint((Graphics2D) g);
                paintHoverEffect((Graphics2D) g);
            }
        };
        gameBoardPanel.setPreferredSize(new Dimension(board.CANVAS_WIDTH, board.CANVAS_HEIGHT));
        add(gameBoardPanel, BorderLayout.CENTER);

        // Tambahkan listener lagi ke panel baru
        addMouseListeners(gameBoardPanel);

        // Revalidate dan repaint frame
        revalidate();
        repaint();
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.pack();
            topFrame.setLocationRelativeTo(null);
        }

        resetGame();
    }

    private void resetGame() {
        board.newGame();
        playAgainButton.setVisible(false);
        currentState = State.PLAYING;
        currentPlayer = Seed.CROSS;

        // Hanya minta nama jika game pertama kali dimulai (atau reset total)
        // Untuk "Play Again", kita gunakan nama yang sama
        if (nameX.equals("Player X")) {
            nameX = JOptionPane.showInputDialog(this, "Enter name for Player X:", "Player X");
            if (nameX == null || nameX.trim().isEmpty()) nameX = "Player X";

            if (gameMode == GameMode.PLAYER_VS_PLAYER) {
                nameO = JOptionPane.showInputDialog(this, "Enter name for Player O:", "Player O");
                if (nameO == null || nameO.trim().isEmpty()) nameO = "Player O";
            }
        }

        if (gameMode == GameMode.PLAYER_VS_AI) {
            nameO = "Skynet AI (" + currentDifficulty.name() + ")";
        }

        repaint();
    }

    private void addMouseListeners(JPanel targetPanel) {
        targetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentState == State.PLAYING) {
                    if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Seed.NOUGHT) return;
                    int row = e.getY() / Cell.SIZE;
                    int col = e.getX() / Cell.SIZE;
                    if (board.isValidMove(row, col)) {
                        updateGame(currentPlayer, row, col);
                    }
                }
            }
        });

        targetPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
                repaint();
            }
        });
    }

    private void updateGame(Seed player, int row, int col) {
        board.placeSeed(player, row, col);
        SoundEffect.EAT_FOOD.play();
        currentState = board.checkGameState(player, row, col);
        if (currentState == State.PLAYING) {
            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
            if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Seed.NOUGHT) {
                triggerAIMove();
            }
        } else {
            playAgainButton.setVisible(true);
            if (currentState == State.CROSS_WON || currentState == State.NOUGHT_WON) SoundEffect.EXPLODE.play();
            else if (currentState == State.DRAW) SoundEffect.DIE.play();
            if (dbManager != null) handleDatabaseUpdate();
            board.startWinAnimation();
        }
        repaint();
    }

    private void triggerAIMove() {
        Timer timer = new Timer(500, e -> {
            int[] move = aiPlayer.findBestMove(this.board, this.currentDifficulty);
            if (move != null && move[0] != -1) {
                updateGame(Seed.NOUGHT, move[0], move[1]);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // ... sisa metode (handleBackButton, handleDatabaseUpdate, paintHoverEffect, paintComponent) tidak berubah signifikan ...
    private void handleBackButton() {
        if (currentState == State.PLAYING) {
            int response = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to quit? The current game will be lost.", "Quit Game",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.NO_OPTION) return;
        }
        // Reset nama saat kembali ke menu
        nameX = "Player X";
        nameO = "Player O";
        cardLayout.show(mainPanel, "MENU");
    }

    private void handleDatabaseUpdate() {
        if (currentState == State.CROSS_WON) {
            dbManager.updatePlayerStats(nameX, State.CROSS_WON);
            dbManager.updatePlayerStats(nameO, State.PLAYING);
        } else if (currentState == State.NOUGHT_WON) {
            dbManager.updatePlayerStats(nameO, State.NOUGHT_WON);
            dbManager.updatePlayerStats(nameX, State.PLAYING);
        } else if (currentState == State.DRAW) {
            dbManager.updatePlayerStats(nameX, State.DRAW);
            dbManager.updatePlayerStats(nameO, State.DRAW);
        }
    }

    private void paintHoverEffect(Graphics2D g2d) {
        if (mousePos != null && currentState == State.PLAYING) {
            int row = mousePos.y / Cell.SIZE;
            int col = mousePos.x / Cell.SIZE;
            if (board.isValidMove(row, col)) {
                g2d.setColor(Theme.HOVER);
                g2d.fillRect(col * Cell.SIZE, row * Cell.SIZE, Cell.SIZE, Cell.SIZE);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Pastikan komponen panel bawah ada sebelum mengaksesnya
        if (getComponentCount() > 1 && getComponent(1) instanceof JPanel) {
            JPanel bottomPanel = (JPanel) getComponent(1);
            if (bottomPanel.getComponentCount() > 2 && bottomPanel.getComponent(2) instanceof JLabel) {
                JLabel statusLabel = (JLabel) bottomPanel.getComponent(2);
                String status;
                if (currentState == State.PLAYING) {
                    status = (currentPlayer == Seed.CROSS ? nameX : nameO) + "'s Turn";
                } else if (currentState == State.CROSS_WON) {
                    status = nameX + " Wins!";
                } else if (currentState == State.NOUGHT_WON) {
                    status = nameO + " Wins!";
                } else if (currentState == State.DRAW){
                    status = "It's a Draw!";
                } else {
                    status = " ";
                }
                statusLabel.setText(status);
            }
        }
    }
}