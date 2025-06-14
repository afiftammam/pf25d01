package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    // Enum untuk mode permainan
    private enum GameMode {
        PLAYER_VS_PLAYER,
        PLAYER_VS_AI
    }

    // --- BLOK KONSTANTA YANG DIPERBAIKI ---
    // Konstanta untuk tampilan yang sebelumnya hilang, sekarang ditambahkan kembali
    public static final String TITLE = "Tic Tac Toe Sederhana";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);
    public static final Color COLOR_WINNING_LINE = new Color(255, 215, 0); // Emas

    // Objek Game
    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private GameMode gameMode;
    private JLabel statusBar;
    private Timer aiTimer; // Timer untuk menunda langkah AI

    private int scoreX;
    private int scoreO;
    private String namaPemainX;
    private String namaPemainO;

    public GameMain() {
        // Listener mouse untuk menangani input pemain
        super.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // Abaikan klik jika game sudah selesai
                if (currentState != State.PLAYING) return;

                // Abaikan klik jika giliran AI
                if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Seed.NOUGHT) {
                    return;
                }

                Point point = e.getPoint();
                int row = point.y / Cell.SIZE;
                int col = point.x / Cell.SIZE;

                if (board.isValidMove(row, col)) {
                    SoundEffect.EAT_FOOD.play();
                    updateGame(currentPlayer, row, col);
                }
            }
        });

        // Pengaturan komponen UI lainnya
        setupUI();

        // Inisialisasi dan mulai permainan
        inisialisasiGameAwal();
    }

    private void setupUI() {
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        JButton tombolMulaiBaru = new JButton("Mulai Baru");
        tombolMulaiBaru.setFont(FONT_STATUS);
        tombolMulaiBaru.addActionListener(e -> mulaiPermainanBaru());

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.add(statusBar, BorderLayout.CENTER);
        panelBawah.add(tombolMulaiBaru, BorderLayout.EAST);

        super.setLayout(new BorderLayout());
        super.add(panelBawah, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));
    }

    // Inisialisasi awal, termasuk pemilihan mode
    public void inisialisasiGameAwal() {
        board = new Board();
        scoreX = 0;
        scoreO = 0;

        // Dialog pemilihan mode permainan
        Object[] options = {"Player vs Player", "Player vs AI"};
        int mode = JOptionPane.showOptionDialog(this,
                "Pilih mode permainan:",
                "Mode Permainan",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        gameMode = (mode == 1) ? GameMode.PLAYER_VS_AI : GameMode.PLAYER_VS_PLAYER;

        mulaiPermainanBaru();
    }

    // Memulai permainan baru (reset papan dan skor)
    public void mulaiPermainanBaru() {
        board.newGame();
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;

        // Reset nama pemain setiap permainan baru dimulai
        namaPemainX = JOptionPane.showInputDialog(this, "Masukkan Nama Pemain X:", "Pemain X");
        if(namaPemainX == null || namaPemainX.trim().isEmpty()) namaPemainX = "Pemain X";

        if (gameMode == GameMode.PLAYER_VS_PLAYER) {
            namaPemainO = JOptionPane.showInputDialog(this, "Masukkan Nama Pemain O:", "Pemain O");
            if(namaPemainO == null || namaPemainO.trim().isEmpty()) namaPemainO = "Pemain O";
        } else {
            namaPemainO = "Komputer AI";
        }

        repaint();
    }

    // Logika utama untuk memperbarui status permainan setelah satu langkah
    private void updateGame(Seed player, int row, int col) {
        currentState = board.stepGame(player, row, col);

        if (currentState == State.CROSS_WON) {
            scoreX++;
            SoundEffect.EXPLODE.play();
        } else if (currentState == State.NOUGHT_WON) {
            scoreO++;
            SoundEffect.EXPLODE.play();
        } else if (currentState == State.DRAW) {
            SoundEffect.DIE.play();
        }

        // Ganti pemain jika permainan masih berlanjut
        if (currentState == State.PLAYING) {
            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
            // Jika giliran AI, panggil AI untuk bergerak
            if (gameMode == GameMode.PLAYER_VS_AI && currentPlayer == Seed.NOUGHT) {
                triggerAIMove();
            }
        }

        repaint();
    }

    // Memicu langkah AI dengan sedikit penundaan
    private void triggerAIMove() {
        if (aiTimer != null && aiTimer.isRunning()) {
            aiTimer.stop();
        }
        aiTimer = new Timer(500, e -> performAIMove());
        aiTimer.setRepeats(false);
        aiTimer.start();
    }

    // AI melakukan langkahnya
    private void performAIMove() {
        int[] move = board.findBestMove();
        if (move != null) {
            updateGame(Seed.NOUGHT, move[0], move[1]);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);
        board.paint(g);
        updateStatusBar();
    }

    // Memperbarui teks di status bar
    private void updateStatusBar() {
        String scoreText = "Skor: " + namaPemainX + " " + scoreX + " - " + scoreO + " " + namaPemainO;
        String status;
        switch (currentState) {
            case PLAYING:
                statusBar.setForeground(Color.BLACK);
                String giliran = (currentPlayer == Seed.CROSS) ? namaPemainX : namaPemainO;
                status = "Giliran " + giliran + " | " + scoreText;
                break;
            case DRAW:
                statusBar.setForeground(Color.RED);
                status = "Seri! Klik 'Mulai Baru'. | " + scoreText;
                break;
            case CROSS_WON:
                statusBar.setForeground(Color.RED);
                status = namaPemainX + " Menang! Klik 'Mulai Baru'. | " + scoreText;
                break;
            case NOUGHT_WON:
                statusBar.setForeground(Color.RED);
                status = namaPemainO + " Menang! Klik 'Mulai Baru'. | " + scoreText;
                break;
            default:
                status = "";
        }
        statusBar.setText(status);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new GameMain());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
