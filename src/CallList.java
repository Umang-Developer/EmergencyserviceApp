// ==================================
// CallList.java
// Description: Manages all Emergency Calls (Add, Remove, Search, Save, Load)
// ==================================

import java.io.*;
import java.util.ArrayList;

/**X* This class manages a list of EmergencyCall objects.
 * Supports add, remove, search, save, and load operations.
 */
public class CallList implements Serializable {

    private ArrayList<EmergencyCall> calls; // Stores all calls
    private static final String DATA_FILE = "calls.dat"; // File to save/load calls

    /*** Constructor initializes the list and loads existing calls from the file.*/
    public CallList() {
        calls = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Adds a new call and automatically saves the list.
     * @param call The EmergencyCall to be added.
     */
    public void addCall(EmergencyCall call) {
        calls.add(call);
        saveToFile();
    }

    /**
     * Removes a call and saves the updated list.
     * @param call The EmergencyCall to be removed.
     * @return true if removal was successful, false otherwise.
     */
    public boolean removeCall(EmergencyCall call) {
        boolean removed = calls.remove(call);
        if (removed) saveToFile();
        return removed;
    }

    /**
     * Returns all calls.
     * @return List of all EmergencyCall objects.
     */
    public ArrayList<EmergencyCall> getAllCalls() {
        return new ArrayList<>(calls);
    }

    /**
     * Filters calls by a specific service.
     * @param service Service type to filter by.
     * @return List of calls requiring the specified service.
     */
    public ArrayList<EmergencyCall> getCallsByService(Service service) {
        ArrayList<EmergencyCall> result = new ArrayList<>();
        for (EmergencyCall call : calls) {
            if (call.requiresService(service)) {
                result.add(call);
            }
        }
        return result;
    }

    /**
     * Saves the call list to a local file.
     */
    private void saveToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(calls);
        } catch (IOException e) {
            System.out.println("Error saving calls: " + e.getMessage());
        }
    }

    /**
     * Loads the call list from the local file.
     */
    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            Object obj = in.readObject();
            if (obj instanceof ArrayList<?>) {
                calls = (ArrayList<EmergencyCall>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            calls = new ArrayList<>(); // Start fresh if file is missing or corrupted
        }
    }
}
