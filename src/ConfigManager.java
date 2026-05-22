import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

    private static final String CONFIG_PATH = "data/config.json";

    private final Gson gson;

    public ConfigManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public AppConfig loadConfig() {
        AppConfig config;
        try {
            FileReader reader = new FileReader(CONFIG_PATH);
            config = gson.fromJson(reader, AppConfig.class);
            reader.close();

            if (config == null) {
                config = buildDefaultConfig();
            }
            System.out.println("Configuration loaded.");

        } catch (IOException e) {
            System.out.println("No config file found. Starting with default settings.");
            config = buildDefaultConfig();
        }
        return config;
    }

    public boolean saveConfig(AppConfig config) {
        boolean saved;
        try {
            FileWriter writer = new FileWriter(CONFIG_PATH);
            gson.toJson(config, writer);
            writer.close();
            System.out.println("Configuration saved to " + CONFIG_PATH);
            saved = true;
        } catch (IOException e) {
            System.out.println("Error: Could not save configuration. " + e.getMessage());
            saved = false;
        }
        return saved;
    }

    private AppConfig buildDefaultConfig() {
        AppConfig config = new AppConfig();
        config.loadDefaults();
        return config;
    }
}
