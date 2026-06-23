package cybercafe.view.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TopupPanel extends JPanel {
    private final Color BG_DARK = new Color(10, 14, 23);
    private final Color PANEL_BG = new Color(20, 25, 40);
    private final Color NEON_CYAN = new Color(0, 245, 255);
    private final Color NEON_PINK = new Color(255, 0, 128);

    public TopupPanel(String username) {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        JLabel lblTitle = new JLabel("[+$] NẠP TIỀN TỰ ĐỘNG", SwingConstants.CENTER);        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(NEON_CYAN);
        lblTitle.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel qrBox = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(NEON_PINK);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
            }
        };
        qrBox.setOpaque(false);
        qrBox.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel lblQRPlaceholder = new JLabel("<html><center><font size='6'>[ ▤ ]</font><br><br>[ QUÉT MÃ QR TẠI ĐÂY ]<br><br><b>MOMO / ZALOPAY / BANKING</b></center></html>");        lblQRPlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
        lblQRPlaceholder.setForeground(Color.WHITE);
        lblQRPlaceholder.setFont(new Font("Consolas", Font.BOLD, 18));
        lblQRPlaceholder.setPreferredSize(new Dimension(250, 250));
        lblQRPlaceholder.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));

        JLabel lblInstruction = new JLabel("<html><center>Nội dung chuyển khoản bắt buộc:<br><font color='#00F5FF' size='5'><b>NAP " + username.toUpperCase() + "</b></font></center></html>", SwingConstants.CENTER);
        lblInstruction.setForeground(Color.LIGHT_GRAY);
        lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnConfirm = new JButton("ĐÃ CHUYỂN KHOẢN (GỬI YÊU CẦU)");
        btnConfirm.setBackground(new Color(50, 205, 50));
        btnConfirm.setForeground(Color.BLACK);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Hệ thống đã ghi nhận yêu cầu.\nVui lòng chờ Thu ngân xác nhận trong ít phút!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        qrBox.add(lblInstruction, BorderLayout.NORTH);
        qrBox.add(lblQRPlaceholder, BorderLayout.CENTER);
        qrBox.add(btnConfirm, BorderLayout.SOUTH);

        centerPanel.add(qrBox);
        add(centerPanel, BorderLayout.CENTER);
    }
}