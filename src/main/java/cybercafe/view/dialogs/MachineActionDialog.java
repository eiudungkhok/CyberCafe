package cybercafe.view.dialogs;

import cybercafe.dao.MachineDAO;
import cybercafe.dao.GamingSessionDAO;
import cybercafe.model.Machine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MachineActionDialog extends JDialog {
    private boolean actionSuccess = false;
    private MachineDAO machineDAO = new MachineDAO();
    private GamingSessionDAO sessionDAO = new GamingSessionDAO();

    // 🎨 BẢNG MÀU ĐỒNG BỘ
    private final Color BG_DARK = new Color(15, 23, 42);
    private final Color PANEL_BG = new Color(30, 41, 59);
    private final Color TEXT_PRIMARY = new Color(241, 245, 249);
    private final Color BTN_DANGER = new Color(239, 68, 68);
    private final Color BTN_WARNING = new Color(245, 158, 11);
    private final Color BTN_SUCCESS = new Color(16, 185, 129);

    public MachineActionDialog(Frame parent, Machine machine) {
        super(parent, "[ ⚙ ] QUẢN LÝ PC " + String.format("%02d", machine.getId()), true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BG_DARK);

        JPanel panel = new JPanel(new GridLayout(4, 1, 15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("TRẠM PC " + machine.getId() + " - " + machine.getStatus(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        // Màu trạng thái êm mắt
        if ("IN_USE".equals(machine.getStatus())) lblTitle.setForeground(BTN_DANGER);
        else if ("MAINTENANCE".equals(machine.getStatus())) lblTitle.setForeground(BTN_WARNING);
        else lblTitle.setForeground(new Color(34, 211, 238)); // Cyan
        panel.add(lblTitle);

        // Nút 1: Đóng Máy (Thanh toán) - Thay Emoji bằng ASCII
        JButton btnCheckout = new JButton("[ ✖ ] ÉP TẮT & THANH TOÁN");
        btnCheckout.setBackground(BTN_DANGER);
        btnCheckout.setForeground(TEXT_PRIMARY);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckout.setFocusPainted(false);
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.setEnabled("IN_USE".equals(machine.getStatus()));

        btnCheckout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn ép tắt PC " + machine.getId() + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String bill = sessionDAO.checkoutAndGenerateBill(machine.getId());
                if (bill != null) {
                    JOptionPane.showMessageDialog(this, bill, "HÓA ĐƠN", JOptionPane.INFORMATION_MESSAGE);
                    actionSuccess = true;
                    dispose();
                }
            }
        });
        panel.add(btnCheckout);

        // Nút 2: Bật / Tắt Chế Độ Bảo Trì
        JButton btnMaintenance = new JButton();
        btnMaintenance.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMaintenance.setFocusPainted(false);
        btnMaintenance.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if ("MAINTENANCE".equals(machine.getStatus())) {
            btnMaintenance.setText("[ ✔ ] HOÀN TẤT BẢO TRÌ");
            btnMaintenance.setBackground(BTN_SUCCESS);
            btnMaintenance.setForeground(BG_DARK);
        } else {
            btnMaintenance.setText("[ ▤ ] ĐƯA VÀO BẢO TRÌ");
            btnMaintenance.setBackground(BTN_WARNING);
            btnMaintenance.setForeground(BG_DARK);
            if ("IN_USE".equals(machine.getStatus())) {
                btnMaintenance.setEnabled(false);
            }
        }

        btnMaintenance.addActionListener(e -> {
            String newStatus = "MAINTENANCE".equals(machine.getStatus()) ? "AVAILABLE" : "MAINTENANCE";
            if (machineDAO.updateMachineStatus(machine.getId(), newStatus)) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái máy: " + newStatus);
                actionSuccess = true;
                dispose();
            }
        });
        panel.add(btnMaintenance);

        add(panel);
    }

    public boolean isActionSuccess() {
        return actionSuccess;
    }
}