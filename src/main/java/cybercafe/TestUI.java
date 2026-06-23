package cybercafe;

import cybercafe.view.panels.DashboardPanel;
import javax.swing.*;

public class TestUI {
    public static void main(String[] args) {
        // Kích hoạt chế độ vẽ font chữ mượt của Java
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CyberCafe Premium 2026");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720); // Chuẩn HD
            frame.setLocationRelativeTo(null); // Giữa màn hình

            // Gắn Dashboard vào khung
            frame.add(new DashboardPanel());

            frame.setVisible(true);
        });
    }
}