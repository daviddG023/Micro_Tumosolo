package StoreBuffer;

import java.util.ArrayList;
import java.util.List;
import reservation_tables.Point;

public class StoreBuffer {
    private String name;       // Name of the buffer (e.g., S1, S2, etc.)
    private boolean busy;      // Indicates if the buffer is busy
    private String address;    // Memory address being stored
    private String v;          // Value to store
    private String q;          // Dependency or reservation station
    private Integer time;      // Time to complete store operation
    private Integer pos;       // Position in execution table
    private List<Point> list;  // List of dependent instructions/points
    private String op;

    // Constructor
    public StoreBuffer(String name) {
        this.name = name;
        this.busy = false;     // Default to not busy
        this.address = null;   // Default to no address
        this.v = null;         // Default value is null
        this.q = null;         // Default dependency is null
        this.time = null;      // Default time is null
        this.pos = null;       // Default position is null
        this.list = new ArrayList<Point>(); // Initialize the list
        this.op = null;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public boolean getBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public String getA() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public List<Point> getList() {
        return list;
    }

    public void add(Point point) {
        this.list.add(point);
    }

    public void reset() {
        this.busy = false;
        this.address = null;
        this.v = null;
        this.q = null;
        this.time = null;
        this.pos = null;
        this.list.clear();
    }

    public void setAll(boolean busy, String address, String v, String q, Integer time, Integer pos,String op) {
        this.busy = busy;
        this.address = address;
        this.v = v;
        this.q = q;
        this.time = time;
        this.pos = pos;
        this.op = op;
    }
    public void setOp(String op) {
        this.op = op;
    }

    public String getOp() {
        return op;
    }


    @Override
    public String toString() {
        return "StoreBuffer{" +
               "name='" + name + '\'' +
               ", busy=" + busy +
               ", address='" + address + '\'' +
               ", v='" + v + '\'' +
               ", q='" + q + '\'' +
               ", time=" + time +
               ", pos=" + pos +
               ", list=" + list +
               '}';
    }
}
