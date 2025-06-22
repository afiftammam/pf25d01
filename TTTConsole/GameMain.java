package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GameMain extends JPanel {

    public enum GameMode { PLAYER_VS_PLAYER, PLAYER_VS_AI, ONLINE_MULTIPLAYER }
    public enum Difficulty { EASY, MEDIUM, HARD }
    public enum GameVariant { STANDARD, MISERE }

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private GameMode gameMode;
    private Difficulty currentDifficulty = Difficulty.HARD;
    private GameVariant currentGameVariant = GameVariant.STANDARD;

    private String nameX = "Player X";
    private String nameO = "Player O";
    private String myUsername;
    private Seed mySeed;
    private String gameId;
    private int moveCount = 0;

    private int winsX = 0;
    private int winsO = 0;
    private int draws = 0;

    private boolean isFirstGame = true;
    private Point mousePos;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private final AIPlayer aiPlayer;
    private final DatabaseManager dbManager;

    private JButton playAgainButton;
    private JPanel gameBoardPanel; // Panel untuk menggambar papan permainan
    // PERBAIKAN: Panel ini bertindak sebagai wadah tetap untuk papan permainan.
    // Menggunakan GridBagLayout memastikan komponen di dalamnya (papan permainan) akan selalu terpusat.
    private final JPanel contentPanel;
    private JLabel statusLabel;
    private final Timer opponentMoveTimer;

    public GameMain(JPanel mainPanel, CardLayout cardLayout, DatabaseManager dbManager) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.dbManager = dbManager;
        this.aiPlayer = new AIPlayer();
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);

        // PERBAIKAN: Inisialisasi contentPanel di konstruktor.
        // Panel ini akan selalu ada di posisi TENGAH (CENTER).
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false); // Buat transparan agar background GameMain terlihat
        add(contentPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Timer untuk mengecek gerakan lawan setiap 2 detik
        opponentMoveTimer = new Timer(2000, e -> checkForOpponentMove());
        opponentMoveTimer.setRepeats(true);
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
        playAgainButton.addActionListener(e -> {
            if (gameMode == GameMode.ONLINE_MULTIPLAYER) {
                JOptionPane.showMessageDialog(this, "Play Again is not supported for online mode.\nPlease start a new game from the menu.");
            } else {
                resetGame();
            }
        });
        bottomPanel.add(playAgainButton, BorderLayout.EAST);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_STATUS);
        statusLabel.setForeground(Theme.TEXT_LIGHT);
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        return bottomPanel;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }

    // Metode untuk memulai game lokal (vs AI atau vs Player)
    public void startNewGame(GameMode mode, int size, GameVariant variant) {
        this.gameMode = mode;
        this.currentGameVariant = variant;
        this.isFirstGame = true;

        setupBoard(size);

        AudioManager.playSound("GAME_START");

        // Reset skor sesi untuk permainan baru
        winsX = 0;
        winsO = 0;
        draws = 0;
        resetGame();
    }

    // Metode untuk memulai game online
    public void startNewGame(GameMode mode, int size, GameVariant variant, String gameId, Seed mySeed, String myUsername, String opponentUsername) {
        this.gameMode = mode;
        this.currentGameVariant = variant;
        this.gameId = gameId;
        this.mySeed = mySeed;
        this.myUsername = myUsername;

        if (mySeed == Seed.CROSS) {
            this.nameX = myUsername;
            this.nameO = opponentUsername;
        } else {
            this.nameX = opponentUsername;
            this.nameO = myUsername;
        }

        setupBoard(size);
        board.newGame();

        currentState = State.PLAYING;
        currentPlayer = Seed.CROSS;
        moveCount = 0;
        updateStatusLabel();

        if (opponentMoveTimer.isRunning()) {
            opponentMoveTimer.stop();
        }
        opponentMoveTimer.start();

        repaint();
    }

    /**
     * PERBAIKAN UTAMA: Metode ini sekarang bertanggung jawab untuk membersihkan
     * dan menyiapkan papan permainan baru dengan benar.
     */
    private void setupBoard(int size) {
        // 1. Hapus SEMUA komponen dari wadah (menghilangkan papan lama)
        contentPanel.removeAll();

        // 2. Buat panel baru untuk papan permainan
        gameBoardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (board != null) {
                    board.paint((Graphics2D) g);
                }
                paintHoverEffect((Graphics2D) g);
            }
        };

        // 3. Buat objek Board baru dengan ukuran yang benar
        this.board = new Board(gameBoardPanel, size);

        // 4. Atur ukuran panel agar sesuai dengan ukuran papan baru
        setPreferredSize(new Dimension(board.CANVAS_WIDTH, board.CANVAS_HEIGHT + 80));
        gameBoardPanel.setPreferredSize(new Dimension(board.CANVAS_WIDTH, board.CANVAS_HEIGHT));

        // 5. Tambahkan listener mouse ke panel papan permainan yang baru
        addMouseListeners(gameBoardPanel);

        // 6. Tambahkan papan permainan baru ke dalam wadah yang sudah kosong
        contentPanel.add(gameBoardPanel, new GridBagConstraints());

        // 7. Validasi ulang UI untuk menggambar perubahan
        contentPanel.revalidate();
        contentPanel.repaint();

        // 8. Sesuaikan ukuran window utama agar pas dengan papan baru
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.pack();
            topFrame.setLocationRelativeTo(null);
        }
    }

    // Metode reset game untuk memulai ronde baru (tanpa mengubah nama/skor)
    private void resetGame() {
        board.newGame();
        playAgainButton.setVisible(false);
        currentState = State.PLAYING;
        currentPlayer = Seed.CROSS;
        updateStatusLabel();

        if (isFirstGame) {
            if (gameMode != GameMode.ONLINE_MULTIPLAYER) {
                String inputX = JOptionPane.showInputDialog(this, "Enter name for Player X:", "Player X");
                if (inputX == null) { cardLayout.show(mainPanel, "MENU"); return; }
                nameX = (inputX.trim().isEmpty()) ? "Player X" : inputX;

                if (gameMode == GameMode.PLAYER_VS_PLAYER) {
                    String inputO = JOptionPane.showInputDialog(this, "Enter name for Player O:", "Player O");
                    if (inputO == null) { cardLayout.show(mainPanel, "MENU"); return; }
                    nameO = (inputO.trim().isEmpty()) ? "Player O" : inputO;
                }
            }
        }

        if (gameMode == GameMode.PLAYER_VS_AI) {
            nameO = "System AI (" + currentDifficulty.name() + ")";
        }

        isFirstGame = false;
        repaint();
    }

    private void checkForOpponentMove() {
        if (gameMode != GameMode.ONLINE_MULTIPLAYER || currentState != State.PLAYING || currentPlayer == mySeed) {
            return;
        }

        int[] move = dbManager.getLatestMove(gameId, moveCount);
        if (move != null) {
            opponentMoveTimer.stop();
            updateGame(currentPlayer, move[0], move[1]);
        }
    }

    private void addMouseListeners(JPanel targetPanel) {
        targetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentState == State.PLAYING) {
                    boolean isMyTurn = true;
                    if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Seed.NOUGHT) {
                        isMyTurn = false;
                    }
                    if (gameMode == GameMode.ONLINE_MULTIPLAYER && currentPlayer != mySeed) {
                        isMyTurn = false;
                    }

                    if (isMyTurn) {
                        int row = e.getY() / Board.CELL_SIZE;
                        int col = e.getX() / Board.CELL_SIZE;
                        if (board.isValidMove(row, col)) {
                            updateGame(currentPlayer, row, col);
                        }
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
        if (gameMode == GameMode.ONLINE_MULTIPLAYER) {
            moveCount++;
            if (player == mySeed) {
                dbManager.recordMove(gameId, moveCount, player, row, col);
            }
        }

        board.placeSeed(player, row, col);
        if (player == Seed.CROSS) AudioManager.playSound("CROSS_MOVE");
        else if (player == Seed.NOUGHT) AudioManager.playSound("NOUGHT_MOVE");

        currentState = board.checkGameState(player, row, col);

        if (currentGameVariant == GameVariant.MISERE && (currentState == State.CROSS_WON || currentState == State.NOUGHT_WON)) {
            currentState = (currentState == State.CROSS_WON) ? State.NOUGHT_WON : State.CROSS_WON;
        }

        if (currentState == State.PLAYING) {
            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
            if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Seed.NOUGHT) {
                triggerAIMove();
            } else if (gameMode == GameMode.ONLINE_MULTIPLAYER && currentPlayer != mySeed) {
                opponentMoveTimer.restart();
            }
        } else {
            if(opponentMoveTimer.isRunning()) opponentMoveTimer.stop();
            playAgainButton.setVisible(gameMode != GameMode.ONLINE_MULTIPLAYER);
            handleEndGameSounds();
            if (dbManager != null) handleDatabaseUpdate();
            board.startWinAnimation();
            if (gameMode == GameMode.ONLINE_MULTIPLAYER) {
                String winner = (currentState == State.CROSS_WON) ? nameX : (currentState == State.NOUGHT_WON) ? nameO : "DRAW";
                dbManager.updateGameWinner(gameId, winner);
            }
        }
        updateStatusLabel();
        repaint();
    }

    private void handleEndGameSounds() {
        if (currentState == State.CROSS_WON) {
            if (gameMode == GameMode.ONLINE_MULTIPLAYER && mySeed == Seed.CROSS) AudioManager.playSound("WIN");
            else if (gameMode == GameMode.ONLINE_MULTIPLAYER && mySeed == Seed.NOUGHT) AudioManager.playSound("LOSE");
            else AudioManager.playSound("WIN");
        } else if (currentState == State.NOUGHT_WON) {
            if (gameMode == GameMode.PLAYER_VS_AI) AudioManager.playSound("LOSE");
            else if (gameMode == GameMode.ONLINE_MULTIPLAYER && mySeed == Seed.NOUGHT) AudioManager.playSound("WIN");
            else if (gameMode == GameMode.ONLINE_MULTIPLAYER && mySeed == Seed.CROSS) AudioManager.playSound("LOSE");
            else AudioManager.playSound("WIN");
        } else if (currentState == State.DRAW) {
            AudioManager.playSound("DRAW");
        }
    }

    private void triggerAIMove() {
        Timer timer = new Timer(500, e -> {
            int[] move = aiPlayer.findBestMove(this.board, this.currentDifficulty, this.currentGameVariant);
            if (move != null && move[0] != -1) {
                updateGame(Seed.NOUGHT, move[0], move[1]);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void handleDatabaseUpdate() {
        if (gameMode != GameMode.ONLINE_MULTIPLAYER) {
            if (currentState == State.CROSS_WON) { winsX++; }
            else if (currentState == State.NOUGHT_WON) { winsO++; }
            else if (currentState == State.DRAW) { draws++; }
        }

        if (currentState == State.CROSS_WON) {
            dbManager.updatePlayerStats(nameX, DatabaseManager.GameResult.WIN);
            dbManager.updatePlayerStats(nameO, DatabaseManager.GameResult.LOSS);
        } else if (currentState == State.NOUGHT_WON) {
            dbManager.updatePlayerStats(nameO, DatabaseManager.GameResult.WIN);
            dbManager.updatePlayerStats(nameX, DatabaseManager.GameResult.LOSS);
        } else if (currentState == State.DRAW) {
            dbManager.updatePlayerStats(nameX, DatabaseManager.GameResult.DRAW);
            dbManager.updatePlayerStats(nameO, DatabaseManager.GameResult.DRAW);
        }
    }

    private void handleBackButton() {
        if (opponentMoveTimer.isRunning()) {
            opponentMoveTimer.stop();
        }
        if (currentState == State.PLAYING) {
            int response = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to quit? The current game will be lost.", "Quit Game",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.NO_OPTION) {
                if(gameMode == GameMode.ONLINE_MULTIPLAYER) opponentMoveTimer.start();
                return;
            }
        }
        cardLayout.show(mainPanel, "MENU");
    }

    private void paintHoverEffect(Graphics2D g2d) {
        boolean isMyTurn = (gameMode != GameMode.ONLINE_MULTIPLAYER) || (currentPlayer == mySeed);

        if (mousePos != null && currentState == State.PLAYING && isMyTurn) {
            int row = mousePos.y / Board.CELL_SIZE;
            int col = mousePos.x / Board.CELL_SIZE;
            if (board.isValidMove(row, col)) {
                g2d.setColor(Theme.HOVER);
                g2d.fillRect(col * Board.CELL_SIZE, row * Board.CELL_SIZE, Board.CELL_SIZE, Board.CELL_SIZE);
            }
        }
    }

    private void updateStatusLabel() {
        String status;
        if (currentState == State.PLAYING) {
            if (gameMode == GameMode.ONLINE_MULTIPLAYER) {
                status = (currentPlayer == mySeed) ? "Giliran Anda (" + myUsername + ")" : "Menunggu " + (mySeed == Seed.CROSS ? nameO : nameX) + "...";
            } else {
                status = (currentPlayer == Seed.CROSS ? nameX : nameO) + "'s Turn";
            }
        } else {
            String endMessage;
            if (currentState == State.CROSS_WON) {
                endMessage = nameX + " Wins!";
            } else if (currentState == State.NOUGHT_WON) {
                endMessage = nameO + " Wins!";
            } else if (currentState == State.DRAW){
                endMessage = "It's a Draw!";
            } else {
                endMessage = " ";
            }

            if(gameMode != GameMode.ONLINE_MULTIPLAYER) {
                String score = String.format(" | Score: %s - %d, %s - %d, Draws - %d", nameX, winsX, nameO, winsO, draws);
                status = endMessage + score;
            } else {
                status = endMessage;
            }
        }
        statusLabel.setText(status);
    }
}