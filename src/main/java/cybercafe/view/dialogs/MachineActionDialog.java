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

    public MachineActionDialog(Frame parent, Machine machine) {
        super(parent, "QUẢN LÝ PC " + String.format("%02d", machine.getId()), true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(15, 18, 25));

        JPanel panel = new JPanel(new GridLayout(4, 1, 15, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("TRẠM PC " + machine.getId() + " - " + machine.getStatus(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        if ("IN_USE".equals(machine.getStatus())) lblTitle.setForeground(new Color(255, 50, 50));
        else if ("MAINTENANCE".equals(machine.getStatus())) lblTitle.setForeground(new Color(255, 165, 0));
        else lblTitle.setForeground(new Color(0, 245, 255));
        panel.add(lblTitle);

        // Nút 1: Đóng Máy (Thanh toán)
        JButton btnCheckout = new JButton("🛑 TẮT MÁY & THANH TOÁN (ÉP TẮT)");
        btnCheckout.setBackground(new Color(220, 20, 60));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckout.setEnabled("IN_USE".equals(machine.getStatus())); // Chỉ bật khi máy đang có người chơi

        btnCheckout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn ép tắt PC " + machine.getId() + " không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String bill = sessionDAO.checkoutAndGenerateBill(machine.getId());
                if (bill != null) {
                    JOptionPane.showMessageDialog(this, bill, "HÓA ĐƠN THANH TOÁN", JOptionPane.INFORMATION_MESSAGE);
                    actionSuccess = true;
                    dispose();
                }
            }
        });
        panel.add(btnCheckout);

        // Nút 2: Bật / Tắt Chế Độ Bảo Trì
        JButton btnMaintenance = new JButton();
        btnMaintenance.setFont(new Font("Segoe UI", Font.BOLD, 14));

        if ("MAINTENANCE".equals(machine.getStatus())) {
            btnMaintenance.setText("✅ HOÀN TẤT BẢO TRÌ (MỞ LẠI MÁY)");
            btnMaintenance.setBackground(new Color(50, 205, 50));
            btnMaintenance.setForeground(Color.BLACK);
        } else {
            btnMaintenance.setText("🛠️ ĐƯA MÁY VÀO TRẠNG THÁI BẢO TRÌ");
            btnMaintenance.setBackground(new Color(255, 165, 0));
            btnMaintenance.setForeground(Color.BLACK);
            // Không cho đem đi bảo trì nếu đang có khách ngồi chơi
            if ("IN_USE".equals(machine.getStatus())) {
                btnMaintenance.setEnabled(false);
            }
        }

        btnMaintenance.addActionListener(e -> {
            String newStatus = "MAINTENANCE".equals(machine.getStatus()) ? "AVAILABLE" : "MAINTENANCE";
            if (machineDAO.updateMachineStatus(machine.getId(), newStatus)) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái máy thành: " + newStatus);
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