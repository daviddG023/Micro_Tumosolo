package LoadBuffer;

import java.util.ArrayList;
import java.util.List;

import reservation_tables.Point;

public class LoadBuffer {
    private String name;      // Name of the buffer (e.g., L1, L2, L3, etc.)
    private boolean busy;     // Indicates if the buffer is busy (true/false)
    private String address;   // Memory address being loaded
    private Integer Time;
    private Integer pos; 
    private List<Point> List;
    private String op;

    // Constructor
    public LoadBuffer(String name) {
        this.name = name;
        this.busy = false;    // Default to not busy
        this.address = null;  // Default to no address
        this.op = null;
        this.Time = null;
        this.pos=null;
        this.List = new ArrayList<Point>();
        
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for busy status
    public boolean getBusy() {
        return busy;
    }

    // Setter for busy status
    public void setBusy(boolean busy) {
        this.busy = busy;
    }
    public void reset() {
    	this.busy = false;
    	this.address = null;
    	this.Time =null;
    	this.List=new ArrayList<Point>();
    }
    public void setBusy() {
        this.busy = false;
    }

    // Getter for address
    public String getA() {
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
    public void setTime(Integer i) {
    	this.Time =i;
    }
    public Integer getTime() {
		return Time;
	}
    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public Integer getPos() {
        return pos;
    }
    public void setOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }
    public List<Point> getList(){
    	return List;
    }
    public void add(Point p) {
    	List.add(p);
    }
    public void setAll(boolean busy, String address, Integer Time, Integer pos, String op) {
        this.busy = busy;
        this.address = address;
        this.Time = Time;
        this.pos = pos;
        this.op = op;
    }


    @Override
    public String toString() {
        return "LoadBuffer{" +
               "name='" + name + '\'' +
               "time='" + Time + '\'' +
               ", busy=" + busy +
               ", address='" + address + '\'' +
               ", Point='" + List + '\'' +
               '}';
    }
}
