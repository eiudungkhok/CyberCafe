package cybercafe.view.panels;

import cybercafe.dao.OrderDAO;
import cybercafe.model.MenuItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodOrderPanel extends JPanel {
    private int machineId;
    private OrderDAO orderDAO = new OrderDAO();

    // Quản lý giỏ hàng cục bộ: Tên món -> Số lượng
    private Map<MenuItem, Integer> cart = new HashMap<>();

    private JPanel gridPanel;
    private JPanel cartItemsPanel;
    private JLabel lblTotalCartPrice;

    // Bảng màu Cyberpunk đồng bộ với hệ thống
    private final Color BG_DARK = new Color(10, 14, 23);
    private final Color PANEL_BG = new Color(20, 25, 40);
    private final Color NEON_CYAN = new Color(0, 245, 255);
    private final Color NEON_ORANGE = new Color(255, 165, 0);

    public FoodOrderPanel(int machineId) {
        this.machineId = machineId;
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // =========================================================
        // 1. KHU VỰC BÊN TRÁI: THỰC ĐƠN & BỘ LỌC (70% Chiều rộng)
        // =========================================================
        JPanel menuContainer = new JPanel(new BorderLayout());
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Thanh công cụ trên cùng: Bộ lọc danh mục
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);

        JButton btnAll = createFilterButton("TẤT CẢ");
        JButton btnFood = createFilterButton("ĐỒ ĂN");
        JButton btnDrink = createFilterButton("NƯỚC UỐNG");
        JButton btnSnack = createFilterButton("ĂN VẶT");

        filterBar.add(btnAll); filterBar.add(btnFood);
        filterBar.add(btnDrink); filterBar.add(btnSnack);
        menuContainer.add(filterBar, BorderLayout.NORTH);

        // Lưới hiển thị các Card món ăn
        gridPanel = new JPanel(new GridLayout(0, 3, 15, 15)); // 3 cột cố định
        gridPanel.setBackground(BG_DARK);

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        menuContainer.add(scrollPane, BorderLayout.CENTER);

        // Nạp tất cả món ăn khi mở tab
        loadMenuCards("ALL");

        // Sự kiện click bộ lọc danh mục
        btnAll.addActionListener(e -> loadMenuCards("ALL"));
        btnFood.addActionListener(e -> loadMenuCards("Food"));
        btnDrink.addActionListener(e -> loadMenuCards("Drink"));
        btnSnack.addActionListener(e -> loadMenuCards("Snack"));

        // =========================================================
        // 2. KHU VỰC BÊN PHẢI: GIỎ HÀNG THỜI GIAN THỰC (30% Chiều rộng)
        // =========================================================
        JPanel cartPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(PANEL_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(139, 92, 246, 100)); // Viền tím mờ ngăn cách
                g.fillRect(0, 0, 2, getHeight());
            }
        };
        cartPanel.setPreferredSize(new Dimension(280, 0));

        JLabel lblCartTitle = new JLabel("🛒 GIỎ HÀNG CỦA BẠN", SwingConstants.CENTER);
        lblCartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCartTitle.setForeground(NEON_CYAN);
        lblCartTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        cartPanel.add(lblCartTitle, BorderLayout.NORTH);

        // Danh sách các món trong giỏ
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(PANEL_BG);

        JScrollPane cartScroll = new JScrollPane(cartItemsPanel);
        cartScroll.setBorder(null);
        cartScroll.getViewport().setBackground(PANEL_BG);
        cartPanel.add(cartScroll, BorderLayout.CENTER);

        // Thanh toán & Gửi đơn xuống bếp
        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setOpaque(false);
        checkoutPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        lblTotalCartPrice = new JLabel("TỔNG TIỀN: 0 đ");
        lblTotalCartPrice.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalCartPrice.setForeground(Color.YELLOW);
        lblTotalCartPrice.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton btnSubmitOrder = new JButton("GỬI ĐƠN XUỐNG BẾP");
        btnSubmitOrder.setBackground(NEON_ORANGE);
        btnSubmitOrder.setForeground(Color.WHITE);
        btnSubmitOrder.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmitOrder.setPreferredSize(new Dimension(0, 45));
        btnSubmitOrder.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmitOrder.setFocusPainted(false);

        // Xử lý gửi giỏ hàng xuống DB
        btnSubmitOrder.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống rỗng!");
                return;
            }
            // Duyệt giỏ hàng lưu vào bảng orders
            boolean success = true;
            for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
                MenuItem item = entry.getKey();
                int qty = entry.getValue();
                if (!orderDAO.placeOrder(machineId, item.getName(), qty, item.getPrice())) {
                    success = false;
                }
            }
            if (success) {
                JOptionPane.showMessageDialog(this, "🚀 Đơn hàng đã gửi đến Nhà bếp thành công!\nVui lòng chờ trong giây lát.");
                cart.clear();
                refreshCartView();
            }
        });

        checkoutPanel.add(lblTotalCartPrice, BorderLayout.NORTH);
        checkoutPanel.add(btnSubmitOrder, BorderLayout.CENTER);
        cartPanel.add(checkoutPanel, BorderLayout.SOUTH);

        // Gộp 2 khu vực vào Panel tổng
        add(menuContainer, BorderLayout.CENTER);
        add(cartPanel, BorderLayout.EAST);
    }

    // Hàm sinh Card món ăn tự động đưa vào lưới
    private void loadMenuCards(String category) {
        gridPanel.removeAll();
        List<MenuItem> items;
        if ("ALL".equals(category)) {
            items = orderDAO.getMenu();
        } else {
            items = orderDAO.getMenuByCategory(category);
        }

        for (MenuItem item : items) {
            JPanel card = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(PANEL_BG);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2.setColor(new Color(38, 45, 64));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                }
            };
            card.setOpaque(false);
            card.setBorder(new EmptyBorder(12, 12, 12, 12));

            // Chữ hiển thị thông tin món
            JLabel lblName = new JLabel(item.getName());
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblName.setForeground(Color.WHITE);

            JLabel lblPrice = new JLabel(item.getPrice() + " đ");
            lblPrice.setFont(new Font("Consolas", Font.BOLD, 14));
            lblPrice.setForeground(NEON_CYAN);

            // Nút thêm vào giỏ nhanh
            JButton btnAdd = new JButton("+ THÊM VÀO GIỎ");
            btnAdd.setBackground(new Color(30, 41, 59));
            btnAdd.setForeground(NEON_CYAN);
            btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnAdd.setFocusPainted(false);
            btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnAdd.addActionListener(e -> {
                cart.put(item, cart.getOrDefault(item, 0) + 1);
                refreshCartView();
            });

            JPanel textPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            textPanel.setOpaque(false);
            textPanel.add(lblName);
            textPanel.add(lblPrice);

            card.add(textPanel, BorderLayout.CENTER);
            card.add(btnAdd, BorderLayout.SOUTH);
            gridPanel.add(card);
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // Hàm vẽ lại Giỏ hàng bên phải mỗi khi thêm/bớt món
    private void refreshCartView() {
        cartItemsPanel.removeAll();
        int totalCartPrice = 0;

        for (Map.Entry<MenuItem, Integer> entry : cart.entrySet()) {
            MenuItem item = entry.getKey();
            int qty = entry.getValue();
            int cost = item.getPrice() * qty;
            totalCartPrice += cost;

            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setOpaque(false);
            itemRow.setBorder(new EmptyBorder(5, 15, 5, 15));
            itemRow.setMaximumSize(new Dimension(300, 35));

            JLabel lblItemName = new JLabel(qty + " x " + item.getName());
            lblItemName.setForeground(Color.LIGHT_GRAY);
            lblItemName.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JLabel lblItemCost = new JLabel(cost + "đ");
            lblItemCost.setForeground(Color.WHITE);
            lblItemCost.setFont(new Font("Consolas", Font.PLAIN, 13));

            itemRow.add(lblItemName, BorderLayout.CENTER);
            itemRow.add(lblItemCost, BorderLayout.EAST);
            cartItemsPanel.add(itemRow);
        }

        lblTotalCartPrice.setText("TỔNG TIỀN: " + totalCartPrice + " đ");
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    private JButton createFilterButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(25, 30, 45));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}