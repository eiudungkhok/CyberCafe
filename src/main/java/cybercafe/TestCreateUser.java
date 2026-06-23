package cybercafe;

import cybercafe.util.DatabasePool;
import cybercafe.util.SecurityUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TestCreateUser {
    public static void main(String[] args) {
        String username = "fker2"; // Tên tài khoản mới
        String plainPassword = "123456"; // Mật khẩu gốc

        // Nhờ SecurityUtils của chính bạn băm mật khẩu
        String hashedPassword = SecurityUtils.hashPassword(plainPassword);
        System.out.println("Mã băm chuẩn sinh ra là: " + hashedPassword);

        String sql = "INSERT INTO customers (username, password_hash, balance, membership_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword); // Lưu mã băm chuẩn vào DB
            stmt.setInt(3, 50000); // 50k
            stmt.setInt(4, 5); // Diamond

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Đã tạo thành công tài khoản: " + username + " / Mật khẩu: " + plainPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}