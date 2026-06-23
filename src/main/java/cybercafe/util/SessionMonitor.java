package cybercafe.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionMonitor {
    // Tạo 2 luồng ngầm chạy song song với giao diện chính
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void startMonitoring() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // 1. Quét database lấy các máy đang hoạt động (ACTIVE)
                // 2. Trừ dần tiền trong tài khoản của khách (balance)
                // 3. Nếu balance <= 0 -> Ra lệnh khóa máy và chuyển status thành COMPLETED
                // 4. Gửi tín hiệu để cập nhật lại Dashboard cho thu ngân thấy

                System.out.println("[HỆ THỐNG] Đã kiểm tra và đồng bộ phiên chơi...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES); // Chạy tự động mỗi 1 phút
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}