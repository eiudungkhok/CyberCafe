package cybercafe.view.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomePanel extends JPanel {
    private final Color BG_DARK = new Color(10, 14, 23);
    private final Color PANEL_BG = new Color(20, 25, 40);
    private final Color NEON_CYAN = new Color(0, 245, 255);
    private final Color NEON_PURPLE = new Color(139, 92, 246);
    private final Color NEON_PINK = new Color(255, 0, 128);

    public HomePanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. BANNER CYBER GAMING LỚN PHÍA TRÊN
        JPanel bannerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Vẽ Gradient nền Cyberpunk rực rỡ
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 24, 82), getWidth(), getHeight(), new Color(139, 92, 246));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Vẽ các đường Grid công nghệ viễn tưởng mờ ngầm
                g2.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < getWidth(); i += 40) {
                    g2.drawLine(i, 0, i, getHeight());
                }
                for (int j = 0; j < getHeight(); j += 40) {
                    g2.drawLine(0, j, getWidth(), j);
                }
            }
        };
        bannerPanel.setPreferredSize(new Dimension(0, 120));
        bannerPanel.setLayout(new BorderLayout());

        JLabel lblWelcome = new JLabel("VIKINGS ESPORTS ARENA - WELCOME BACK", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Consolas", Font.BOLD, 24));
        lblWelcome.setForeground(Color.WHITE);
        bannerPanel.add(lblWelcome, BorderLayout.CENTER);

        // 2. KHU VỰC THÔNG TIN (CHIA ĐÔI: GIẢI ĐẤU & NHIỆM VỤ HẰNG NGÀY)
        JPanel contentGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        contentGrid.setOpaque(false);

        // Nửa trái: Lịch thi đấu giải đấu Esports từ quán
        JPanel tournamentPanel = createSectionPanel("GIẢI ĐẤU ĐANG DIỄN RA", NEON_CYAN);
        JTextArea txtTournaments = createStyledTextArea(
                "• [TOURNAMENT] LEAGUE OF LEGENDS VKU CUP 2026\n" +
                        "  - Thời gian: 14:00 - Thứ Bảy tuần này\n" +
                        "  - Trạng thái: Đang mở đăng ký (Miễn phí combo nước)\n\n" +
                        "• [ESPORTS] VALORANT DEATHMATCH KING\n" +
                        "  - Sự kiện cày Rank đêm nhận ngay quà top-up vào tài khoản\n" +
                        "  - Diễn ra khung giờ: 23:00 - 05:00 hằng ngày"
        );
        tournamentPanel.add(txtTournaments, BorderLayout.CENTER);

        // Nửa phải: Hệ thống Nhiệm vụ & Vòng quay may mắn
        JPanel questPanel = createSectionPanel("NHIỆM VỤ HẰNG NGÀY (DAILY QUESTS)", NEON_PURPLE);

        JPanel questBody = new JPanel(new BorderLayout(0, 15));
        questBody.setOpaque(false);

        JTextArea txtQuests = createStyledTextArea(
                "◽ [Nhiệm vụ 1] Cày Rank đủ 3 giờ liên tiếp (Thưởng: +50 Điểm tích lũy)\n" +
                        "◽ [Nhiệm vụ 2] Gọi Mì trứng xúc xích từ giao diện (Thưởng: Giảm 5% giờ chơi)\n" +
                        "◽ [Nhiệm vụ 3] Nạp tiền khung giờ vàng 12h-13h (Thưởng: Tặng 10% giá trị)"
        );

        JButton btnLuckySpin = new JButton("🎰 VÒNG QUAY MAY MẮN (LUCKY SPIN)");
        btnLuckySpin.setBackground(NEON_PINK);
        btnLuckySpin.setForeground(Color.WHITE);
        btnLuckySpin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuckySpin.setFocusPainted(false);
        btnLuckySpin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLuckySpin.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "🎉 Hệ thống ghi nhận bạn có 1 lượt quay miễn phí hôm nay!\nPhần thưởng ngẫu nhiên đang được chuẩn bị mở kho...",
                "Lucky Spin Rewards", JOptionPane.INFORMATION_MESSAGE));

        questBody.add(txtQuests, BorderLayout.CENTER);
        questBody.add(btnLuckySpin, BorderLayout.SOUTH);
        questPanel.add(questBody, BorderLayout.CENTER);

        contentGrid.add(tournamentPanel);
        contentGrid.add(questPanel);

        add(bannerPanel, BorderLayout.NORTH);
        add(contentGrid, BorderLayout.CENTER);
    }

    private JPanel createSectionPanel(String title, Color neonColor) {
        JPanel panel = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(neonColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(neonColor);
        panel.add(lblTitle, BorderLayout.NORTH);

        return panel;
    }

    private JTextArea createStyledTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setForeground(Color.LIGHT_GRAY);
        area.setBackground(PANEL_BG);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }
}