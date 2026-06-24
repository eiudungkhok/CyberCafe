package cybercafe.view.panels;

import cybercafe.dao.StatsDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminStatsPanel extends JPanel {
    private StatsDAO statsDAO = new StatsDAO();
    private DefaultTableModel historyTableModel;
    private JTable historyTable;

    private final Color BG_DARK = new Color(15, 23, 42);
    private final Color PANEL_BG = new Color(30, 41, 59);
    private final Color TEXT_PRIMARY = new Color(241, 245, 249);

    public AdminStatsPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- KHU VỰC 1: DOANH THU (TRÊN CÙNG) ---
        JPanel revenuePanel = new JPanel(new GridLayout(1, 4, 15, 0));
        revenuePanel.setOpaque(false);
        revenuePanel.setPreferredSize(new Dimension(0, 100));

        revenuePanel.add(createStatCard("DOANH THU NGÀY", statsDAO.getRevenueByPeriod("DAY") + " VNĐ", "#22D3EE")); // Cyan
        revenuePanel.add(createStatCard("DOANH THU TUẦN", statsDAO.getRevenueByPeriod("WEEK") + " VNĐ", "#A855F7")); // Tím
        revenuePanel.add(createStatCard("DOANH THU THÁNG", statsDAO.getRevenueByPeriod("MONTH") + " VNĐ", "#EC4899")); // Hồng
        revenuePanel.add(createStatCard("DOANH THU NĂM", statsDAO.getRevenueByPeriod("YEAR") + " VNĐ", "#F59E0B"));   // Cam

        add(revenuePanel, BorderLayout.NORTH);

        // --- KHU VỰC 2: TRA CỨU NHẬT KÝ MÁY (BÊN DƯỚI) ---
        JPanel historyPanel = new JPanel(new BorderLayout(0, 10));
        historyPanel.setOpaque(false);

        // Thanh công cụ chọn máy
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterBar.setOpaque(false);
        JLabel lblFilter = new JLabel("[ 🔍 ] CHỌN MÁY ĐỂ KIỂM TRA LỊCH SỬ SỬ DỤNG:  ");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFilter.setForeground(Color.WHITE);

        JComboBox<String> cbMachine = new JComboBox<>();
        for (int i = 1; i <= 15; i++) cbMachine.addItem("PC " + String.format("%02d", i));
        cbMachine.setFont(new Font("Consolas", Font.BOLD, 14));
        cbMachine.setBackground(PANEL_BG);
        cbMachine.setForeground(TEXT_PRIMARY);

        JButton btnSearch = new JButton("TRÍCH XUẤT NHẬT KÝ");
        btnSearch.setBackground(new Color(16, 185, 129)); // Xanh Emerald
        btnSearch.setForeground(BG_DARK);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        filterBar.add(lblFilter);
        filterBar.add(cbMachine);
        filterBar.add(btnSearch);

        // Bảng dữ liệu
        String[] cols = {"Tài Khoản", "CCCD / SĐT", "Giờ Bắt Đầu (Đăng nhập)", "Giờ Kết Thúc (Đăng xuất)"};
        historyTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        historyTable = new JTable(historyTableModel);
        historyTable.setRowHeight(35);
        historyTable.setBackground(PANEL_BG);
        historyTable.setForeground(TEXT_PRIMARY);
        historyTable.setFont(new Font("Consolas", Font.PLAIN, 14));
        historyTable.getTableHeader().setBackground(new Color(51, 65, 85));
        historyTable.getTableHeader().setForeground(new Color(34, 211, 238));
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        historyTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105)));

        // Sự kiện ấn nút trích xuất
        btnSearch.addActionListener(e -> {
            int pcId = cbMachine.getSelectedIndex() + 1;
            historyTableModel.setRowCount(0); // Xóa dữ liệu cũ
            List<String[]> logs = statsDAO.getMachineHistory(pcId);
            for (String[] row : logs) {
                historyTableModel.addRow(row);
            }
        });

        historyPanel.add(filterBar, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        add(historyPanel, BorderLayout.CENTER);
    }

    // Tiện ích tạo Card Doanh thu
    private JPanel createStatCard(String title, String value, String hexColor) {
        JPanel card = new JPanel(new GridLayout(2, 1)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.decode(hexColor));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.LIGHT_GRAY);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Consolas", Font.BOLD, 22));
        lblVal.setForeground(Color.decode(hexColor));

        card.add(lblTitle);
        card.add(lblVal);
        return card;
    }
}