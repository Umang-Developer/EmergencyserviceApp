// ==================================
// EmergencyServiceManager.java
// Description: Manages emergency calls for GUI operations
// ==================================

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This class handles the emergency call list specifically for the GUI.
 * It allows adding, removing, saving, and loading calls.
 */
public class EmergencyServiceManager {

    private List<EmergencyCall> callList; // Internal list to store calls
    private final String FILE_NAME = "emergency_calls.dat"; // Separate file for GUI usage

    /**
     * Constructor loads existing calls if available.
     */
    public EmergencyServiceManager() {
        callList = loadCallsFromFile();
    }

    /**
     * Returns all stored calls.
     * @return List of EmergencyCall
     */
    public List<EmergencyCall> getCallList() {
        return callList;
    }

    /**
     * Adds a new call from the GUI form.
     *
     * @param name     Caller name
     * @param phone    Caller phone number
     * @param desc     Description of emergency
     * @param services Services selected by the user
     */
    public void addCallFromGUI(String name, String phone, String desc, List<String> services) {
        EnumSet<Service> serviceEnums = convertToEnumSet(services);
        callList.add(new EmergencyCall(name, phone, desc, serviceEnums, LocalDateTime.now()));
        saveCallsToFile();
    }

    /**
     * Removes a call by matching caller's name and phone number.
     *
     * @param name  Caller name
     * @param phone Phone number
     */
    public void removeCallFromGUI(String name, String phone) {
        callList.removeIf(call -> call.getCallerName().equalsIgnoreCase(name)
                && call.getPhoneNumber().equals(phone));
        saveCallsToFile();
    }

    /**
     * Converts service names to EnumSet<Service>.
     */
    private EnumSet<Service> convertToEnumSet(List<String> serviceStrings) {
        EnumSet<Service> services = EnumSet.noneOf(Service.class);
        for (String s : serviceStrings) {
            switch (s.toLowerCase()) {
                case "fire":
                    services.add(Service.FIRE);
                    break;
                case "police":
                    services.add(Service.POLICE);
                    break;
                case "ambulance":
                    services.add(Service.AMBULANCE);
                    break;
            }
        }
        return services;
    }

    /**
     * Saves the call list to the GUI file.
     */
    private void saveCallsToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(callList);
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    /**
     * Loads the call list from the GUI file.
     */
    @SuppressWarnings("unchecked")
    private List<EmergencyCall> loadCallsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (List<EmergencyCall>) in.readObject();
        } catch (Exception e) {
            System.out.println("Error loading from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
