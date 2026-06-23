package cybercafe.view.dialogs;

import cybercafe.dao.CustomerDAO;
import cybercafe.dao.GamingSessionDAO;

import javax.swing.*;
import java.awt.*;

public class MemberLoginDialog extends JDialog {
    private boolean loginSuccess = false;
    private CustomerDAO customerDAO;
    private GamingSessionDAO sessionDAO;

    public MemberLoginDialog(Frame parent, int machineId) {
        super(parent, "ĐĂNG NHẬP HỘI VIÊN - PC " + machineId, true);
        this.customerDAO = new CustomerDAO();
        this.sessionDAO = new GamingSessionDAO();

        setSize(350, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(20, 24, 35));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        formPanel.setOpaque(false);

        JLabel lblUser = new JLabel("Tài khoản:");
        lblUser.setForeground(Color.WHITE);
        JTextField txtUser = new JTextField();
        txtUser.setBackground(new Color(30, 35, 50));
        txtUser.setForeground(Color.WHITE);
        txtUser.setCaretColor(Color.WHITE);
        txtUser.setBorder(BorderFactory.createLineBorder(new Color(139, 92, 246)));

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setForeground(Color.WHITE);
        JPasswordField txtPass = new JPasswordField();
        txtPass.setBackground(new Color(30, 35, 50));
        txtPass.setForeground(Color.WHITE);
        txtPass.setCaretColor(Color.WHITE);
        txtPass.setBorder(BorderFactory.createLineBorder(new Color(139, 92, 246)));

        formPanel.add(lblUser); formPanel.add(txtUser);
        formPanel.add(lblPass); formPanel.add(txtPass);

        JButton btnLogin = new JButton("GỌI MÁY");
        btnLogin.setBackground(new Color(139, 92, 246)); // Tím Neon
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            int customerId = customerDAO.authenticateCustomer(user, pass);

            if (customerId == -1) {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
            } else if (customerId == -2) {
                JOptionPane.showMessageDialog(this, "Tài khoản ĐÃ HẾT TIỀN. Vui lòng nạp thêm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            } else {
                // Đăng nhập thành công -> Gọi máy
                if (sessionDAO.startMemberSession(machineId, customerId)) {
                    loginSuccess = true;
                    dispose();
                }
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(btnLogin, BorderLayout.SOUTH);
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }
}