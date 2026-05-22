import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportManager {

    private static final String REPORTS_FOLDER = "reports/";

    public void printSummary(List<Journey> journeys, String riderName) {
        if (journeys.isEmpty()) {
            System.out.println("No journeys to summarise.");
            return;
        }

        Summary s = buildSummary(journeys);

        System.out.println("\n========== End-of-Day Summary ==========");
        System.out.println("Rider:                  " + riderName);
        System.out.println("Total journeys:         " + s.totalJourneys);
        System.out.printf( "Total charged:          £%.2f%n", s.totalCharged);
        System.out.printf( "Average per journey:    £%.2f%n", s.averageCost);
        System.out.printf( "Total without caps:     £%.2f%n", s.totalWithoutCaps);
        System.out.printf( "Savings from caps:      £%.2f%n", s.capSavings);
        System.out.println("Daily cap reached:      " + (s.capSavings > 0.001 ? "Yes" : "No"));

        if (s.mostExpensiveId != -1) {
            System.out.printf("Most expensive journey: ID %d (£%.2f)%n",
                    s.mostExpensiveId, s.mostExpensiveCharge);
        }

        System.out.println("\nPeak journeys:          " + s.peakCount);
        System.out.println("Off-peak journeys:      " + s.offPeakCount);

        System.out.println("\nJourneys by zones crossed:");
        for (int z = 1; z <= 5; z++) {
            int count = s.zonesCrossedCounts.getOrDefault(z, 0);
            if (count > 0) {
                System.out.println("  " + z + " zone(s): " + count + " journey(s)");
            }
        }
        System.out.println("=========================================");
    }

    public boolean saveTextReport(List<Journey> journeys, String riderName) {
        if (journeys.isEmpty()) {
            System.out.println("No journeys to export.");
            return false;
        }

        String filePath = buildReportPath(riderName, "txt");
        Summary s = buildSummary(journeys);
        boolean saved;

        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write("CityRide Lite — End-of-Day Report\n");
            writer.write("Rider: " + riderName + "\n");
            writer.write("Date:  " + getTodayDateString() + "\n\n");
            writer.write("Total journeys:      " + s.totalJourneys + "\n");
            writer.write(String.format("Total charged:       £%.2f%n", s.totalCharged));
            writer.write(String.format("Average per journey: £%.2f%n", s.averageCost));
            writer.write(String.format("Savings from caps:   £%.2f%n", s.capSavings));
            writer.write("Cap reached:         " + (s.capSavings > 0.001 ? "Yes" : "No") + "\n");
            writer.write("Peak journeys:       " + s.peakCount + "\n");
            writer.write("Off-peak journeys:   " + s.offPeakCount + "\n\n");

            writer.write("--- Journey Details ---\n");
            for (Journey j : journeys) {
                writer.write(String.format(
                        "[%d] %s %s | Zones %d to %d | %s | %s | Charged: £%.2f%n",
                        j.getJourneyId(), j.getDate(), j.getTime(),
                        j.getFromZone(), j.getToZone(),
                        j.getPassengerType().getDisplayName(),
                        j.getTimeBand().getDisplayName(),
                        j.getChargedFare()
                ));
            }

            writer.close();
            System.out.println("Text report saved to: " + filePath);
            saved = true;

        } catch (IOException e) {
            System.out.println("Error: Could not save text report. " + e.getMessage());
            saved = false;
        }
        return saved;
    }

    public boolean saveCsvReport(List<Journey> journeys, String riderName) {
        if (journeys.isEmpty()) {
            System.out.println("No journeys to export.");
            return false;
        }

        String filePath = buildReportPath(riderName, "csv");
        boolean saved;

        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write("ID,Date,Time,FromZone,ToZone,PassengerType,TimeBand,BaseFare,DiscountedFare,ChargedFare\n");

            for (Journey j : journeys) {
                writer.write(String.format(
                        "%d,%s,%s,%d,%d,%s,%s,%.2f,%.2f,%.2f%n",
                        j.getJourneyId(), j.getDate(), j.getTime(),
                        j.getFromZone(), j.getToZone(),
                        j.getPassengerType().getDisplayName(),
                        j.getTimeBand().getDisplayName(),
                        j.getBaseFare(), j.getDiscountedFare(), j.getChargedFare()
                ));
            }

            writer.close();
            System.out.println("CSV report saved to: " + filePath);
            saved = true;

        } catch (IOException e) {
            System.out.println("Error: Could not save CSV report. " + e.getMessage());
            saved = false;
        }
        return saved;
    }

    private Summary buildSummary(List<Journey> journeys) {
        Summary s = new Summary();
        s.totalJourneys = journeys.size();

        for (Journey j : journeys) {
            s.totalCharged     += j.getChargedFare();
            s.totalWithoutCaps += j.getDiscountedFare();

            if (j.getTimeBand() == TimeBand.PEAK) {
                s.peakCount++;
            } else {
                s.offPeakCount++;
            }

            int zones = j.getZonesCrossed();
            s.zonesCrossedCounts.put(zones,
                    s.zonesCrossedCounts.getOrDefault(zones, 0) + 1);

            if (j.getChargedFare() > s.mostExpensiveCharge) {
                s.mostExpensiveCharge = j.getChargedFare();
                s.mostExpensiveId     = j.getJourneyId();
            }
        }

        s.capSavings = s.totalWithoutCaps - s.totalCharged;

        if (s.totalJourneys > 0) {
            s.averageCost = s.totalCharged / s.totalJourneys;
        }

        return s;
    }

    private String buildReportPath(String riderName, String extension) {
        String safeName = riderName.trim().replace(" ", "_");
        String date = getTodayDateString().replace("/", "-");
        return REPORTS_FOLDER + date + "_" + safeName + "_report." + extension;
    }

    private String getTodayDateString() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private static class Summary {
        int    totalJourneys       = 0;
        double totalCharged        = 0.0;
        double totalWithoutCaps    = 0.0;
        double capSavings          = 0.0;
        double averageCost         = 0.0;
        int    peakCount           = 0;
        int    offPeakCount        = 0;
        int    mostExpensiveId     = -1;
        double mostExpensiveCharge = -1.0;
        Map<Integer, Integer> zonesCrossedCounts = new HashMap<>();
    }
}
