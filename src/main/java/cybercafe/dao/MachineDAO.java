package cybercafe.dao;

import cybercafe.model.Machine;
import cybercafe.util.DatabasePool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MachineDAO {

    // 1. Lấy danh sách toàn bộ máy (Kèm theo thời gian chơi nếu đang IN_USE)
// 1. Lấy danh sách toàn bộ máy (Kèm theo thời gian chơi và số dư)
    public List<Machine> getAllMachines() {
        List<Machine> machineList = new ArrayList<>();
        // SỬA SQL: Móc thêm bảng customers để lấy balance
        String sql = "SELECT m.*, gs.start_time, c.balance " +
                "FROM machines m " +
                "LEFT JOIN gaming_sessions gs ON m.id = gs.machine_id AND gs.end_time IS NULL " +
                "LEFT JOIN customers c ON gs.customer_id = c.id";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Machine m = new Machine(
                        rs.getInt("id"),
                        rs.getString("zone_type"),
                        rs.getString("status"),
                        rs.getInt("hourly_rate")
                );
                m.setStartTime(rs.getTimestamp("start_time"));
                m.setCustomerBalance(rs.getInt("balance")); // Nạp số dư vào Model

                machineList.add(m);
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy dữ liệu máy: " + e.getMessage());
        }
        return machineList;
    }
    // 2. Cập nhật trạng thái máy (Ví dụ: Đổi từ AVAILABLE sang IN_USE)
    public boolean updateMachineStatus(int machineId, String newStatus) {
        String sql = "UPDATE machines SET status = ? WHERE id = ?";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, machineId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu cập nhật thành công

        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật trạng thái máy " + machineId + ": " + e.getMessage());
            return false;
        }
    }
    // 3. Lấy giá tiền 1 giờ của máy
    public int getHourlyRate(int machineId) {
        String sql = "SELECT hourly_rate FROM machines WHERE id = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, machineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("hourly_rate");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 15000; // Trả về mặc định nếu lỗi
    }
    // Lấy trạng thái hiện tại của một máy trạm
    public String getMachineStatus(int machineId) {
        String sql = "SELECT status FROM machines WHERE id = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, machineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "UNKNOWN";
    }

}