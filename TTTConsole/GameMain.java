package TTTConsole;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    //untuk tampilan
    public static final String TITLE = "Tic Tac Toe Sederhana";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Objek
    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;

    private int scoreX;
    private int scoreO;
    private String namaPemainX;
    private String namaPemainO;
    private JButton tombolMulaiBaru;

    private int lastPlayedRow = -1;
    private int lastPlayedCol = -1;
    public static final Color HIGHLIGHT_COLOR = Color.YELLOW;
    public static final int HIGHLIGHT_THICKNESS = 4;

    public GameMain() {
        JOptionPane.showMessageDialog(this,
                "Selamat datang di Game Tic Tac Toe!\nSiap gaaa?",
                "Selamat Datang:)!",
                JOptionPane.INFORMATION_MESSAGE);

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        currentState = board.stepGame(currentPlayer, row, col);
                        lastPlayedRow = row;
                        lastPlayedCol = col;
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                    }
                }
                repaint();
            }
        });

        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        tombolMulaiBaru = new JButton("Mulai Baru");
        tombolMulaiBaru.setFont(FONT_STATUS);
        tombolMulaiBaru.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mulaiPermainanBaru();
                repaint();
            }
        });

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.add(statusBar, BorderLayout.CENTER);
        panelBawah.add(tombolMulaiBaru, BorderLayout.EAST);

        super.setLayout(new BorderLayout());
        super.add(panelBawah, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            parentFrame.setJMenuBar(createMenuBar());
        }

        inisialisasiGameAwal();
        mulaiPermainanBaru();
    }

    public void inisialisasiGameAwal() {
        board = new Board();
        scoreX = 0;
        scoreO = 0;
    }

    public void mulaiPermainanBaru() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED; // kosong semua sel
            }
        }
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;

        lastPlayedRow = -1; // reset
        lastPlayedCol = -1; // Resettt

        // nama pemain X
        namaPemainX = JOptionPane.showInputDialog(this, "Masukkan Nama Pemain X:", "Nama Pemain", JOptionPane.PLAIN_MESSAGE);
        if (namaPemainX == null || namaPemainX.trim().isEmpty()) {
            namaPemainX = "Pemain X";
        }

        // nama pemain O
        namaPemainO = JOptionPane.showInputDialog(this, "Masukkan Nama Pemain O:", "Nama Pemain", JOptionPane.PLAIN_MESSAGE);
        if (namaPemainO == null || namaPemainO.trim().isEmpty()) {
            namaPemainO = "Pemain O";
        }
    }
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuBantuan = new JMenu("Bantuan");
        JMenuItem itemTentang = new JMenuItem("Tentang");

        itemTentang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String infoProyek = "Nama Proyek: pf25d01\n" +
                        "Versi: 1.0 (Mini Project Dasprog 2025)\n" +
                        "Anggota Tim:\n" +
                        "1. 5026241159, M afif Tammam\n" +
                        "2. 5026241147, Akhtar Ibrahim\n" +
                        "3. 5026241062, Hilman";
                JOptionPane.showMessageDialog(GameMain.this,
                        infoProyek,
                        "Tentang Aplikasi",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        menuBantuan.add(itemTentang);
        menuBar.add(menuBantuan);
        return menuBar;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);

        board.paint(g);

        if (currentState == State.PLAYING && lastPlayedRow != -1 && lastPlayedCol != -1) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(HIGHLIGHT_COLOR);
            g2d.setStroke(new BasicStroke(HIGHLIGHT_THICKNESS));
            int x = lastPlayedCol * Cell.SIZE + HIGHLIGHT_THICKNESS / 2;
            int y = lastPlayedRow * Cell.SIZE + HIGHLIGHT_THICKNESS / 2;
            int size = Cell.SIZE - HIGHLIGHT_THICKNESS;
            g2d.drawRect(x, y, size, size);
        }

        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            String namaSekarang = (currentPlayer == Seed.CROSS) ? namaPemainX : namaPemainO;
            statusBar.setText("Giliran " + namaSekarang + " | Skor: " + namaPemainX + " " + scoreX + " - " + scoreO + " " + namaPemainO);
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("Seri! Klik 'Mulai Baru'. | Skor: " + namaPemainX + " " + scoreX + " - " + scoreO + " " + namaPemainO);
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            if (currentState != State.CROSS_WON) {
                scoreX++;
            }
            statusBar.setText(namaPemainX + " Menang! Klik 'Mulai Baru'. | Skor: " + namaPemainX + " " + scoreX + " - " + scoreO + " " + namaPemainO);
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            if (currentState != State.NOUGHT_WON) {
                scoreO++;
            }
            statusBar.setText(namaPemainO + " Menang! Klik 'Mulai Baru'. | Skor: " + namaPemainX + " " + scoreX + " - " + scoreO + " " + namaPemainO);
        }
    }

    public static void main(String[] args) {
        // Jalankan kode GUI di Event-Dispatching Thread
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TITLE);
                GameMain gamePanel = new GameMain(); // Buat instance GameMain
                frame.setContentPane(gamePanel); // Set konten frame
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}