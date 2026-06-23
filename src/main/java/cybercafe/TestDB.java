package cybercafe; // Chữ thường toàn bộ

import cybercafe.util.DatabasePool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("Đang khởi động Database Pool...");
        try {
            Connection conn = DatabasePool.getConnection();
            System.out.println("✅ LẤY KẾT NỐI TỪ POOL THÀNH CÔNG!");

            PreparedStatement stmt = conn.prepareStatement("SELECT 1");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ DATABASE PHẢN HỒI HOÀN HẢO!");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}