public enum PassengerType {

    ADULT         (0.00, 8.00, "Adult"),
    STUDENT       (0.25, 6.00, "Student"),
    CHILD         (0.50, 4.00, "Child"),
    SENIOR_CITIZEN(0.30, 7.00, "Senior Citizen");

    private final double discountRate;
    private final double dailyCap;
    private final String displayName;

    PassengerType(double discountRate, double dailyCap, String displayName) {
        this.discountRate = discountRate;
        this.dailyCap     = dailyCap;
        this.displayName  = displayName;
    }

    public double getDiscountRate() { return discountRate; }
    public double getDailyCap()     { return dailyCap; }
    public String getDisplayName()  { return displayName; }

    public static PassengerType fromString(String input) {
        if (input == null) {
            return null;
        }
        String cleaned = input.trim().toLowerCase();
        switch (cleaned) {
            case "adult":
                return ADULT;
            case "student":
                return STUDENT;
            case "child":
                return CHILD;
            case "senior":
            case "senior citizen":
            case "senior_citizen":
                return SENIOR_CITIZEN;
            default:
                return null;
        }
    }
}
