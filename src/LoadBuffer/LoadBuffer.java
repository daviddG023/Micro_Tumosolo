package LoadBuffer;

public class LoadBuffer {
    private String name;      // Name of the buffer (e.g., L1, L2, L3, etc.)
    private boolean busy;     // Indicates if the buffer is busy (true/false)
    private String address;   // Memory address being loaded

    // Constructor
    public LoadBuffer(String name) {
        this.name = name;
        this.busy = false;    // Default to not busy
        this.address = null;  // Default to no address
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for busy status
    public boolean isBusy() {
        return busy;
    }

    // Setter for busy status
    public void setBusy(boolean busy) {
        this.busy = busy;
    }
    public void reset() {
    	this.busy = false;
    	this.address = null;
    }
    public void setBusy() {
        this.busy = false;
    }

    // Getter for address
    public String getAddress() {
        return address;
    }

    // Setter for address
    public void setAddress(String address) {
        this.address = address;
    }
 // Setter for address
    public void resetAddress() {
        this.address = null;
    }
    @Override
    public String toString() {
        return "LoadBuffer{" +
               "name='" + name + '\'' +
               ", busy=" + busy +
               ", address='" + address + '\'' +
               '}';
    }
}
