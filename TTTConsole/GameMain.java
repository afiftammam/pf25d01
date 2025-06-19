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

    private String nameX = "Player X";
    private String nameO = "Player O";

    private Point mousePos;

    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private final AIPlayer aiPlayer;
    private final DatabaseManager dbManager;

    // --- TOMBOL BARU DIDEKLARASIKAN DI SINI ---
    private JButton playAgainButton;

    public GameMain(JPanel mainPanel, CardLayout cardLayout, DatabaseManager dbManager) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.dbManager = dbManager;
        this.board = new Board(this);
        this.aiPlayer = new AIPlayer();

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 70));
        setBackground(Theme.BG_MAIN);

        JPanel gameBoardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Theme.BG_MAIN);
                board.paint((Graphics2D) g);
                paintHoverEffect((Graphics2D) g);
            }
        };
        gameBoardPanel.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT));
        add(gameBoardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(Theme.BG_PANEL);
        backButton.setForeground(Theme.TEXT_LIGHT);
        backButton.addActionListener(e -> handleBackButton());
        bottomPanel.add(backButton, BorderLayout.WEST);

        // --- INISIALISASI DAN STYLE TOMBOL PLAY AGAIN ---
        playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        playAgainButton.setBackground(Theme.WIN_LINE); // Warna yang menarik perhatian
        playAgainButton.setForeground(Theme.BG_MAIN);
        playAgainButton.setVisible(false); // Sembunyikan di awal
        playAgainButton.addActionListener(e -> resetGame());
        bottomPanel.add(playAgainButton, BorderLayout.EAST); // Tambahkan ke sisi kanan

        JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_STATUS);
        statusLabel.setForeground(Theme.TEXT_LIGHT);
        bottomPanel.add(statusLabel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        SoundEffect.initGame();
        addMouseListeners(gameBoardPanel);
    }

    public void setDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }

    public void startNewGame(GameMode mode) {
        this.gameMode = mode;
        resetGame();
    }

    private void resetGame() {
        board.newGame();
        playAgainButton.setVisible(false); // Sembunyikan tombol saat game baru dimulai
        currentState = State.PLAYING;
        currentPlayer = Seed.CROSS;

        nameX = JOptionPane.showInputDialog(this, "Enter name for Player X:", "Player X");
        if (nameX == null || nameX.trim().isEmpty()) nameX = "Player X";

        if (gameMode == GameMode.PLAYER_VS_PLAYER) {
            nameO = JOptionPane.showInputDialog(this, "Enter name for Player O:", "Player O");
            if (nameO == null || nameO.trim().isEmpty()) nameO = "Player O";
        } else {
            nameO = "Skynet AI (" + currentDifficulty.name() + ")";
        }
        repaint();
    }

    private void addMouseListeners(JPanel targetPanel) {
        targetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Hapus logika lama untuk restart game dari sini.
                // Klik di papan hanya berfungsi saat permainan sedang berlangsung.
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
            // --- MUNCULKAN TOMBOL PLAY AGAIN SAAT GAME SELESAI ---
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

    private void handleBackButton() {
        if (currentState == State.PLAYING) {
            int response = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to quit? The current game will be lost.", "Quit Game",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.NO_OPTION) return;
        }
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
        JPanel bottomPanel = (JPanel) getComponent(1);
        JLabel statusLabel = (JLabel) bottomPanel.getComponent(2); // Indeks label sekarang 2
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
        statusLabel.setText(status);
    }
}