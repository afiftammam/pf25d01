package TTTConsole;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

public class GameMain extends JPanel {

    public enum GameMode { PLAYER_VS_PLAYER, PLAYER_VS_AI }
    public enum Difficulty { EASY, MEDIUM, HARD }
    public enum GameVariant { STANDARD, MISERE }

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    // ... (fields lain tetap sama) ...
    private GameMode gameMode;
    private Difficulty currentDifficulty = Difficulty.HARD;
    private GameVariant currentGameVariant = GameVariant.STANDARD;
    private int boardSize = 3;
    private String nameX = "Player X";
    private String nameO = "Player O";
    private boolean isFirstGame = true;
    private Point mousePos;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private final AIPlayer aiPlayer;
    private final DatabaseManager dbManager;


    private JPanel gameBoardPanel;
    private JPanel playerXPanel;
    private JPanel playerOPanel;
    private JLabel statusLabel;

    // PERBAIKAN: Menggunakan nama variabel tema yang baru (camelCase)
    private final Border activePlayerBorder = BorderFactory.createLineBorder(Theme.accentColor, 3);
    private final Border inactivePlayerBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);

    public GameMain(JPanel mainPanel, CardLayout cardLayout, DatabaseManager dbManager) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.dbManager = dbManager;
        this.aiPlayer = new AIPlayer();
        initUI();
        SoundEffect.initGame();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.bgMain); // Perbaikan
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        playerXPanel = createPlayerPanel("X", Theme.CROSS_COLOR); // Perbaikan
        add(playerXPanel, BorderLayout.WEST);

        playerOPanel = createPlayerPanel("O", Theme.NOUGHT_COLOR); // Perbaikan
        add(playerOPanel, BorderLayout.EAST);

        gameBoardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (board != null) {
                    board.paint((Graphics2D) g);
                    paintHoverEffect((Graphics2D) g);
                }
            }
        };
        gameBoardPanel.setOpaque(false); // Buat transparan agar background utama terlihat
        add(gameBoardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0));
        bottomPanel.setOpaque(false);
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> handleBackButton());
        bottomPanel.add(backButton, BorderLayout.WEST);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_STATUS);
        statusLabel.setForeground(Theme.textLight); // Perbaikan
        bottomPanel.add(statusLabel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createPlayerPanel(String playerSymbol, Color symbolColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.bgPanel); // Perbaikan
        panel.setPreferredSize(new Dimension(150, 100));
        panel.setBorder(inactivePlayerBorder);

        JLabel nameLabel = new JLabel("Player " + playerSymbol, SwingConstants.CENTER);
        nameLabel.setFont(Theme.FONT_BUTTON);
        nameLabel.setForeground(Theme.textLight); // Perbaikan
        panel.add(nameLabel, BorderLayout.NORTH);

        JLabel symbolLabel = new JLabel(playerSymbol, SwingConstants.CENTER);
        symbolLabel.setFont(new Font("Segoe UI", Font.BOLD, 100));
        symbolLabel.setForeground(symbolColor);
        panel.add(symbolLabel, BorderLayout.CENTER);

        return panel;
    }

    // Sisa metode (startNewGame, resetGame, updateTurnIndicator, dll) tidak ada perubahan logika,
    // hanya memastikan semua penggunaan variabel Theme sudah benar. Kode lengkap tetap disertakan.

    public void startNewGame(GameMode mode, int size, GameVariant variant) {
        this.gameMode = mode;
        this.boardSize = size;
        this.currentGameVariant = variant;
        this.isFirstGame = true;

        this.board = new Board(this, size);
        int boardDim = Board.CELL_SIZE * size;
        gameBoardPanel.setPreferredSize(new Dimension(boardDim, boardDim));

        addMouseListeners(gameBoardPanel);

        resetGame();

        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.pack();
            topFrame.setLocationRelativeTo(null);
        }
        revalidate();
        repaint();
    }

    private void resetGame() {
        board.newGame();

        if (isFirstGame) {
            String inputX = JOptionPane.showInputDialog(this, "Enter name for Player X:", "Player X");
            if (inputX == null) { cardLayout.show(mainPanel, "MENU"); return; }
            nameX = (inputX.trim().isEmpty()) ? "Player X" : inputX;

            if (gameMode == GameMode.PLAYER_VS_PLAYER) {
                String inputO = JOptionPane.showInputDialog(this, "Enter name for Player O:", "Player O");
                if (inputO == null) { cardLayout.show(mainPanel, "MENU"); return; }
                nameO = (inputO.trim().isEmpty()) ? "Player O" : inputO;
            }
        }

        if (gameMode == GameMode.PLAYER_VS_AI) {
            nameO = "AI (" + currentDifficulty.name() + ")";
        }

        ((JLabel) ((BorderLayout)playerXPanel.getLayout()).getLayoutComponent(BorderLayout.NORTH)).setText(nameX);
        ((JLabel) ((BorderLayout)playerOPanel.getLayout()).getLayoutComponent(BorderLayout.NORTH)).setText(nameO);

        isFirstGame = false;
        currentState = State.PLAYING;
        currentPlayer = Seed.CROSS;
        updateTurnIndicator();
    }

    private void updateTurnIndicator() {
        if (currentState == State.PLAYING) {
            statusLabel.setText((currentPlayer == Seed.CROSS ? nameX : nameO) + "'s Turn");
            playerXPanel.setBorder(currentPlayer == Seed.CROSS ? activePlayerBorder : inactivePlayerBorder);
            playerOPanel.setBorder(currentPlayer == Seed.NOUGHT ? activePlayerBorder : inactivePlayerBorder);
        } else {
            playerXPanel.setBorder(inactivePlayerBorder);
            playerOPanel.setBorder(inactivePlayerBorder);
            if (currentState == State.CROSS_WON) statusLabel.setText(nameX + " Wins!");
            else if (currentState == State.NOUGHT_WON) statusLabel.setText(nameO + " Wins!");
            else if (currentState == State.DRAW) statusLabel.setText("It's a Draw!");
        }
    }

    private void addMouseListeners(JPanel targetPanel) {
        for(MouseListener ml : targetPanel.getMouseListeners()) { targetPanel.removeMouseListener(ml); }
        for(MouseMotionListener mml : targetPanel.getMouseMotionListeners()) { targetPanel.removeMouseMotionListener(mml); }

        targetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentState == State.PLAYING) {
                    if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Seed.NOUGHT) return;
                    int row = e.getY() / Board.CELL_SIZE;
                    int col = e.getX() / Board.CELL_SIZE;
                    if (board.isValidMove(row, col)) {
                        updateGame(currentPlayer, row, col);
                    }
                } else {
                    resetGame();
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

        if (currentGameVariant == GameVariant.MISERE && (currentState == State.CROSS_WON || currentState == State.NOUGHT_WON)) {
            currentState = (currentState == State.CROSS_WON) ? State.NOUGHT_WON : State.CROSS_WON;
        }

        if (currentState == State.PLAYING) {
            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
            if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Seed.NOUGHT) {
                triggerAIMove();
            }
        } else {
            if (currentState == State.CROSS_WON || currentState == State.NOUGHT_WON) SoundEffect.EXPLODE.play();
            else if (currentState == State.DRAW) SoundEffect.DIE.play();
            if (dbManager != null) handleDatabaseUpdate();
            board.startWinAnimation();
        }
        updateTurnIndicator();
        repaint();
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

    public void setDifficulty(Difficulty difficulty) { this.currentDifficulty = difficulty; }

    private void handleBackButton() {
        if (currentState == State.PLAYING) {
            int response = JOptionPane.showConfirmDialog( this, "Are you sure you want to quit? The current game will be lost.", "Quit Game", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.NO_OPTION) return;
        }
        isFirstGame = true;
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
            int row = mousePos.y / Board.CELL_SIZE;
            int col = mousePos.x / Board.CELL_SIZE;
            if (board.isValidMove(row, col)) {
                g2d.setColor(Theme.HOVER_COLOR); // Perbaikan
                g2d.fillRect(col * Board.CELL_SIZE, row * Board.CELL_SIZE, Board.CELL_SIZE, Board.CELL_SIZE);
            }
        }
    }
}