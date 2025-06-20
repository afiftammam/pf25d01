package TTTConsole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class SettingsPanel extends JPanel {
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private final JFrame mainFrame; // Referensi ke frame utama

    public SettingsPanel(JPanel mainPanel, CardLayout cardLayout, JFrame mainFrame) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.mainFrame = mainFrame;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Judul
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.textLight);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Konten
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JCheckBox muteSoundCheckBox = new JCheckBox("Mute All Sounds");
        muteSoundCheckBox.setSelected(SoundEffect.volume == SoundEffect.Volume.MUTE);
        muteSoundCheckBox.addItemListener(e -> {
            SoundEffect.volume = (e.getStateChange() == ItemEvent.SELECTED) ? SoundEffect.Volume.MUTE : SoundEffect.Volume.LOW;
        });

        JRadioButton darkThemeRadio = new JRadioButton("Cyber Theme (Dark)");
        darkThemeRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Theme.applyCyberTheme();
                Theme.updateUI(mainFrame);
            }
        });

        JRadioButton lightThemeRadio = new JRadioButton("Mint Theme (Light)");
        lightThemeRadio.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Theme.applyMintTheme();
                Theme.updateUI(mainFrame);
            }
        });

        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(darkThemeRadio);
        themeGroup.add(lightThemeRadio);
        darkThemeRadio.setSelected(true); // Default

        // Style dan tambahkan ke panel
        styleOption(muteSoundCheckBox);
        styleOption(darkThemeRadio);
        styleOption(lightThemeRadio);

        contentPanel.add(muteSoundCheckBox);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(new JLabel("UI Theme:"));
        contentPanel.add(darkThemeRadio);
        contentPanel.add(lightThemeRadio);

        // Atur agar komponen berada di tengah
        for(Component c : contentPanel.getComponents()) {
            ((JComponent)c).setAlignmentX(Component.CENTER_ALIGNMENT);
            if (c instanceof JLabel) {
                c.setForeground(Theme.textLight);
                c.setFont(Theme.FONT_STATUS);
            }
        }

        add(contentPanel, BorderLayout.CENTER);

        // Tombol Kembali
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleOption(AbstractButton button) {
        button.setFont(Theme.FONT_STATUS.deriveFont(18f));
        button.setForeground(Theme.textLight);
        button.setOpaque(false);
        button.setFocusPainted(false);
    }
}