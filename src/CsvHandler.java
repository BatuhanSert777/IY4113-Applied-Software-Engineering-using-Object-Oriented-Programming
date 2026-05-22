import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvHandler {

    private static final String CSV_HEADER =
            "ID,Date,Time,FromZone,ToZone,PassengerType,TimeBand,Payment,BaseFare,DiscountedFare,ChargedFare";

    private static final int COL_ID             = 0;
    private static final int COL_DATE           = 1;
    private static final int COL_TIME           = 2;
    private static final int COL_FROM_ZONE      = 3;
    private static final int COL_TO_ZONE        = 4;
    private static final int COL_PASSENGER_TYPE = 5;
    private static final int COL_TIME_BAND      = 6;
    private static final int COL_PAYMENT        = 7;

    public List<Journey> importFromCsv(String filePath) {
        List<Journey> journeys = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine(); // skip header row

            while ((line = reader.readLine()) != null) {
                Journey journey = parseLine(line);
                if (journey != null) {
                    journeys.add(journey);
                }
            }
            reader.close();
            System.out.println("Imported " + journeys.size() + " journey(s) from: " + filePath);

        } catch (IOException e) {
            System.out.println("Error: Could not open file '" + filePath + "'. " + e.getMessage());
        }

        return journeys;
    }

    public boolean exportToCsv(List<Journey> journeys, String filePath) {
        boolean success;
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(CSV_HEADER + "\n");

            for (Journey j : journeys) {
                writer.write(buildCsvLine(j) + "\n");
            }

            writer.close();
            System.out.println("Journeys exported to: " + filePath);
            success = true;

        } catch (IOException e) {
            System.out.println("Error: Could not write to file '" + filePath + "'. " + e.getMessage());
            success = false;
        }
        return success;
    }

    private String buildCsvLine(Journey j) {
        return j.getJourneyId() + ","
                + j.getDate() + ","
                + j.getTime() + ","
                + j.getFromZone() + ","
                + j.getToZone() + ","
                + j.getPassengerType().name() + ","
                + j.getTimeBand().name() + ","
                + j.getPaymentOption().name() + ","
                + String.format("%.2f", j.getBaseFare()) + ","
                + String.format("%.2f", j.getDiscountedFare()) + ","
                + String.format("%.2f", j.getChargedFare());
    }

    private Journey parseLine(String line) {
        Journey journey = null;
        try {
            String[] parts = line.split(",");

            int id                      = Integer.parseInt(parts[COL_ID].trim());
            String date                 = parts[COL_DATE].trim();
            String time                 = parts[COL_TIME].trim();
            int fromZone                = Integer.parseInt(parts[COL_FROM_ZONE].trim());
            int toZone                  = Integer.parseInt(parts[COL_TO_ZONE].trim());
            PassengerType passengerType = PassengerType.valueOf(parts[COL_PASSENGER_TYPE].trim());
            TimeBand timeBand           = TimeBand.valueOf(parts[COL_TIME_BAND].trim());
            PaymentOption payment       = PaymentOption.valueOf(parts[COL_PAYMENT].trim());

            journey = new Journey(id, date, time, fromZone, toZone,
                    passengerType, timeBand, payment);

        } catch (Exception e) {
            System.out.println("Warning: Skipping invalid CSV line: " + line);
        }
        return journey;
    }
}
