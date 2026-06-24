package cybercafe.view.panels;

import cybercafe.dao.CustomerDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    // Thay thế đoạn khai báo màu cũ bằng cụm này:
    private final Color BG_DARK = new Color(15, 23, 42);
    private final Color PANEL_BG = new Color(30, 41, 59);
    private final Color NEON_CYAN = new Color(34, 211, 238);
    private final Color NEON_PINK = new Color(236, 72, 153);
    private final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private final Color TEXT_MUTED = new Color(148, 163, 184);
    public SettingsPanel(int customerId) {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        JLabel lblTitle = new JLabel("⚙ CÀI ĐẶT TÀI KHOẢN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(new EmptyBorder(30, 0, 30, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel formBox = new JPanel(new GridLayout(7, 1, 5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        formBox.setOpaque(false);
        formBox.setBorder(new EmptyBorder(20, 30, 20, 30));
        formBox.setPreferredSize(new Dimension(350, 350));

        JLabel lbl1 = new JLabel("Mật khẩu hiện tại:"); lbl1.setForeground(Color.LIGHT_GRAY);
        JPasswordField txtOld = createPassField();

        JLabel lbl2 = new JLabel("Mật khẩu mới:"); lbl2.setForeground(Color.LIGHT_GRAY);
        JPasswordField txtNew = createPassField();

        JLabel lbl3 = new JLabel("Nhập lại mật khẩu mới:"); lbl3.setForeground(Color.LIGHT_GRAY);
        JPasswordField txtConfirm = createPassField();

        JButton btnSave = new JButton("LƯU THAY ĐỔI");
        btnSave.setBackground(NEON_CYAN);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> {
            String oldP = new String(txtOld.getPassword());
            String newP = new String(txtNew.getPassword());
            String confP = new String(txtConfirm.getPassword());

            if (oldP.isEmpty() || newP.isEmpty() || confP.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            if (!newP.equals(confP)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CustomerDAO dao = new CustomerDAO();
            if (dao.changePassword(customerId, oldP, newP)) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
                txtOld.setText(""); txtNew.setText(""); txtConfirm.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Sai mật khẩu hiện tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        formBox.add(lbl1); formBox.add(txtOld);
        formBox.add(lbl2); formBox.add(txtNew);
        formBox.add(lbl3); formBox.add(txtConfirm);
        formBox.add(btnSave);

        centerPanel.add(formBox);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPasswordField createPassField() {
        JPasswordField txt = new JPasswordField();
        txt.setBackground(new Color(30, 35, 50));
        txt.setForeground(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), new EmptyBorder(5, 10, 5, 10)));
        return txt;
    }
}