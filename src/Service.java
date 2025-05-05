// ==================================
// Service.java
// Description: Defines Emergency Services
// ==================================

public enum Service {
    FIRE, POLICE, AMBULANCE;

    /**
     * Returns a nicely formatted service name.
     */
    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
