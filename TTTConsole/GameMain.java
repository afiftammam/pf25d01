package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GameMain extends JPanel {

    public enum GameMode { PLAYER_VS_PLAYER, PLAYER_VS_AI }
    public enum Difficulty { EASY, MEDIUM, HARD }
    public enum GameVariant { STANDARD, MISERE }

    private Board board;
    private State currentState;
    private Seed currentPlayer;
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

    private JButton playAgainButton;
    private JPanel gameBoardPanel;

    public GameMain(JPanel mainPanel, CardLayout cardLayout, DatabaseManager dbManager) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.dbManager = dbManager;
        this.aiPlayer = new AIPlayer();

        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);

        gameBoardPanel = new JPanel();
        add(gameBoardPanel, BorderLayout.CENTER);

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

    public void startNewGame(GameMode mode, int size, GameVariant variant) {
        this.gameMode = mode;
        this.boardSize = size;
        this.currentGameVariant = variant;
        this.isFirstGame = true;

        this.board = new Board(this, size);
        setPreferredSize(new Dimension(board.CANVAS_WIDTH, board.CANVAS_HEIGHT + 70));

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

        addMouseListeners(gameBoardPanel);

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

        if (isFirstGame) {
            String inputX = JOptionPane.showInputDialog(this, "Enter name for Player X:", "Player X");

            if (inputX == null) {
                cardLayout.show(mainPanel, "MENU");
                return;
            }

            nameX = (inputX.trim().isEmpty()) ? "Player X" : inputX;

            if (gameMode == GameMode.PLAYER_VS_PLAYER) {
                String inputO = JOptionPane.showInputDialog(this, "Enter name for Player O:", "Player O");

                if (inputO == null) {
                    cardLayout.show(mainPanel, "MENU");
                    return;
                }

                nameO = (inputO.trim().isEmpty()) ? "Player O" : inputO;
            }
        }

        if (gameMode == GameMode.PLAYER_VS_AI) {
            nameO = "System AI (" + currentDifficulty.name() + ")";
        }

        isFirstGame = false;
        repaint();
    }

    private void addMouseListeners(JPanel targetPanel) {
        targetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse diklik pada papan di (" + e.getX() + ", " + e.getY() + "). Status game saat ini: " + currentState);

                if (currentState == State.PLAYING) {
                    System.out.println("Memproses langkah permainan...");
                    if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Seed.NOUGHT) {
                        System.out.println("Klik diabaikan, ini giliran AI.");
                        return;
                    }
                    int row = e.getY() / Board.CELL_SIZE;
                    int col = e.getX() / Board.CELL_SIZE;
                    if (board.isValidMove(row, col)) {
                        System.out.println("Langkah valid di (" + row + ", " + col + "). Memperbarui game.");
                        updateGame(currentPlayer, row, col);
                    } else {
                        System.out.println("Langkah tidak valid.");
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

        if (currentGameVariant == GameVariant.MISERE && (currentState == State.CROSS_WON || currentState == State.NOUGHT_WON)) {
            currentState = (currentState == State.CROSS_WON) ? State.NOUGHT_WON : State.CROSS_WON;
        }

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
            int[] move = aiPlayer.findBestMove(this.board, this.currentDifficulty, this.currentGameVariant);
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
        nameX = "Player X";
        nameO = "Player O";
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
                g2d.setColor(Theme.HOVER);
                g2d.fillRect(col * Board.CELL_SIZE, row * Board.CELL_SIZE, Board.CELL_SIZE, Board.CELL_SIZE);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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