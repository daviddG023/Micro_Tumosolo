package RegFile;
import java.util.ArrayList;
import java.util.List;

import reservation_tables.Point;
public class RegFile {
    private String name;      // Name of the register (e.g., R1, R2, ...)
    private String qi;        // Reservation station (e.g., M1), or null if none
    private double value; 

    // Constructor
     public RegFile(String name) {
        this.name = name;
        this.qi = null;       // Default to no reservation station
        this.value = 0;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for Qi
    public String getQi() {
        return qi;
    }

    // Setter for Qi
    public void setQi(String qi) {
        this.qi = qi;
    }
    public double getValue() {
        return value;
    }

    // Setter for Qi
    public void setValue(double d) {
        this.value =d;
    }
 // reset for Qi
    public void resetRow() {
        this.qi = null;
    }

    @Override
    public String toString() {
        return "Register{" +
               "name='" + name + '\'' +
               ", qi=" + (qi == null ? "null" : qi) +
               ", value=" + value +

               '}';
    }
    
}

