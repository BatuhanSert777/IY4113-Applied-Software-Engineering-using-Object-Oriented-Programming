public class FareResult {

    private final double baseFare;
    private final double discountedFare;
    private final double chargedFare;

    public FareResult(double baseFare, double discountedFare, double chargedFare) {
        this.baseFare       = baseFare;
        this.discountedFare = discountedFare;
        this.chargedFare    = chargedFare;
    }

    public double getBaseFare()       { return baseFare; }
    public double getDiscountedFare() { return discountedFare; }
    public double getChargedFare()    { return chargedFare; }
}
