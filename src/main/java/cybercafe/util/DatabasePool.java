package cybercafe.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabasePool {
    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            // Cấu hình URL cố định vào thẳng DB của bạn (Bỏ qua file XML hay lỗi)
            config.setJdbcUrl("jdbc:mysql://localhost:3306/cybercafe?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh");
            config.setUsername("root");
            config.setPassword(""); // Mặc định XAMPP

            // Tối ưu hóa Connection Pool cho quán Net
            config.setMaximumPoolSize(20);      // Tối đa 20 luồng kết nối chạy cùng lúc
            config.setMinimumIdle(5);           // Lúc vắng khách vẫn giữ sẵn 5 kết nối để gọi là có ngay
            config.setIdleTimeout(300000);      // Ngắt các kết nối rảnh rỗi sau 5 phút
            config.setMaxLifetime(600000);      // Reset kết nối sau 10 phút để tránh lỗi tràn RAM
            config.setConnectionTimeout(10000); // Báo lỗi nếu XAMPP tắt (sau 10s)

            // Bật Cache tăng tốc Query SQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            System.out.println("✅ [HikariCP] Đã khởi tạo Hệ thống Quản lý Kết nối thành công!");
        } catch (Exception e) {
            System.err.println("❌ [HikariCP] Lỗi khởi tạo: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}