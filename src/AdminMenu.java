import java.util.Map;

public class AdminMenu {

    private static final String VALID_TYPES = "ADULT, STUDENT, CHILD, SENIOR_CITIZEN";

    private final AppConfig config;
    private final ConfigManager configManager;
    private final InputHelper input;

    private boolean running;

    public AdminMenu(AppConfig config, ConfigManager configManager, InputHelper input) {
        this.config        = config;
        this.configManager = configManager;
        this.input         = input;
        this.running       = true;
    }

    public void start() {
        System.out.println("\nAdmin access granted.");

        while (running) {
            printMenu();
            int choice = input.readMenuChoice("\nYour choice: ");
            handleChoice(choice);
        }
    }

    private void printMenu() {
        System.out.println("\n--- Admin Menu ---");
        System.out.println(" 1. View active configuration");
        System.out.println(" 2. Add a base fare rule");
        System.out.println(" 3. Update a base fare rule");
        System.out.println(" 4. Delete a base fare rule");
        System.out.println(" 5. Add a passenger discount");
        System.out.println(" 6. Update a passenger discount");
        System.out.println(" 7. Delete a passenger discount");
        System.out.println(" 8. Add a daily cap");
        System.out.println(" 9. Update a daily cap");
        System.out.println("10. Delete a daily cap");
        System.out.println("11. Update peak time window");
        System.out.println("12. Save configuration to file");
        System.out.println(" 0. Exit admin menu");
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1:  viewConfig();        break;
            case 2:  addBaseFare();       break;
            case 3:  updateBaseFare();    break;
            case 4:  deleteBaseFare();    break;
            case 5:  addDiscount();       break;
            case 6:  updateDiscount();    break;
            case 7:  deleteDiscount();    break;
            case 8:  addDailyCap();       break;
            case 9:  updateDailyCap();    break;
            case 10: deleteDailyCap();    break;
            case 11: updatePeakWindow();  break;
            case 12: configManager.saveConfig(config); break;
            case 0:  running = false;     break;
            default: System.out.println("Invalid choice. Please enter a number from the menu.");
        }
    }

    // ── View ─────────────────────────────────────────────────────────────────

    private void viewConfig() {
        System.out.println("\n--- Active Configuration ---");

        System.out.println("\nBase Fares:");
        for (Map.Entry<String, Double> entry : config.getBaseFares().entrySet()) {
            System.out.printf("  %-15s £%.2f%n", entry.getKey(), entry.getValue());
        }

        System.out.println("\nPassenger Discounts:");
        for (Map.Entry<String, Double> entry : config.getDiscountRates().entrySet()) {
            System.out.printf("  %-20s %.0f%%%n", entry.getKey(), entry.getValue() * 100);
        }

        System.out.println("\nDaily Caps:");
        for (Map.Entry<String, Double> entry : config.getDailyCaps().entrySet()) {
            System.out.printf("  %-20s £%.2f%n", entry.getKey(), entry.getValue());
        }

        System.out.println("\nPeak Time Windows:");
        System.out.println("  Morning peak: " + config.getPeakStartTime()
                + " to " + config.getPeakEndTime());
        System.out.println("  Evening peak: " + config.getPeakStartTime2()
                + " to " + config.getPeakEndTime2());
    }

    // ── Base fares ────────────────────────────────────────────────────────────

    private void addBaseFare() {
        System.out.println("\n--- Add Base Fare Rule ---");
        System.out.println("Format: <zones>:<TIMEBAND>   Example: 6:PEAK or 6:OFF_PEAK");
        viewConfig();

        String key = input.readNonEmptyString("Enter new fare key: ").toUpperCase().trim();

        if (config.getBaseFares().containsKey(key)) {
            System.out.println("Error: Key '" + key + "' already exists. Use option 3 to update it.");
            return;
        }

        double newFare = input.readPositiveDouble("Enter fare in pounds (e.g. 3.50): £");

        if (newFare <= 0) {
            System.out.println("Error: Fare must be greater than zero. No changes made.");
            return;
        }

        config.getBaseFares().put(key, newFare);
        System.out.println("Base fare added: " + key + " = £" + String.format("%.2f", newFare));
        System.out.println("Remember to save the configuration (option 12).");
    }

    private void updateBaseFare() {
        System.out.println("\n--- Update Base Fare ---");
        System.out.println("Format: <zones>:<TIMEBAND>   Example: 2:PEAK or 3:OFF_PEAK");
        viewConfig();

        String key = input.readNonEmptyString("Enter fare key to update: ").toUpperCase().trim();

        if (!config.getBaseFares().containsKey(key)) {
            System.out.println("Error: Key '" + key + "' does not exist. Use option 2 to add it.");
            return;
        }

        double newFare = input.readPositiveDouble("Enter new fare in pounds (e.g. 3.50): £");

        if (newFare <= 0) {
            System.out.println("Error: Fare must be greater than zero. No changes made.");
            return;
        }

        config.getBaseFares().put(key, newFare);
        System.out.println("Base fare updated: " + key + " = £" + String.format("%.2f", newFare));
        System.out.println("Remember to save the configuration (option 12).");
    }

    private void deleteBaseFare() {
        System.out.println("\n--- Delete Base Fare Rule ---");
        viewConfig();

        String key = input.readNonEmptyString("Enter fare key to delete: ").toUpperCase().trim();

        if (!config.getBaseFares().containsKey(key)) {
            System.out.println("Error: Key '" + key + "' does not exist. No changes made.");
            return;
        }

        boolean confirm = input.readYesNo("Are you sure you want to delete '" + key + "'?");

        if (!confirm) {
            System.out.println("Deletion cancelled. No changes made.");
            return;
        }

        config.getBaseFares().remove(key);
        System.out.println("Base fare deleted: " + key);
        System.out.println("Remember to save the configuration (option 12).");
    }

    // ── Passenger discounts ───────────────────────────────────────────────────

    private void addDiscount() {
        System.out.println("\n--- Add Passenger Discount ---");
        System.out.println("Valid types: " + VALID_TYPES);
        viewConfig();

        String key = input.readNonEmptyString("Enter passenger type to add: ").toUpperCase().trim();

        if (!isValidPassengerTypeKey(key)) {
            System.out.println("Error: '" + key + "' is not a valid passenger type.");
            System.out.println("Valid types: " + VALID_TYPES);
            return;
        }

        if (config.getDiscountRates().containsKey(key)) {
            System.out.println("Error: A discount for '" + key + "' already exists. Use option 6 to update it.");
            return;
        }

        double newRate = input.readPositiveDouble("Enter discount rate as decimal (e.g. 0.25 for 25%): ");

        if (newRate > 1.0) {
            System.out.println("Error: Discount rate cannot be greater than 1.00 (100%). No changes made.");
            return;
        }

        config.getDiscountRates().put(key, newRate);
        System.out.printf("Discount added: %s = %.0f%%%n", key, newRate * 100);
        System.out.println("Remember to save the configuration (option 12).");
    }

    private void updateDiscount() {
        System.out.println("\n--- Update Passenger Discount ---");
        System.out.println("Valid types: " + VALID_TYPES);

        String key = input.readNonEmptyString("Enter passenger type to update: ").toUpperCase().trim();

        if (!config.getDiscountRates().containsKey(key)) {
            System.out.println("Error: Passenger type '" + key + "' not found. Use option 5 to add it.");
            return;
        }

        double newRate = input.readPositiveDouble("Enter new discount rate as decimal (e.g. 0.25 for 25%): ");

        if (newRate > 1.0) {
            System.out.println("Error: Discount rate cannot be greater than 1.00 (100%). No changes made.");
            return;
        }

        config.getDiscountRates().put(key, newRate);
        System.out.printf("Discount updated: %s = %.0f%%%n", key, newRate * 100);
        System.out.println("Remember to save the configuration (option 12).");
    }

    private void deleteDiscount() {
        System.out.println("\n--- Delete Passenger Discount ---");
        viewConfig();

        String key = input.readNonEmptyString("Enter passenger type to delete discount for: ").toUpperCase().trim();

        if (!config.getDiscountRates().containsKey(key)) {
            System.out.println("Error: No discount entry found for '" + key + "'. No changes made.");
            return;
        }

        boolean confirm = input.readYesNo("Are you sure you want to delete the discount for '" + key + "'?");

        if (!confirm) {
            System.out.println("Deletion cancelled. No changes made.");
            return;
        }

        config.getDiscountRates().remove(key);
        System.out.println("Discount deleted for: " + key);
        System.out.println("Remember to save the configuration (option 12).");
    }

    // ── Daily caps ────────────────────────────────────────────────────────────

    private void addDailyCap() {
        System.out.println("\n--- Add Daily Cap ---");
        System.out.println("Valid types: " + VALID_TYPES);
        viewConfig();

        String key = input.readNonEmptyString("Enter passenger type to add cap for: ").toUpperCase().trim();

        if (!isValidPassengerTypeKey(key)) {
            System.out.println("Error: '" + key + "' is not a valid passenger type.");
            System.out.println("Valid types: " + VALID_TYPES);
            return;
        }

        if (config.getDailyCaps().containsKey(key)) {
            System.out.println("Error: A cap for '" + key + "' already exists. Use option 9 to update it.");
            return;
        }

        double newCap = input.readPositiveDouble("Enter daily cap in pounds (e.g. 8.00): £");

        if (newCap <= 0) {
            System.out.println("Error: Daily cap must be greater than zero. No changes made.");
            return;
        }

        config.getDailyCaps().put(key, newCap);
        System.out.printf("Daily cap added: %s = £%.2f%n", key, newCap);
        System.out.println("Remember to save the configuration (option 12).");
    }

    private void updateDailyCap() {
        System.out.println("\n--- Update Daily Cap ---");
        System.out.println("Valid types: " + VALID_TYPES);

        String key = input.readNonEmptyString("Enter passenger type to update: ").toUpperCase().trim();

        if (!config.getDailyCaps().containsKey(key)) {
            System.out.println("Error: Passenger type '" + key + "' not found. Use option 8 to add it.");
            return;
        }

        double newCap = input.readPositiveDouble("Enter new daily cap in pounds (e.g. 8.00): £");

        if (newCap <= 0) {
            System.out.println("Error: Daily cap must be greater than zero. No changes made.");
            return;
        }

        config.getDailyCaps().put(key, newCap);
        System.out.printf("Daily cap updated: %s = £%.2f%n", key, newCap);
        System.out.println("Remember to save the configuration (option 12).");
    }

    private void deleteDailyCap() {
        System.out.println("\n--- Delete Daily Cap ---");
        viewConfig();

        String key = input.readNonEmptyString("Enter passenger type to delete cap for: ").toUpperCase().trim();

        if (!config.getDailyCaps().containsKey(key)) {
            System.out.println("Error: No cap entry found for '" + key + "'. No changes made.");
            return;
        }

        boolean confirm = input.readYesNo("Are you sure you want to delete the daily cap for '" + key + "'?");

        if (!confirm) {
            System.out.println("Deletion cancelled. No changes made.");
            return;
        }

        config.getDailyCaps().remove(key);
        System.out.println("Daily cap deleted for: " + key);
        System.out.println("Remember to save the configuration (option 12).");
    }

    // ── Peak window ───────────────────────────────────────────────────────────

    private void updatePeakWindow() {
        System.out.println("\n--- Update Peak Time Windows ---");
        System.out.println("  1. Morning peak: " + config.getPeakStartTime()
                + " to " + config.getPeakEndTime());
        System.out.println("  2. Evening peak: " + config.getPeakStartTime2()
                + " to " + config.getPeakEndTime2());

        int windowChoice = input.readMenuChoice("Which window to update (1 or 2): ");
        if (windowChoice != 1 && windowChoice != 2) {
            System.out.println("Invalid choice. No changes made.");
            return;
        }

        System.out.println("Enter new start time:");
        String newStart = input.readTime();

        System.out.println("Enter new end time:");
        String newEnd = input.readTime();

        // String comparison works for HH:mm because lexicographic order matches time order
        if (newEnd.compareTo(newStart) <= 0) {
            System.out.println("Error: End time must be after start time. No changes made.");
            return;
        }

        if (windowChoice == 1) {
            config.setPeakStartTime(newStart);
            config.setPeakEndTime(newEnd);
            System.out.println("Morning peak window updated: " + newStart + " to " + newEnd);
        } else {
            config.setPeakStartTime2(newStart);
            config.setPeakEndTime2(newEnd);
            System.out.println("Evening peak window updated: " + newStart + " to " + newEnd);
        }
        System.out.println("Remember to save the configuration (option 12).");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private boolean isValidPassengerTypeKey(String key) {
        boolean valid = false;
        for (PassengerType type : PassengerType.values()) {
            if (type.name().equals(key)) {
                valid = true;
            }
        }
        return valid;
    }
}
