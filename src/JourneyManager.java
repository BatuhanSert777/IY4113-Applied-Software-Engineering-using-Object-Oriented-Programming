import java.util.ArrayList;
import java.util.List;

public class JourneyManager {

    private final List<Journey> journeys;
    private final FareCalculator fareCalculator;
    private int nextJourneyId;

    public JourneyManager(FareCalculator fareCalculator) {
        this.fareCalculator = fareCalculator;
        this.journeys       = new ArrayList<>();
        this.nextJourneyId  = 1;
    }

    public Journey addJourney(String date, String time,
                              int fromZone, int toZone,
                              PassengerType passengerType,
                              TimeBand timeBand,
                              PaymentOption paymentOption) {
        Journey journey = new Journey(
                nextJourneyId, date, time,
                fromZone, toZone,
                passengerType, timeBand, paymentOption);

        FareResult result = fareCalculator.calculateFare(
                fromZone, toZone, passengerType, timeBand);
        journey.setFares(result);

        journeys.add(journey);
        nextJourneyId++;

        return journey;
    }

    public Journey editJourney(int journeyId, String date, String time,
                               int fromZone, int toZone,
                               PassengerType passengerType,
                               TimeBand timeBand,
                               PaymentOption paymentOption) {
        int index = findIndexById(journeyId);
        if (index == -1) {
            return null;
        }

        Journey journey = journeys.get(index);
        journey.setDate(date);
        journey.setTime(time);
        journey.setFromZone(fromZone);
        journey.setToZone(toZone);
        journey.setPassengerType(passengerType);
        journey.setTimeBand(timeBand);
        journey.setPaymentOption(paymentOption);

        recalculateAllFares();

        return journeys.get(index);
    }

    public boolean deleteJourney(int journeyId) {
        int index = findIndexById(journeyId);
        if (index == -1) {
            return false;
        }
        journeys.remove(index);
        recalculateAllFares();
        return true;
    }

    public void loadJourneys(List<Journey> imported) {
        journeys.clear();
        journeys.addAll(imported);

        int maxId = 0;
        for (Journey j : journeys) {
            if (j.getJourneyId() > maxId) {
                maxId = j.getJourneyId();
            }
        }
        nextJourneyId = maxId + 1;

        recalculateAllFares();
    }

    public void printAllJourneys() {
        if (journeys.isEmpty()) {
            System.out.println("No journeys recorded yet.");
            return;
        }
        System.out.println("\n--- All Journeys Today ---");
        for (Journey j : journeys) {
            j.printSummary();
        }
    }

    public List<Journey> getAllJourneys() {
        return new ArrayList<>(journeys);
    }

    public boolean isEmpty() {
        return journeys.isEmpty();
    }

    private void recalculateAllFares() {
        fareCalculator.resetTotals();
        for (Journey j : journeys) {
            FareResult result = fareCalculator.calculateFare(
                    j.getFromZone(), j.getToZone(),
                    j.getPassengerType(), j.getTimeBand());
            j.setFares(result);
        }
    }

    private int findIndexById(int journeyId) {
        int foundIndex = -1;
        for (int i = 0; i < journeys.size(); i++) {
            if (journeys.get(i).getJourneyId() == journeyId) {
                foundIndex = i;
            }
        }
        return foundIndex;
    }
}
