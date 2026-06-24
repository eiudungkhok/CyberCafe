package cybercafe.dao;

import cybercafe.util.DatabasePool;
import cybercafe.util.SecurityUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    public int authenticateCustomer(String username, String plainPassword) {
        String sql = "SELECT id, password_hash, balance FROM customers WHERE username = ?";

        // Dòng lệnh Log kiểm tra
        System.out.println("🔍 [HỆ THỐNG] Đang kiểm tra tài khoản: " + username);

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✅ [HỆ THỐNG] Đã tìm thấy username '" + username + "' trong Database!");

                    String hash = rs.getString("password_hash");
                    int balance = rs.getInt("balance");

                    // Kiểm tra BCrypt
                    boolean isMatch = SecurityUtils.verifyPassword(plainPassword, hash);
                    System.out.println("🔄 [HỆ THỐNG] Kết quả đối chiếu mật khẩu BCrypt: " + (isMatch ? "KHỚP ĐỒNG BỘ" : "KHÔNG KHỚP"));

                    if (isMatch) {
                        if (balance <= 0) {
                            System.out.println("❌ [HỆ THỐNG] Tài khoản hết tiền! Số dư: " + balance);
                            return -2;
                        }
                        System.out.println("🚀 [HỆ THỐNG] Đăng nhập thành công! ID Khách hàng: " + rs.getInt("id"));
                        return rs.getInt("id");
                    }
                } else {
                    System.out.println("❌ [HỆ THỐNG] Không tìm thấy tên tài khoản '" + username + "' trong bảng customers!");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi SQL: " + e.getMessage());
        }
        return -1;
    }
    // 1. Tạo tài khoản mới (Mặc định thẻ Bronze - ID = 1, Số dư = 0)
// Tạo tài khoản mới có kèm CCCD/SĐT
    public boolean createCustomer(String username, String password, String cccdPhone) {
        // Cột cccd_phone vừa được chúng ta tạo ở bảng customers trong CSDL
        String sql = "INSERT INTO customers (username, password, balance, tier_id, cccd_phone) VALUES (?, ?, 0, 1, ?)";
        try (java.sql.Connection conn = cybercafe.util.DatabasePool.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, cccdPhone); // Truyền giá trị CCCD vào CSDL
            return stmt.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // 2. Lấy danh sách toàn bộ Username để hiển thị lên ComboBox
    public java.util.List<String> getAllUsernames() {
        java.util.List<String> users = new java.util.ArrayList<>();
        String sql = "SELECT username FROM customers";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // 3. Nâng cấp hạng thẻ cho khách
    public boolean upgradeMembership(String username, int newMembershipId) {
        String sql = "UPDATE customers SET membership_id = ? WHERE username = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newMembershipId);
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi nâng cấp hạng thẻ: " + e.getMessage());
            return false;
        }
    }
    // 4. Nạp tiền vào tài khoản
    public boolean addBalance(String username, int amount) {
        // Lệnh UPDATE này tự động lấy số dư cũ cộng thêm tiền mới
        String sql = "UPDATE customers SET balance = balance + ? WHERE username = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, amount);
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi nạp tiền: " + e.getMessage());
            return false;
        }
    }
    // 5. Lấy số dư Real-time của khách hàng
    public int getBalance(int customerId) {
        String sql = "SELECT balance FROM customers WHERE id = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    // 6. Lấy toàn bộ Profile Thành Tích
    public cybercafe.model.CustomerProfile getCustomerProfile(int customerId) {
        String sql = "SELECT c.username, c.total_play_minutes, c.total_spent, c.total_orders, m.tier_name " +
                "FROM customers c LEFT JOIN memberships m ON c.membership_id = m.id WHERE c.id = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new cybercafe.model.CustomerProfile(
                            rs.getString("username"),
                            rs.getString("tier_name") != null ? rs.getString("tier_name") : "BRONZE",
                            rs.getInt("total_play_minutes"),
                            rs.getInt("total_spent"),
                            rs.getInt("total_orders")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Tính năng Đổi mật khẩu cho khách
    public boolean changePassword(int customerId, String oldPass, String newPass) {
        String sql = "UPDATE customers SET password = ? WHERE id = ? AND password = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPass);
            stmt.setInt(2, customerId);
            stmt.setString(3, oldPass);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}