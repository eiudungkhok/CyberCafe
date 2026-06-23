package cybercafe.model;

import java.sql.Timestamp;

public class Machine {
    private int id;
    private String zoneType;
    private String status;
    private int hourlyRate;
    private Timestamp startTime;
    private int customerBalance; // THÊM MỚI: Số dư của khách

    public Machine(int id, String zoneType, String status, int hourlyRate) {
        this.id = id;
        this.zoneType = zoneType;
        this.status = status;
        this.hourlyRate = hourlyRate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getZoneType() { return zoneType; }
    public void setZoneType(String zoneType) { this.zoneType = zoneType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(int hourlyRate) { this.hourlyRate = hourlyRate; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    // --- GET/SET CHO SỐ DƯ ---
    public int getCustomerBalance() { return customerBalance; }
    public void setCustomerBalance(int customerBalance) { this.customerBalance = customerBalance; }
}