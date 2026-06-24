package cybercafe.view.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PromoPanel extends JPanel {
    // Thay thế đoạn khai báo màu cũ bằng cụm này:
    private final Color BG_DARK = new Color(15, 23, 42);
    private final Color PANEL_BG = new Color(30, 41, 59);
    private final Color NEON_CYAN = new Color(34, 211, 238);
    private final Color NEON_PINK = new Color(236, 72, 153);
    private final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private final Color TEXT_MUTED = new Color(148, 163, 184);

    public PromoPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("[✦] SỰ KIỆN & ƯU ĐÃI ĐỘC QUYỀN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(255, 215, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        gridPanel.add(createPromoCard("[>>] NẠP LIỀN TAY - NHẬN NGAY QUÀ KHỦNG", "Nạp 50K tặng 10K. Nạp 100K tặng 1 chai Sting + 20K vào tài khoản chính.", "#FF0080"));
        gridPanel.add(createPromoCard("[☾] COMBO ĐÊM (22H - 7H SÁNG)", "Chơi xuyên đêm chỉ với 30.000 VNĐ. Tặng kèm 1 tô mì trứng chần siêu cấp.", "#00F5FF"));
        gridPanel.add(createPromoCard("[★] GIẢI ĐẤU VALORANT WINTER ARENA", "Tổng giải thưởng 50 Củ. Đăng ký tham gia ngay tại Quầy thu ngân trước 30/12.", "#8B5CF6"));
        gridPanel.add(createPromoCard("[♦] ĐẶC QUYỀN HỘI VIÊN DIAMOND", "Giảm 10% toàn bộ dịch vụ Ăn uống. Tặng 1 giờ chơi miễn phí vào ngày sinh nhật.", "#FFD700"));
        add(gridPanel, BorderLayout.CENTER);
    }

    private JPanel createPromoCard(String title, String desc, String hexColor) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.decode(hexColor));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblT = new JLabel("<html><font color='" + hexColor + "'>" + title + "</font></html>");
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblD = new JLabel("<html><font color='white'>" + desc + "</font></html>");
        lblD.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblD, BorderLayout.CENTER);
        return card;
    }
}