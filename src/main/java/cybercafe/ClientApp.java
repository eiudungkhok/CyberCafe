package cybercafe;

import cybercafe.dao.CustomerDAO;
import cybercafe.dao.GamingSessionDAO;
import cybercafe.dao.MachineDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
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

    // 🎨 BẢNG MÀU UI/UX
    private final Color BG_DARK = new Color(15, 23, 42);
    private final Color PANEL_BG = new Color(30, 41, 59);
    private final Color NEON_CYAN = new Color(34, 211, 238);
    private final Color NEON_PURPLE = new Color(168, 85, 247);
    private final Color NEON_PINK = new Color(236, 72, 153);

    private final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private final Color TEXT_MUTED = new Color(148, 163, 184);

    private JPanel mainContainer;
    private CardLayout cardLayout;

    private JPanel dashboardContent;
    private JLabel lblSessionTimer;
    private JLabel lblBalance;
    private JLabel lblUser;
    private JLabel lblLockUser;

    // Biến lưu tọa độ chuột để kéo thả cửa sổ
    private Point initialClick;

    public ClientApp() {
        setTitle("VIKINGS CYBER ARENA - CLIENT SYSTEM");
        setSize(1024, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true); // Tắt viền Windows mặc định

        // Setup Layout tổng của JFrame
        setLayout(new BorderLayout());

        // 1. Gắn Thanh Title Bar tự chế vào TRÊN CÙNG
        add(createTitleBar(), BorderLayout.NORTH);

        // 2. Chuyển đổi các màn hình (Main Container)
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(BG_DARK);

        mainContainer.add(createLoginScreen(), "LOGIN");
        mainContainer.add(createDashboardScreen(), "DASHBOARD");
        mainContainer.add(createLockScreen(), "LOCK_SCREEN");

        add(mainContainer, BorderLayout.CENTER);
        cardLayout.show(mainContainer, "LOGIN");
    }

    // =========================================================
    // THANH TIÊU ĐỀ TÙY CHỈNH (CUSTOM TITLE BAR)
    // =========================================================
    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(BG_DARK);
        titleBar.setPreferredSize(new Dimension(getWidth(), 35));

        // Logic kéo thả di chuyển cửa sổ
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                // Không cho kéo thả nếu đang phóng to toàn màn hình
                if (getExtendedState() == JFrame.MAXIMIZED_BOTH) return;

                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        // Tiêu đề nhỏ bên trái
        JLabel lblTitle = new JLabel("  [ ❖ ] CYBER CAFE SYSTEM - CLIENT OS v2.0");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(TEXT_MUTED);
        titleBar.add(lblTitle, BorderLayout.WEST);

        // Tổ hợp 3 nút bên phải
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);

        JButton btnMin = createTitleButton("—");
        btnMin.addActionListener(e -> setState(JFrame.ICONIFIED));

        JButton btnMax = createTitleButton("◻");
        btnMax.addActionListener(e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });

        JButton btnClose = createTitleButton("✕");
        // Hiệu ứng rê chuột vào nút Đóng chuyển màu đỏ
        btnClose.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnClose.setBackground(new Color(239, 68, 68));
                btnClose.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btnClose.setBackground(BG_DARK);
                btnClose.setForeground(TEXT_MUTED);
            }
        });
        btnClose.addActionListener(e -> {
            if (currentMachineId != -1) {
                sessionDAO.checkoutAndGenerateBill(currentMachineId);
            }
            System.exit(0);
        });

        btnPanel.add(btnMin);
        btnPanel.add(btnMax);
        btnPanel.add(btnClose);

        titleBar.add(btnPanel, BorderLayout.EAST);
        return titleBar;
    }

    private JButton createTitleButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_MUTED);
        btn.setBackground(BG_DARK);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (!text.equals("✕")) {
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(PANEL_BG);
                    btn.setForeground(TEXT_PRIMARY);
                }
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(BG_DARK);
                    btn.setForeground(TEXT_MUTED);
                }
            });
        }
        return btn;
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

        JLabel lblLogo = new JLabel("< CYBER CAFE CLIENT SYSTEM >");
        lblLogo.setFont(new Font("Consolas", Font.BOLD, 36));
        lblLogo.setForeground(NEON_CYAN);

        JLabel lblNews = new JLabel("<html><div style='color: #F1F5F9; font-family: Segoe UI; line-height: 1.5;'>"
                + "<h2 style='color: #A855F7; margin-bottom: 5px;'>[★] GIẢI ĐẤU VALORANT WINTER 2026</h2>"
                + "<p style='color: #94A3B8;'>Tổng giải thưởng lên đến 50.000.000 VNĐ. Đăng ký ngay tại quầy!</p><br>"
                + "<h3 style='color: #EC4899; margin-bottom: 5px;'>[✦] ƯU ĐÃI NẠP TIỀN HÔM NAY</h3>"
                + "<p style='color: #94A3B8;'>- Nạp 50k tặng 10k<br>- Nạp 100k tặng Nước tăng lực Monster</p>"
                + "</div></html>");

        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setOpaque(false);
        qrPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)), "NẠP TIỀN NHANH (MOMO/ZALOPAY)", 0, 0, new Font("Segoe UI", Font.BOLD, 12), NEON_CYAN));
        JLabel lblQR = new JLabel("<html><center><br><font size='5'>[ ▤ ]</font><br><br>Mã giao dịch: <b style='color:#22D3EE'>#CYBER_8829</b></center></html>");
        lblQR.setForeground(TEXT_MUTED);
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(71, 85, 105));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 16, 16);
            }
        };
        formBox.setOpaque(false);
        formBox.setBorder(new EmptyBorder(25, 30, 25, 30));
        formBox.setPreferredSize(new Dimension(360, 430));

        JLabel lblLoginTitle = new JLabel("SYSTEM LOGIN", SwingConstants.CENTER);
        lblLoginTitle.setFont(new Font("Consolas", Font.BOLD, 22));
        lblLoginTitle.setForeground(TEXT_PRIMARY);

        JLabel lblSelectPC = new JLabel("CHỌN MÁY TRẠM (SIMULATION):");
        lblSelectPC.setForeground(TEXT_MUTED);
        lblSelectPC.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JComboBox<Integer> cbMachine = new JComboBox<>();
        for (int i = 1; i <= 15; i++) cbMachine.addItem(i);
        cbMachine.setBackground(BG_DARK);
        cbMachine.setForeground(TEXT_PRIMARY);
        cbMachine.setFont(new Font("Consolas", Font.BOLD, 14));

        JTextField txtUser = createStyledTextField();
        JPasswordField txtPass = new JPasswordField();
        txtPass.setBackground(BG_DARK);
        txtPass.setForeground(TEXT_PRIMARY);
        txtPass.setCaretColor(TEXT_PRIMARY);
        txtPass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)), new EmptyBorder(8, 12, 8, 12)));
        txtPass.setFont(new Font("Consolas", Font.PLAIN, 14));

        JButton btnLogin = new JButton("KÍCH HOẠT TRẠM (LOGIN)");
        btnLogin.setBackground(NEON_CYAN);
        btnLogin.setForeground(BG_DARK);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        btnLogin.addActionListener(e -> {
            String u = txtUser.getText();
            String p = new String(txtPass.getPassword());
            int pcId = (Integer) cbMachine.getSelectedItem();

            String pcStatus = machineDAO.getMachineStatus(pcId);
            if ("MAINTENANCE".equalsIgnoreCase(pcStatus)) {
                JOptionPane.showMessageDialog(this, "⛔ MÁY PC " + String.format("%02d", pcId) + " ĐANG ĐƯỢC BẢO TRÌ!\nVui lòng chọn máy trạm khác.", "Hệ thống bảo trì", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int customerId = customerDAO.authenticateCustomer(u, p);
            if (customerId > 0) {
                int currentPlayingUserOnThisPc = sessionDAO.getPlayingCustomerId(pcId);
                if (currentPlayingUserOnThisPc != -1 && currentPlayingUserOnThisPc != customerId) {
                    JOptionPane.showMessageDialog(this, "⛔ MÁY PC " + String.format("%02d", pcId) + " ĐANG CÓ NGƯỜI SỬ DỤNG!", "Truy cập bị từ chối", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int stuckPcId = sessionDAO.getPlayingMachineId(customerId);
                if (stuckPcId != -1) {
                    if (stuckPcId == pcId) {
                        int choice = JOptionPane.showConfirmDialog(this, "Phát hiện máy vừa bị ngắt kết nối đột ngột.\nKhôi phục phiên chơi?", "Phục hồi", JOptionPane.YES_NO_OPTION);
                        if (choice == JOptionPane.YES_OPTION) sessionDAO.forceCheckoutByCustomerId(customerId);
                        else return;
                    } else {
                        JOptionPane.showMessageDialog(this, "Tài khoản ĐANG ĐƯỢC SỬ DỤNG tại PC " + String.format("%02d", stuckPcId) + "!", "Bảo mật", JOptionPane.ERROR_MESSAGE);
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
        lblRealTime.setForeground(TEXT_MUTED);
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

        JPanel sidebar = new JPanel(new GridLayout(0, 1, 0, 10)); // Fixed: Gridlayout(0, 1) không giới hạn hàng
        sidebar.setBackground(new Color(15, 23, 42));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(51, 65, 85)));
        sidebar.setPreferredSize(new Dimension(250, 0)); // Nới rộng thêm không gian

        JButton btnHome = createMenuButton("[❖] TRANG CHỦ");
        JButton btnFoodMenu = createMenuButton("[♨] GỌI MÓN (FOOD)");
        JButton btnChat = createMenuButton("[💬] NHẮN TIN ADMIN");
        btnChat.setForeground(new Color(52, 211, 153));
        JButton btnTopup = createMenuButton("[+$] NẠP TIỀN");
        JButton btnAchievement = createMenuButton("[★] THÀNH TÍCH");
        JButton btnPromo = createMenuButton("[✦] KHUYẾN MÃI");
        JButton btnSettings = createMenuButton("[≡] CÀI ĐẶT");

        JButton btnLock = createMenuButton("[✖] KHÓA MÁY (AFK)");
        btnLock.setForeground(new Color(245, 158, 11));
        btnLock.addActionListener(e -> {
            lblLockUser.setText("TÀI KHOẢN: " + currentUsername.toUpperCase() + " ĐANG ĐI VẮNG");
            cardLayout.show(mainContainer, "LOCK_SCREEN");
        });

        JButton btnLogout = createMenuButton("[⏏] ĐĂNG XUẤT");
        btnLogout.setForeground(NEON_PINK);
        btnLogout.addActionListener(e -> {
            if (sessionTimer != null) sessionTimer.stop();
            String bill = sessionDAO.checkoutAndGenerateBill(currentMachineId);
            JOptionPane.showMessageDialog(this, bill, "HÓA ĐƠN", JOptionPane.INFORMATION_MESSAGE);
            currentMachineId = -1; currentUsername = null; currentCustomerId = -1;
            cardLayout.show(mainContainer, "LOGIN");
        });

        sidebar.add(btnHome);
        sidebar.add(btnFoodMenu);
        sidebar.add(btnChat);
        sidebar.add(btnTopup);
        sidebar.add(btnAchievement);
        sidebar.add(btnPromo);
        sidebar.add(btnSettings);
        sidebar.add(btnLock);
        sidebar.add(btnLogout);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(51, 65, 85)));
        header.setPreferredSize(new Dimension(0, 80));

        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setOpaque(false);
        userInfo.setBorder(new EmptyBorder(15, 25, 10, 20));
        lblUser = new JLabel("PLAYER: CHƯA ĐĂNG NHẬP");
        lblUser.setFont(new Font("Consolas", Font.BOLD, 18));
        lblUser.setForeground(TEXT_PRIMARY);
        lblBalance = new JLabel("SỐ DƯ: 0 VNĐ | HẠNG: DIAMOND");
        lblBalance.setForeground(NEON_CYAN);
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

        btnHome.addActionListener(e -> switchPanel(new cybercafe.view.panels.HomePanel()));
        btnFoodMenu.addActionListener(e -> switchPanel(new cybercafe.view.panels.FoodOrderPanel(currentMachineId)));
        btnChat.addActionListener(e -> switchPanel(new cybercafe.view.panels.ClientChatPanel(currentMachineId)));
        btnAchievement.addActionListener(e -> switchPanel(new cybercafe.view.panels.AchievementPanel(currentCustomerId)));
        btnTopup.addActionListener(e -> switchPanel(new cybercafe.view.panels.TopupPanel(currentUsername)));
        btnPromo.addActionListener(e -> switchPanel(new cybercafe.view.panels.PromoPanel()));
        btnSettings.addActionListener(e -> switchPanel(new cybercafe.view.panels.SettingsPanel(currentCustomerId)));

        dashboardPanel.add(sidebar, BorderLayout.WEST);
        dashboardPanel.add(header, BorderLayout.NORTH);
        dashboardPanel.add(dashboardContent, BorderLayout.CENTER);

        return dashboardPanel;
    }

    private void switchPanel(JPanel panel) {
        dashboardContent.removeAll();
        dashboardContent.add(panel);
        dashboardContent.revalidate();
        dashboardContent.repaint();
    }

    // =========================================================
    // MÀN HÌNH 3: LOCK SCREEN
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(NEON_PINK);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 16, 16);
            }
        };
        box.setOpaque(false);
        box.setBorder(new EmptyBorder(30, 40, 30, 40));
        box.setPreferredSize(new Dimension(450, 350));

        JLabel lblIcon = new JLabel("[ ✖ _ ✖ ]", SwingConstants.CENTER);
        lblIcon.setForeground(NEON_PINK);
        lblIcon.setFont(new Font("Consolas", Font.BOLD, 40));

        JLabel lblTitle = new JLabel("MÁY TRẠM ĐÃ BỊ KHÓA", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(TEXT_PRIMARY);

        lblLockUser = new JLabel("TÀI KHOẢN: ... ĐANG ĐI VẮNG", SwingConstants.CENTER);
        lblLockUser.setFont(new Font("Consolas", Font.PLAIN, 15));
        lblLockUser.setForeground(TEXT_MUTED);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setHorizontalAlignment(SwingConstants.CENTER);
        txtPass.setBackground(BG_DARK);
        txtPass.setForeground(TEXT_PRIMARY);
        txtPass.setCaretColor(TEXT_PRIMARY);
        txtPass.setFont(new Font("Consolas", Font.PLAIN, 18));
        txtPass.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(NEON_PINK), new EmptyBorder(8, 12, 8, 12)));

        JButton btnUnlock = new JButton("MỞ KHÓA MÀN HÌNH");
        btnUnlock.setBackground(NEON_PINK);
        btnUnlock.setForeground(BG_DARK);
        btnUnlock.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnUnlock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUnlock.setFocusPainted(false);

        btnUnlock.addActionListener(e -> {
            String p = new String(txtPass.getPassword());
            if (customerDAO.authenticateCustomer(currentUsername, p) > 0) {
                txtPass.setText("");
                cardLayout.show(mainContainer, "DASHBOARD");
            } else {
                JOptionPane.showMessageDialog(lockPanel, "Sai mật khẩu! Không thể mở khóa.", "Cảnh báo", JOptionPane.ERROR_MESSAGE);
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

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_MUTED);
        btn.setBackground(new Color(15, 23, 42));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 25, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(PANEL_BG);
                if (btn.getForeground() == TEXT_MUTED) btn.setForeground(TEXT_PRIMARY);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(15, 23, 42));
                if (btn.getForeground() == TEXT_PRIMARY && !text.contains("ĐĂNG XUẤT") && !text.contains("KHÓA MÁY")) {
                    btn.setForeground(TEXT_MUTED);
                }
            }
        });
        return btn;
    }

    private JTextField createStyledTextField() {
        JTextField txt = new JTextField();
        txt.setBackground(BG_DARK);
        txt.setForeground(TEXT_PRIMARY);
        txt.setCaretColor(TEXT_PRIMARY);
        txt.setFont(new Font("Consolas", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)), new EmptyBorder(8, 12, 8, 12)));
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