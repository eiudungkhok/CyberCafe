package cybercafe.view.panels;

import cybercafe.dao.CustomerDAO;
import cybercafe.model.CustomerProfile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class AchievementPanel extends JPanel {
    private final Color BG_DARK = new Color(10, 14, 23);
    private final Color PANEL_BG = new Color(20, 25, 40);
    private final Color NEON_CYAN = new Color(0, 245, 255);
    private final Color NEON_PURPLE = new Color(139, 92, 246);
    private final Color GOLDEN = new Color(255, 215, 0);

    public AchievementPanel(int customerId) {
        setLayout(new BorderLayout(20, 20));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 🚀 Kéo dữ liệu Real-time từ DB
        CustomerDAO dao = new CustomerDAO();
        CustomerProfile profile = dao.getCustomerProfile(customerId);

        if (profile == null) {
            add(new JLabel("Lỗi tải dữ liệu thành tích!", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        DecimalFormat df = new DecimalFormat("#,###");
        int totalHours = profile.getTotalPlayMinutes() / 60;
        int remainingMinutes = profile.getTotalPlayMinutes() % 60;

        // 1. PROFILE BADGE
        JPanel profileCard = new JPanel(new GridLayout(1, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_BG); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(GOLDEN); g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        profileCard.setOpaque(false);
        profileCard.setBorder(new EmptyBorder(20, 25, 20, 25));
        profileCard.setPreferredSize(new Dimension(0, 100));

        JLabel lblBadge = new JLabel("<html><font size='5' color='#FFF'>HỘI VIÊN: <b>" + profile.getUsername().toUpperCase() + "</b></font><br><font color='#FFD700'>👑 CẤP BẬC: CHUYÊN GIA ESPORTS</font></html>");
        JLabel lblTierDetails = new JLabel("<html><right><font color='#00F5FF'>HẠNG THẺ: " + profile.getTierName().toUpperCase() + "</font><br><font color='#A7F3D0'>Đặc quyền được áp dụng tự động khi tính tiền</font></right></html>", SwingConstants.RIGHT);

        profileCard.add(lblBadge);
        profileCard.add(lblTierDetails);

        // 2. KHỐI LƯỚI THỐNG KÊ CHIẾN TÍCH
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        statsGrid.setOpaque(false);

        statsGrid.add(createStatCard("TỔNG GIỜ CÀY RANK", totalHours + "h " + remainingMinutes + "p", NEON_CYAN));
        statsGrid.add(createStatCard("TỔNG TIỀN ĐÃ NẠP", df.format(profile.getTotalSpent()) + " đ", GOLDEN));
        statsGrid.add(createStatCard("ĐƠN DỊCH VỤ ĐÃ ĐẶT", profile.getTotalOrders() + " Món", NEON_PURPLE));

// 3. THANH TIẾN TRÌNH LÊN CẤP ĐỘNG
        int targetToNextRank = 1000000; // 1 Triệu = 1 Cấp
        int currentProgress = profile.getTotalSpent() % targetToNextRank;
        int percentage = (int) ((currentProgress / (double) targetToNextRank) * 100);
        int missingPoints = targetToNextRank - currentProgress;

        JPanel progressPanel = new JPanel(new BorderLayout(0, 10));
        progressPanel.setOpaque(false);

        JLabel lblProgressTitle = new JLabel("TIẾN TRÌNH LÊN CẤP BẬC KẾ TIẾP");
        lblProgressTitle.setForeground(Color.WHITE);
        lblProgressTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        // ===== LOGIC MỚI: KIỂM TRA HẠNG MAX (DIAMOND) =====
        String currentTier = profile.getTierName().toUpperCase();
        if (currentTier.contains("DIAMOND") || currentTier.contains("5")) {
            // Nếu đã là Diamond (Hạng 5) -> Cho full 100%
            progressBar.setValue(100);
            progressBar.setString("100% (ĐÃ ĐẠT CẤP BẬC TỐI ĐA - CẢM ƠN ĐẠI CAO THỦ!)");

            progressBar.setForeground(GOLDEN); // Đổi thanh thành màu Vàng Gold cho ngầu
            lblProgressTitle.setText("TIẾN TRÌNH LÊN CẤP BẬC KẾ TIẾP (MAX RANK REACHED)");
        } else {
            // Các hạng khác chạy bình thường
            progressBar.setValue(percentage);
            progressBar.setString(percentage + "% (Còn thiếu " + df.format(missingPoints) + " đ để nâng cấp Hạng Thẻ mới)");
            progressBar.setForeground(NEON_PURPLE);
        }
        // ==================================================

        progressBar.setBackground(new Color(30, 35, 50));
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        progressBar.setPreferredSize(new Dimension(0, 25));

        progressPanel.add(lblProgressTitle, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);

        add(profileCard, BorderLayout.NORTH);
        add(statsGrid, BorderLayout.CENTER);
        add(progressPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_BG); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(new Color(38, 45, 64)); g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        card.setOpaque(false); card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title); lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12)); lblTitle.setForeground(Color.GRAY);
        JLabel lblValue = new JLabel(value); lblValue.setFont(new Font("Consolas", Font.BOLD, 22)); lblValue.setForeground(accentColor);

        card.add(lblTitle); card.add(lblValue);
        return card;
    }
}