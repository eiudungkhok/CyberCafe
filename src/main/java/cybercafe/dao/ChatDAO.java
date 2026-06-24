package cybercafe.dao;

import cybercafe.util.DatabasePool;
import java.sql.*;

public class ChatDAO {
    // 1. Hàm gửi tin nhắn
    public boolean sendMessage(int machineId, String sender, String message) {
        String sql = "INSERT INTO chat_messages (machine_id, sender, message, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, machineId);
            stmt.setString(2, sender); // 'CLIENT' hoặc 'ADMIN'
            stmt.setString(3, message);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Hàm lấy lịch sử chat (ĐÃ FIX BẢO MẬT: CHỈ LẤY CỦA NGƯỜI ĐANG CHƠI)
    public String getChatHistory(int machineId) {
        StringBuilder history = new StringBuilder();

        // GIẢI THÍCH PHÉP THUẬT SQL:
        // Lấy thời gian đăng nhập (start_time) của phiên đang chơi (end_time IS NULL).
        // Nếu lấy được -> Lọc các tin nhắn từ thời gian đó trở đi.
        // Nếu máy đang trống (Không ai chơi) -> COALESCE sẽ trả về năm '2099-01-01', lúc này không có tin nhắn nào thỏa mãn -> Khung chat rỗng!
        String sql = "SELECT sender, message, DATE_FORMAT(created_at, '%H:%i') as time " +
                "FROM chat_messages " +
                "WHERE machine_id = ? " +
                "AND created_at >= COALESCE((SELECT MAX(start_time) FROM gaming_sessions WHERE machine_id = ? AND end_time IS NULL), '2099-01-01') " +
                "ORDER BY created_at ASC";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, machineId);
            stmt.setInt(2, machineId); // Chèn ID máy lần 2 cho cái Sub-Query

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String sender = rs.getString("sender");
                    String msg = rs.getString("message");
                    String time = rs.getString("time");

                    if ("CLIENT".equals(sender)) {
                        history.append("[").append(time).append("] BẠN: ").append(msg).append("\n\n");
                    } else {
                        history.append("[").append(time).append("] 👑 ADMIN: ").append(msg).append("\n\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history.toString();
    }
}