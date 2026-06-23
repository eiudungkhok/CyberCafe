package cybercafe.view.dialogs;

import cybercafe.dao.CustomerDAO;

import javax.swing.*;
import java.awt.*;

public class CustomerManagerDialog extends JDialog {
    private CustomerDAO customerDAO;
    private JComboBox<String> cbUsers;
    private JComboBox<String> cbUsersTopup; // ComboBox cho khu vực nạp tiền

    public CustomerManagerDialog(Frame parent) {
        super(parent, "TRUNG TÂM QUẢN LÝ HỘI VIÊN", true);
        this.customerDAO = new CustomerDAO();

        setSize(450, 550); // Kéo dài cửa sổ ra thêm để lấy chỗ chứa tính năng Nạp tiền
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(15, 18, 25));

        // ==== KHU VỰC 1: TẠO TÀI KHOẢN MỚI ====
        JPanel createPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        createPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0, 245, 255)), "✨ TẠO TÀI KHOẢN MỚI ✨"));
        ((javax.swing.border.TitledBorder) createPanel.getBorder()).setTitleColor(new Color(0, 245, 255));
        createPanel.setBackground(new Color(20, 25, 40));
        createPanel.setOpaque(true);

        JLabel lblNewUser = new JLabel(" Tên đăng nhập:"); lblNewUser.setForeground(Color.WHITE);
        JTextField txtNewUser = new JTextField();

        JLabel lblNewPass = new JLabel(" Mật khẩu:"); lblNewPass.setForeground(Color.WHITE);
        JPasswordField txtNewPass = new JPasswordField();

        JButton btnCreate = new JButton("TẠO TÀI KHOẢN");
        btnCreate.setBackground(new Color(0, 245, 255));
        btnCreate.setForeground(Color.BLACK);
        btnCreate.setFont(new Font("Segoe UI", Font.BOLD, 12));

        createPanel.add(lblNewUser); createPanel.add(txtNewUser);
        createPanel.add(lblNewPass); createPanel.add(txtNewPass);
        createPanel.add(new JLabel("")); createPanel.add(btnCreate);

        // ==== KHU VỰC 2: NÂNG CẤP HẠNG THẺ ====
        JPanel upgradePanel = new JPanel(new GridLayout(3, 2, 10, 15));
        upgradePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(255, 215, 0)), "👑 NÂNG CẤP HẠNG THẺ 👑"));
        ((javax.swing.border.TitledBorder) upgradePanel.getBorder()).setTitleColor(new Color(255, 215, 0));
        upgradePanel.setBackground(new Color(20, 25, 40));
        upgradePanel.setOpaque(true);

        JLabel lblSelectUser = new JLabel(" Chọn Khách hàng:"); lblSelectUser.setForeground(Color.WHITE);
        cbUsers = new JComboBox<>();

        JLabel lblSelectTier = new JLabel(" Chọn Hạng mới:"); lblSelectTier.setForeground(Color.WHITE);
        String[] tiers = {"1 - Bronze", "2 - Silver", "3 - Gold", "4 - Platinum", "5 - Diamond"};
        JComboBox<String> cbTiers = new JComboBox<>(tiers);

        JButton btnUpgrade = new JButton("NÂNG CẤP");
        btnUpgrade.setBackground(new Color(255, 215, 0));
        btnUpgrade.setForeground(Color.BLACK);
        btnUpgrade.setFont(new Font("Segoe UI", Font.BOLD, 12));

        upgradePanel.add(lblSelectUser); upgradePanel.add(cbUsers);
        upgradePanel.add(lblSelectTier); upgradePanel.add(cbTiers);
        upgradePanel.add(new JLabel("")); upgradePanel.add(btnUpgrade);

        // ==== KHU VỰC 3: NẠP TIỀN VÀO TÀI KHOẢN ====
        JPanel topupPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        topupPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(50, 205, 50)), "💵 NẠP TIỀN GIỜ CHƠI 💵"));
        ((javax.swing.border.TitledBorder) topupPanel.getBorder()).setTitleColor(new Color(50, 205, 50));
        topupPanel.setBackground(new Color(20, 25, 40));
        topupPanel.setOpaque(true);

        JLabel lblSelectUserTopup = new JLabel(" Chọn Khách hàng:"); lblSelectUserTopup.setForeground(Color.WHITE);
        cbUsersTopup = new JComboBox<>();

        JLabel lblAmount = new JLabel(" Số tiền nạp (VNĐ):"); lblAmount.setForeground(Color.WHITE);
        JTextField txtAmount = new JTextField();

        JButton btnTopup = new JButton("NẠP TIỀN NGAY");
        btnTopup.setBackground(new Color(50, 205, 50));
        btnTopup.setForeground(Color.BLACK);
        btnTopup.setFont(new Font("Segoe UI", Font.BOLD, 12));

        topupPanel.add(lblSelectUserTopup); topupPanel.add(cbUsersTopup);
        topupPanel.add(lblAmount); topupPanel.add(txtAmount);
        topupPanel.add(new JLabel("")); topupPanel.add(btnTopup);

        // Khởi tạo dữ liệu cho cả 2 ComboBox
        refreshUserList();

        // ==== XỬ LÝ SỰ KIỆN NÚT BẤM ====
        btnCreate.addActionListener(e -> {
            String u = txtNewUser.getText().trim();
            String p = new String(txtNewPass.getPassword()).trim();
            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không được để trống thông tin!"); return;
            }
            if (customerDAO.createCustomer(u, p)) {
                JOptionPane.showMessageDialog(this, "Tạo thành công tài khoản: " + u);
                txtNewUser.setText(""); txtNewPass.setText("");
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
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 0, 10)); // Sửa lại thành 3 hàng
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(15, 18, 25));
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
}