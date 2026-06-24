package cybercafe.view.components;

import cybercafe.model.Machine;
import cybercafe.view.dialogs.MachineActionDialog;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MachineCard extends JPanel {
    private Machine machine;

    // 🎨 Màu Cyber-Slate êm mắt
    private final Color PANEL_BG = new Color(30, 41, 59);
    private final Color TEXT_PRIMARY = new Color(241, 245, 249);

    public MachineCard(Machine machine, Runnable onActionCompleted) {
        this.machine = machine;
        setLayout(new BorderLayout());
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color statusColor;
        String statusText;
        String icon = "[ 💻 ]";

        if ("IN_USE".equals(machine.getStatus())) {
            statusColor = new Color(239, 68, 68); // Đỏ Rose Pastel
            statusText = "ĐANG CHƠI";
        } else if ("MAINTENANCE".equals(machine.getStatus())) {
            statusColor = new Color(245, 158, 11); // Cam Amber
            statusText = "BẢO TRÌ";
            icon = "[ ⚙ ]";
        } else {
            statusColor = new Color(34, 211, 238); // Cyan dịu
            statusText = "SẴN SÀNG";
        }

        setBackground(PANEL_BG);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(statusColor, 2),
                new EmptyBorder(15, 10, 15, 10)
        ));

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Consolas", Font.BOLD, 30));
        lblIcon.setForeground(statusColor);

        JLabel lblName = new JLabel("PC - " + String.format("%02d", machine.getId()), SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(TEXT_PRIMARY);

        JLabel lblStatus = new JLabel(statusText, SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(statusColor);

        add(lblIcon, BorderLayout.NORTH);
        add(lblName, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        // Hover Effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MachineActionDialog dialog = new MachineActionDialog((Frame) SwingUtilities.getWindowAncestor(MachineCard.this), machine);
                dialog.setVisible(true);
                if (dialog.isActionSuccess() && onActionCompleted != null) {
                    onActionCompleted.run();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) { setBackground(new Color(51, 65, 85)); }
            @Override
            public void mouseExited(MouseEvent e) { setBackground(PANEL_BG); }
        });
    }
}