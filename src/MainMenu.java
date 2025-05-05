// ==================================
// MainMenu.java
// ==================================

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * MainMenu provides the console interface for recording, viewing, and resolving emergency calls.
 */
public class MainMenu {

    // Scanner for user input
    private static Scanner scanner = new Scanner(System.in);

    // List to store all emergency calls
    private static CallList callList = new CallList();

    public static void main(String[] args) {
        // Display welcome message
        System.out.println("===== Emergency Services Call System =====");

        boolean exit = false;
        // Main loop for the menu
        while (!exit) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": addNewCall(); break; // Record new call
                case "2": viewCallsByService(Service.FIRE); break; // View Fire calls
                case "3": viewCallsByService(Service.POLICE); break; // View Police calls
                case "4": viewCallsByService(Service.AMBULANCE); break; // View Ambulance calls
                case "5": removeCall(); break; // Remove resolved call
                case "6":
                    System.out.println("✅ Goodbye!"); // Exit message
                    exit = true;
                    break;
                default:
                    System.out.println("❌ Invalid choice."); // Handle invalid input
            }
        }
    }

    // Display the main menu options
    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Record new emergency call");
        System.out.println("2. View Fire service calls");
        System.out.println("3. View Police service calls");
        System.out.println("4. View Ambulance service calls");
        System.out.println("5. Remove a resolved call");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    // Record a new emergency call with validation
    private static void addNewCall() {
        System.out.println("----- Record New Call -----");

        // Caller name validation
        String name;
        while (true) {
            System.out.print("Enter caller name (letters only): ");
            name = scanner.nextLine().trim();
            if (!name.matches("[A-Za-z ]+")) System.out.println("❌ Name must contain only letters.");
            else break;
        }

        // Phone number validation
        String phone;
        while (true) {
            System.out.print("Enter phone number (10-11 digits): ");
            phone = scanner.nextLine().trim();
            if (!phone.matches("\\d{10,11}")) System.out.println("❌ Invalid phone number.");
            else {
                phone = "+44" + phone; // Automatically add UK country code
                break;
            }
        }

        // Emergency description input
        System.out.print("Enter description of emergency: ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) description = "No description provided";

        // Services selection with validation
        EnumSet<Service> services = EnumSet.noneOf(Service.class);
        while (services.isEmpty()) {
            System.out.println("Enter required services (Example: FP = Fire & Police):");
            System.out.println("F = Fire, P = Police, A = Ambulance");
            System.out.print("Your input: ");
            String input = scanner.nextLine().toUpperCase().replaceAll("\\s+", "");

            if (!input.matches("^[FPA]+$")) {
                System.out.println("❌ Invalid input. Only F, P, A letters are allowed.");
                continue;
            }

            // Ensure no duplicate services
            Set<Character> unique = new HashSet<>();
            boolean valid = true;
            for (char c : input.toCharArray()) {
                if (!unique.add(c)) {
                    System.out.println("❌ Duplicate services are not allowed (e.g., FF, PP). Try again.");
                    valid = false;
                    break;
                }
            }
            if (!valid) continue;

            // Add selected services
            for (char c : unique) {
                switch (c) {
                    case 'F': services.add(Service.FIRE); break;
                    case 'P': services.add(Service.POLICE); break;
                    case 'A': services.add(Service.AMBULANCE); break;
                }
            }

            if (services.isEmpty()) {
                System.out.println("❌ You must select at least one service.");
            }
        }

        // Create and add the call to the list
        EmergencyCall call = new EmergencyCall(name, phone, description, services, LocalDateTime.now());
        callList.addCall(call);
        System.out.println("✅ Emergency call recorded successfully.");
    }

    // Display all calls filtered by service
    private static void viewCallsByService(Service service) {
        System.out.println("----- " + service.toString() + " Service Calls -----");
        ArrayList<EmergencyCall> calls = callList.getCallsByService(service);
        if (calls.isEmpty()) {
            System.out.println("No calls found.");
            return;
        }
        printTable(calls); // Display formatted table
    }

    // Remove a resolved call
    private static void removeCall() {
        ArrayList<EmergencyCall> calls = callList.getAllCalls();
        if (calls.isEmpty()) {
            System.out.println("No calls to remove.");
            return;
        }

        System.out.println("----- Remove Call -----");
        printTable(calls); // Display list of calls

        System.out.print("Select call number to remove (0 to cancel): ");
        String input = scanner.nextLine();
        int index;

        try {
            index = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input.");
            return;
        }

        // Valid removal or cancel option
        if (index >= 1 && index <= calls.size()) {
            callList.removeCall(calls.get(index - 1));
            System.out.println("✅ Call removed.");
        } else if (index == 0) {
            System.out.println("Cancelled.");
        } else {
            System.out.println("Invalid number.");
        }
    }

    // ======================================
    // AUTO-WRAP PROFESSIONAL TABLE FUNCTION
    // ======================================

    // Display emergency calls in a table format with auto-wrap for long descriptions
    private static void printTable(ArrayList<EmergencyCall> calls) {
        System.out.println("+----+------------+----------------+------------------------------------------------------------+----------------------------+---------------------+");
        System.out.println("| No | Caller     | Phone          | Description                                                | Services                   | Time                |");
        System.out.println("+----+------------+----------------+------------------------------------------------------------+----------------------------+---------------------+");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        // Display each call
        for (int i = 0; i < calls.size(); i++) {
            EmergencyCall call = calls.get(i);
            String services = formatServices(call.getServicesRequired());
            printWrappedRow(i + 1, call.getCallerName(), call.getPhoneNumber(), call.getDescription(), services, call.getTimestamp().format(formatter));
        }

        System.out.println("+----+------------+----------------+------------------------------------------------------------+----------------------------+---------------------+");
    }

    // Print long descriptions in multiple lines automatically
    private static void printWrappedRow(int no, String caller, String phone, String desc, String services, String time) {
        int descWidth = 60;
        ArrayList<String> descLines = wrapText(desc, descWidth);

        for (int i = 0; i < descLines.size(); i++) {
            if (i == 0) {
                // First line with all fields
                System.out.printf("| %-2d | %-10s | %-14s | %-60s | %-26s | %-19s |\n",
                        no, caller, phone, descLines.get(i), services, time);
            } else {
                // Next lines for wrapped description only
                System.out.printf("| %-2s | %-10s | %-14s | %-60s | %-26s | %-19s |\n",
                        "", "", "", descLines.get(i), "", "");
            }
        }
    }

    // Break long text into multiple lines (word wrapping)
    private static ArrayList<String> wrapText(String text, int width) {
        ArrayList<String> lines = new ArrayList<>();
        while (text.length() > width) {
            lines.add(text.substring(0, width));
            text = text.substring(width);
        }
        lines.add(text);
        return lines;
    }

    // Format services as a comma-separated string
    private static String formatServices(EnumSet<Service> services) {
        StringBuilder sb = new StringBuilder();
        for (Service s : services) {
            sb.append(s.toString()).append(", ");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "";
    }
}
