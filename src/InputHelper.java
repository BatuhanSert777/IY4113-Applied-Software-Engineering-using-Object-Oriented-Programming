import java.util.Scanner;
import java.util.regex.Pattern;

public class InputHelper {

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^\\d{2}:\\d{2}$");

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readMenuChoice(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        int choice;
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            choice = -1;
        }
        return choice;
    }

    public int readPositiveInt(String prompt) {
        int result = -1;
        boolean keepAsking = true;
        while (keepAsking) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    result = value;
                    keepAsking = false;
                } else {
                    System.out.println("Please enter a number greater than zero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number, e.g. 1");
            }
        }
        return result;
    }

    public double readPositiveDouble(String prompt) {
        double result = 0.0;
        boolean keepAsking = true;
        while (keepAsking) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value >= 0) {
                    result = value;
                    keepAsking = false;
                } else {
                    System.out.println("Please enter 0 or a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number, e.g. 3.50");
            }
        }
        return result;
    }

    public String readDate() {
        String result = "";
        boolean keepAsking = true;
        while (keepAsking) {
            System.out.print("Enter date (format: dd/MM/yyyy, e.g. 06/04/2026): ");
            String input = scanner.nextLine().trim();
            if (DATE_PATTERN.matcher(input).matches()) {
                result = input;
                keepAsking = false;
            } else {
                System.out.println("Invalid format. Please use dd/MM/yyyy, e.g. 06/04/2026.");
            }
        }
        return result;
    }

    public String readTime() {
        String result = "";
        boolean keepAsking = true;
        while (keepAsking) {
            System.out.print("Enter time (format: HH:mm, e.g. 08:30): ");
            String input = scanner.nextLine().trim();
            if (TIME_PATTERN.matcher(input).matches()) {
                result = input;
                keepAsking = false;
            } else {
                System.out.println("Invalid format. Please use HH:mm, e.g. 08:30.");
            }
        }
        return result;
    }

    public int readZone(String prompt) {
        int result = -1;
        boolean keepAsking = true;
        while (keepAsking) {
            System.out.print(prompt + " (1 to 5, e.g. 2): ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= 1 && value <= 5) {
                    result = value;
                    keepAsking = false;
                } else {
                    System.out.println("Zone must be between 1 and 5. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
            }
        }
        return result;
    }

    public PassengerType readPassengerType() {
        PassengerType result = null;
        boolean keepAsking = true;
        while (keepAsking) {
            System.out.print("Passenger type (Adult / Student / Child / Senior Citizen): ");
            PassengerType type = PassengerType.fromString(scanner.nextLine());
            if (type != null) {
                result = type;
                keepAsking = false;
            } else {
                System.out.println("Not recognised. Please enter: Adult, Student, Child, or Senior Citizen.");
            }
        }
        return result;
    }

    public TimeBand readTimeBand() {
        TimeBand result = null;
        boolean keepAsking = true;
        while (keepAsking) {
            System.out.print("Time band (Peak or Off-Peak): ");
            TimeBand band = TimeBand.fromString(scanner.nextLine());
            if (band != null) {
                result = band;
                keepAsking = false;
            } else {
                System.out.println("Not recognised. Please enter: Peak or Off-Peak.");
            }
        }
        return result;
    }

    public PaymentOption readPaymentOption() {
        PaymentOption result = null;
        boolean keepAsking = true;
        while (keepAsking) {
            System.out.print("Payment option (Contactless / Oyster / Cash): ");
            PaymentOption option = PaymentOption.fromString(scanner.nextLine());
            if (option != null) {
                result = option;
                keepAsking = false;
            } else {
                System.out.println("Not recognised. Please enter: Contactless, Oyster, or Cash.");
            }
        }
        return result;
    }

    public String readNonEmptyString(String prompt) {
        String result = "";
        boolean keepAsking = true;
        while (keepAsking) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                result = input;
                keepAsking = false;
            } else {
                System.out.println("This field cannot be empty. Please enter a value.");
            }
        }
        return result;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public boolean readYesNo(String prompt) {
        boolean answer = false;
        boolean keepAsking = true;
        while (keepAsking) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                answer = true;
                keepAsking = false;
            } else if (input.equals("n") || input.equals("no")) {
                answer = false;
                keepAsking = false;
            } else {
                System.out.println("Please enter y (yes) or n (no).");
            }
        }
        return answer;
    }
}
