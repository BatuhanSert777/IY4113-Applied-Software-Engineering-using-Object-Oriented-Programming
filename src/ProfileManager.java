import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProfileManager {

    private static final String PROFILES_FOLDER = "data/profiles/";

    private final Gson gson;

    public ProfileManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public boolean saveProfile(Rider rider) {
        String filePath = buildFilePath(rider.getName());

        ProfileData data    = new ProfileData();
        data.name           = rider.getName();
        data.passengerType  = rider.getPassengerType().name();
        data.defaultPayment = rider.getDefaultPayment().name();

        boolean saved;
        try {
            FileWriter writer = new FileWriter(filePath);
            gson.toJson(data, writer);
            writer.close();
            System.out.println("Profile saved to: " + filePath);
            saved = true;
        } catch (IOException e) {
            System.out.println("Error: Could not save profile. " + e.getMessage());
            saved = false;
        }
        return saved;
    }

    public Rider loadProfile(String riderName) {
        String filePath = buildFilePath(riderName);
        Rider rider = null;

        try {
            FileReader reader = new FileReader(filePath);
            ProfileData data  = gson.fromJson(reader, ProfileData.class);
            reader.close();

            if (data == null) {
                System.out.println("Profile file was empty.");
            } else {
                PassengerType type    = PassengerType.valueOf(data.passengerType);
                PaymentOption payment = PaymentOption.valueOf(data.defaultPayment);
                rider = new Rider(data.name, type, payment);
                System.out.println("Profile loaded for: " + data.name);
            }

        } catch (IOException e) {
            System.out.println("No saved profile found for: " + riderName);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Profile file contains invalid data and could not be loaded.");
        }

        return rider;
    }

    private String buildFilePath(String riderName) {
        String safeName = riderName.trim().replace(" ", "_");
        return PROFILES_FOLDER + safeName + ".json";
    }

    private static class ProfileData {
        String name;
        String passengerType;
        String defaultPayment;
    }
}
