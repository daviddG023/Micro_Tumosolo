package StoreBuffer;

import java.util.ArrayList;
import java.util.List;
import reservation_tables.Point;

public class StoreBuffers {
    private List<StoreBuffer> storeBuffers; // List to hold all StoreBuffer objects

    // Constructor to create 'n' store buffers dynamically
    public StoreBuffers(int n) {
        storeBuffers = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            storeBuffers.add(new StoreBuffer("S" + i)); // Generate buffer names (S1, S2, ...)
        }
    }

    public void setStationV(int index,String vk) {
		 if (index >= 0 && index < storeBuffers.size()) {
			 storeBuffers.get(index).setV(vk);
		 } else {
			 throw new IndexOutOfBoundsException("Invalid station index: " + index);
		 }
		 
	}
    
    
    // Get a specific store buffer by index
    public StoreBuffer getStoreBuffer(int index) {
        if (index >= 0 && index < storeBuffers.size()) {
            return storeBuffers.get(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid buffer index: " + index);
        }
    }

    // Reset all store buffers (set busy to false, clear values, etc.)
    public void resetAll() {
        for (StoreBuffer buffer : storeBuffers) {
            buffer.reset();
        }
    }

    // Get the first empty (non-busy) store buffer
    public int getFirstEmpty() {
        for (int i = 0; i < storeBuffers.size(); i++) {
            if (!storeBuffers.get(i).getBusy()) return i;
        }
        return -1; // Return -1 if all buffers are busy
    }

    // Add a dependent instruction (Point) to a store buffer
    public void add(int i, Point p) {
        storeBuffers.get(i).add(p);
    }

    // Getters
    public StoreBuffer get(int i) {
        return storeBuffers.get(i);
    }

    public int size() {
        return storeBuffers.size();
    }

    // Print all store buffers
    public void printAllBuffers() {
        for (StoreBuffer buffer : storeBuffers) {
            System.out.println(buffer);
        }
    }
    public String getV(int i) {
        return storeBuffers.get(i).getV();
    }
}
