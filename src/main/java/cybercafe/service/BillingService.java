package cybercafe.service;

import cybercafe.model.Customer;

public class BillingService {
    private static final int HOURLY_RATE_STANDARD = 15000;
    private static final int HOURLY_RATE_VIP = 25000;

    public double calculateTotalBill(Customer customer, int minutesPlayed, double foodTotal, boolean isVipZone) {
        double hourlyRate = isVipZone ? HOURLY_RATE_VIP : HOURLY_RATE_STANDARD;
        double timeCost = (minutesPlayed / 60.0) * hourlyRate;

        double total = timeCost + foodTotal;

        // Tự động tính chiết khấu nếu khách có Thẻ Thành Viên (Membership)
        if (customer != null && customer.getMembership() != null) {
            double discount = customer.getMembership().getDiscountPercent() / 100.0;
            total = total - (total * discount);
        }

        // Thuế VAT 10% (Tùy chọn)
        total = total * 1.10;

        return Math.round(total); // Làm tròn số tiền VND
    }
}