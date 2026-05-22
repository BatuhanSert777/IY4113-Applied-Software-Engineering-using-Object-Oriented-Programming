public class Rider extends User {

    private PassengerType passengerType;
    private PaymentOption defaultPayment;

    public Rider(String name, PassengerType passengerType, PaymentOption defaultPayment) {
        super(name);
        this.passengerType  = passengerType;
        this.defaultPayment = defaultPayment;
    }

    @Override
    public String getRole() {
        return "Rider";
    }

    public void printProfile() {
        System.out.println("--- Rider Profile ---");
        System.out.println("Name:            " + getName());
        System.out.println("Passenger type:  " + passengerType.getDisplayName());
        System.out.println("Default payment: " + defaultPayment.getDisplayName());
    }

    public PassengerType getPassengerType()                     { return passengerType; }
    public void setPassengerType(PassengerType passengerType)   { this.passengerType = passengerType; }

    public PaymentOption getDefaultPayment()                    { return defaultPayment; }
    public void setDefaultPayment(PaymentOption defaultPayment) { this.defaultPayment = defaultPayment; }
}
