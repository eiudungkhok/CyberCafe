package cybercafe.view.dialogs;

import cybercafe.dao.CustomerDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CustomerManagerDialog extends JDialog {
    private CustomerDAO customerDAO;
    private JComboBox<String> cbUsers;
    private JComboBox<String> cbUsersTopup;

    // 🎨 BẢNG MÀU UI/UX MỚI
    private final Color BG_DARK = new Color(15, 23, 42);
    private final Color PANEL_BG = new Color(30, 41, 59);
    private final Color NEON_CYAN = new Color(34, 211, 238);
    private final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private final Color TEXT_MUTED = new Color(148, 163, 184);

    public CustomerManagerDialog(Frame parent) {
        super(parent, "[ ❖ ] TRUNG TÂM QUẢN LÝ HỘI VIÊN", true);
        this.customerDAO = new CustomerDAO();

        setSize(480, 600); // Kéo dài thêm xíu để chứa ô CCCD
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);

        // ==== KHU VỰC 1: TẠO TÀI KHOẢN MỚI ====
        JPanel createPanel = new JPanel(new GridLayout(4, 2, 10, 15)); // Đổi thành 4 hàng
        createPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(NEON_CYAN), "✨ TẠO TÀI KHOẢN MỚI (BẮT BUỘC ĐỊNH DANH)", 0, 0, new Font("Segoe UI", Font.BOLD, 12), NEON_CYAN));
        createPanel.setBackground(PANEL_BG);
        createPanel.setOpaque(true);

        JLabel lblNewUser = new JLabel(" Tên đăng nhập:"); lblNewUser.setForeground(TEXT_PRIMARY);
        JTextField txtNewUser = createStyledTextField();

        JLabel lblNewPass = new JLabel(" Mật khẩu:"); lblNewPass.setForeground(TEXT_PRIMARY);
        JPasswordField txtNewPass = new JPasswordField();
        styleTextField(txtNewPass);

        // THÊM MỚI Ô CCCD/SĐT
        JLabel lblCccd = new JLabel(" CCCD / SĐT:"); lblCccd.setForeground(new Color(236, 72, 153)); // Màu hồng cảnh báo
        JTextField txtCccd = createStyledTextField();

        JButton btnCreate = new JButton("TẠO TÀI KHOẢN");
        btnCreate.setBackground(NEON_CYAN);
        btnCreate.setForeground(BG_DARK);
        btnCreate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCreate.setFocusPainted(false);
        btnCreate.setCursor(new Cursor(Cursor.HAND_CURSOR));

        createPanel.add(lblNewUser); createPanel.add(txtNewUser);
        createPanel.add(lblNewPass); createPanel.add(txtNewPass);
        createPanel.add(lblCccd); createPanel.add(txtCccd);
        createPanel.add(new JLabel("")); createPanel.add(btnCreate);

        // ==== KHU VỰC 2: NÂNG CẤP HẠNG THẺ ====
        JPanel upgradePanel = new JPanel(new GridLayout(3, 2, 10, 15));
        upgradePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(245, 158, 11)), "👑 NÂNG CẤP HẠNG THẺ", 0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(245, 158, 11)));
        upgradePanel.setBackground(PANEL_BG);
        upgradePanel.setOpaque(true);

        JLabel lblSelectUser = new JLabel(" Chọn Khách hàng:"); lblSelectUser.setForeground(TEXT_PRIMARY);
        cbUsers = new JComboBox<>();
        styleComboBox(cbUsers);

        JLabel lblSelectTier = new JLabel(" Chọn Hạng mới:"); lblSelectTier.setForeground(TEXT_PRIMARY);
        String[] tiers = {"1 - Bronze", "2 - Silver", "3 - Gold", "4 - Platinum", "5 - Diamond"};
        JComboBox<String> cbTiers = new JComboBox<>(tiers);
        styleComboBox(cbTiers);

        JButton btnUpgrade = new JButton("NÂNG CẤP");
        btnUpgrade.setBackground(new Color(245, 158, 11)); // Cam Amber
        btnUpgrade.setForeground(BG_DARK);
        btnUpgrade.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpgrade.setFocusPainted(false);
        btnUpgrade.setCursor(new Cursor(Cursor.HAND_CURSOR));

        upgradePanel.add(lblSelectUser); upgradePanel.add(cbUsers);
        upgradePanel.add(lblSelectTier); upgradePanel.add(cbTiers);
        upgradePanel.add(new JLabel("")); upgradePanel.add(btnUpgrade);

        // ==== KHU VỰC 3: NẠP TIỀN VÀO TÀI KHOẢN ====
        JPanel topupPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        topupPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(16, 185, 129)), "💵 NẠP TIỀN GIỜ CHƠI", 0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(16, 185, 129)));
        topupPanel.setBackground(PANEL_BG);
        topupPanel.setOpaque(true);

        JLabel lblSelectUserTopup = new JLabel(" Chọn Khách hàng:"); lblSelectUserTopup.setForeground(TEXT_PRIMARY);
        cbUsersTopup = new JComboBox<>();
        styleComboBox(cbUsersTopup);

        JLabel lblAmount = new JLabel(" Số tiền nạp (VNĐ):"); lblAmount.setForeground(TEXT_PRIMARY);
        JTextField txtAmount = createStyledTextField();

        JButton btnTopup = new JButton("NẠP TIỀN NGAY");
        btnTopup.setBackground(new Color(16, 185, 129)); // Xanh Emerald
        btnTopup.setForeground(BG_DARK);
        btnTopup.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTopup.setFocusPainted(false);
        btnTopup.setCursor(new Cursor(Cursor.HAND_CURSOR));

        topupPanel.add(lblSelectUserTopup); topupPanel.add(cbUsersTopup);
        topupPanel.add(lblAmount); topupPanel.add(txtAmount);
        topupPanel.add(new JLabel("")); topupPanel.add(btnTopup);

        refreshUserList();

        // ==== XỬ LÝ SỰ KIỆN NÚT BẤM ====
        btnCreate.addActionListener(e -> {
            String u = txtNewUser.getText().trim();
            String p = new String(txtNewPass.getPassword()).trim();
            String cccd = txtCccd.getText().trim(); // Lấy CCCD

            if (u.isEmpty() || p.isEmpty() || cccd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ BẮT BUỘC nhập đầy đủ Tên, Mật khẩu và SĐT/CCCD (Theo quy định An Ninh)!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cccd.length() < 9) {
                JOptionPane.showMessageDialog(this, "⚠️ SĐT hoặc CCCD không hợp lệ! Vui lòng kiểm tra lại.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Gọi hàm tạo user CÓ CHỨA CCCD
            if (customerDAO.createCustomer(u, p, cccd)) {
                JOptionPane.showMessageDialog(this, "Tạo thành công tài khoản: " + u);
                txtNewUser.setText(""); txtNewPass.setText(""); txtCccd.setText("");
                refreshUserList();
            } else {
                JOptionPane.showMessageDialog(this, "Tên tài khoản đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpgrade.addActionListener(e -> {
            String selectedUser = (String) cbUsers.getSelectedItem();
            if (selectedUser == null) return;
            int tierId = Integer.parseInt(cbTiers.getSelectedItem().toString().substring(0, 1));
            if (customerDAO.upgradeMembership(selectedUser, tierId)) {
                JOptionPane.showMessageDialog(this, "Đã nâng cấp " + selectedUser + " lên hạng " + cbTiers.getSelectedItem());
            }
        });

        btnTopup.addActionListener(e -> {
            String selectedUser = (String) cbUsersTopup.getSelectedItem();
            if (selectedUser == null) return;

            try {
                int amount = Integer.parseInt(txtAmount.getText().trim());
                if (amount <= 0) throw new NumberFormatException();

                if (customerDAO.addBalance(selectedUser, amount)) {
                    JOptionPane.showMessageDialog(this, "Nạp thành công " + amount + "đ vào tài khoản " + selectedUser + "!");
                    txtAmount.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số tiền nạp không hợp lệ! Vui lòng nhập số nguyên dương.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Bố cục tổng
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BG_DARK);
        mainPanel.add(createPanel);
        mainPanel.add(upgradePanel);
        mainPanel.add(topupPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void refreshUserList() {
        cbUsers.removeAllItems();
        cbUsersTopup.removeAllItems();
        for (String user : customerDAO.getAllUsernames()) {
            cbUsers.addItem(user);
            cbUsersTopup.addItem(user);
        }
    }

    // Tiện ích UI
    private JTextField createStyledTextField() {
        JTextField txt = new JTextField();
        styleTextField(txt);
        return txt;
    }

    private void styleTextField(JTextField txt) {
        txt.setBackground(BG_DARK);
        txt.setForeground(TEXT_PRIMARY);
        txt.setCaretColor(TEXT_PRIMARY);
        txt.setFont(new Font("Consolas", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)), new EmptyBorder(5, 10, 5, 10)));
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setBackground(BG_DARK);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(new Font("Consolas", Font.PLAIN, 14));
    }
}