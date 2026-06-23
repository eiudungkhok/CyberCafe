package cybercafe.dao;

import cybercafe.util.DatabasePool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class GamingSessionDAO {

    // 1. Bắt đầu phiên chơi cho khách vãng lai
    public boolean startGuestSession(int machineId, String guestName) {
        String insertSession = "INSERT INTO gaming_sessions (machine_id, guest_name, start_time) VALUES (?, ?, NOW())";
        String updateMachine = "UPDATE machines SET status = 'IN_USE' WHERE id = ?";

        try (Connection conn = DatabasePool.getConnection()) {
            conn.setAutoCommit(false); // Bật Transaction để đảm bảo an toàn dữ liệu

            try (PreparedStatement stmt1 = conn.prepareStatement(insertSession);
                 PreparedStatement stmt2 = conn.prepareStatement(updateMachine)) {

                stmt1.setInt(1, machineId);
                stmt1.setString(2, guestName);
                stmt1.executeUpdate();

                stmt2.setInt(1, machineId);
                stmt2.executeUpdate();

                conn.commit(); // Thành công cả 2 lệnh thì mới lưu vào DB
                return true;
            } catch (SQLException e) {
                conn.rollback(); // Lỗi 1 trong 2 lệnh thì hủy bỏ toàn bộ
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi mở máy khách vãng lai: " + e.getMessage());
            return false;
        }
    }
    // Thêm hàm này vào trong GamingSessionDAO.java
    public boolean startMemberSession(int machineId, int customerId) {
        String insertSession = "INSERT INTO gaming_sessions (machine_id, customer_id, start_time) VALUES (?, ?, NOW())";
        String updateMachine = "UPDATE machines SET status = 'IN_USE' WHERE id = ?";

        try (Connection conn = DatabasePool.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt1 = conn.prepareStatement(insertSession);
                 PreparedStatement stmt2 = conn.prepareStatement(updateMachine)) {

                stmt1.setInt(1, machineId);
                stmt1.setInt(2, customerId);
                stmt1.executeUpdate();

                stmt2.setInt(1, machineId);
                stmt2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi mở máy VIP: " + e.getMessage());
            return false;
        }
    }
    // 2. Lấy thời gian bắt đầu của một máy đang hoạt động
    public Timestamp getStartTime(int machineId) {
        String sql = "SELECT start_time FROM gaming_sessions WHERE machine_id = ? AND end_time IS NULL LIMIT 1";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, machineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("start_time");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy thời gian phiên chơi: " + e.getMessage());
        }
        return null;
    }

    // 3. Kết thúc phiên chơi (Thanh toán / Tắt máy)
    public boolean endSession(int machineId) {
        String updateSession = "UPDATE gaming_sessions SET end_time = NOW() WHERE machine_id = ? AND end_time IS NULL";
        String updateMachine = "UPDATE machines SET status = 'AVAILABLE' WHERE id = ?";

        try (Connection conn = DatabasePool.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(updateSession);
                 PreparedStatement stmt2 = conn.prepareStatement(updateMachine)) {

                stmt1.setInt(1, machineId);
                stmt1.executeUpdate();

                stmt2.setInt(1, machineId);
                stmt2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết thúc phiên chơi: " + e.getMessage());
            return false;
        }
    }
    // 4. Thanh toán, xuất hóa đơn và lưu Lịch sử giao dịch
    public String checkoutAndGenerateBill(int machineId) {
        String queryInfo = "SELECT gs.id as session_id, gs.customer_id, gs.guest_name, gs.start_time, " +
                "m.hourly_rate, c.username, c.balance, mem.tier_name, mem.discount_percent " +
                "FROM gaming_sessions gs " +
                "JOIN machines m ON gs.machine_id = m.id " +
                "LEFT JOIN customers c ON gs.customer_id = c.id " +
                "LEFT JOIN memberships mem ON c.membership_id = mem.id " +
                "WHERE gs.machine_id = ? AND gs.end_time IS NULL";

        String updateSession = "UPDATE gaming_sessions SET end_time = NOW() WHERE machine_id = ? AND end_time IS NULL";
        String updateMachine = "UPDATE machines SET status = 'AVAILABLE' WHERE id = ?";
        String deductBalance = "UPDATE customers SET balance = balance - ?, total_spent = total_spent + ?, total_play_minutes = total_play_minutes + ? WHERE id = ?";
        String insertTransaction = "INSERT INTO transactions (machine_id, duration_minutes, time_cost, total_amount) VALUES (?, ?, ?, ?)";
        // Lệnh mới: Vừa trừ tiền balance, vừa CỘNG DỒN tiền đã tiêu và số phút đã chơi
        try (Connection conn = DatabasePool.getConnection()) {
            conn.setAutoCommit(false); // Bật Transaction bảo vệ dữ liệu

            try (PreparedStatement stmtInfo = conn.prepareStatement(queryInfo)) {
                stmtInfo.setInt(1, machineId);
                ResultSet rs = stmtInfo.executeQuery();

                if (rs.next()) {
                    // Lấy dữ liệu
                    int sessionId = rs.getInt("session_id");
                    int customerId = rs.getInt("customer_id");
                    String customerName = rs.getString("username");
                    if (rs.wasNull()) customerName = rs.getString("guest_name"); // Nếu là khách vãng lai

                    Timestamp startTime = rs.getTimestamp("start_time");
                    int hourlyRate = rs.getInt("hourly_rate");
                    double discountPercent = rs.getDouble("discount_percent");
                    String tierName = rs.getString("tier_name");

                    // Tính toán thời gian (Phút)
                    long endMillis = System.currentTimeMillis();
                    long startMillis = startTime.getTime();
                    int durationMinutes = (int) ((endMillis - startMillis) / (1000 * 60));
                    if (durationMinutes < 1) durationMinutes = 1; // Chơi chưa tới 1 phút vẫn tính 1 phút

                    // Tính tiền
                    int baseCost = (int) (durationMinutes * (hourlyRate / 60.0));
                    int discountAmount = (int) (baseCost * (discountPercent / 100.0));
                    int finalCost = baseCost - discountAmount;

                    // 1. Trừ tiền tài khoản & Cộng dồn thành tích (Nếu là Hội Viên)
                    if (customerId > 0) {
                        try (PreparedStatement stmtDeduct = conn.prepareStatement(deductBalance)) {
                            stmtDeduct.setInt(1, finalCost);         // Trừ balance
                            stmtDeduct.setInt(2, finalCost);         // Cộng dồn vào total_spent
                            stmtDeduct.setInt(3, durationMinutes);   // Cộng dồn vào total_play_minutes
                            stmtDeduct.setInt(4, customerId);
                            stmtDeduct.executeUpdate();
                        }
                    }
                    // 2. Lưu lịch sử Giao dịch
                    try (PreparedStatement stmtTrans = conn.prepareStatement(insertTransaction)) {
                        stmtTrans.setInt(1, machineId);
                        stmtTrans.setInt(2, durationMinutes);
                        stmtTrans.setInt(3, baseCost);
                        stmtTrans.setInt(4, finalCost);
                        stmtTrans.executeUpdate();
                    }

                    // 3. Đóng phiên chơi & Tắt máy
                    try (PreparedStatement stmtEnd = conn.prepareStatement(updateSession);
                         PreparedStatement stmtFree = conn.prepareStatement(updateMachine)) {
                        stmtEnd.setInt(1, machineId);
                        stmtEnd.executeUpdate();

                        stmtFree.setInt(1, machineId);
                        stmtFree.executeUpdate();
                    }

                    conn.commit(); // XÁC NHẬN LƯU VÀO DATABASE

                    // 4. Tạo chuỗi hiển thị Hóa Đơn (Receipt)
                    StringBuilder bill = new StringBuilder();
                    bill.append("🧾 HÓA ĐƠN THANH TOÁN (PC ").append(machineId).append(")\n");
                    bill.append("------------------------------------\n");
                    bill.append("👤 Khách hàng: ").append(customerName).append("\n");
                    if (tierName != null) bill.append("👑 Hạng thẻ: ").append(tierName).append(" (Giảm ").append(discountPercent).append("%)\n");
                    bill.append("⏱ Thời gian chơi: ").append(durationMinutes).append(" phút\n");
                    bill.append("💵 Tiền giờ: ").append(baseCost).append(" đ\n");
                    if (discountAmount > 0) bill.append("🎁 Chiết khấu: -").append(discountAmount).append(" đ\n");
                    bill.append("------------------------------------\n");
                    bill.append("💰 TỔNG CẦN THANH TOÁN: ").append(finalCost).append(" VNĐ");

                    return bill.toString();
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi thanh toán: " + e.getMessage());
        }
        return "Lỗi xuất hóa đơn!";
    }
// 5. Lấy ID của máy tính mà tài khoản đang chơi (Trả về -1 nếu không chơi)
    public int getPlayingMachineId(int customerId) {
        String sql = "SELECT machine_id FROM gaming_sessions WHERE customer_id = ? AND end_time IS NULL";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                // Nếu tìm thấy, trả về đúng số PC đó
                if (rs.next()) return rs.getInt("machine_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Không chơi thì trả về -1
    }    // 6. Ép đóng phiên chơi cũ nếu tài khoản bị kẹt
    public void forceCheckoutByCustomerId(int customerId) {
        String sql = "SELECT machine_id FROM gaming_sessions WHERE customer_id = ? AND end_time IS NULL";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int oldMachineId = rs.getInt("machine_id");
                    // Gọi luôn hàm thanh toán xịn sò để đóng phiên, trừ tiền và xuất lịch sử
                    checkoutAndGenerateBill(oldMachineId);
                    System.out.println("🔧 Hệ thống đã tự động gỡ kẹt cho PC " + oldMachineId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Lấy ID của khách hàng đang ngồi tại một Máy cụ thể (Trả về -1 nếu máy trống)
    public int getPlayingCustomerId(int machineId) {
        String sql = "SELECT customer_id FROM gaming_sessions WHERE machine_id = ? AND end_time IS NULL";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, machineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("customer_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Máy đang trống
    }
}