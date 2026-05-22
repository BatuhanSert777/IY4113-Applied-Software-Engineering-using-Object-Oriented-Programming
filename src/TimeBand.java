public enum TimeBand {

    PEAK    ("Peak"),
    OFF_PEAK("Off-Peak");

    private final String displayName;

    TimeBand(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    public static TimeBand fromString(String input) {
        if (input == null) {
            return null;
        }
        String cleaned = input.trim().toLowerCase();
        switch (cleaned) {
            case "peak":
                return PEAK;
            case "off-peak":
            case "off peak":
            case "offpeak":
            case "off_peak":
                return OFF_PEAK;
            default:
                return null;
        }
    }
}
