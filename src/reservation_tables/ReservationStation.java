package reservation_tables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationStation {
    private String name;  // Name of the station (e.g., A1, A2, A3)
    private boolean busy; // Indicates if the station is busy (true/false)
    private String op;    // Operation (e.g., ADD, MUL)
    private String vj;    // Value of the first operand
    private String vk;    // Value of the second operand
    private String qj;    // Tag of the first operand source (reservation station or register)
    private String qk;    // Tag of the second operand source (reservation station or register)
    private String a;     // Address or immediate value
    private Integer Time;
    private Integer pos; 
    private List<Point> List;
    
    // Constructor
    public ReservationStation(String name) {
        this.name = name;
        this.busy = false; // Default to not busy
        this.op = null;
        this.vj = null;
        this.vk = null;
        this.qj = null;
        this.qk = null;
        this.a = null;
        this.Time = null;
        this.pos=null;
        this.List = new ArrayList<Point>();
    }

    // Getters and setters for all fields
    public String getName() {
        return name;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getVj() {
        return vj;
    }

    public void setVj(String vj) {
        this.vj = vj;
    }

    public String getVk() {
        return vk;
    }

    public void setVk(String vk) {
        this.vk = vk;
    }

    public String getQj() {
        return qj;
    }

    public void setQj(String qj) {
        this.qj = qj;
    }

    public String getQk() {
        return qk;
    }

    public void setQk(String qk) {
        this.qk = qk;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }
    public boolean getBusy() {
        return busy;
    }
    // Reset all fields
    public void reset() {
        this.busy = false;
        this.op = null;
        this.vj = null;
        this.vk = null;
        this.qj = null;
        this.qk = null;
        this.a = null;
        this.Time=null;
        this.List = new ArrayList<Point>(); 
        this.pos=null; 
}

    // Get all fields as a Map
    public Map<String, Object> getAll() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", name);
        fields.put("busy", busy);
        fields.put("op", op);
        fields.put("vj", vj);
        fields.put("vk", vk);
        fields.put("qj", qj);
        fields.put("qk", qk);
        fields.put("a", a);
        fields.put("Time",Time);
        fields.put("pos",pos);
        return fields;
    }

    // Set all fields at once
    public void setAll(boolean busy, String op, String vj, String vk, 
    		String qj, String qk, String a,Integer Time,Integer pos) {
        this.busy = busy;
        this.op = op;
        this.vj = vj;
        this.vk = vk;
        this.qj = qj;
        this.qk = qk;
        this.a = a;
        this.Time = Time;
        this.pos = pos;
    }
    public void setAll(String op, String vj, String vk, String qj, String qk) {
        this.op = op;
        this.vj = vj;
        this.vk = vk;
        this.qj = qj;
        this.qk = qk;
    }
    public void setTime(Integer i) {
    	this.Time =i;
    }

    @Override
    public String toString() {
        return "ReservationStation{" +
        	   "busy='" + busy + '\'' +
        	   "Time='" + Time + '\''+    		
               " name='" + name + '\'' +
               ", busy=" + busy +
               ", op='" + op + '\'' +
               ", vj='" + vj + '\'' +
               ", vk='" + vk + '\'' +
               ", qj='" + qj + '\'' +
               ", qk='" + qk + '\'' +
               ", a='" + a + '\'' +
               ", pos='" + pos + '\'' +
               "======='"  + '\''+
               "point='" + List + '\'' +
               '}';
    }

	public Integer getTime() {
		return Time;
	}

	public void setJ(String vj) {
		this.vj = vj;
		this.qj = null;
	}
	public void setK(String vk) {
		this.vk = vk;
		this.qk = null;
	}
	public void setPos(Integer pos) {
        this.pos = pos;
    }

    public Integer getPos() {
        return pos;
    }
    public List<Point> getList(){
    	return List;
    }
    public void add(Point p) {
    	List.add(p);
    }
	
}
