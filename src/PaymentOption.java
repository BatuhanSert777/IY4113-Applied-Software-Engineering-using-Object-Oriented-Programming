public enum PaymentOption {

    CONTACTLESS("Contactless"),
    OYSTER     ("Oyster Card"),
    CASH       ("Cash");

    private final String displayName;

    PaymentOption(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    public static PaymentOption fromString(String input) {
        if (input == null) {
            return null;
        }
        String cleaned = input.trim().toLowerCase();
        switch (cleaned) {
            case "contactless":
                return CONTACTLESS;
            case "oyster":
            case "oyster card":
                return OYSTER;
            case "cash":
                return CASH;
            default:
                return null;
        }
    }
}
