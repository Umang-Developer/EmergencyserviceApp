// ==================================
// EmergencyCall.java
// Description: Represents a single emergency call
// ==================================

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.EnumSet;

/**
 * EmergencyCall class represents details of a recorded emergency call.
 * It holds caller info, description, selected services, and timestamp.
 */
public class EmergencyCall implements Serializable {

    private String callerName;                     // Name of the caller
    private String phoneNumber;                    // Phone number (+44 formatted)
    private String description;                    // Description of emergency
    private LocalDateTime timestamp;               // Time when the call was recorded
    private EnumSet<Service> servicesRequired;     // Set of required services

    private static final long serialVersionUID = 1L; // Java recommended for Serializable classes

    /**
     * Constructor for EmergencyCall object.
     *
     * @param callerName        The caller's name
     * @param phoneNumber       The caller's phone number (prefixed by +44)
     * @param description       Emergency description
     * @param servicesRequired  List of required services
     * @param timestamp         Date and time when the call is created
     */
    public EmergencyCall(String callerName, String phoneNumber, String description,
                         EnumSet<Service> servicesRequired, LocalDateTime timestamp) {
        this.callerName = callerName;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.servicesRequired = servicesRequired;
        this.timestamp = timestamp;
    }

    // --- Getters ---

    public String getCallerName() {
        return callerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public EnumSet<Service> getServicesRequired() {
        return servicesRequired;
    }

    /**
     * Check if this call requires a specific service.
     *
     * @param service The service to check
     * @return true if required, false otherwise
     */
    public boolean requiresService(Service service) {
        return servicesRequired.contains(service);
    }

    /**
     * String representation of the Emergency Call.
     */
    @Override
    public String toString() {
        return "Caller: " + callerName + ", Phone: " + phoneNumber +
                ", Emergency: " + description + ", Services: " + servicesRequired;
    }
}
