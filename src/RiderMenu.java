import java.util.List;

public class RiderMenu {

    private final Rider rider;
    private final JourneyManager journeyManager;
    private final ProfileManager profileManager;
    private final JourneyStore journeyStore;
    private final CsvHandler csvHandler;
    private final ReportManager reportManager;
    private final AppConfig config;
    private final InputHelper input;

    private boolean running;

    public RiderMenu(Rider rider,
                     JourneyManager journeyManager,
                     ProfileManager profileManager,
                     JourneyStore journeyStore,
                     CsvHandler csvHandler,
                     ReportManager reportManager,
                     AppConfig config,
                     InputHelper input) {
        this.rider          = rider;
        this.journeyManager = journeyManager;
        this.profileManager = profileManager;
        this.journeyStore   = journeyStore;
        this.csvHandler     = csvHandler;
        this.reportManager  = reportManager;
        this.config         = config;
        this.input          = input;
        this.running        = true;
    }

    public void start() {
        System.out.println("\nWelcome, " + rider.getName() + "!");
        rider.printProfile();

        while (running) {
            printMenu();
            int choice = input.readMenuChoice("\nYour choice: ");
            handleChoice(choice);
        }
    }

    private void printMenu() {
        System.out.println("\n--- Rider Menu ---");
        System.out.println("1.  Add journey");
        System.out.println("2.  Edit journey");
        System.out.println("3.  Delete journey");
        System.out.println("4.  List all journeys");
        System.out.println("5.  Import journeys from CSV");
        System.out.println("6.  Export journeys to CSV");
        System.out.println("7.  View end-of-day summary");
        System.out.println("8.  Save text report");
        System.out.println("9.  Save CSV report");
        System.out.println("10. View my profile");
        System.out.println("11. Save profile");
        System.out.println("0.  Save and exit");
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1:  addJourney();      break;
            case 2:  editJourney();     break;
            case 3:  deleteJourney();   break;
            case 4:  journeyManager.printAllJourneys(); break;
            case 5:  importFromCsv();   break;
            case 6:  exportToCsv();     break;
            case 7:  reportManager.printSummary(journeyManager.getAllJourneys(), rider.getName()); break;
            case 8:  reportManager.saveTextReport(journeyManager.getAllJourneys(), rider.getName()); break;
            case 9:  reportManager.saveCsvReport(journeyManager.getAllJourneys(), rider.getName()); break;
            case 10: rider.printProfile(); break;
            case 11: profileManager.saveProfile(rider); break;
            case 0:  handleExit();      break;
            default: System.out.println("Invalid choice. Please enter a number from the menu.");
        }
    }

    private void addJourney() {
        System.out.println("\n--- Add Journey ---");
        String date  = input.readDate();
        String time  = input.readTime();
        int fromZone = input.readZone("From zone");
        int toZone   = input.readZone("To zone");

        PassengerType type = rider.getPassengerType();
        System.out.println("Passenger type: " + type.getDisplayName() + " (from your profile)");

        TimeBand band = config.determineTimeBand(time);
        System.out.println("Time band: " + band.getDisplayName()
                + " (based on journey time and peak windows)");

        System.out.println("Default payment: " + rider.getDefaultPayment().getDisplayName());
        boolean useDefault = input.readYesNo("Use your default payment?");
        PaymentOption payment = useDefault ? rider.getDefaultPayment() : input.readPaymentOption();

        Journey added = journeyManager.addJourney(date, time, fromZone, toZone, type, band, payment);

        System.out.println("\nJourney added:");
        added.printSummary();
        System.out.printf("Running total charged today: £%.2f%n", calculateDayTotal());
    }

    private void editJourney() {
        if (journeyManager.isEmpty()) {
            System.out.println("No journeys to edit.");
            return;
        }

        System.out.println("\n--- Edit Journey ---");
        journeyManager.printAllJourneys();

        int id = input.readPositiveInt("Enter the ID of the journey to edit: ");

        System.out.println("Enter new details for journey " + id + ":");
        String date  = input.readDate();
        String time  = input.readTime();
        int fromZone = input.readZone("From zone");
        int toZone   = input.readZone("To zone");

        PassengerType type = rider.getPassengerType();
        System.out.println("Passenger type: " + type.getDisplayName() + " (from your profile)");
        TimeBand band = config.determineTimeBand(time);
        System.out.println("Time band: " + band.getDisplayName()
                + " (based on journey time and peak windows)");

        PaymentOption payment = input.readPaymentOption();

        Journey updated = journeyManager.editJourney(id, date, time, fromZone, toZone, type, band, payment);

        if (updated == null) {
            System.out.println("Error: Journey ID " + id + " not found.");
        } else {
            System.out.println("Journey updated:");
            updated.printSummary();
        }
    }

    private void deleteJourney() {
        if (journeyManager.isEmpty()) {
            System.out.println("No journeys to delete.");
            return;
        }

        System.out.println("\n--- Delete Journey ---");
        journeyManager.printAllJourneys();

        int id = input.readPositiveInt("Enter the ID of the journey to delete: ");
        boolean deleted = journeyManager.deleteJourney(id);

        if (deleted) {
            System.out.println("Journey " + id + " deleted. All fares have been recalculated.");
        } else {
            System.out.println("Error: Journey ID " + id + " not found.");
        }
    }

    private void importFromCsv() {
        String filePath = input.readNonEmptyString(
                "Enter CSV file path to import (e.g. data/journeys.csv): ");

        List<Journey> imported = csvHandler.importFromCsv(filePath);

        if (!imported.isEmpty()) {
            boolean replace = input.readYesNo("Replace current journeys with imported ones?");
            if (replace) {
                journeyManager.loadJourneys(imported);
                System.out.println("Journeys loaded and fares recalculated.");
            }
        }
    }

    private void exportToCsv() {
        if (journeyManager.isEmpty()) {
            System.out.println("No journeys to export.");
            return;
        }
        String filePath = input.readNonEmptyString(
                "Enter output file path (e.g. data/my_journeys.csv): ");
        csvHandler.exportToCsv(journeyManager.getAllJourneys(), filePath);
    }

    private void handleExit() {
        boolean saveProfile = input.readYesNo("Save your profile before exiting?");
        if (saveProfile) {
            profileManager.saveProfile(rider);
        }

        if (!journeyManager.isEmpty()) {
            journeyStore.saveJourneys(journeyManager.getAllJourneys(), rider.getName());
            System.out.println("Journeys saved to session file.");

            boolean exportCsv = input.readYesNo("Also export journeys to CSV?");
            if (exportCsv) {
                exportToCsv();
            }
        }

        System.out.println("Goodbye, " + rider.getName() + "!");
        running = false;
    }

    private double calculateDayTotal() {
        double total = 0.0;
        for (Journey j : journeyManager.getAllJourneys()) {
            total += j.getChargedFare();
        }
        return total;
    }
}
