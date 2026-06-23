package cybercafe.dao;

import cybercafe.model.MenuItem;
import cybercafe.util.DatabasePool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // ==========================================
    // 1. CHỨC NĂNG CỦA KHÁCH HÀNG (CLIENT)
    // ==========================================

    public List<MenuItem> getMenu() {
        List<MenuItem> menu = new ArrayList<>();
        String sql = "SELECT name, price, category FROM menu_items";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                menu.add(new MenuItem(rs.getString("name"), rs.getInt("price")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return menu;
    }

    public List<MenuItem> getMenuByCategory(String category) {
        List<MenuItem> menu = new ArrayList<>();
        String sql = "SELECT name, price FROM menu_items WHERE category = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    menu.add(new MenuItem(rs.getString("name"), rs.getInt("price")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return menu;
    }

    public boolean placeOrder(int machineId, String itemName, int quantity, int price) {
        String sqlOrder = "INSERT INTO orders (machine_id, item_name, quantity, price, total_cost, order_time) VALUES (?, ?, ?, ?, ?, NOW())";
        String sqlUpdateStats = "UPDATE customers SET total_orders = total_orders + ? WHERE id = (SELECT customer_id FROM gaming_sessions WHERE machine_id = ? AND end_time IS NULL)";

        try (Connection conn = DatabasePool.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtOrder = conn.prepareStatement(sqlOrder);
                 PreparedStatement stmtStats = conn.prepareStatement(sqlUpdateStats)) {

                stmtOrder.setInt(1, machineId);
                stmtOrder.setString(2, itemName);
                stmtOrder.setInt(3, quantity);
                stmtOrder.setInt(4, price);
                stmtOrder.setInt(5, quantity * price);
                stmtOrder.executeUpdate();

                stmtStats.setInt(1, quantity);
                stmtStats.setInt(2, machineId);
                stmtStats.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi đặt đồ: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // 2. CHỨC NĂNG NHÀ BẾP (GỘP ĐƠN)
    // ==========================================

    // Lấy danh sách PENDING: Tự động gom tất cả món của cùng 1 Máy vào 1 Đơn
    public List<cybercafe.model.Order> getPendingOrders() {
        List<cybercafe.model.Order> orders = new ArrayList<>();
        String sql = "SELECT o.machine_id, " +
                "COALESCE(c.username, '[KHÁCH ĐÃ VỀ]') AS username, " +
                "GROUP_CONCAT(CONCAT(o.item_name, ' (x', o.quantity, ')') SEPARATOR ', ') AS full_order, " +
                "SUM(o.quantity) AS total_items, " +
                "SUM(o.total_cost) AS grand_total, " +
                "MIN(o.order_time) AS order_time " +
                "FROM orders o " +
                "LEFT JOIN gaming_sessions gs ON o.machine_id = gs.machine_id AND gs.end_time IS NULL " +
                "LEFT JOIN customers c ON gs.customer_id = c.id " +
                "WHERE o.status = 'PENDING' " +
                "GROUP BY o.machine_id, c.username " +
                "ORDER BY order_time ASC";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                orders.add(new cybercafe.model.Order(
                        0,
                        rs.getInt("machine_id"),
                        rs.getString("username"),
                        rs.getString("full_order"),
                        rs.getInt("total_items"),
                        rs.getInt("grand_total"),
                        rs.getString("order_time")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return orders;
    }

    // Chốt Đơn: Xác nhận hoàn thành TẤT CẢ các món của 1 Máy cùng lúc
    public boolean completeOrderByMachine(int machineId) {
        String sql = "UPDATE orders SET status = 'COMPLETED' WHERE machine_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, machineId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Chuông báo động: Đếm số lượng MÁY đang chờ đồ
    public int countPendingOrders() {
        String sql = "SELECT COUNT(DISTINCT machine_id) FROM orders WHERE status = 'PENDING'";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ==========================================
    // 3. QUẢN LÝ KHO (CRUD BẰNG ID)
    // ==========================================

    public boolean addMenuItem(String name, int price, String category) {
        String sql = "INSERT INTO menu_items (name, price, category) VALUES (?, ?, ?)";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name); stmt.setInt(2, price); stmt.setString(3, category);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<cybercafe.model.MenuItem> getAllMenuItemsFull() {
        List<cybercafe.model.MenuItem> list = new ArrayList<>();
        String sql = "SELECT id, name, price, category FROM menu_items ORDER BY category, name";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cybercafe.model.MenuItem item = new cybercafe.model.MenuItem(rs.getString("name"), rs.getInt("price"));
                item.setId(rs.getInt("id")); // Nạp ID
                item.setCategory(rs.getString("category"));
                list.add(item);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateMenuItem(int id, String name, int price, String category) {
        String sql = "UPDATE menu_items SET name = ?, price = ?, category = ? WHERE id = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, price);
            stmt.setString(3, category);
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteMenuItemById(int id) {
        String sql = "DELETE FROM menu_items WHERE id = ?";
        try (Connection conn = DatabasePool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}