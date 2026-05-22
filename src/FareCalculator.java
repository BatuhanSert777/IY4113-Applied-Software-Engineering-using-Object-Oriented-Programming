import java.util.EnumMap;

public class FareCalculator {

    private final EnumMap<PassengerType, Double> runningTotals;
    private final AppConfig config;

    public FareCalculator(AppConfig config) {
        this.config        = config;
        this.runningTotals = new EnumMap<>(PassengerType.class);
        resetTotals();
    }

    public void resetTotals() {
        for (PassengerType type : PassengerType.values()) {
            runningTotals.put(type, 0.0);
        }
    }

    public FareResult calculateFare(int fromZone, int toZone,
                                    PassengerType passengerType,
                                    TimeBand timeBand) {
        int zonesCrossed = Math.abs(toZone - fromZone) + 1;

        double baseFare       = config.getBaseFare(zonesCrossed, timeBand);
        double discountRate   = config.getDiscountRate(passengerType);
        double discountedFare = baseFare * (1.0 - discountRate);

        double dailyCap     = config.getDailyCap(passengerType);
        double currentTotal = runningTotals.get(passengerType);

        double chargedFare;
        if (currentTotal >= dailyCap) {
            chargedFare = 0.0;
        } else if (currentTotal + discountedFare > dailyCap) {
            chargedFare = dailyCap - currentTotal;
        } else {
            chargedFare = discountedFare;
        }

        runningTotals.put(passengerType, currentTotal + chargedFare);

        return new FareResult(baseFare, discountedFare, chargedFare);
    }

    public double getRunningTotal(PassengerType passengerType) {
        return runningTotals.get(passengerType);
    }
}
