package cybercafe.view.panels;

import cybercafe.dao.ChatDAO;
import cybercafe.dao.MachineDAO;
import cybercafe.model.Machine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class AdminChatPanel extends JPanel {
    private ChatDAO chatDAO = new ChatDAO();
    private MachineDAO machineDAO = new MachineDAO();
    private int selectedMachineId = -1;

    private JTextArea txtHistory;
    private JTextField txtInput;
    private JLabel lblChatTitle;
    private JPanel machineListPanel;
    private Timer refreshTimer;

    private final Color BG_DARK = new Color(15, 23, 42);
    private final Color PANEL_BG = new Color(30, 41, 59);
    private final Color NEON_CYAN = new Color(34, 211, 238);
    private final Color TEXT_PRIMARY = new Color(241, 245, 249);

    public AdminChatPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // -- Cột Trái: Danh sách máy PC --
        machineListPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        machineListPanel.setBackground(PANEL_BG);
        JScrollPane scrollMachines = new JScrollPane(machineListPanel);
        scrollMachines.setPreferredSize(new Dimension(150, 0));
        scrollMachines.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)), "CHỌN PC", 0, 0, new Font("Segoe UI", Font.BOLD, 12), TEXT_PRIMARY));
        loadMachineList();

        // -- Cột Phải: Khung Chat --
        JPanel chatArea = new JPanel(new BorderLayout(10, 10));
        chatArea.setOpaque(false);

        lblChatTitle = new JLabel("CHƯA CHỌN MÁY ĐỂ CHAT", SwingConstants.CENTER);
        lblChatTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblChatTitle.setForeground(NEON_CYAN);
        chatArea.add(lblChatTitle, BorderLayout.NORTH);

        txtHistory = new JTextArea();
        txtHistory.setEditable(false);
        txtHistory.setBackground(PANEL_BG);
        txtHistory.setForeground(TEXT_PRIMARY);
        txtHistory.setFont(new Font("Consolas", Font.PLAIN, 15));
        txtHistory.setLineWrap(true);
        txtHistory.setWrapStyleWord(true);
        JScrollPane scrollHistory = new JScrollPane(txtHistory);
        scrollHistory.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)));
        chatArea.add(scrollHistory, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setOpaque(false);
        txtInput = new JTextField();
        txtInput.setBackground(PANEL_BG);
        txtInput.setForeground(TEXT_PRIMARY);
        txtInput.setCaretColor(TEXT_PRIMARY);
        txtInput.setFont(new Font("Consolas", Font.PLAIN, 15));
        txtInput.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)), new EmptyBorder(10, 15, 10, 15)));
        txtInput.setEnabled(false);

        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) sendMessage();
            }
        });

        JButton btnSend = new JButton("[ ➤ ] PHẢN HỒI");
        btnSend.setBackground(NEON_CYAN);
        btnSend.setForeground(BG_DARK);
        btnSend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSend.setFocusPainted(false);
        btnSend.setEnabled(false);
        btnSend.addActionListener(e -> sendMessage());

        inputPanel.add(txtInput, BorderLayout.CENTER);
        inputPanel.add(btnSend, BorderLayout.EAST);
        chatArea.add(inputPanel, BorderLayout.SOUTH);

        add(scrollMachines, BorderLayout.WEST);
        add(chatArea, BorderLayout.CENTER);

        // Tự động quét tin nhắn mới từ Khách
        refreshTimer = new Timer(2000, e -> { if (selectedMachineId != -1) loadHistory(); });
        refreshTimer.start();
    }

    private void loadMachineList() {
        machineListPanel.removeAll();
        List<Machine> machines = machineDAO.getAllMachines();
        for (Machine m : machines) {
            JButton btn = new JButton("PC " + String.format("%02d", m.getId()));
            btn.setBackground(BG_DARK);
            btn.setForeground(TEXT_PRIMARY);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Consolas", Font.BOLD, 14));
            btn.addActionListener(e -> {
                selectedMachineId = m.getId();
                lblChatTitle.setText("ĐANG CHAT VỚI PC " + String.format("%02d", m.getId()));
                txtInput.setEnabled(true);
                ((JButton)((JPanel)txtInput.getParent()).getComponent(1)).setEnabled(true);
                loadHistory();
            });
            machineListPanel.add(btn);
        }
        machineListPanel.revalidate();
        machineListPanel.repaint();
    }

    private void sendMessage() {
        if (selectedMachineId == -1) return;
        String msg = txtInput.getText().trim();
        if (!msg.isEmpty()) {
            if (chatDAO.sendMessage(selectedMachineId, "ADMIN", msg)) {
                txtInput.setText("");
                loadHistory();
            }
        }
    }

    private void loadHistory() {
        String newHistory = chatDAO.getChatHistory(selectedMachineId);
        if (!txtHistory.getText().equals(newHistory)) {
            txtHistory.setText(newHistory);
            txtHistory.setCaretPosition(txtHistory.getDocument().getLength());
        }
    }
}