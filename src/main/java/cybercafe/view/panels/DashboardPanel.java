package cybercafe.view.panels;

import cybercafe.dao.MachineDAO;
import cybercafe.model.Machine;
import cybercafe.view.components.MachineCard;
import cybercafe.view.dialogs.KitchenManagerDialog;
import cybercafe.view.dialogs.CustomerManagerDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private MachineDAO machineDAO = new MachineDAO();
    private JPanel gridPanel;
    private Timer refreshTimer;

    private final Color BG_DARK = new Color(15, 23, 42);
    private final Color PANEL_BG = new Color(30, 41, 59);
    private final Color NEON_CYAN = new Color(34, 211, 238);
    private final Color TEXT_PRIMARY = new Color(241, 245, 249);

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // Tạo 2 Tab Chính: Sơ đồ máy & Khung Chat
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(PANEL_BG);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBorder(null);

        // =====================================
        // TAB 1: SƠ ĐỒ MÁY TRẠM
        // =====================================
        JPanel machineTab = new JPanel(new BorderLayout());
        machineTab.setBackground(BG_DARK);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("NEON ESPORTS - TRUNG TÂM ĐIỀU KHIỂN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(NEON_CYAN);

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolBar.setOpaque(false);

        JButton btnKitchen = new JButton("[ ♨ ] BẾP & DỊCH VỤ");
        btnKitchen.setBackground(new Color(245, 158, 11)); // Cam dịu
        btnKitchen.setForeground(BG_DARK);
        btnKitchen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKitchen.setFocusPainted(false);
        btnKitchen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKitchen.addActionListener(e -> new KitchenManagerDialog((Frame) SwingUtilities.getWindowAncestor(this)).setVisible(true));

        JButton btnCustomer = new JButton("[ ❖ ] QUẢN LÝ HỘI VIÊN");
        btnCustomer.setBackground(NEON_CYAN);
        btnCustomer.setForeground(BG_DARK);
        btnCustomer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCustomer.setFocusPainted(false);
        btnCustomer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCustomer.addActionListener(e -> new CustomerManagerDialog((Frame) SwingUtilities.getWindowAncestor(this)).setVisible(true));

        toolBar.add(btnKitchen);
        toolBar.add(btnCustomer);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(toolBar, BorderLayout.EAST);

        gridPanel = new JPanel(new GridLayout(0, 5, 15, 15));
        gridPanel.setBackground(BG_DARK);
        gridPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        loadMachines();

        machineTab.add(topBar, BorderLayout.NORTH);
        machineTab.add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        // =====================================
        // TAB 2: TỔNG ĐÀI TIN NHẮN (CHAT VỚI KHÁCH)
        // =====================================

        AdminChatPanel chatPanel = new AdminChatPanel();
        AdminStatsPanel statsPanel = new AdminStatsPanel(); // Khởi tạo Tab Thống Kê

        tabbedPane.addTab("[ ☷ ] SƠ ĐỒ PHÒNG MÁY ", machineTab);
        tabbedPane.addTab("[ 💬 ] TỔNG ĐÀI HỖ TRỢ ", chatPanel);
        tabbedPane.addTab("[ 📊 ] BÁO CÁO & NHẬT KÝ ", statsPanel); // Thêm Tab thứ 3 này



        add(tabbedPane, BorderLayout.CENTER);

        // Auto refresh trạng thái máy
        refreshTimer = new Timer(3000, e -> loadMachines());
        refreshTimer.start();
    }

    private void loadMachines() {
        gridPanel.removeAll();
        List<Machine> machines = machineDAO.getAllMachines();
        for (Machine machine : machines) {
            gridPanel.add(new MachineCard(machine, this::loadMachines));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }
}