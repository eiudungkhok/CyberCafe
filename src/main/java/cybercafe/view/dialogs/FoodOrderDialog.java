package cybercafe.view.dialogs;

import cybercafe.dao.OrderDAO;
import cybercafe.model.MenuItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FoodOrderDialog extends JDialog {
    private OrderDAO orderDAO;

    public FoodOrderDialog(Frame parent, int machineId) {
        super(parent, "GỌI DỊCH VỤ - PC " + machineId, true);
        this.orderDAO = new OrderDAO();

        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(25, 30, 45));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setOpaque(false);

        JLabel lblItem = new JLabel("Chọn món:"); lblItem.setForeground(Color.WHITE);
        JComboBox<MenuItem> cbMenu = new JComboBox<>();
        List<MenuItem> items = orderDAO.getMenu();
        for (MenuItem item : items) { cbMenu.addItem(item); }

        JLabel lblQty = new JLabel("Số lượng:"); lblQty.setForeground(Color.WHITE);
        JSpinner spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1)); // Tối thiểu 1, tối đa 10

        formPanel.add(lblItem); formPanel.add(cbMenu);
        formPanel.add(lblQty); formPanel.add(spinQty);

        JButton btnOrder = new JButton("CHỐT ĐƠN");
        btnOrder.setBackground(new Color(255, 165, 0)); // Màu cam
        btnOrder.setForeground(Color.WHITE);
        btnOrder.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOrder.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnOrder.addActionListener(e -> {
            MenuItem selectedItem = (MenuItem) cbMenu.getSelectedItem();
            int qty = (Integer) spinQty.getValue();

            if (selectedItem != null) {
                if (orderDAO.placeOrder(machineId, selectedItem.getName(), qty, selectedItem.getPrice())) {
                    JOptionPane.showMessageDialog(this, "Đã gọi " + qty + " x " + selectedItem.getName() + "!\nNhà bếp đang chuẩn bị...", "Order Thành Công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Có lỗi xảy ra, vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(btnOrder, BorderLayout.SOUTH);
    }
}