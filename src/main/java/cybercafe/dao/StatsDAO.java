package cybercafe.dao;

import cybercafe.util.DatabasePool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatsDAO {
    // 1. Trích xuất Lịch sử ngồi máy (Phục vụ C.A kiểm tra)
    public List<String[]> getMachineHistory(int machineId) {
        List<String[]> history = new ArrayList<>();
        // Kết hợp 2 bảng để lấy Tên khách, CCCD và Thời gian chơi
        String sql = "SELECT c.username, c.cccd_phone, DATE_FORMAT(gs.start_time, '%d/%m/%Y %H:%i:%s') as t_start, DATE_FORMAT(gs.end_time, '%d/%m/%Y %H:%i:%s') as t_end " +
                "FROM gaming_sessions gs " +
                "JOIN customers c ON gs.customer_id = c.id " +
                "WHERE gs.machine_id = ? " +
                "ORDER BY gs.start_time DESC LIMIT 50"; // Lấy 50 lượt gần nhất

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, machineId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String user = rs.getString("username");
                    String cccd = rs.getString("cccd_phone");
                    String start = rs.getString("t_start");
                    String end = rs.getString("t_end");
                    if (end == null) end = "ĐANG CHƠI (CHƯA TẮT MÁY)";
                    history.add(new String[]{user, cccd, start, end});
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return history;
    }

    // 2. Tính tổng doanh thu dịch vụ theo chu kỳ
    public int getRevenueByPeriod(String period) {
        String condition = "";
        switch(period) {
            case "DAY": condition = "DATE(order_time) = CURDATE()"; break;
            case "WEEK": condition = "YEARWEEK(order_time, 1) = YEARWEEK(CURDATE(), 1)"; break;
            case "MONTH": condition = "MONTH(order_time) = MONTH(CURDATE()) AND YEAR(order_time) = YEAR(CURDATE())"; break;
            case "YEAR": condition = "YEAR(order_time) = YEAR(CURDATE())"; break;
        }
        String sql = "SELECT COALESCE(SUM(total_cost), 0) FROM orders WHERE status = 'COMPLETED' AND " + condition;
        try (Connection conn = DatabasePool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}