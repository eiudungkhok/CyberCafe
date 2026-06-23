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

    public KitchenManagerDialog(Frame parent) {
        super(parent, "🔥 TRUNG TÂM QUẢN LÝ BẾP & THỰC ĐƠN ARENA", true);
        setSize(950, 550); // Mở rộng chiều ngang một chút để chứa chuỗi tên món dài
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(15, 18, 25));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(20, 25, 40));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // =========================================================
        // TAB 1: QUẢN LÝ ĐƠN HÀNG GỘP (LIVE)
        // =========================================================
        JPanel ordersPanel = new JPanel(new BorderLayout(10, 10));
        ordersPanel.setBackground(new Color(15, 18, 25));
        ordersPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Bỏ cột ID Đơn, thay bằng danh sách chi tiết các món
        String[] orderCols = {"Máy PC", "Khách hàng", "Chi tiết các món ăn", "Tổng SL", "Tổng Tiền Thu", "Thời gian đặt"};
        orderTableModel = new DefaultTableModel(orderCols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        orderTable = new JTable(orderTableModel);
        styleTable(orderTable);
        // Chỉnh cho cột "Chi tiết các món ăn" rộng ra
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(350);

        refreshOrderTable();

        JButton btnComplete = new JButton("XÁC NHẬN GIAO ĐỒ & XUẤT HÓA ĐƠN GỘP");
        btnComplete.setBackground(new Color(50, 205, 50));
        btnComplete.setForeground(Color.BLACK);
        btnComplete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnComplete.setPreferredSize(new Dimension(0, 40));
        btnComplete.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 đơn gộp của khách để xử lý!");
                return;
            }

            // Cột 0 bây giờ là Máy PC
            int pcId = (int) orderTableModel.getValueAt(selectedRow, 0);
            String customer = (String) orderTableModel.getValueAt(selectedRow, 1);
            String items = (String) orderTableModel.getValueAt(selectedRow, 2);
            int total = (int) orderTableModel.getValueAt(selectedRow, 4); // Cột Tổng tiền

            // Gọi hàm chốt đơn THEO MÁY
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
        JPanel menuPanel = new JPanel(new BorderLayout(10, 10));
        menuPanel.setBackground(new Color(15, 18, 25));
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] menuCols = {"ID Món", "Tên Món Ăn", "Đơn Giá (VNĐ)", "Phân Loại Danh Mục"};
        menuTableModel = new DefaultTableModel(menuCols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        menuTable = new JTable(menuTableModel);
        styleTable(menuTable);
        refreshMenuTable();

        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setOpaque(false);

        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setOpaque(false);
        JTextField txtName = new JTextField();
        JTextField txtPrice = new JTextField();
        JComboBox<String> cbCategory = new JComboBox<>(new String[]{"Food", "Drink", "Snack"});

        inputPanel.add(new JLabel("Tên món:", SwingConstants.RIGHT)); inputPanel.add(txtName);
        inputPanel.add(new JLabel("Giá tiền (đ):", SwingConstants.RIGHT)); inputPanel.add(txtPrice);
        inputPanel.add(new JLabel("Danh mục:", SwingConstants.RIGHT)); inputPanel.add(cbCategory);

        JButton btnClear = new JButton("LÀM MỚI FORM");
        btnClear.setBackground(new Color(100, 116, 139));
        btnClear.setForeground(Color.WHITE);
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

        JButton btnAdd = new JButton("➕ THÊM MÓN MỚI");
        btnAdd.setBackground(new Color(0, 191, 255)); btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton btnEdit = new JButton("⚙ CẬP NHẬT MÓN");
        btnEdit.setBackground(new Color(255, 165, 0)); btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton btnDel = new JButton("❌ XÓA MÓN");
        btnDel.setBackground(new Color(220, 20, 60)); btnDel.setForeground(Color.WHITE); btnDel.setFont(new Font("Segoe UI", Font.BOLD, 13));

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

        tabbedPane.addTab("🔔 ĐƠN CHỜ XỬ LÝ (LIVE)", ordersPanel);
        tabbedPane.addTab("📦 QUẢN LÝ THỰC ĐƠN TRONG KHO", menuPanel);
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
                    o.getMachineId(), o.getCustomerName(), o.getItemName(), // itemName giờ là chuỗi gom dài
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

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setBackground(new Color(30, 35, 50));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(0, 245, 255));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(new Color(139, 92, 246));
        table.setSelectionForeground(Color.WHITE);
    }
}