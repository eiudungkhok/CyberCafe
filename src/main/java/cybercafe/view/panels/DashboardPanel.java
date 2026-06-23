package cybercafe.view.panels;

import cybercafe.dao.MachineDAO;
import cybercafe.model.Machine;
import cybercafe.view.components.MachineCard;
import cybercafe.view.dialogs.CustomerManagerDialog;
import cybercafe.view.dialogs.MachineActionDialog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private MachineDAO machineDAO;
    private JPanel gridPanel;
    private Timer autoSyncTimer;

    public DashboardPanel() {
        machineDAO = new MachineDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(15, 18, 25));

        // ====== 1. HEADER ======
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 18, 25));

        JLabel lblTitle = new JLabel("CYBER CAFE DASHBOARD");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(0, 245, 255));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 0));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnManageMembers = new JButton("QUẢN LÝ HỘI VIÊN");
        btnManageMembers.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnManageMembers.setBackground(new Color(255, 215, 0));
        btnManageMembers.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnManageMembers.addActionListener(e -> {
            Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            new CustomerManagerDialog(topFrame).setVisible(true);
        });

        // ===== THÊM NÚT QUẢN LÝ BẾP =====
        JButton btnKitchen = new JButton("🍔 BẾP & DỊCH VỤ (0)");
        btnKitchen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKitchen.setBackground(new Color(255, 165, 0));
        btnKitchen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKitchen.addActionListener(e -> {
            Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            new cybercafe.view.dialogs.KitchenManagerDialog(topFrame).setVisible(true);
        });

        // Chỉ tạo btnPanel MỘT LẦN duy nhất
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 30));
        btnPanel.add(btnKitchen);
        btnPanel.add(btnManageMembers);
        headerPanel.add(btnPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ====== 2. LƯỚI HIỂN THỊ MÁY TRẠM ======
        gridPanel = new JPanel(new GridLayout(0, 5, 20, 20));
        gridPanel.setBackground(new Color(15, 18, 25));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        loadMachinesFromDB();

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(15, 18, 25));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // ====== 3. TRÁI TIM ĐỒNG BỘ REAL-TIME ======
        autoSyncTimer = new Timer(2000, e -> {
            loadMachinesFromDB();

            // Cập nhật số lượng đơn chờ trên nút Bếp liên tục mỗi 2s
            cybercafe.dao.OrderDAO orderDAO = new cybercafe.dao.OrderDAO();
            int pending = orderDAO.countPendingOrders();
            if (pending > 0) {
                btnKitchen.setText("🔔 BẾP CÓ ĐƠN (" + pending + ")");
                btnKitchen.setBackground(new Color(255, 50, 50));
                btnKitchen.setForeground(Color.WHITE);
            } else {
                btnKitchen.setText("🍔 BẾP & DỊCH VỤ");
                btnKitchen.setBackground(new Color(255, 165, 0));
                btnKitchen.setForeground(Color.BLACK);
            }
        });
        autoSyncTimer.start();
    }

    private void loadMachinesFromDB() {
        gridPanel.removeAll();
        List<Machine> machines = machineDAO.getAllMachines();

        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);

        for (Machine machine : machines) {
            MachineCard card = new MachineCard(machine);

            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    MachineActionDialog dialog = new MachineActionDialog(topFrame, machine);
                    dialog.setVisible(true);
                    if (dialog.isActionSuccess()) {
                        loadMachinesFromDB();
                    }
                }
            });

            gridPanel.add(card);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
}