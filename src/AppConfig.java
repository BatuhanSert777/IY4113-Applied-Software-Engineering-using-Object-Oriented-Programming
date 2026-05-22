import java.util.HashMap;
import java.util.Map;

public class AppConfig {

    private Map<String, Double> baseFares;
    private Map<String, Double> dailyCaps;
    private Map<String, Double> discountRates;

    // Two peak windows stored as HH:mm strings
    private String peakStartTime;
    private String peakEndTime;
    private String peakStartTime2;
    private String peakEndTime2;

    private String adminPassword;

    public void loadDefaults() {
        baseFares = new HashMap<>();
        baseFares.put("1:PEAK",     3.00);
        baseFares.put("1:OFF_PEAK", 2.50);
        baseFares.put("2:PEAK",     3.70);
        baseFares.put("2:OFF_PEAK", 3.20);
        baseFares.put("3:PEAK",     4.50);
        baseFares.put("3:OFF_PEAK", 4.00);
        baseFares.put("4:PEAK",     5.30);
        baseFares.put("4:OFF_PEAK", 4.80);
        baseFares.put("5:PEAK",     6.10);
        baseFares.put("5:OFF_PEAK", 5.60);

        dailyCaps = new HashMap<>();
        dailyCaps.put("ADULT",          8.00);
        dailyCaps.put("STUDENT",        6.00);
        dailyCaps.put("CHILD",          4.00);
        dailyCaps.put("SENIOR_CITIZEN", 7.00);

        discountRates = new HashMap<>();
        discountRates.put("ADULT",          0.00);
        discountRates.put("STUDENT",        0.25);
        discountRates.put("CHILD",          0.50);
        discountRates.put("SENIOR_CITIZEN", 0.30);

        peakStartTime  = "07:00";
        peakEndTime    = "09:30";
        peakStartTime2 = "17:00";
        peakEndTime2   = "19:00";
        adminPassword  = "admin123";
    }

    public double getBaseFare(int zonesCrossed, TimeBand timeBand) {
        String key = zonesCrossed + ":" + timeBand.name();
        if (baseFares.containsKey(key)) {
            return baseFares.get(key);
        }
        double base = 2.50 + (zonesCrossed - 1) * 0.70;
        return timeBand == TimeBand.PEAK ? base + 0.50 : base;
    }

    public double getDailyCap(PassengerType passengerType) {
        return dailyCaps.getOrDefault(passengerType.name(), 8.00);
    }

    public double getDiscountRate(PassengerType passengerType) {
        return discountRates.getOrDefault(passengerType.name(), 0.00);
    }

    public boolean isAdminPasswordCorrect(String entered) {
        return adminPassword != null && adminPassword.equals(entered);
    }

    // Classifies a time string as PEAK or OFF_PEAK by checking both peak windows
    public TimeBand determineTimeBand(String time) {
        int inputMins = toMinutes(time);
        int start1    = toMinutes(peakStartTime  != null ? peakStartTime  : "07:00");
        int end1      = toMinutes(peakEndTime     != null ? peakEndTime    : "09:30");
        int start2    = toMinutes(peakStartTime2 != null ? peakStartTime2 : "17:00");
        int end2      = toMinutes(peakEndTime2    != null ? peakEndTime2   : "19:00");

        if ((inputMins >= start1 && inputMins <= end1) ||
            (inputMins >= start2 && inputMins <= end2)) {
            return TimeBand.PEAK;
        }
        return TimeBand.OFF_PEAK;
    }

    private int toMinutes(String hhmm) {
        String[] parts = hhmm.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    // ── Getters and setters ──────────────────────────────────────────────────

    public Map<String, Double> getBaseFares()                        { return baseFares; }
    public void setBaseFares(Map<String, Double> baseFares)          { this.baseFares = baseFares; }

    public Map<String, Double> getDailyCaps()                        { return dailyCaps; }
    public void setDailyCaps(Map<String, Double> dailyCaps)          { this.dailyCaps = dailyCaps; }

    public Map<String, Double> getDiscountRates()                    { return discountRates; }
    public void setDiscountRates(Map<String, Double> discountRates)  { this.discountRates = discountRates; }

    public String getPeakStartTime()            { return peakStartTime; }
    public void setPeakStartTime(String time)   { this.peakStartTime = time; }

    public String getPeakEndTime()              { return peakEndTime; }
    public void setPeakEndTime(String time)     { this.peakEndTime = time; }

    public String getPeakStartTime2()           { return peakStartTime2; }
    public void setPeakStartTime2(String time)  { this.peakStartTime2 = time; }

    public String getPeakEndTime2()             { return peakEndTime2; }
    public void setPeakEndTime2(String time)    { this.peakEndTime2 = time; }

    public String getAdminPassword()              { return adminPassword; }
    public void setAdminPassword(String password) { this.adminPassword = password; }
}
