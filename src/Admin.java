public class Admin extends User {

    public Admin() {
        super("Administrator");
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    public boolean authenticate(String enteredPassword, AppConfig config) {
        return config.isAdminPasswordCorrect(enteredPassword);
    }
}
