// afiftammam/pf25d01/pf25d01-3dfb4fb69b1a626b65b6f26d53b96147041a1c8f/TTTConsole/GameMain.java
package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameMain extends JPanel {

    public enum GameMode { PLAYER_VS_PLAYER, PLAYER_VS_AI, ONLINE_MULTIPLAYER }
    public enum Difficulty { EASY, MEDIUM, HARD }
    public enum GameVariant { STANDARD, MISERE }

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private GameMode gameMode;
    private Difficulty currentDifficulty = Difficulty.EASY;
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
    private JPanel gameBoardPanel;
    private final JPanel contentPanel;
    private final JLabel statusLabel;
    private final JLabel scoreLabel;
    private final Timer opponentMoveTimer;

    public GameMain(JPanel mainPanel, CardLayout cardLayout, DatabaseManager dbManager) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.dbManager = dbManager;
        this.aiPlayer = new AIPlayer();
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        statusLabel = new JLabel("Welcome!", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_STATUS.deriveFont(20f));
        statusLabel.setForeground(Theme.TEXT_LIGHT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        scoreLabel = new JLabel(" ", SwingConstants.CENTER);
        scoreLabel.setFont(Theme.FONT_STATUS.deriveFont(14f));
        scoreLabel.setForeground(Theme.ACCENT_COLOR);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(statusLabel);
        topPanel.add(scoreLabel);
        add(topPanel, BorderLayout.NORTH);

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        add(contentPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        opponentMoveTimer = new Timer(2000, e -> checkForOpponentMove());
        opponentMoveTimer.setRepeats(true);
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

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

        return bottomPanel;
    }

    private void updateStatusLabel() {
        String statusText;
        String scoreText = " ";

        if (currentState == State.PLAYING) {
            if (gameMode == GameMode.ONLINE_MULTIPLAYER) {
                statusText = (currentPlayer == mySeed) ? "Your Turn (" + myUsername + ")" : "Waiting for " + (mySeed == Seed.CROSS ? nameO : nameX) + "...";
            } else {
                statusText = (currentPlayer == Seed.CROSS ? nameX : nameO) + "'s Turn";
            }
        } else {
            if (currentState == State.CROSS_WON) {
                statusText = nameX + " Wins!";
            } else if (currentState == State.NOUGHT_WON) {
                statusText = nameO + " Wins!";
            } else if (currentState == State.DRAW){
                statusText = "It's a Draw!";
            } else {
                statusText = " ";
            }

            if (gameMode != GameMode.ONLINE_MULTIPLAYER) {
                scoreText = String.format("Score: %s [%d] - %s [%d] - Draws [%d]", nameX, winsX, nameO, winsO, draws);
            }
        }

        statusLabel.setText(statusText);
        scoreLabel.setText(scoreText);
    }

    public void setDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }

    public void startNewGame(GameMode mode, int size, GameVariant variant) {
        this.gameMode = mode;
        this.currentGameVariant = variant;
        this.isFirstGame = true;

        setupBoard(size);

        AudioManager.playSound("GAME_START");

        winsX = 0;
        winsO = 0;
        draws = 0;
        resetGame();
    }

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

    private void setupBoard(int size) {
        contentPanel.removeAll();

        gameBoardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (board != null) {
                    // --- [PERBAIKAN] ---
                    // Logika penggambaran yang lebih aman
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();

                    // Pastikan ROWS dan COLS tidak nol untuk menghindari error pembagian
                    if (board.COLS > 0 && board.ROWS > 0) {
                        int newCellSize = Math.min(panelWidth / board.COLS, panelHeight / board.ROWS);

                        // Selalu update ukuran sel. Ini memastikan ukurannya selalu sinkron
                        // dengan ukuran panel, bahkan saat panel baru muncul.
                        board.setCellSize(newCellSize);

                        // Hanya coba menggambar jika ada ruang (ukuran sel > 0)
                        if (newCellSize > 0) {
                            board.paint((Graphics2D) g);
                        }
                    }
                }
                paintHoverEffect((Graphics2D) g);
            }
        };

        this.board = new Board(gameBoardPanel, size);

        addMouseListeners(gameBoardPanel);

        // Gunakan GridBagConstraints agar panel papan bisa mengisi ruang yang tersedia
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        contentPanel.add(gameBoardPanel, gbc);

        contentPanel.revalidate();
        contentPanel.repaint();

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null && !topFrame.isUndecorated()) {
            topFrame.pack();
            topFrame.setLocationRelativeTo(null);
        }
    }

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

                    if (isMyTurn && board != null && board.CELL_SIZE > 0) {
                        int totalWidth = board.COLS * board.CELL_SIZE;
                        int totalHeight = board.ROWS * board.CELL_SIZE;
                        int startX = (gameBoardPanel.getWidth() - totalWidth) / 2;
                        int startY = (gameBoardPanel.getHeight() - totalHeight) / 2;

                        int mouseX = e.getX() - startX;
                        int mouseY = e.getY() - startY;

                        int row = mouseY / board.CELL_SIZE;
                        int col = mouseX / board.CELL_SIZE;

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

        if (mousePos != null && currentState == State.PLAYING && isMyTurn && board != null && board.CELL_SIZE > 0) {
            int totalWidth = board.COLS * board.CELL_SIZE;
            int totalHeight = board.ROWS * board.CELL_SIZE;
            int startX = (gameBoardPanel.getWidth() - totalWidth) / 2;
            int startY = (gameBoardPanel.getHeight() - totalHeight) / 2;

            int mouseX = mousePos.x - startX;
            int mouseY = mousePos.y - startY;

            int row = mouseY / board.CELL_SIZE;
            int col = mouseX / board.CELL_SIZE;

            if (board.isValidMove(row, col)) {
                g2d.setColor(Theme.HOVER);
                g2d.fillRect(startX + col * board.CELL_SIZE, startY + row * board.CELL_SIZE, board.CELL_SIZE, board.CELL_SIZE);
            }
        }
    }
}