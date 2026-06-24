package cybercafe.view.dialogs;

import cybercafe.dao.OrderDAO;
import cybercafe.model.Order;
import cybercafe.model.MenuItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KitchenManagerDialog extends JDialog {
    private OrderDAO orderDAO = new OrderDAO();
    private DefaultTableModel orderTableModel;
    private DefaultTableModel menuTableModel;
    private JTable orderTable;
    private JTable menuTable;
    private Timer autoRefreshTimer;
    private int selectedMenuId = -1;

    // 🎨 BẢNG MÀU UI/UX HIỆN ĐẠI (Đồng bộ với Client)
    private final Color BG_DARK = new Color(15, 23, 42);
    private final Color PANEL_BG = new Color(30, 41, 59);
    private final Color NEON_CYAN = new Color(34, 211, 238);
    private final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private final Color TEXT_MUTED = new Color(148, 163, 184);

    // Màu nút bấm chuẩn UI
    private final Color BTN_SUCCESS = new Color(16, 185, 129); // Xanh Emerald
    private final Color BTN_WARNING = new Color(245, 158, 11); // Cam Amber
    private final Color BTN_DANGER = new Color(239, 68, 68);   // Đỏ Rose
    private final Color BTN_INFO = new Color(14, 165, 233);    // Xanh dương nhạt

    public KitchenManagerDialog(Frame parent) {
        super(parent, "[ ♨ ] TRUNG TÂM QUẢN LÝ BẾP & THỰC ĐƠN", true);
        setSize(950, 580);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BG_DARK);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(PANEL_BG);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBorder(null);

        // =========================================================
        // TAB 1: QUẢN LÝ ĐƠN HÀNG GỘP (LIVE)
        // =========================================================
        JPanel ordersPanel = new JPanel(new BorderLayout(10, 10));
        ordersPanel.setBackground(BG_DARK);
        ordersPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] orderCols = {"Máy PC", "Khách hàng", "Chi tiết các món ăn", "Tổng SL", "Tổng Tiền Thu", "Thời gian đặt"};
        orderTableModel = new DefaultTableModel(orderCols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        orderTable = new JTable(orderTableModel);
        styleTable(orderTable);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(350);

        refreshOrderTable();

        JButton btnComplete = new JButton("[ ✔ ] XÁC NHẬN GIAO ĐỒ & XUẤT HÓA ĐƠN GỘP");
        btnComplete.setBackground(BTN_SUCCESS);
        btnComplete.setForeground(BG_DARK); // Chữ tối trên nền sáng
        btnComplete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnComplete.setPreferredSize(new Dimension(0, 45));
        btnComplete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComplete.setFocusPainted(false);

        btnComplete.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 đơn gộp của khách để xử lý!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int pcId = (int) orderTableModel.getValueAt(selectedRow, 0);
            String customer = (String) orderTableModel.getValueAt(selectedRow, 1);
            String items = (String) orderTableModel.getValueAt(selectedRow, 2);
            int total = (int) orderTableModel.getValueAt(selectedRow, 4);

            if (orderDAO.completeOrderByMachine(pcId)) {
                String receipt = "🧾 HÓA ĐƠN DỊCH VỤ ẨM THỰC (GỘP)\n" +
                        "--------------------------------------------------\n" +
                        "💻 Giao đến: PC " + String.format("%02d", pcId) + " (Khách: " + customer + ")\n" +
                        "🍔 Các món: " + items + "\n" +
                        "💰 TỔNG CẦN THU: " + total + " VNĐ\n" +
                        "--------------------------------------------------\n" +
                        "(Thu tiền mặt tại quầy, không trừ tài khoản máy)";
                JOptionPane.showMessageDialog(this, receipt, "Bưng đồ & Thu tiền", JOptionPane.INFORMATION_MESSAGE);
                refreshOrderTable();
            }
        });

        ordersPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        ordersPanel.add(btnComplete, BorderLayout.SOUTH);

        // =========================================================
        // TAB 2: QUẢN LÝ THỰC ĐƠN KHO
        // =========================================================
        JPanel menuPanel = new JPanel(new BorderLayout(15, 15));
        menuPanel.setBackground(BG_DARK);
        menuPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] menuCols = {"ID Món", "Tên Món Ăn", "Đơn Giá (VNĐ)", "Phân Loại Danh Mục"};
        menuTableModel = new DefaultTableModel(menuCols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        menuTable = new JTable(menuTableModel);
        styleTable(menuTable);
        refreshMenuTable();

        JPanel formContainer = new JPanel(new BorderLayout(10, 15));
        formContainer.setOpaque(false);

        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        inputPanel.setOpaque(false);

        JTextField txtName = createStyledTextField();
        JTextField txtPrice = createStyledTextField();
        JComboBox<String> cbCategory = new JComboBox<>(new String[]{"Food", "Drink", "Snack"});
        cbCategory.setBackground(BG_DARK);
        cbCategory.setForeground(TEXT_PRIMARY);
        cbCategory.setFont(new Font("Consolas", Font.PLAIN, 14));

        JLabel l1 = new JLabel("Tên món:", SwingConstants.RIGHT); l1.setForeground(TEXT_MUTED);
        JLabel l2 = new JLabel("Giá tiền (đ):", SwingConstants.RIGHT); l2.setForeground(TEXT_MUTED);
        JLabel l3 = new JLabel("Danh mục:", SwingConstants.RIGHT); l3.setForeground(TEXT_MUTED);

        inputPanel.add(l1); inputPanel.add(txtName);
        inputPanel.add(l2); inputPanel.add(txtPrice);
        inputPanel.add(l3); inputPanel.add(cbCategory);

        JButton btnClear = new JButton("[ ↺ ] LÀM MỚI FORM");
        btnClear.setBackground(new Color(71, 85, 105));
        btnClear.setForeground(TEXT_PRIMARY);
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        inputPanel.add(new JLabel()); inputPanel.add(btnClear);

        menuTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = menuTable.getSelectedRow();
                if (row != -1) {
                    selectedMenuId = (int) menuTableModel.getValueAt(row, 0);
                    txtName.setText(menuTableModel.getValueAt(row, 1).toString());
                    txtPrice.setText(menuTableModel.getValueAt(row, 2).toString());
                    cbCategory.setSelectedItem(menuTableModel.getValueAt(row, 3).toString());
                }
            }
        });

        btnClear.addActionListener(e -> {
            txtName.setText(""); txtPrice.setText(""); selectedMenuId = -1;
        });

        JPanel actionBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionBtnPanel.setOpaque(false);

        // Thay bằng Ký tự ASCII
        JButton btnAdd = new JButton("[ + ] THÊM MỚI");
        btnAdd.setBackground(BTN_INFO); btnAdd.setForeground(BG_DARK); btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnAdd.setFocusPainted(false);

        JButton btnEdit = new JButton("[ ⚙ ] CẬP NHẬT");
        btnEdit.setBackground(BTN_WARNING); btnEdit.setForeground(BG_DARK); btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnEdit.setFocusPainted(false);

        JButton btnDel = new JButton("[ ✖ ] XÓA MÓN");
        btnDel.setBackground(BTN_DANGER); btnDel.setForeground(TEXT_PRIMARY); btnDel.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnDel.setFocusPainted(false);

        actionBtnPanel.add(btnAdd); actionBtnPanel.add(btnEdit); actionBtnPanel.add(btnDel);
        formContainer.add(inputPanel, BorderLayout.CENTER);
        formContainer.add(actionBtnPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            if (orderDAO.addMenuItem(txtName.getText().trim(), Integer.parseInt(txtPrice.getText().trim()), cbCategory.getSelectedItem().toString())) {
                btnClear.doClick(); refreshMenuTable();
            }
        });

        btnEdit.addActionListener(e -> {
            if (orderDAO.updateMenuItem(selectedMenuId, txtName.getText().trim(), Integer.parseInt(txtPrice.getText().trim()), cbCategory.getSelectedItem().toString())) {
                btnClear.doClick(); refreshMenuTable();
            }
        });

        btnDel.addActionListener(e -> {
            if (orderDAO.deleteMenuItemById(selectedMenuId)) {
                btnClear.doClick(); refreshMenuTable();
            }
        });

        menuPanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);
        menuPanel.add(formContainer, BorderLayout.SOUTH);

        // Thay Tên Tab bằng Ký tự ASCII
        tabbedPane.addTab("[ ▤ ] ĐƠN CHỜ (LIVE) ", ordersPanel);
        tabbedPane.addTab("[ ☷ ] QUẢN LÝ THỰC ĐƠN ", menuPanel);
        add(tabbedPane);

        autoRefreshTimer = new Timer(3000, e -> refreshOrderTable());
        autoRefreshTimer.start();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { autoRefreshTimer.stop(); }
        });
    }

    private void refreshOrderTable() {
        orderTableModel.setRowCount(0);
        List<Order> orders = orderDAO.getPendingOrders();
        for (Order o : orders) {
            orderTableModel.addRow(new Object[]{
                    o.getMachineId(), o.getCustomerName(), o.getItemName(),
                    o.getQuantity(), o.getTotalCost(), o.getOrderTime()
            });
        }
    }

    private void refreshMenuTable() {
        menuTableModel.setRowCount(0);
        List<MenuItem> items = orderDAO.getAllMenuItemsFull();
        for (MenuItem item : items) {
            menuTableModel.addRow(new Object[]{item.getId(), item.getName(), item.getPrice(), item.getCategory()});
        }
    }

    // Hàm style bảng êm mắt, hiện đại
    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setBackground(BG_DARK);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(new Color(51, 65, 85)); // Màu viền mờ ảo

        // Header không còn màu Cyan chói lóa nữa
        table.getTableHeader().setBackground(new Color(51, 65, 85));
        table.getTableHeader().setForeground(NEON_CYAN);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        table.setSelectionBackground(new Color(2, 132, 199)); // Xanh dương chọn dòng
        table.setSelectionForeground(Color.WHITE);

        // Bỏ viền Focus khó chịu khi click vào ô
        table.setFocusable(false);
    }

    private JTextField createStyledTextField() {
        JTextField txt = new JTextField();
        txt.setBackground(BG_DARK);
        txt.setForeground(TEXT_PRIMARY);
        txt.setCaretColor(TEXT_PRIMARY);
        txt.setFont(new Font("Consolas", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)), new EmptyBorder(5, 10, 5, 10)));
        return txt;
    }
}