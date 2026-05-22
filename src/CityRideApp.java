import java.io.File;
import java.util.List;
import java.util.Scanner;

public class CityRideApp {

    private final Scanner scanner;
    private final InputHelper input;
    private final ConfigManager configManager;
    private final ProfileManager profileManager;
    private final CsvHandler csvHandler;
    private final ReportManager reportManager;

    private AppConfig config;

    public CityRideApp() {
        this.scanner        = new Scanner(System.in);
        this.input          = new InputHelper(scanner);
        this.configManager  = new ConfigManager();
        this.profileManager = new ProfileManager();
        this.csvHandler     = new CsvHandler();
        this.reportManager  = new ReportManager();
    }

    public void run() {
        printWelcome();

        new File("data/profiles").mkdirs();
        new File("data/journeys").mkdirs();
        new File("reports").mkdirs();

        config = configManager.loadConfig();

        boolean appRunning = true;
        while (appRunning) {
            printRoleMenu();
            int choice = input.readMenuChoice("Select role: ");

            switch (choice) {
                case 1:
                    startRiderSession();
                    break;
                case 2:
                    startAdminSession();
                    break;
                case 0:
                    System.out.println("\nExiting CityRide Lite. Goodbye!");
                    appRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 0.");
            }
        }

        scanner.close();
    }

    private void printWelcome() {
        System.out.println("==========================================");
        System.out.println("        CityRide Lite — Part 2");
        System.out.println("==========================================");
    }

    private void printRoleMenu() {
        System.out.println("\n--- Select Role ---");
        System.out.println("1. Rider");
        System.out.println("2. Admin");
        System.out.println("0. Exit");
    }

    private void startRiderSession() {
        Rider rider = null;

        boolean hasExistingProfile = input.readYesNo("Do you have a saved profile to load?");

        if (hasExistingProfile) {
            String name = input.readNonEmptyString("Enter your name to load profile: ");
            rider = profileManager.loadProfile(name);

            if (rider == null) {
                System.out.println("Profile not found. You will need to create a new one.");
            }
        }

        if (rider == null) {
            rider = createNewProfile();
        }

        FareCalculator fareCalculator = new FareCalculator(config);
        JourneyManager journeyManager = new JourneyManager(fareCalculator);

        JourneyStore journeyStore = new JourneyStore();
        List<Journey> savedJourneys = journeyStore.loadJourneys(rider.getName());
        if (!savedJourneys.isEmpty()) {
            boolean resume = input.readYesNo(
                    "Found " + savedJourneys.size() + " saved journey(s). Resume previous session?");
            if (resume) {
                journeyManager.loadJourneys(savedJourneys);
                System.out.println("Previous session resumed.");
            }
        }

        RiderMenu riderMenu = new RiderMenu(
                rider, journeyManager, profileManager, journeyStore,
                csvHandler, reportManager, config, input);

        riderMenu.start();
    }

    private Rider createNewProfile() {
        System.out.println("\n--- Create New Profile ---");
        String name           = input.readNonEmptyString("Your name: ");
        PassengerType type    = input.readPassengerType();
        PaymentOption payment = input.readPaymentOption();

        Rider rider = new Rider(name, type, payment);
        System.out.println("\nProfile created for: " + name);

        boolean saveNow = input.readYesNo("Save this profile now?");
        if (saveNow) {
            profileManager.saveProfile(rider);
        }

        return rider;
    }

    private void startAdminSession() {
        Admin admin = new Admin();
        String password = input.readLine("Enter admin password: ");

        if (!admin.authenticate(password, config)) {
            System.out.println("Incorrect password. Access denied.");
            return;
        }

        AdminMenu adminMenu = new AdminMenu(config, configManager, input);
        adminMenu.start();
    }
}
