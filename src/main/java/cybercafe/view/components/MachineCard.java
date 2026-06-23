package cybercafe.view.components;

import cybercafe.model.Machine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MachineCard extends JPanel {
    private Machine machine;
    private float hue = 0.0f;
    private Timer rgbTimer;
    private Timer clockTimer;
    private boolean isHovered = false;

    public MachineCard(Machine machine) {
        this.machine = machine;
        setOpaque(false);
        setPreferredSize(new Dimension(200, 150));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        if ("IN_USE".equals(machine.getStatus())) {
            startRgbAnimation();
            clockTimer = new Timer(1000, e -> repaint());
            clockTimer.start();
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
        });
    }

    private void startRgbAnimation() {
        rgbTimer = new Timer(50, e -> {
            hue += 0.01f;
            if (hue > 1.0f) hue = 0.0f;
            repaint();
        });
        rgbTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(20, 25, 40, 220));
        g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 15, 15);

        Color borderColor;
        if ("IN_USE".equals(machine.getStatus())) {
            borderColor = Color.getHSBColor(hue, 1.0f, 1.0f);
        } else if ("AVAILABLE".equals(machine.getStatus())) {
            borderColor = new Color(0, 245, 255);
        } else {
            borderColor = new Color(100, 100, 100);
        }

        if (isHovered) {
            borderColor = borderColor.brighter();
            g2.setStroke(new BasicStroke(4f));
        } else {
            g2.setStroke(new BasicStroke(2f));
        }

        g2.setColor(borderColor);
        g2.drawRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 15, 15);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Consolas", Font.BOLD, 24));
        g2.drawString("PC " + String.format("%02d", machine.getId()), 20, 40);

        // ==== PHẦN LÕI: ĐẾM NGƯỢC THỜI GIAN NHƯ CLIENT ====
        if ("IN_USE".equals(machine.getStatus()) && machine.getStartTime() != null) {
            long totalMillisAvailable = (long) ((machine.getCustomerBalance() / (double) machine.getHourlyRate()) * 3600 * 1000);
            long elapsedMillis = System.currentTimeMillis() - machine.getStartTime().getTime();
            long remainMillis = totalMillisAvailable - elapsedMillis;

            if (remainMillis < 0) remainMillis = 0; // Đề phòng hiện số âm

            long h = remainMillis / (60 * 60 * 1000);
            long m = (remainMillis / (60 * 1000)) % 60;
            long s = (remainMillis / 1000) % 60;

            String timeString = String.format("%02d:%02d:%02d", h, m, s);

            g2.setFont(new Font("Consolas", Font.BOLD, 18));

            // Nếu dưới 5 phút thì chuyển chữ đỏ báo động
            if (remainMillis <= 5 * 60 * 1000) {
                g2.setColor(new Color(255, 50, 50));
            } else {
                g2.setColor(new Color(50, 205, 50));
            }

            // Đã xóa emoji gây lỗi ô vuông
            g2.drawString("TIME: " + timeString, 20, 85);

        } else {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(150, 150, 150));
            g2.drawString("Khu vuc: " + machine.getZoneType(), 20, 70);
            g2.drawString("Gia: " + (machine.getHourlyRate() / 1000) + "k/h", 20, 90);
        }

        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.setColor(borderColor);
        g2.drawString(machine.getStatus(), 20, 120);

        g2.dispose();
    }
}