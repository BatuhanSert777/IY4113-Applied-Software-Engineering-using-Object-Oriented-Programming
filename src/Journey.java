public class Journey {

    private final int journeyId;

    private String date;
    private String time;
    private int fromZone;
    private int toZone;
    private PassengerType passengerType;
    private TimeBand timeBand;
    private PaymentOption paymentOption;

    private double baseFare;
    private double discountedFare;
    private double chargedFare;

    public Journey(int journeyId, String date, String time,
                   int fromZone, int toZone,
                   PassengerType passengerType,
                   TimeBand timeBand,
                   PaymentOption paymentOption) {
        this.journeyId     = journeyId;
        this.date          = date;
        this.time          = time;
        this.fromZone      = fromZone;
        this.toZone        = toZone;
        this.passengerType = passengerType;
        this.timeBand      = timeBand;
        this.paymentOption = paymentOption;
    }

    public void setFares(FareResult result) {
        this.baseFare       = result.getBaseFare();
        this.discountedFare = result.getDiscountedFare();
        this.chargedFare    = result.getChargedFare();
    }

    public int getZonesCrossed() {
        return Math.abs(toZone - fromZone) + 1;
    }

    public void printSummary() {
        double discountSaving = baseFare - discountedFare;
        double capSaving      = discountedFare - chargedFare;

        System.out.printf(
            "[%d] %s %s | Zones %d→%d (%d zones) | %s | %s | Payment: %s%n",
            journeyId, date, time,
            fromZone, toZone, getZonesCrossed(),
            passengerType.getDisplayName(),
            timeBand.getDisplayName(),
            paymentOption.getDisplayName()
        );
        System.out.printf(
            "     Base: £%.2f | Discount saving: £%.2f | Cap saving: £%.2f | Charged: £%.2f%n",
            baseFare, discountSaving, capSaving, chargedFare
        );
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int getJourneyId()               { return journeyId; }
    public String getDate()                 { return date; }
    public String getTime()                 { return time; }
    public int getFromZone()                { return fromZone; }
    public int getToZone()                  { return toZone; }
    public PassengerType getPassengerType() { return passengerType; }
    public TimeBand getTimeBand()           { return timeBand; }
    public PaymentOption getPaymentOption() { return paymentOption; }
    public double getBaseFare()             { return baseFare; }
    public double getDiscountedFare()       { return discountedFare; }
    public double getChargedFare()          { return chargedFare; }

    // ── Setters ──────────────────────────────────────────────────────────────

    public void setDate(String date)                          { this.date = date; }
    public void setTime(String time)                          { this.time = time; }
    public void setFromZone(int fromZone)                     { this.fromZone = fromZone; }
    public void setToZone(int toZone)                         { this.toZone = toZone; }
    public void setPassengerType(PassengerType passengerType) { this.passengerType = passengerType; }
    public void setTimeBand(TimeBand timeBand)                { this.timeBand = timeBand; }
    public void setPaymentOption(PaymentOption paymentOption) { this.paymentOption = paymentOption; }
}
