package reservation_tables;

import java.util.ArrayList;
import java.util.List;



public class ReservationStations {
    private List<ReservationStation> stations; // List to hold all reservation stations 

    // Constructor to create 'n' reservation stations dynamically
    public ReservationStations(int n,String s) {
        stations = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            stations.add(new ReservationStation(s+""+ i)); // Generate names A1, A2, ...
        }
    }

    // Get a specific reservation station by index
    public ReservationStation getStation(int index) {
        if (index >= 0 && index < stations.size()) {
            return stations.get(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid station index: " + index);
        }
    }

    // Reset all reservation stations
    public void resetAllStations() {
        for (ReservationStation station : stations) {
            station.reset();
        }
    }

    // Print the state of all reservation stations
    public void printAllStations() {
        for (ReservationStation station : stations) {
            System.out.println(station);
        }
    }
    public void printAStations(int i) {
    	System.out.println(stations.get(i));
        
    }
    public ReservationStation get(int i) {
    	return stations.get(i);
    }
    public int size() {
    	return stations.size();
    }

    // Update a specific reservation station with setAll
    public void setStation(int index, ReservationStation r) {
        if (index >= 0 && index < stations.size()) {
            stations.get(index).setAll(r.getBusy(), r.getOp(), r.getVj(), r.getVk(),r.getQj(), r.getQk(),r.getA(),r.getTime(),r.getPos());
        } else {
            throw new IndexOutOfBoundsException("Invalid station index: " + index);
        }
    }
    // Update a specific reservation station with setAll
    public void setStation(int index, String op, String vj, String vk, String qj, String qk) {
        if (index >= 0 && index < stations.size()) {
            stations.get(index).setAll(op, vj, vk, qj, qk);
        } else {
            throw new IndexOutOfBoundsException("Invalid station index: " + index);
        }
    }

	public void setTime(int stationIndex, Integer time) {
		stations.get(stationIndex).setTime(time);
		
	}

	public void setStationK(int index,String vk) {
		 if (index >= 0 && index < stations.size()) {
	            stations.get(index).setK(vk);
	        } else {
	            throw new IndexOutOfBoundsException("Invalid station index: " + index);
	        }
		 
	}
	public void setStationJ(int index,String vj) {
		 if (index >= 0 && index < stations.size()) {
	            stations.get(index).setJ(vj);
	        } else {
	            throw new IndexOutOfBoundsException("Invalid station index: " + index);
	        }
		
	}
	public int getFirstEmpty() {
		for(int i=0;i<stations.size();i++) {
			if(!stations.get(i).getBusy())return i;
		}
		return -1;
	}
	public void reset(int i) {
		stations.get(i).reset();
	}
	
	public void add(int i,Point p) {
		stations.get(i).add(p);
	}
}
