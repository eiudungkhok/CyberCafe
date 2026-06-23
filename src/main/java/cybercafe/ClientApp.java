package cybercafe;

import cybercafe.dao.CustomerDAO;
import cybercafe.dao.GamingSessionDAO;
import cybercafe.dao.MachineDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientApp extends JFrame {
    private int currentMachineId = -1;
    private String currentUsername = null;
    private int currentCustomerId = -1;
    private Timer sessionTimer;

    private CustomerDAO customerDAO = new CustomerDAO();
    private GamingSessionDAO sessionDAO = new GamingSessionDAO();
    private MachineDAO machineDAO = new MachineDAO();

    // Core Colors - Phong cách Cyberpunk 2077
    private final Color BG_DARK = new Color(10, 14, 23);
    private final Color PANEL_BG = new Color(20, 25, 40, 230);
    private final Color NEON_CYAN = new Color(0, 245, 255);
    private final Color NEON_PURPLE = new Color(139, 92, 246);
    private final Color NEON_PINK = new Color(255, 0, 128);

    private JPanel mainContainer;
    private CardLayout cardLayout;

    private JPanel dashboardContent;
    private JLabel lblSessionTimer;
    private JLabel lblBalance;
    private JLabel lblUser;

    // Nhãn dùng cho màn hình Khóa máy
    private JLabel lblLockUser;

    public ClientApp() {
        setTitle("VIKINGS CYBER ARENA - CLIENT SYSTEM");
        setSize(1024, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (currentMachineId != -1) {
                    sessionDAO.checkoutAndGenerateBill(currentMachineId);
                }
                System.exit(0);
            }
        });

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(BG_DARK);

        // Đăng ký 3 màn hình vào hệ thống
        mainContainer.add(createLoginScreen(), "LOGIN");
        mainContainer.add(createDashboardScreen(), "DASHBOARD");
        mainContainer.add(createLockScreen(), "LOCK_SCREEN"); // Màn hình Khóa mới

        add(mainContainer);
        cardLayout.show(mainContainer, "LOGIN");
    }

    // =========================================================
    // MÀN HÌNH 1: SUPER LOGIN SCREEN
    // =========================================================
    private JPanel createLoginScreen() {
        JPanel loginPanel = new JPanel(new BorderLayout());
        loginPanel.setBackground(BG_DARK);

        JPanel leftPromoPanel = new JPanel(new BorderLayout());
        leftPromoPanel.setOpaque(false);
        leftPromoPanel.setBorder(new EmptyBorder(40, 40, 40, 20));

        // Tìm ở hàm createLoginScreen()
        JLabel lblLogo = new JLabel("< NEON ESPORTS ARENA >");
        lblLogo.setFont(new Font("Consolas", Font.BOLD, 36));
        lblLogo.setForeground(NEON_CYAN);

        JLabel lblNews = new JLabel("<html><div style='color: white; font-family: Segoe UI;'>"
                + "<h2 style='color: #8B5CF6;'>🔥 GIẢI ĐẤU VALORANT WINTER 2026 🔥</h2>"
                + "<p>Tổng giải thưởng lên đến 50.000.000 VNĐ. Đăng ký ngay tại quầy!</p><br>"
                + "<h3 style='color: #FF0080;'>🎁 ƯU ĐÃI NẠP TIỀN HÔM NAY</h3>"
                + "<p>- Nạp 50k tặng 10k<br>- Nạp 100k tặng Nước tăng lực Monster</p>"
                + "</div></html>");

        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setOpaque(false);
        qrPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(NEON_CYAN), "NẠP TIỀN NHANH (MOMO/ZALOPAY)", 0, 0, new Font("Segoe UI", Font.BOLD, 12), NEON_CYAN));
        JLabel lblQR = new JLabel("<html><center><br>[ QR CODE PLACEHOLDER ]<br><br>Mã giao dịch: <b>#CYBER_8829</b></center></html>");
        lblQR.setForeground(Color.LIGHT_GRAY);
        lblQR.setHorizontalAlignment(SwingConstants.CENTER);
        qrPanel.add(lblQR, BorderLayout.CENTER);

        leftPromoPanel.add(lblLogo, BorderLayout.NORTH);
        leftPromoPanel.add(lblNews, BorderLayout.CENTER);
        leftPromoPanel.add(qrPanel, BorderLayout.SOUTH);

        JPanel rightFormContainer = new JPanel(new GridBagLayout());
        rightFormContainer.setOpaque(false);
        rightFormContainer.setBorder(new EmptyBorder(0, 0, 0, 40));

        JPanel formBox = new JPanel(new GridLayout(7, 1, 10, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(NEON_PURPLE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
            }
        };
        formBox.setOpaque(false);
        formBox.setBorder(new EmptyBorder(20, 30, 20, 30));
        formBox.setPreferredSize(new Dimension(350, 420));

        JLabel lblLoginTitle = new JLabel("SYSTEM LOGIN", SwingConstants.CENTER);
        lblLoginTitle.setFont(new Font("Consolas", Font.BOLD, 24));
        lblLoginTitle.setForeground(Color.WHITE);

        JLabel lblSelectPC = new JLabel("CHỌN MÁY TRẠM (SIMULATION):");
        lblSelectPC.setForeground(Color.LIGHT_GRAY);
        lblSelectPC.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JComboBox<Integer> cbMachine = new JComboBox<>();
        for (int i = 1; i <= 15; i++) cbMachine.addItem(i);
        cbMachine.setBackground(new Color(30, 35, 50));
        cbMachine.setForeground(Color.WHITE);
        cbMachine.setFont(new Font("Consolas", Font.BOLD, 14));

        JTextField txtUser = createStyledTextField("Nhập tài khoản...");
        JPasswordField txtPass = new JPasswordField();
        txtPass.setBackground(new Color(30, 35, 50));
        txtPass.setForeground(Color.WHITE);
        txtPass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(NEON_CYAN), new EmptyBorder(5, 10, 5, 10)));
        txtPass.setFont(new Font("Consolas", Font.PLAIN, 14));

        JButton btnLogin = new JButton("KÍCH HOẠT TRẠM (LOGIN)");
        btnLogin.setBackground(NEON_CYAN);
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);

        btnLogin.addActionListener(e -> {
            String u = txtUser.getText();
            String p = new String(txtPass.getPassword());
            int pcId = (Integer) cbMachine.getSelectedItem();

            // 🛡️ LỚP BẢO VỆ 0: KIỂM TRA MÁY CÓ BỊ BẢO TRÌ HAY HỎNG KHÔNG
            String pcStatus = machineDAO.getMachineStatus(pcId);
            if ("MAINTENANCE".equalsIgnoreCase(pcStatus)) {
                JOptionPane.showMessageDialog(this,
                        "⛔ MÁY PC " + String.format("%02d", pcId) + " ĐANG ĐƯỢC BẢO TRÌ!\n" +
                                "Vui lòng chọn máy trạm khác.",
                        "Hệ thống bảo trì",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int customerId = customerDAO.authenticateCustomer(u, p);
            if (customerId > 0) {
                // 🛡️ LỚP BẢO VỆ 1: CHỐNG 2 NGƯỜI NGỒI 1 MÁY
                int currentPlayingUserOnThisPc = sessionDAO.getPlayingCustomerId(pcId);
                if (currentPlayingUserOnThisPc != -1 && currentPlayingUserOnThisPc != customerId) {
                    JOptionPane.showMessageDialog(this,
                            "⛔ MÁY PC " + String.format("%02d", pcId) + " ĐANG CÓ NGƯỜI SỬ DỤNG!\n" +
                                    "Vui lòng chọn một máy trạm khác đang trống.",
                            "Truy cập bị từ chối",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 🛡️ LỚP BẢO VỆ 2: CHỐNG 1 NGƯỜI CHƠI 2 MÁY
                int stuckPcId = sessionDAO.getPlayingMachineId(customerId);
                if (stuckPcId != -1) {
                    if (stuckPcId == pcId) {
                        int choice = JOptionPane.showConfirmDialog(this,
                                "Hệ thống phát hiện máy này vừa bị ngắt kết nối đột ngột.\nBạn có muốn khôi phục và đăng nhập lại không?",
                                "Phục hồi phiên chơi",
                                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                        if (choice == JOptionPane.YES_OPTION) {
                            sessionDAO.forceCheckoutByCustomerId(customerId);
                            JOptionPane.showMessageDialog(this, "Đã dọn dẹp phiên cũ! Đang tiến hành đăng nhập...");
                        } else {
                            return;
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Tài khoản này ĐANG ĐƯỢC SỬ DỤNG tại PC " + String.format("%02d", stuckPcId) + "!\n" +
                                        "⚠️ Nếu bạn bị lộ mật khẩu, vui lòng báo Thu ngân.",
                                "Cảnh báo Bảo mật",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                if (sessionDAO.startMemberSession(pcId, customerId)) {
                    currentMachineId = pcId;
                    currentUsername = u;
                    currentCustomerId = customerId;
                    setupDashboardData();
                    cardLayout.show(mainContainer, "DASHBOARD");
                }
            } else if (customerId == -2) {
                JOptionPane.showMessageDialog(this, "Tài khoản hết tiền! Hãy ra quầy nạp thêm.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Truy cập bị từ chối. Sai thông tin!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JLabel lblRealTime = new JLabel("", SwingConstants.CENTER);
        lblRealTime.setForeground(Color.GRAY);
        lblRealTime.setFont(new Font("Consolas", Font.PLAIN, 12));
        Timer t = new Timer(1000, e -> lblRealTime.setText(new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss").format(new Date())));
        t.start();

        formBox.add(lblLoginTitle);
        formBox.add(lblSelectPC);
        formBox.add(cbMachine);
        formBox.add(txtUser);
        formBox.add(txtPass);
        formBox.add(btnLogin);
        formBox.add(lblRealTime);

        rightFormContainer.add(formBox);

        loginPanel.add(leftPromoPanel, BorderLayout.CENTER);
        loginPanel.add(rightFormContainer, BorderLayout.EAST);
        return loginPanel;
    }

    // =========================================================
    // MÀN HÌNH 2: HIGH-END DASHBOARD
    // =========================================================
    private JPanel createDashboardScreen() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(BG_DARK);

        JPanel sidebar = new JPanel(new GridLayout(8, 1, 0, 15));
        sidebar.setBackground(new Color(15, 18, 28));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, NEON_PURPLE));
        sidebar.setPreferredSize(new Dimension(200, 0));

        // Đổi toàn bộ Emoji thành ASCII Geometric Symbols
        JButton btnHome = createMenuButton("[❖] TRANG CHỦ");
        JButton btnFoodMenu = createMenuButton("[♨] GỌI MÓN (FOOD)");
        JButton btnTopup = createMenuButton("[+$] NẠP TIỀN");
        JButton btnAchievement = createMenuButton("[★] THÀNH TÍCH");
        JButton btnPromo = createMenuButton("[✦] KHUYẾN MÃI");
        JButton btnSettings = createMenuButton("[≡] CÀI ĐẶT");

        JButton btnLock = createMenuButton("[✖] KHÓA MÁY (AFK)");
        btnLock.setForeground(new Color(255, 165, 0));
        btnLock.addActionListener(e -> {
            lblLockUser.setText("TÀI KHOẢN: " + currentUsername.toUpperCase() + " ĐANG ĐI VẮNG");
            cardLayout.show(mainContainer, "LOCK_SCREEN"); // Bật khiên lên
        });

        JButton btnLogout = createMenuButton("[⏏] ĐĂNG XUẤT");
        btnLogout.setForeground(NEON_PINK);
        btnLogout.addActionListener(e -> {
            if (sessionTimer != null) sessionTimer.stop();
            String bill = sessionDAO.checkoutAndGenerateBill(currentMachineId);
            JOptionPane.showMessageDialog(this, bill, "HÓA ĐƠN THANH TOÁN", JOptionPane.INFORMATION_MESSAGE);
            currentMachineId = -1; currentUsername = null; currentCustomerId = -1;
            cardLayout.show(mainContainer, "LOGIN");
        });

        sidebar.add(btnHome);
        sidebar.add(btnFoodMenu);
        sidebar.add(btnTopup);
        sidebar.add(btnAchievement);
        sidebar.add(btnPromo);
        sidebar.add(btnSettings);
        sidebar.add(btnLock); // Thay chỗ trống bằng nút Khóa máy
        sidebar.add(btnLogout);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 25, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, NEON_CYAN));
        header.setPreferredSize(new Dimension(0, 80));

        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setOpaque(false);
        userInfo.setBorder(new EmptyBorder(15, 20, 10, 20));
        lblUser = new JLabel("PLAYER: CHƯA ĐĂNG NHẬP");
        lblUser.setFont(new Font("Consolas", Font.BOLD, 18));
        lblUser.setForeground(Color.WHITE);
        lblBalance = new JLabel("SỐ DƯ: 0 VNĐ | HẠNG: DIAMOND");
        lblBalance.setForeground(Color.LIGHT_GRAY);
        userInfo.add(lblUser);
        userInfo.add(lblBalance);

        JPanel timerInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timerInfo.setOpaque(false);
        timerInfo.setBorder(new EmptyBorder(10, 20, 10, 20));
        lblSessionTimer = new JLabel("00:00:00");
        lblSessionTimer.setFont(new Font("Consolas", Font.BOLD, 36));
        lblSessionTimer.setForeground(NEON_CYAN);
        timerInfo.add(lblSessionTimer);

        header.add(userInfo, BorderLayout.WEST);
        header.add(timerInfo, BorderLayout.EAST);

        dashboardContent = new JPanel(new BorderLayout());
        dashboardContent.setBackground(BG_DARK);
        dashboardContent.add(new cybercafe.view.panels.HomePanel());

        btnHome.addActionListener(e -> {
            dashboardContent.removeAll();
            dashboardContent.add(new cybercafe.view.panels.HomePanel());
            dashboardContent.revalidate();
            dashboardContent.repaint();
        });

        btnFoodMenu.addActionListener(e -> {
            dashboardContent.removeAll();
            dashboardContent.add(new cybercafe.view.panels.FoodOrderPanel(currentMachineId));
            dashboardContent.revalidate();
            dashboardContent.repaint();
        });

        btnAchievement.addActionListener(e -> {
            dashboardContent.removeAll();
            dashboardContent.add(new cybercafe.view.panels.AchievementPanel(currentCustomerId));
            dashboardContent.revalidate();
            dashboardContent.repaint();
        });
        // ======== 3 SỰ KIỆN MỚI BỔ SUNG ========
        btnTopup.addActionListener(e -> {
            dashboardContent.removeAll();
            dashboardContent.add(new cybercafe.view.panels.TopupPanel(currentUsername));
            dashboardContent.revalidate();
            dashboardContent.repaint();
        });

        btnPromo.addActionListener(e -> {
            dashboardContent.removeAll();
            dashboardContent.add(new cybercafe.view.panels.PromoPanel());
            dashboardContent.revalidate();
            dashboardContent.repaint();
        });

        btnSettings.addActionListener(e -> {
            dashboardContent.removeAll();
            dashboardContent.add(new cybercafe.view.panels.SettingsPanel(currentCustomerId));
            dashboardContent.revalidate();
            dashboardContent.repaint();
        });

        dashboardPanel.add(sidebar, BorderLayout.WEST);
        dashboardPanel.add(header, BorderLayout.NORTH);
        dashboardPanel.add(dashboardContent, BorderLayout.CENTER);

        return dashboardPanel;
    }

    // =========================================================
    // MÀN HÌNH 3: LOCK SCREEN (KHÓA MÁY BẢO VỆ TÀI KHOẢN)
    // =========================================================
    private JPanel createLockScreen() {
        JPanel lockPanel = new JPanel(new GridBagLayout());
        lockPanel.setBackground(BG_DARK);

        JPanel box = new JPanel(new GridLayout(5, 1, 10, 15)) {
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
        box.setOpaque(false);
        box.setBorder(new EmptyBorder(30, 40, 30, 40));
        box.setPreferredSize(new Dimension(450, 350));

        JLabel lblIcon = new JLabel("[ ✖ _ ✖ ]", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 40));

        JLabel lblTitle = new JLabel("MÁY TRẠM ĐÃ BỊ KHÓA", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(NEON_PINK);

        lblLockUser = new JLabel("TÀI KHOẢN: ... ĐANG ĐI VẮNG", SwingConstants.CENTER);
        lblLockUser.setFont(new Font("Consolas", Font.BOLD, 16));
        lblLockUser.setForeground(Color.LIGHT_GRAY);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setHorizontalAlignment(SwingConstants.CENTER);
        txtPass.setBackground(new Color(30, 35, 50));
        txtPass.setForeground(Color.WHITE);
        txtPass.setFont(new Font("Consolas", Font.PLAIN, 18));
        txtPass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(NEON_PINK), new EmptyBorder(5, 10, 5, 10)));

        JButton btnUnlock = new JButton("MỞ KHÓA MÀN HÌNH");
        btnUnlock.setBackground(NEON_PINK);
        btnUnlock.setForeground(Color.WHITE);
        btnUnlock.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnUnlock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUnlock.setFocusPainted(false);

        // Sự kiện: Chỉ khi gõ ĐÚNG Mật khẩu của tài khoản đang chơi thì mới mở
        btnUnlock.addActionListener(e -> {
            String p = new String(txtPass.getPassword());
            if (customerDAO.authenticateCustomer(currentUsername, p) > 0) {
                txtPass.setText(""); // Xóa pass đi cho an toàn
                cardLayout.show(mainContainer, "DASHBOARD"); // Gỡ bỏ màn hình khóa
            } else {
                JOptionPane.showMessageDialog(lockPanel, "Sai mật khẩu! Không thể mở khóa.", "Cảnh báo bảo mật", JOptionPane.ERROR_MESSAGE);
            }
        });

        box.add(lblIcon);
        box.add(lblTitle);
        box.add(lblLockUser);
        box.add(txtPass);
        box.add(btnUnlock);

        lockPanel.add(box);
        return lockPanel;
    }

    // Tiện ích tạo Nút Menu Sidebar
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(15, 18, 28));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 20, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(30, 35, 50)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(new Color(15, 18, 28)); }
        });
        return btn;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField txt = new JTextField();
        txt.setBackground(new Color(30, 35, 50));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setFont(new Font("Consolas", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(NEON_CYAN), new EmptyBorder(5, 10, 5, 10)));
        return txt;
    }

    private void setupDashboardData() {
        lblUser.setText("PLAYER: " + currentUsername.toUpperCase());
        Timestamp startTime = sessionDAO.getStartTime(currentMachineId);

        if (sessionTimer != null) sessionTimer.stop();
        sessionTimer = new Timer(1000, e -> {
            int currentBalance = customerDAO.getBalance(currentCustomerId);
            int hourlyRate = machineDAO.getHourlyRate(currentMachineId);

            lblBalance.setText("SỐ DƯ: " + currentBalance + " VNĐ | MÁY: PC " + currentMachineId);

            long totalMillis = (long) ((currentBalance / (double) hourlyRate) * 3600 * 1000);
            long elapsed = System.currentTimeMillis() - startTime.getTime();
            long remain = totalMillis - elapsed;

            if (remain <= 0) {
                sessionTimer.stop();
                sessionDAO.checkoutAndGenerateBill(currentMachineId);
                JOptionPane.showMessageDialog(this, "HẾT GIỜ CHƠI! MÁY TỰ ĐỘNG KHÓA.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                currentMachineId = -1; currentUsername = null; currentCustomerId = -1;
                cardLayout.show(mainContainer, "LOGIN");
                return;
            }

            long h = remain / 3600000;
            long m = (remain / 60000) % 60;
            long s = (remain / 1000) % 60;
            lblSessionTimer.setText(String.format("%02d:%02d:%02d", h, m, s));

            if (remain <= 300000) {
                lblSessionTimer.setForeground(NEON_PINK);
            } else {
                lblSessionTimer.setForeground(NEON_CYAN);
            }
        });
        sessionTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientApp().setVisible(true));
    }
}