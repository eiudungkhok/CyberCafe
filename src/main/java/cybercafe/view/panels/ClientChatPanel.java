package cybercafe.view.panels;

import cybercafe.dao.ChatDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ClientChatPanel extends JPanel {
    private int machineId;
    private ChatDAO chatDAO = new ChatDAO();
    private JTextArea txtHistory;
    private JTextField txtInput;
    private Timer refreshTimer;

    // 🎨 Màu sắc chuẩn Modern Cyber-Slate
    private final Color BG_DARK = new Color(15, 23, 42);
    private final Color PANEL_BG = new Color(30, 41, 59);
    private final Color NEON_CYAN = new Color(34, 211, 238);
    private final Color TEXT_PRIMARY = new Color(241, 245, 249);

    public ClientChatPanel(int machineId) {
        this.machineId = machineId;
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("[ 💬 ] KÊNH LIÊN LẠC HỖ TRỢ (ADMIN)", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(NEON_CYAN);
        add(lblTitle, BorderLayout.NORTH);

        // Khung hiển thị tin nhắn
        txtHistory = new JTextArea();
        txtHistory.setEditable(false);
        txtHistory.setBackground(PANEL_BG);
        txtHistory.setForeground(TEXT_PRIMARY);
        txtHistory.setFont(new Font("Consolas", Font.PLAIN, 15));
        txtHistory.setLineWrap(true);
        txtHistory.setWrapStyleWord(true);
        txtHistory.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollHistory = new JScrollPane(txtHistory);
        scrollHistory.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)));
        add(scrollHistory, BorderLayout.CENTER);

        // Khung nhập tin nhắn
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);

        txtInput = new JTextField();
        txtInput.setBackground(PANEL_BG);
        txtInput.setForeground(TEXT_PRIMARY);
        txtInput.setCaretColor(TEXT_PRIMARY);
        txtInput.setFont(new Font("Consolas", Font.PLAIN, 15));
        txtInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105)),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // Cho phép ấn Enter để gửi tin nhắn luôn cho nhanh
        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) sendMessage();
            }
        });

        JButton btnSend = new JButton("[ ➤ ] GỬI TIN");
        btnSend.setBackground(NEON_CYAN);
        btnSend.setForeground(BG_DARK);
        btnSend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSend.setFocusPainted(false);
        btnSend.addActionListener(e -> sendMessage());

        inputPanel.add(txtInput, BorderLayout.CENTER);
        inputPanel.add(btnSend, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // Auto-refresh: Tự động quét DB lấy tin nhắn mới mỗi 2 giây
        refreshTimer = new Timer(2000, e -> loadHistory());
        refreshTimer.start();
        loadHistory();
    }

    private void sendMessage() {
        String msg = txtInput.getText().trim();
        if (!msg.isEmpty()) {
            if (chatDAO.sendMessage(machineId, "CLIENT", msg)) {
                txtInput.setText("");
                loadHistory();
            }
        }
    }

    private void loadHistory() {
        String newHistory = chatDAO.getChatHistory(machineId);
        // Chỉ cập nhật giao diện nếu có tin nhắn mới, tránh bị giật màn hình
        if (!txtHistory.getText().equals(newHistory)) {
            txtHistory.setText(newHistory);
            txtHistory.setCaretPosition(txtHistory.getDocument().getLength()); // Tự động cuộn xuống đáy
        }
    }
}